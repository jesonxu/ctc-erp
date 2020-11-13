package com.dahantc.erp.task;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.enums.ParamType;
import com.dahantc.erp.util.CustomerChangeConstant;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.contract.entity.Contract;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerStatistics.service.ICustomerStatisticsService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.supplierContactLog.service.ISupplierContactLogService;
import com.dahantc.erp.vo.supplierContacts.service.ISupplierContactsService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 客户更改
 * 
 * @author 8520
 */
@Component
public class BackTaskCustomerChangeTask {

	private static Logger logger = LoggerFactory.getLogger(BackTaskCustomerChangeTask.class);

	private ICustomerService customerService;

	private ICustomerTypeService customerTypeService;

	private IContractService contractService;

	private ISupplierContactLogService supplierContactLogService;

	private IMsgCenterService msgCenterService;

	private IMsgDetailService msgDetailService;

	private IDepartmentService departmentService;

	private IUserService userService;

	private ICustomerStatisticsService customerStatisticsService;

	private ICustomerProductService customerProductService;

	private Environment ev;

	private ISupplierContactsService supplierContactsService;

	private IParameterService parameterService;

	/**
	 * 分析客户信息 每天早上9点进行
	 */
	@Scheduled(cron = "0 0 9 * * ?")
	public void analysisCustomerInfo() {
		try {
			// 根据收支表生成日常费用统计表
			long startTime = System.currentTimeMillis();
			WeixinMessage.initwxParam(ev);
			updateParameter();
			analysisCustomer();
			logger.info("检查客户是否需要变更级别完成，耗时：" + (System.currentTimeMillis() - startTime) + " MS");
		} catch (Exception e) {
			logger.error("BackTask-BackTaskCustomerChangeTask-Task is error...", e);
		}
	}

