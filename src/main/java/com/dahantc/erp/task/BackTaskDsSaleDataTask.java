package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.DsOrderStatus;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.dsSaleData.entity.DsCustomerReceiveData;
import com.dahantc.erp.vo.dsSaleData.entity.DsSaleData;
import com.dahantc.erp.vo.dsSaleData.service.IDsCustomerReceiveDataService;
import com.dahantc.erp.vo.dsSaleData.service.IDsSaleDataService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Component
public class BackTaskDsSaleDataTask implements SchedulingConfigurer {
	
	private static final Logger logger = LogManager.getLogger(BackTaskDsSaleDataTask.class);

	@Autowired
	private IBaseDao baseDao;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IDsOrderService dsOrderService;
	
	@Autowired
	private IDsSaleDataService dsSaleDataService;
	
	@Autowired
	private ICustomerService customerService;
	
	@Autowired
	private IDsCustomerReceiveDataService dsCustomerReceiveDataService;
	
	@Autowired
	private IDepartmentService departmentService;
	
	private static String CRON = "0 0 1 * * ?"; // 0秒0分1时 每日每月不管星期几
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行任务
					logger.info("BackTask-DsSaleData-Task is running...");
					autoStatisticDsSaleData();
					autoStatisticDsCustomerRecreive();
				} catch (Exception e) {
					logger.error("BackTask-DsSaleData-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-DsSaleData-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}
	
	/**
	 * 自动生成销售统计记录
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void autoStatisticDsSaleData() throws BaseException {
		SearchFilter filter = new SearchFilter();
		List<User> users;
		try {
			filter.getRules().add(new SearchRule("jobType", Constants.ROP_CN, JobType.Sales.name()));
			users = userService.queryAllBySearchFilter(filter);
			List<DsSaleData> dsSaleDatas = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.format2);
			String dayFirst = sdf.format(DateUtil.getPreviousStartDateTime());
			String dayLast = sdf.format(DateUtil.getPreviousEndDateTime());
			for (User user : users) {
				DsSaleData dsSaleData = new DsSaleData();
				Department department = departmentService.read(user.getDeptId());
				dsSaleData.setDeptName(department.getDeptname());
				int customerAddCount = 0;
				int logAddCount = 0;
				dsSaleData.setOssUserName(user.getRealName());
				
				SearchFilter customerFilter = new SearchFilter();
				customerFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, user.getOssUserId()));
				List<Customer> customers = customerService.queryAllBySearchFilter(customerFilter);
				int customerCount = 0;
				int newCustomerCount = 0;
				int oldCustomerCount = 0;
				if (!CollectionUtils.isEmpty(customers)) {
					customerCount = customers.size();
					for (Customer customer : customers) {
						SearchFilter orderFilter = new SearchFilter();
						orderFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customer.getCustomerId()));
						orderFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getPreviousStartDateTime()));
						orderFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getPreviousEndDateTime()));
						List<DsOrder> dsOrders = dsOrderService.queryAllBySearchFilter(orderFilter);
						if (!CollectionUtils.isEmpty(dsOrders)) {
							if (dsOrders.size()==1) {
								newCustomerCount = newCustomerCount + 1;
							}else {
								oldCustomerCount = oldCustomerCount + 1;
							}
						}
					}
				}
				dsSaleData.setCustomerCount(customerCount);
				dsSaleData.setNewCustomerCount(newCustomerCount);
				dsSaleData.setOldCustomerCount(oldCustomerCount);
				String customerCountSql = "SELECT count(*),ossuserid from erp_customer"
						 + " where ossuserid =" + "\"" +  user.getOssUserId() + "\""
						 + " and wtime >= " + "\"" + dayFirst + "\""
						 + " and wtime <= " + "\"" + dayLast + "\""
						 + " group by ossuserid";
				//查询新增客户数
				List<Object[]> customerAddList = (List<Object[]>) baseDao.selectSQL(customerCountSql);
				if (!CollectionUtils.isEmpty(customerAddList)) {
					for (Object[] object : customerAddList) {
						customerAddCount = Integer.valueOf(object[0].toString());
					}
				}
				dsSaleData.setAddCustomerCount(customerAddCount);
				//查询新增日志数
				String logCount = "SELECT count(*),ossuserid from erp_supplier_contactlog"
						 + " where ossuserid =" + "\"" +  user.getOssUserId() + "\""
						 + " and wtime >= " + "\"" + dayFirst + "\""
						 + " and wtime <= " + "\"" + dayLast + "\""
						 + " group by ossuserid";
				List<Object[]> logAddList = (List<Object[]>) baseDao.selectSQL(logCount);
				if (!CollectionUtils.isEmpty(logAddList)) {
					for (Object[] object : logAddList) {
						logAddCount = Integer.valueOf(object[0].toString());
					}
				}
				dsSaleData.setAddLogCount(logAddCount);
				//查询签单数
				SearchFilter dsOrderFilter = new SearchFilter();
				dsOrderFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
				dsOrderFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.getPreviousStartDateTime()));
				dsOrderFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.getPreviousEndDateTime()));
				dsOrderFilter.getRules().add(new SearchRule("orderStatus", Constants.ROP_EQ, DsOrderStatus.FINISH.getCode()));
				List<DsOrder> dsOrders = dsOrderService.queryAllBySearchFilter(dsOrderFilter);
				BigDecimal totalPrice = new BigDecimal(0);
				BigDecimal purchaseCost = new BigDecimal(0);
				if (!CollectionUtils.isEmpty(dsOrders)) {
					dsSaleData.setOrderCount(dsOrders.size());
					//签单金额
					for (DsOrder dsOrder : dsOrders) {
						totalPrice = totalPrice.add(dsOrder.getSalesMoney());
						if (dsOrder.getPurchaseCost() != null) {
							purchaseCost = purchaseCost.add(dsOrder.getPurchaseCost());
						}
					}
					dsSaleData.setOrderTotalPrice(totalPrice);
					//成本总金额
				}else {
					dsSaleData.setOrderCount(0);
					dsSaleData.setOrderTotalPrice(totalPrice);
				}
				//累计客户毛利
				BigDecimal grossProfit = totalPrice.subtract(purchaseCost);
				dsSaleData.setGrossProfit(grossProfit);
				//毛利率
				BigDecimal grossProfitRate = new BigDecimal(0);
				if (!grossProfit.equals(BigDecimal.ZERO)) {
					grossProfitRate = grossProfit.divide(totalPrice, 2, BigDecimal.ROUND_HALF_UP);
				}
				dsSaleData.setGrossProfitRate(grossProfitRate);
				//客户回款
				
				//业绩目标
				
				//利润目标
				
				//业绩完成率
				
				//毛利完成率
				
				dsSaleData.setWtime(new Date());
				dsSaleDatas.add(dsSaleData);
			}
			dsSaleDataService.saveByBatch(dsSaleDatas);
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("BackTask-DsSaleData-Task is error...", e);
		}
	}
	
	/**
	 * 自动生成销售统计记录
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void autoStatisticDsCustomerRecreive() throws BaseException {
		SearchFilter filter = new SearchFilter();
		List<User> users;
		List<DsCustomerReceiveData> dsCustomerReceiveDatas = new ArrayList<>();
		try {
			filter.getRules().add(new SearchRule("jobType", Constants.ROP_CN, JobType.Sales.name()));
			users = userService.queryAllBySearchFilter(filter);
			SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.format2);
			String dayFirst = sdf.format(DateUtil.getPreviousStartDateTime());
			String dayLast = sdf.format(DateUtil.getPreviousEndDateTime());
			for (User user : users) {
				String sql = "select sum(salesmoney) as totalmoney,customername,ossusername" + " from erp_ds_order"
						+ " where ossuserid =" + "\"" + user.getOssUserId() + "\"" 
						+ " and orderStatus =" + "\"" + DsOrderStatus.FINISH.getCode() + "\"" 
						+ " and wtime >= " + "\"" + dayFirst
						+ "\"" + " and wtime <= " + "\"" + dayLast + "\"" + " group by customername";
				List<Object[]> dsOrders = (List<Object[]>) baseDao.selectSQL(sql);
				for (Object[] object : dsOrders) {
					DsCustomerReceiveData dsCustomerReceiveData = new DsCustomerReceiveData();
					Department department = departmentService.read(user.getDeptId());
					dsCustomerReceiveData.setDeptName(department.getDeptname());
					dsCustomerReceiveData.setOssUserName(user.getRealName());
					dsCustomerReceiveData.setOrderTotalPrice(new BigDecimal((object[0]).toString()));
					dsCustomerReceiveData.setCustomerName(object[1].toString());
					dsCustomerReceiveData.setWtime(new Date());
					dsCustomerReceiveDatas.add(dsCustomerReceiveData);
				}
			}
			dsCustomerReceiveDataService.saveByBatch(dsCustomerReceiveDatas);
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("BackTask-DsSaleData-Task is error...", e);
		}
	}

}
