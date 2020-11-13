package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
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
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;
import com.dahantc.erp.vo.billpenaltyinterest.service.IBillPenaltyInterestService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

/**
 * 逾期已经产生罚息的账单提醒
 *
 */
@Component
public class BackTaskAlarmPenaltyInterestTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskAlarmPenaltyInterestTask.class);

	private static String CRON = "0 0 10 4 * ?";

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IBillPenaltyInterestService billPenaltyInterestService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTask-AlarmPenaltyInterest-Task is running...");
				try {
					doAlarm();
				} catch (Exception e) {
					logger.error("BackTask-AlarmPenaltyInterest-Task is error...", e);
				}
			}

		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-AlarmPenaltyInterest-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	private void doAlarm() {
		try {
			Date currentDate = DateUtil.getCurrentStartDateTime();
			Map<String, String> cacheCustMap = new HashMap<>();
			Map<String, CustomerProduct> cacheProductMap = new HashMap<>();
			Map<String, String> customerUserMap = new HashMap<>();

			// 查询逾期账单
			List<ProductBills> bills = queryAllHasPenaltyInterestBills(currentDate);
			if (CollectionUtils.isEmpty(bills)) {
				logger.info("无逾期账单");
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

			// 逾期账单按照销售分组
			Map<String, List<ProductBills>> group = groupByCust(bills, customerUserMap);
			group.forEach((ossUserId, pBills) -> {
				if (CollectionUtils.isEmpty(pBills)) return;
				StringBuffer msg = new StringBuffer("您好，您的以下客户已有对账单产生逾期：\r\n");
				// 销售的账单按客户分组
				Map<String, List<ProductBills>> custGroup = pBills.stream().collect(Collectors.groupingBy(ProductBills::getEntityId));
				custGroup.forEach((customerId, billList) -> {
					String str = buildMsg(customerId, pBills, cacheCustMap, cacheProductMap, currentDate);
					if (StringUtils.isNotBlank(str)) {
						msg.append(str).append("\r\n");
					}
				});
				msg.append("请及时处理，以避免产生更多罚息。");
				WeixinMessage.sendMessage("", ossUserId, msg.toString());
			});
		} catch (Exception e) {
			logger.error("账单逾期提醒任务执行失败", e);
		}
	}

	@SuppressWarnings("deprecation")
	private String buildMsg(String customerId, List<ProductBills> pBills, Map<String, String> cacheCustMap, Map<String, CustomerProduct> cacheProductMap,
			Date currentDate) {
		StringBuffer msg = new StringBuffer();
		try {
			msg.append(cacheCustMap.get(customerId) + "：\r\n");
			List<BillPenaltyInterest> list = billPenaltyInterestService.queryPenaltyInterestListByBillId(customerId);
			BigDecimal totalHappendPenaltyInterest = BigDecimal.ZERO;
			BigDecimal currentMonthPenaltyInterest = BigDecimal.ZERO;
			for (ProductBills productBills : pBills) {
				int penaltyInterestDays = 0;

				for (BillPenaltyInterest billPenaltyInterest : list) {
					penaltyInterestDays += billPenaltyInterest.getPenaltyInterestDays();
					if (currentDate.getYear() == billPenaltyInterest.getWtime().getYear()
							&& currentDate.getMonth() == billPenaltyInterest.getWtime().getMonth()) {
						currentMonthPenaltyInterest = billPenaltyInterest.getPenaltyInterest();
					} else {
						currentMonthPenaltyInterest = currentMonthPenaltyInterest.add(billPenaltyInterest.getPenaltyInterest());
					}
				}

				CustomerProduct product = cacheProductMap.get(productBills.getProductId());

				Timestamp finalReceiveTime = productBills.getFinalReceiveTime();
				msg.append(
						String.format("产品【%s】账期【%d】个月，账单时间【%s】逾期时间：%s，共逾期%d天；\r\n",
								productTypeService.readOneByProperty("productTypeValue", product.getProductType()).getProductTypeName(),
								product.getBillPeriod(), DateUtil.convert(productBills.getWtime(), DateUtil.format4),
								DateUtil.convert(finalReceiveTime.getDate() == 1 ? finalReceiveTime : DateUtil.getNextDayStart(finalReceiveTime),
										DateUtil.format1) + "~" + DateUtil.convert(DateUtil.getLastDayStart(new Date()), DateUtil.format1),
								penaltyInterestDays));
			}
			return msg.subSequence(0, msg.length() - 3) + String.format("。\r\n已产生罚息%s元（提成已抵扣%s元，待抵扣%s元）。",
					totalHappendPenaltyInterest.add(currentMonthPenaltyInterest).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),
					totalHappendPenaltyInterest.setScale(2, BigDecimal.ROUND_HALF_UP).toString(),
					currentMonthPenaltyInterest.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		} catch (Exception e) {
			logger.error("生成账单逾期提醒消息异常", e);
		}
		return "";
	}

	private Map<String, List<ProductBills>> groupByCust(List<ProductBills> bills, Map<String, String> customerUserMap) {
		return bills.stream().collect(Collectors.groupingBy(bill -> {
			return customerUserMap.getOrDefault(bill.getEntityId(), "");
		}));
	}

	private List<ProductBills> queryAllHasPenaltyInterestBills(Date currentDate) {
		try {
			String hql = "FROM ProductBills WHERE billStatus IN :billStatus AND finalReceiveTime < :currentDate AND entityType = :entityType";
			Map<String, Object> params = new HashMap<>();
			params.put("billStatus", Arrays.asList(new Integer[] { BillStatus.RECONILED.ordinal(), BillStatus.WRITED_OFF.ordinal() }));
			params.put("currentDate", currentDate);
			params.put("entityType", EntityType.CUSTOMER.ordinal());
			return baseDao.findByhql(hql, params, 0);
		} catch (BaseException e) {
			logger.error("查询逾期账单失败", e);
		}
		return null;
	}

}