	private void updateParameter() {
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, ParamType.CUSTOMER_CHANGE_RULE.ordinal()));
		filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		try {
			List<Parameter> parameterList = parameterService.findAllByCriteria(filter);
			if (!CollectionUtils.isEmpty(parameterList)) {
				for (Parameter parameter : parameterList) {
					switch (parameter.getParamkey()) {
					case "CONTRACT_WARN_INTERVAL":
						CustomerChangeConstant.CONTRACT_WARN_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "CONTRACT_COST_INTERVAL":
						CustomerChangeConstant.CONTRACT_COST_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "CONTRACT_BEFORE_EXPIRE_INTERVAL":
						CustomerChangeConstant.CONTRACT_BEFORE_EXPIRE_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "CONTRACT_OVERDUE_DOWNGRADE":
						CustomerChangeConstant.CONTRACT_OVERDUE_DOWNGRADE = Integer.parseInt(parameter.getParamvalue());
						break;
					case "WARNING_INTERVAL":
						CustomerChangeConstant.WARNING_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "TEST_LOG_INTERVAL":
						CustomerChangeConstant.TEST_LOG_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "TEST_COST_INTERVAL":
						CustomerChangeConstant.TEST_COST_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "INTEREST_CHANGE_INTERVAL":
						CustomerChangeConstant.INTEREST_CHANGE_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					case "SILENCE_CHANGE_INTERVAL":
						CustomerChangeConstant.SILENCE_CHANGE_INTERVAL = Integer.parseInt(parameter.getParamvalue());
						break;
					default:
						break;
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("获取客户变更规则的系统参数异常", e);
		}
	}

	/**
	 * 分析处理客户的升降级
	 */
	public void analysisCustomer() {
		// 查询客户信息
		try {
			// 获取客户类型
			List<CustomerType> customerTypes = customerTypeService.queryAllBySearchFilter(new SearchFilter());
			if (customerTypes == null || customerTypes.isEmpty()) {
				logger.info("未能获取到客户的类型信息，本次客户分析取消");
				return;
			}
			// 客户类型
			Map<String, CustomerType> customerTypeMap = customerTypes.stream().collect(Collectors.toMap(CustomerType::getCustomerTypeId, t -> t, (o, n) -> o));
			Map<Integer, CustomerType> valueCustomerTypeMap = customerTypes.stream()
					.collect(Collectors.toMap(CustomerType::getCustomerTypeValue, t -> t, (o, n) -> o));
			customerTypes.clear();
			// 客户较多 只能进行分批查询 分批处理,防止一次查询过多，导致消耗资源过多 (根据创建时间 进行分页查询)
			int page = 1;
			while (true) {
				// 不查询公共池中的客户信息
				String customerHql = "select c from Customer c left join CustomerType ct on c.customerTypeId = ct.customerTypeId where ct.customerTypeValue != :customerType order by ct.customerTypeValue asc, c.wtime asc ";
				String customerCountHql = "select count(1) from Customer c left join CustomerType ct on c.customerTypeId = ct.customerTypeId where ct.customerTypeValue != :customerType ";
				Map<String, Object> customerParam = new HashMap<>(1);
				customerParam.put("customerType", CustomerTypeValue.PUBLIC.getCode());
				PageResult<Customer> pageResult = customerService.findByhql(customerHql, customerCountHql, customerParam, page,
						CustomerChangeConstant.CUSTOMER_BATCH_SIZE);
				if (pageResult == null || pageResult.getData() == null || pageResult.getData().isEmpty()) {
					break;
				}
				List<Customer> customerList = pageResult.getData();
				// 客户分类
				Map<String, List<Customer>> typeCustomerInfos = customerList.stream().collect(Collectors.groupingBy(Customer::getCustomerTypeId));
				customerList.clear();
				for (String typeId : typeCustomerInfos.keySet()) {
					CustomerType customerType = customerTypeMap.get(typeId);
					if (customerType == null) {
						continue;
					}
					// 客户信息
					List<Customer> typeCustomers = typeCustomerInfos.get(typeId);
					if (typeCustomers != null && !typeCustomers.isEmpty()) {
						if (customerType.getCustomerTypeValue() == CustomerTypeValue.CONTRACTED.getCode()) {
							// 合同客户
							analysisContractCustomer(typeCustomers, valueCustomerTypeMap);
						} else if (customerType.getCustomerTypeValue() == CustomerTypeValue.TESTING.getCode()) {
							// 测试客户
							analysisTestCustomer(typeCustomers, valueCustomerTypeMap);
						} else if (customerType.getCustomerTypeValue() == CustomerTypeValue.INTENTION.getCode()) {
							// 意向客户
							analysisInterestCustomer(typeCustomers, valueCustomerTypeMap);
						} else if (customerType.getCustomerTypeValue() == CustomerTypeValue.SILENCE.getCode()) {
							// 沉默客户
							analysisSilenceCustomer(typeCustomers, valueCustomerTypeMap);
						}
					}
				}
				page = page + 1;
			}
		} catch (ServiceException e) {
			logger.error("查询客户信息异常", e);
		}
	}

	/**
	 * 处理合同客户
	 * 
	 * @param customers
	 *            客户信息
	 * @param customerTypeMap
	 *            客户类型
	 */
	private void analysisContractCustomer(List<Customer> customers, Map<Integer, CustomerType> customerTypeMap) {
		// 1、合同客户，1个月没有联系日志，通知对应销售和部门领导，一个客户一个月只通知一次。
		// 2、合同客户，合同到期前一个月，通知对应销售和部门领导，一个客户一个月只通知一次。
		// 3、合同客户，合同到期后三个月还没有续签完成，自动降级到测试客户，并通知相关销售和其上级。
		try {
			List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			// 查询所有的客户合同
			Map<String, List<Contract>> contracts = contractService.findEntityContract(customerIds, EntityType.CUSTOMER.ordinal());
			// 查询最近一个月的联系日志
			Map<String, Integer> logInfo = supplierContactLogService.queryCustomerContactLog(customerIds, CustomerChangeConstant.CONTRACT_WARN_INTERVAL, null);
			// 一个月内已经有的警告信息
			Map<String, Integer> warnInfo = msgCenterService.queryCustomerWarnCount(customerIds, CustomerChangeConstant.WARNING_INTERVAL);
			// 批量查询用户
			Map<String, User> userMap = userService.findUserByIds(customers.stream().map(Customer::getOssuserId).collect(Collectors.toList()));
			// 客户的消耗量（三个月内）
			Map<String, Long> customerCosts = customerStatisticsService.findCustomerCost(customerIds, CustomerChangeConstant.CONTRACT_COST_INTERVAL, null);
			// 客户处于当前状态的时间
			Map<String, Timestamp> customerStateTime = customerService.findCustomerInThisStateTime(customers);
			for (Customer customer : customers) {
				// 对应销售
				User user = userMap.get(customer.getOssuserId());
				// 在最近一个月是否已经发了警告
				boolean hadWarn = false;
				if (warnInfo != null && !warnInfo.isEmpty()) {
					// 最近一个月警告的次数
					Integer waningTimes = warnInfo.get(customer.getCustomerId());
					hadWarn = waningTimes != null && waningTimes > 0;
				}
				// 客户合同
				List<Contract> customerContract = contracts.get(customer.getCustomerId());
				if (customerContract == null || customerContract.isEmpty()) {
					// 没有合同
					if (StringUtil.isBlank(customer.getContractFiles()) && !hadWarn) {
						notice(customer, user, "合同客户：" + customer.getCompanyName() + "，没有合同，请尽快上传");
					}
				} else {
					// 合同存在 需要判断合同的有效期(到期前一个月应该通知)
					// 一个月内的合同数量
					AtomicInteger oneMonthValidCount = new AtomicInteger(0);
					// 过期三个月内的合同数量
					AtomicInteger overdueWithinThreeMonthCount = new AtomicInteger(0);
					// 有效期在一个月以上的合同数
					AtomicInteger normalContractCount = new AtomicInteger(0);
					// 一个月后的时间
					long monthLater = DateUtil.getMonthBefore(-CustomerChangeConstant.CONTRACT_BEFORE_EXPIRE_INTERVAL);
					// 三个月之前时间
					long threeMonthAgo = DateUtil.getMonthBefore(CustomerChangeConstant.CONTRACT_OVERDUE_DOWNGRADE);
					customerContract.forEach(contract -> {
						// 有效期
						long validTime = contract.getValidityDateEnd().getTime();
						if (validTime - System.currentTimeMillis() >= 0) {
							// 前提是必须要在有效期内
							if (validTime - monthLater <= 0) {
								// 正常 有效期在一个月内 需要警告合同
								oneMonthValidCount.incrementAndGet();
							} else {
								// 正常不需要警告合同
								normalContractCount.incrementAndGet();
							}
						} else if (validTime - threeMonthAgo >= 0) {
							// 过期三个月内（其余的过期的不用管）
							overdueWithinThreeMonthCount.incrementAndGet();
						}
					});
					// 是否有联系日志
					boolean oneMonthHasContact = false;
					if (logInfo != null) {
						// 检查联系日志
						Integer contactLogCount = logInfo.get(customer.getCustomerId());
						oneMonthHasContact = contactLogCount != null && contactLogCount > 0;
					}
					// 是否有消耗量
					boolean hasCost = false;
					if (customerCosts != null) {
						Long cost = customerCosts.get(customer.getCustomerId());
						hasCost = cost != null && cost > 0;
					}
					// 客户处于这种状态的时间
					boolean withinStateThanInterval = false;
					if (customerStateTime != null) {
						// 处于这种状态的时间点
						Timestamp stateTime = customerStateTime.get(customer.getCustomerId());
						long timePoint = DateUtil.getMonthBefore(CustomerChangeConstant.CONTRACT_WARN_INTERVAL);
						withinStateThanInterval = stateTime != null && (stateTime.getTime() - timePoint <= 0);
					}
					// 需要把三个条件进行合并判断
					if (normalContractCount.get() > 0) {
						// 需要进行通知（客户处于这种状态一个月以上，且一个月内没有联系）
						if (!oneMonthHasContact && !hadWarn && withinStateThanInterval) {
							String content = "客户：" + customer.getCompanyName() + "，已经" + CustomerChangeConstant.CONTRACT_WARN_INTERVAL + "个月没有联系过";
							notice(customer, user, content);
						}
					} else if (oneMonthValidCount.get() > 0) {
						if (!hadWarn) {
							// 通知 （合同时间可能会有交叉 只能这样进行通知）
							String content = "客户：" + customer.getCompanyName() + "，合同将在" + CustomerChangeConstant.CONTRACT_BEFORE_EXPIRE_INTERVAL
									+ "个月内过期，请尽快续签";
							notice(customer, user, content);
						}
					} else if (overdueWithinThreeMonthCount.get() == 0 && !hasCost) {
						// 没有过期三个月内的合同（合同都过期三个月以上了） 并且 三个月内没有消耗量 ->证明这个客户已经真的没有用了
						// （防止客户信息异常导致错误降级）
						CustomerType from = customerTypeMap.get(CustomerTypeValue.CONTRACTED.getCode());
						CustomerType to = customerTypeMap.get(CustomerTypeValue.TESTING.getCode());
						String depict = "客户：" + customer.getCompanyName() + "，因合同过期已经超过" + CustomerChangeConstant.CONTRACT_OVERDUE_DOWNGRADE
								+ "个月，且在" + CustomerChangeConstant.CONTRACT_COST_INTERVAL + "个月内没有任何产品消耗，由" + from.getCustomerTypeName() + "降至" + to.getCustomerTypeName();
						// 通知（不管最近是否已经通知）
						notice(customer, user, depict);
						// 客户进行降级处理
						customerService.changeGrade(customer, user, from, to, depict, false);
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("分析处理合同客户异常", e);
		}
	}

	/**
	 * 通知
	 * 
	 * @param customer
	 *            客户
	 * @param user
	 *            用户（销售）
	 * @param content
	 *            通知的内容
	 */
	private void notice(Customer customer, User user, String content) {
		if (user == null) {
			logger.info("客户的销售为空，无法进行通知");
			return;
		}
		// 写消息中心
		String deptId = customer.getDeptId();
		String leaderContent = "销售：" + user.getRealName() + "," + content;
		try {
			if ("true".equals(WeixinMessage.getWeixinParam().getTest().toLowerCase())) {
				// 消息中心需要增加一条记录
				// 消息中心中需要通知到人
				String msgId = writeMsg(customer, content);
				msgNoticeUser(msgId, user);
				// 用于测试环境
				logger.info("测试环境：--通知销售[" + user.getRealName() + "]信息：" + content);
				List<User> noticeLeaderInfo = findUserLeader(deptId);
				if (noticeLeaderInfo == null || noticeLeaderInfo.isEmpty()) {
					logger.info("未能查找到领导信息，本次通知领导失败");
					return;
				}
				String leaderMsgId = writeMsg(customer, leaderContent);
				msgNoticeUser(leaderMsgId, noticeLeaderInfo.toArray(new User[noticeLeaderInfo.size()]));
				noticeLeaderInfo.forEach(leader -> {
					logger.info("测试环境：--通知领导[" + leader.getRealName() + "]信息：" + leaderContent);
				});
				return;
			}
			// 微信通知 销售本人
			String sendRes = WeixinMessage.sendMessageByMobile(user.getContactMobile(), content);
			if (StringUtils.isBlank(sendRes)) {
				WeixinMessage.sendMessage("", user.getOssUserId(), content);
			}
			// 消息中心需要增加一条记录
			// 消息中心中需要通知到人
			String msgId = writeMsg(customer, content);
			msgNoticeUser(msgId, user);
			List<User> noticeLeaderInfo = findUserLeader(deptId);
			if (noticeLeaderInfo == null || noticeLeaderInfo.isEmpty()) {
				logger.info("未能查找到领导信息，本次通知领导失败，部门Id：" + deptId);
				return;
			}
			String leaderMsgId = writeMsg(customer, leaderContent);
			msgNoticeUser(leaderMsgId, noticeLeaderInfo.toArray(new User[noticeLeaderInfo.size()]));
			noticeLeaderInfo.forEach(leader -> {
				// 通知销售对应的领导
				String sendLeaderRes = WeixinMessage.sendMessageByMobile(leader.getContactMobile(), leaderContent);
				if (StringUtils.isBlank(sendLeaderRes)) {
					WeixinMessage.sendMessage("", leader.getOssUserId(), leaderContent);
				}
			});
		} catch (ServiceException e) {
			logger.error("获取需要通知的人异常", e);
		}
	}

	/**
	 * 写消息中心日志
	 * 
	 * @param customer
	 *            客户
	 * @param content
	 *            内容
	 * @return 消息id
	 */
	private String writeMsg(Customer customer, String content) {
		MsgCenter msgCenter = new MsgCenter();
		msgCenter.setInfotype(MsgCenter.CUSTOMER_WARNING);
		msgCenter.setMessagesourceid(customer.getCustomerId());
		msgCenter.setMessagedetail(content);
		msgCenter.setWtime(new Date());
		try {
			msgCenterService.save(msgCenter);
			return msgCenter.getMessageid();
		} catch (ServiceException e) {
			logger.error("保存消息中心消息异常", e);
		}
		return null;
	}

	/**
	 * 通知消息 通知到指定用户
	 * 
	 * @param msgId
	 *            消息id
	 * @param users
	 *            用户
	 */
	private void msgNoticeUser(String msgId, User... users) {
		if (StringUtil.isBlank(msgId)) {
			return;
		}
		try {
			List<MsgDetail> msgDetailList = new ArrayList<>();
			for (User user : users) {
				MsgDetail msgDetail = new MsgDetail();
				msgDetail.setState(1);
				msgDetail.setMessageid(msgId);
				msgDetail.setUserid(user.getOssUserId());
				msgDetail.setWtime(new Date());
				msgDetailList.add(msgDetail);
			}
			msgDetailService.saveByBatch(msgDetailList);
		} catch (ServiceException e) {
			logger.error("保存通知消息用户详情异常", e);
		}
	}

	/**
	 * 查找部门领导
	 * 
	 * @param deptId
	 *            部门
	 * @return 需要通知的用户
	 */
	private List<User> findUserLeader(String deptId) throws ServiceException {
		if (StringUtil.isBlank(deptId)) {
			return null;
		}
		SearchFilter userFilter = new SearchFilter();
		userFilter.getRules().add(new SearchRule("identityType", Constants.ROP_EQ, IdentityType.LEADER_IN_DEPT.ordinal()));
		userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
		List<User> leaderList = userService.queryAllBySearchFilter(userFilter);
		if (leaderList != null && !leaderList.isEmpty()) {
			return leaderList;
		}
		Department department = departmentService.read(deptId);
		if (department != null && StringUtil.isNotBlank(department.getParentid())) {
			Department fatherDept = departmentService.read(department.getParentid());
			if (fatherDept != null && !deptId.equals(fatherDept.getDeptid())) {
				return findUserLeader(fatherDept.getDeptid());
			}
		}
		return null;
	}

	/**
	 * 处理测试客户
	 * 
	 * @param customers
	 *            客户信息
	 * @param customerTypeMap
	 *            客户类型
	 */
	private void analysisTestCustomer(List<Customer> customers, Map<Integer, CustomerType> customerTypeMap) {
		// 测试客户，1个月没有联系日志，自动降级到意向客户，并通知相关销售和其上级。
		// 测试客户：1个月有联系日志，且合同流程归档（合同在有效期内），系统自动升级到合同客户。(归合同流程管)
		List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		// 查询最近一个月的联系日志
		Map<String, Integer> logInfo = supplierContactLogService.queryCustomerContactLog(customerIds, CustomerChangeConstant.TEST_LOG_INTERVAL, null);
		// 批量查询用户
		Map<String, User> users = userService.findUserByIds(customers.stream().map(Customer::getOssuserId).collect(Collectors.toList()));
		// 客户的消耗量（一个月内）
		Map<String, Long> customerCosts = customerStatisticsService.findCustomerCost(customerIds, CustomerChangeConstant.TEST_COST_INTERVAL, null);
		// 客户处于当前状态的时间
		Map<String, Timestamp> customerTimeInfo = customerService.findCustomerInThisStateTime(customers);
		try {
			for (Customer customer : customers) {
				// 对应销售
				User user = users.get(customer.getOssuserId());
				// 是否有联系日志
				boolean hasContact = false;
				if (logInfo != null) {
					// 检查联系日志
					Integer logCount = logInfo.get(customer.getCustomerId());
					hasContact = logCount != null && logCount > 0;
				}
				// 是否有消耗量
				boolean hasCost = false;
				if (customerCosts != null) {
					Long cost = customerCosts.get(customer.getCustomerId());
					hasCost = cost != null && cost > 0;
				}
				// 客户处于这种状态是否已经有一个月
				boolean moreThanOneMonth = false;
				if (customerTimeInfo != null) {
					// 处于这种状态的时间点
					Timestamp stateTime = customerTimeInfo.get(customer.getCustomerId());
					moreThanOneMonth = stateTime != null && (stateTime.getTime() - DateUtil.getMonthBefore(CustomerChangeConstant.TEST_LOG_INTERVAL) <= 0);
				}
				if (!hasContact && !hasCost && moreThanOneMonth) {
					// 客户处于这种状态超过一个月，且一个月没有联系日志，也没有量（客户进行降级）
					CustomerType from = customerTypeMap.get(CustomerTypeValue.TESTING.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.INTENTION.getCode());
					String depict = "客户：" + customer.getCompanyName() + "，测试客户" + CustomerChangeConstant.TEST_LOG_INTERVAL + "个月没有联系过，由"
							+ from.getCustomerTypeName() + "降至" + to.getCustomerTypeName();
					try {
						// 通知（不管最近是否已经通知）
						notice(customer, user, depict);
					} catch (Exception e) {
						logger.error("通知异常", e);
					}
					// 客户进行降级处理
					customerService.changeGrade(customer, user, from, to, depict, false);
				}
			}
		} catch (ServiceException e) {
			logger.error("处理测试客户信息异常", e);
		}
	}

	/**
	 * 意向客户
	 * 
	 * @param customers
	 *            客户信息
	 * @param customerTypeMap
	 *            客户类型
	 */
	private void analysisInterestCustomer(List<Customer> customers, Map<Integer, CustomerType> customerTypeMap) {
		// ----降级：15日没有联系日志，自动降级到沉默客户，并通知相关销售和其上级。
		// ----升级：15日有联系日志， 且测试账号流程归档or该客户下产品有账号，系统自动升级到测试客户。
		// ----升级：15日有联系日志， 且测试账号流程归档or该客户下产品有账号，且合同流程归档，系统自动升级到合同客户。（归合同流程管）
		List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		// 查询最近15天的联系日志
		Map<String, Integer> contactLog = supplierContactLogService.queryCustomerContactLog(customerIds, null, CustomerChangeConstant.INTEREST_CHANGE_INTERVAL);
		// 批量查询用户
		Map<String, User> userMap = userService.findUserByIds(customers.stream().map(Customer::getOssuserId).collect(Collectors.toList()));
		// 客户产品是否有账号
		Map<String, Boolean> productAccountInfo = customerProductService.customerProductHasProduct(customerIds);
		// 客户的消耗量（15天内）
		Map<String, Long> customerCostInfo = customerStatisticsService.findCustomerCost(customerIds, null, CustomerChangeConstant.INTEREST_CHANGE_INTERVAL);
		// 客户处于当前状态的时间
		Map<String, Timestamp> customerTimeInfo = customerService.findCustomerInThisStateTime(customers);
		try {
			for (Customer customer : customers) {
				// 对应销售
				User user = userMap.get(customer.getOssuserId());
				// 是否有联系日志
				boolean fifteenDaysHasContact = false;
				if (contactLog != null) {
					// 检查联系日志
					Integer contactLogCount = contactLog.get(customer.getCustomerId());
					fifteenDaysHasContact = (contactLogCount != null && contactLogCount > 0);
				}
				// 是否有消耗量
				boolean hasCost = false;
				if (customerCostInfo != null) {
					Long cost = customerCostInfo.get(customer.getCustomerId());
					hasCost = cost != null && cost > 0;
				}
				// 产品有账号
				boolean productHasAccount = false;
				if (productAccountInfo != null) {
					Boolean hasAccount = productAccountInfo.get(customer.getCustomerId());
					productHasAccount = hasAccount != null && hasAccount;
				}
				// 客户处于这种状态是否已经有15月
				boolean moreThanFifteenDays = false;
				if (customerTimeInfo != null) {
					// 处于这种状态的时间点
					Timestamp stateTime = customerTimeInfo.get(customer.getCustomerId());
					// 时间节点
					long timePoint = DateUtil.getDayBefore(new Date(), CustomerChangeConstant.INTEREST_CHANGE_INTERVAL);
					moreThanFifteenDays = stateTime != null && (stateTime.getTime() - timePoint <= 0);
				}
				if (!fifteenDaysHasContact && !hasCost && moreThanFifteenDays) {
					// 客户处于这种状态超过15天，且15天没有联系日志，也没有量（客户进行降级）
					CustomerType from = customerTypeMap.get(CustomerTypeValue.INTENTION.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.SILENCE.getCode());
					String depict = "客户：" + customer.getCompanyName() + "，意向客户" + CustomerChangeConstant.INTEREST_CHANGE_INTERVAL + "天没有联系过，由"
							+ from.getCustomerTypeName() + "降至" + to.getCustomerTypeName();
					// 通知（不管最近是否已经通知）
					notice(customer, user, depict);
					// 客户进行降级处理
					customerService.changeGrade(customer, user, from, to, depict, false);
				} else if (fifteenDaysHasContact && productHasAccount) {
					// 15天内有联系日志，且产品有账号（进行升级）（主要是针对原来的合同客户变到意向客户的）
					CustomerType from = customerTypeMap.get(CustomerTypeValue.INTENTION.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.TESTING.getCode());
					String depict = "客户：" + customer.getCompanyName() + "，意向客户" + CustomerChangeConstant.INTEREST_CHANGE_INTERVAL + "天内有联系，且产品有账号，由"
							+ from.getCustomerTypeName() + "升至" + to.getCustomerTypeName();
					// 客户进行升级处理
					customerService.changeGrade(customer, user, from, to, depict, false);
				}
			}
		} catch (ServiceException e) {
			logger.error("处理意向客户信息异常", e);
		}
	}

	/**
	 * 沉默客户
	 * 
	 * @param customers
	 *            客户信息
	 * @param customerTypeMap
	 *            客户类型
	 */
	private void analysisSilenceCustomer(List<Customer> customers, Map<Integer, CustomerType> customerTypeMap) {
		// ----降级：3个月没有联系日志，自动降到公共池中，并通知相关销售和其上级。
		// ----升级：3个月有联系日志，且测试账号流程归档or该客户下产品有账号，系统自动升级到测试客户。
		// ----升级：3个月有联系日志，且测试账号流程归档or该客户下产品有账号，且合同流程归档，系统自动升级到合同客户。(归合同流程管)
		// ----升级：3个月有联系日志，且客户基本信息完善率高于一定阈值（手机+人名+职位、电话、QQ、微信、邮件等联系方式录入完整）系统自动升级到意向客户。
		List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		// 查询最近3个月的联系日志
		Map<String, Integer> logs = supplierContactLogService.queryCustomerContactLog(customerIds, CustomerChangeConstant.SILENCE_CHANGE_INTERVAL, null);
		// 批量查询用户
		Map<String, User> users = userService.findUserByIds(customers.stream().map(Customer::getOssuserId).collect(Collectors.toList()));
		// 客户产品是否有账号
		Map<String, Boolean> productAccountInfo = customerProductService.customerProductHasProduct(customerIds);
		// 客户的消耗量（三个月内）
		Map<String, Long> customerCosts = customerStatisticsService.findCustomerCost(customerIds, CustomerChangeConstant.SILENCE_CHANGE_INTERVAL, null);
		// 客户处于当前状态的时间
		Map<String, Timestamp> customerStateTime = customerService.findCustomerInThisStateTime(customers);
		// 客户的联系信息
		Map<String, Boolean> contactInfo = supplierContactsService.customerContactChangeInfo(customerIds, CustomerChangeConstant.SILENCE_CHANGE_INTERVAL, null);
		try {
			for (Customer customer : customers) {
				// 对应销售
				User user = users.get(customer.getOssuserId());
				// 三个月内是否有联系日志
				boolean hasContact = false;
				if (logs != null) {
					Integer contactLogCount = logs.get(customer.getCustomerId());
					hasContact = contactLogCount != null && contactLogCount > 0;
				}
				// 三个月是否有消耗量
				boolean hasCost = false;
				if (customerCosts != null) {
					Long cost = customerCosts.get(customer.getCustomerId());
					hasCost = cost != null && cost > 0;
				}
				// 客户产品产品有账号
				boolean hasProductAccount = false;
				if (productAccountInfo != null) {
					Boolean hasAccount = productAccountInfo.get(customer.getCustomerId());
					hasProductAccount = hasAccount != null && hasAccount;
				}
				// 客户处于这种状态是否已经有3月
				boolean moreThanThreeMonth = false;
				if (customerStateTime != null) {
					// 处于这种状态的时间点
					Timestamp stateTime = customerStateTime.get(customer.getCustomerId());
					long timePoint = DateUtil.getMonthBefore(CustomerChangeConstant.SILENCE_CHANGE_INTERVAL);
					moreThanThreeMonth = stateTime != null && (stateTime.getTime() - timePoint <= 0);
				}
				// 信息完善情况
				boolean infomationComplete = false;
				if (contactInfo != null) {
					Boolean hasChangeContact = contactInfo.get(customer.getCustomerId());
					infomationComplete = hasChangeContact != null && hasChangeContact;
				}
				if (infomationComplete && hasContact) {
					// ----升级：3个月有联系日志，且客户基本信息完善率高于一定阈值（手机+人名+职位、电话、QQ、微信、邮件等联系方式录入完整）系统自动升级到意向客户。
					// 信息完善，且三个月内有日志 由 沉默客户 转成 意向客户
					CustomerType from = customerTypeMap.get(CustomerTypeValue.SILENCE.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.INTENTION.getCode());
					String depict = from.getCustomerTypeName() + "：" + customer.getCompanyName() + "，" + CustomerChangeConstant.SILENCE_CHANGE_INTERVAL
							+ "个月内有联系，且基本信息完善，升为 " + to.getCustomerTypeName();
					// 客户进行升级处理
					customerService.changeGrade(customer, user, from, to, depict, false);
				} else if (moreThanThreeMonth && !hasContact && !hasCost) {
					// ----降级：3个月没有联系日志，自动降到公共池中，并通知相关销售和其上级。
					// 处于这种状态超过三个月，且三个月没有联系日志，也没有量（客户进行降级，放入公共池）
					CustomerType from = customerTypeMap.get(CustomerTypeValue.SILENCE.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.PUBLIC.getCode());
					String depict = from.getCustomerTypeName() + "：" + customer.getCompanyName() + "，" + CustomerChangeConstant.SILENCE_CHANGE_INTERVAL
							+ "个月没有联系过，变更为" + to.getCustomerTypeName();
					// 通知（不管最近是否已经通知）
					notice(customer, user, depict);
					// 客户进行降级处理(放入公共池)
					customerService.changeGrade(customer, user, from, to, depict, true);
				} else if (hasContact && hasProductAccount) {
					// ----升级：3个月有联系日志，且测试账号流程归档（归开户流程）or该客户下产品有账号（这里升级），系统自动升级到测试客户。
					// 由 -沉默客户- 升级到 -测试客户-
					CustomerType from = customerTypeMap.get(CustomerTypeValue.SILENCE.getCode());
					CustomerType to = customerTypeMap.get(CustomerTypeValue.TESTING.getCode());
					String depict = from.getCustomerTypeName() + "：" + customer.getCompanyName() + "，" + CustomerChangeConstant.SILENCE_CHANGE_INTERVAL
							+ "个月内有联系，且产品有账号，升为 " + to.getCustomerTypeName();
					// 客户进行升级处理
					customerService.changeGrade(customer, user, from, to, depict, false);
				}
			}
		} catch (ServiceException e) {
			logger.error("处理沉默客户信息异常", e);
		}
	}

	@Autowired
	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@Autowired
	public void setCustomerTypeService(ICustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	@Autowired
	public void setContractService(IContractService contractService) {
		this.contractService = contractService;
	}

	@Autowired
	public void setSupplierContactLogService(ISupplierContactLogService supplierContactLogService) {
		this.supplierContactLogService = supplierContactLogService;
	}

	@Autowired
	public void setMsgCenterService(IMsgCenterService msgCenterService) {
		this.msgCenterService = msgCenterService;
	}

	@Autowired
	public void setMsgDetailService(IMsgDetailService msgDetailService) {
		this.msgDetailService = msgDetailService;
	}

	@Autowired
	public void setDepartmentService(IDepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setCustomerStatisticsService(ICustomerStatisticsService customerStatisticsService) {
		this.customerStatisticsService = customerStatisticsService;
	}

	@Autowired
	public void setCustomerProductService(ICustomerProductService customerProductService) {
		this.customerProductService = customerProductService;
	}

	@Autowired
	public void setEv(Environment ev) {
		this.ev = ev;
	}

	@Autowired
	public void setSupplierContactsService(ISupplierContactsService supplierContactsService) {
		this.supplierContactsService = supplierContactsService;
	}

	@Autowired
	public void setParameterService(IParameterService parameterService) {
		this.parameterService = parameterService;
	}
}
