package com.dahantc.erp.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

/**
 * 即将产生罚息的账单提醒
 *
 */
@Component
public class BackTaskAlarmWillPenaltyInterestTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskAlarmWillPenaltyInterestTask.class);

	private static String CRON = "0 0 10 * * ?";

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTaskAlarmWillPenaltyInterestTask is running...");
				try {
					doAlarm();
				} catch (Exception e) {
					logger.error("BackTaskAlarmWillPenaltyInterestTask is error...", e);
				}
			}

		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTaskAlarmWillPenaltyInterestTask：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void doAlarm() {

		// 判断是否是月底的第五个工作日
		Date currentDate = DateUtil.getCurrentStartDateTime();
		Date thisMonthFinal = DateUtil.getThisMonthFinal(currentDate);

		int diff = 0;
		int monthLastDayWeek = thisMonthFinal.getDay();
		switch (monthLastDayWeek) {
		case 0:
			diff = 6;
			break;
		case 6:
			diff = 5;
			break;
		case 5:
			diff = 4;
			break;
		default:
			diff = 6;
			break;
		}
		if (DateUtil.getDiffDays(thisMonthFinal, currentDate) != diff) {
			logger.info("当前日期为：" + DateUtil.convert(currentDate, DateUtil.format1) + "，不是这个月的最后第五个工作日，任务退出！");
			return;
		}

		try {
			Map<String, String> cacheCustMap = new HashMap<>();
			Map<String, CustomerProduct> cacheProductMap = new HashMap<>();
			// 客户id -> 销售id
			Map<String, String> customerUserMap = new HashMap<>();

			// 查询逾期账单
			List<ProductBills> bills = queryAllWillPenaltyInterestBills(currentDate);
			if (CollectionUtils.isEmpty(bills)) {
				logger.info("无即将逾期账单，提醒任务退出");
				return;
			}

			// 查询所有客户
			List<Customer> customers = customerService.queryAllBySearchFilter(null);
			for (Customer customer : customers) {
				customerUserMap.put(customer.getCustomerId(), customer.getOssuserId());
				cacheCustMap.put(customer.getCustomerId(), customer.getCompanyName());
			}
			customers.clear();
			customers = null;

			// 查询所有产品
			List<CustomerProduct> products = customerProductService.queryAllByFilter(null);
			for (CustomerProduct product : products) {
				cacheProductMap.put(product.getProductId(), product);
			}
			products.clear();
			products = null;

			// 将要逾期的账单按照销售分组
			Map<String, List<ProductBills>> group = groupByCust(bills, customerUserMap);
			group.forEach((ossUserId, pBills) -> {
				if (CollectionUtils.isEmpty(pBills)) return;
				StringBuffer msg = new StringBuffer("您好，您有以下客户即将产生逾期，请尽快催款，避免产生罚息：\r\n");
				// 销售的账单按客户分组
				Map<String, List<ProductBills>> custGroup = pBills.stream().collect(Collectors.groupingBy(ProductBills::getEntityId));
				custGroup.forEach((customerId, billList) -> {
					String str = buildMsg(customerId, pBills, cacheCustMap, cacheProductMap, currentDate);
					if (StringUtils.isNotBlank(str)) {
						msg.append(str).append("\r\n");
					}
				});
				WeixinMessage.sendMessage("", ossUserId, msg.toString());
			});
		} catch (Exception e) {
			logger.error("即将逾期账单提醒任务执行失败", e);
		}
	}

	private String buildMsg(String customerId, List<ProductBills> pBills, Map<String, String> cacheCustMap, Map<String, CustomerProduct> cacheProductMap,
			Date currentDate) {
		StringBuffer msg = new StringBuffer();
		try {
			msg.append(cacheCustMap.get(customerId) + "：\r\n");
			for (ProductBills productBills : pBills) {
				CustomerProduct product = cacheProductMap.get(productBills.getProductId());

				msg.append(String.format("产品【%s】账期【%d】个月，账单时间【%s】最后收款时间：%s；\r\n",
						productTypeService.readOneByProperty("productTypeValue", product.getProductType()).getProductTypeName(), product.getBillPeriod(),
						DateUtil.convert(productBills.getWtime(), DateUtil.format4), DateUtil.convert(productBills.getFinalReceiveTime(), DateUtil.format1)));
			}
			return msg.subSequence(0, msg.length() - 3) + "。";
		} catch (Exception e) {
			logger.error("生成即将逾期账单提醒消息异常", e);
		}
		return "";
	}

	private Map<String, List<ProductBills>> groupByCust(List<ProductBills> bills, Map<String, String> customerUserMap) {
		return bills.stream().collect(Collectors.groupingBy(bill -> {
			return customerUserMap.getOrDefault(bill.getEntityId(), "");
		}));
	}

	private List<ProductBills> queryAllWillPenaltyInterestBills(Date currentDate) {
		try {
			String hql = "FROM ProductBills WHERE billStatus IN :billStatus AND finalReceiveTime < :lastMonthStart AND entityType = :entityType";
			Map<String, Object> params = new HashMap<>();
			params.put("billStatus", BillStatus.RECONILED.ordinal());
			params.put("lastMonthStart", DateUtil.getNextMonthFirst(currentDate));
			params.put("entityType", EntityType.CUSTOMER.ordinal());
			return baseDao.findByhql(hql, params, 0);
		} catch (BaseException e) {
			logger.error("查询即将逾期账单失败", e);
		}
		return null;
	}

}
