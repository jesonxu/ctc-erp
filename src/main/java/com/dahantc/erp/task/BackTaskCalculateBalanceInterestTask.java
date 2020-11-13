package com.dahantc.erp.task;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.util.AccountConfigUtil;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.accountbalance.entity.AccountBalance;
import com.dahantc.erp.vo.accountbalance.service.IAccountBalanceService;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;
import com.dahantc.erp.vo.balanceinterest.service.IBalanceInterestService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计前一天的账户余额和计息
 *
 */
@Component
public class BackTaskCalculateBalanceInterestTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskCalculateBalanceInterestTask.class);

	private static String CRON = "0 0 1 * * ?";

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IAccountBalanceService accountBalanceService;

	@Autowired
	private IBalanceInterestService balanceInterestService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {

				logger.info("BackTask-CalculateBalanceInterest-Task is running...");

				// 查询计息率
				BigDecimal interestRatio = getInterestRatio();
				if (interestRatio == null) {
					logger.info("系统参数不存在：计息利率不存在或者小于0，任务退出");
					return;
				}

				// 查询后统计时间
				Date startDate = getStartDate();
				if (startDate == null) {
					logger.info("未获取到统计时间：未获取到统计时间，任务退出");
					return;
				}
				startDate = DateUtil.getDayStart(startDate);

				// 查询所有客户, allAccountMap: loginName -> customerId
				Map<String, Customer> allCustomerMap = getAllCustomer();
				Map<String, String> allAccountMap = getAllAccount();

				// 按天计算
				Date endDate = DateUtil.getNextDayStart(new Date());
				if (DateUtil.getNextDayStart(startDate).getTime() < endDate.getTime()) {
					long _startTime = System.currentTimeMillis();
					Date _startDate = DateUtil.getNextDayStart(startDate);
					logger.info(DateUtil.convert(_startDate, DateUtil.format1) + "~" + DateUtil.convert(DateUtil.getLastDayStart(endDate), DateUtil.format1)
							+ "，按天计算余额计息开始。。。");
					while (DateUtil.getNextDayStart(startDate).getTime() < endDate.getTime()) {
						logger.info("按天计算余额计息开始：" + DateUtil.convert(startDate, DateUtil.format1));
						startDate = DateUtil.getNextDayStart(startDate);
						doCalculateInterest(allCustomerMap, allAccountMap, startDate, DateUtil.getNextDayStart(startDate), interestRatio);
					}
					logger.info(DateUtil.convert(_startDate, DateUtil.format1) + "~" + DateUtil.convert(DateUtil.getLastDayStart(endDate), DateUtil.format1)
							+ "，按天计算余额计息完成，耗时：" + (System.currentTimeMillis() - _startTime));
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Date nextExecutionTime = null;
				logger.info("BackTask-CalculateBalanceInterest-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	private void doCalculateInterest(Map<String, Customer> allCustomerMap, Map<String, String> allAccountMap, Date startDate, Date endDate,
			BigDecimal interestRatio) {
		try {
			// 查询账户余额信息
			List<AccountBalance> allAccountBalanceList = getAllAccountBalance(startDate, endDate);
			if (CollectionUtils.isEmpty(allAccountBalanceList)) {
				logger.info(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1) + "，未查询到余额信息");
			}
			// 按客户分组
			Map<String, List<AccountBalanceInfo>> groupMap = allAccountBalanceList.stream()
					.filter(accountBalance -> allAccountMap.get(accountBalance.getAccount()) != null)
					.map(accountBalance -> new AccountBalanceInfo(allAccountMap.get(accountBalance.getAccount()), accountBalance))
					.collect(Collectors.groupingBy(AccountBalanceInfo::getCustomerId));
			// 计算并保存
			List<BalanceInterest> balanceInterestList = new ArrayList<>();
			allCustomerMap.keySet().forEach(customerId -> {
				BalanceInterest balanceInterest = calculate(allCustomerMap.get(customerId), groupMap.get(customerId), startDate, interestRatio);
				if (balanceInterest != null) {
					balanceInterestList.add(balanceInterest);
				}
			});
			balanceInterestService.saveByBatch(balanceInterestList);
			logger.info(DateUtil.convert(startDate, DateUtil.format1) + "，保存" + balanceInterestList.size() + "条记录");
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private BalanceInterest calculate(Customer customer, List<AccountBalanceInfo> infos, Date startDate, BigDecimal interestRatio) {
		if (CollectionUtils.isEmpty(infos)) {
			return null;
		}
		BalanceInterest balanceInterest = new BalanceInterest();
		balanceInterest.setCustomerId(customer.getCustomerId());
		balanceInterest.setDeptId(customer.getDeptId());
		balanceInterest.setInterestRatio(interestRatio);
		balanceInterest.setOssuserId(customer.getOssuserId());
		BigDecimal accountBalanceTotal = BigDecimal.ZERO;
		for (AccountBalanceInfo accountBalanceInfo : infos) {
			accountBalanceTotal = accountBalanceTotal.add(accountBalanceInfo.getAccountBalance().getAccountBalance());
		}
		balanceInterest.setAccountBalance(accountBalanceTotal);
		balanceInterest.setInterest(accountBalanceTotal.multiply(interestRatio).setScale(2, BigDecimal.ROUND_HALF_UP));
		balanceInterest.setWtime(new Timestamp(DateUtil.getLastDayStart(startDate).getTime()));
		return balanceInterest;
	}

	/**
	 * 获取计息的账号 金额信息（过滤掉不需要计息的账号）
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return 账号 - 金额信息
	 */
	private List<AccountBalance> getAllAccountBalance(Date startDate, Date endDate) {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
			List<AccountBalance> accountBalanceList = accountBalanceService.queryAllBySearchFilter(searchFilter);
			// 过滤掉不需要计息的账号
			if (accountBalanceList != null && !accountBalanceList.isEmpty()) {
				// 获取配置的不需要计息的账号
				List<String> accountList = AccountConfigUtil.getInstance().readAllOnlyAccount();
				if (accountList != null && !accountList.isEmpty()) {
					return accountBalanceList.stream()
							.filter(accountBalance -> !accountList.contains(accountBalance.getAccount()))
							.collect(Collectors.toList());
				}
			}
			return accountBalanceList;
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	private Map<String, Customer> getAllCustomer() {
		try {
			List<Customer> list = customerService.queryAllBySearchFilter(null);
			if (list != null) {
				return list.stream().collect(Collectors.toMap(Customer::getCustomerId, customer -> customer));
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	private Map<String, String> getAllAccount() {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("settleType", Constants.ROP_NE, SettleType.After.ordinal()));
			List<CustomerProduct> list = customerProductService.queryAllByFilter(searchFilter);
			if (list != null) {
				Map<String, String> result = new HashMap<>();
				list.forEach(product -> {
					if (StringUtils.isNotBlank(product.getAccount())) {
						String[] arr = product.getAccount().split("\\|");
						for (String loginName : arr) {
							if (StringUtils.isNotBlank(loginName)) {
								result.put(loginName, product.getCustomerId());
							}
						}
					}
				});
				return result;
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	private Date getStartDate() {
		try {

			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<BalanceInterest> list = balanceInterestService.findByFilter(1, 0, searchFilter);
			if (!CollectionUtils.isEmpty(list)) {
				return DateUtil.getNextDayStart((Date) list.get(0).getWtime());
			}

			searchFilter.getOrders().clear();
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<AccountBalance> lis2 = accountBalanceService.findByFilter(1, 0, searchFilter);
			if (!CollectionUtils.isEmpty(lis2)) {
				return DateUtil.getLastDayStart((Date) lis2.get(0).getWtime());
			}

		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	private BigDecimal getInterestRatio() {
		try {
			Parameter parameter = parameterService.getOneParameterByProperty("paramkey", "interest_ratio");
			if (parameter != null) {
				String paramvalue = parameter.getParamvalue();
				if (StringUtils.isNotBlank(paramvalue) && NumberUtils.isParsable(paramvalue)) {
					BigDecimal interestRatio = new BigDecimal(paramvalue);
					if (interestRatio.signum() > 0) {
						return interestRatio;
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private class AccountBalanceInfo {

		private String customerId;
		private AccountBalance accountBalance;

		public AccountBalanceInfo(String customerId, AccountBalance accountBalance) {
			this.customerId = customerId;
			this.accountBalance = accountBalance;
		}

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public AccountBalance getAccountBalance() {
			return accountBalance;
		}

		public void setAccountBalance(AccountBalance accountBalance) {
			this.accountBalance = accountBalance;
		}

	}

}