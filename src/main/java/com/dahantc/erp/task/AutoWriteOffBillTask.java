package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.PlatformType;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.enums.NeedAuto;
import com.dahantc.erp.enums.RelateStatus;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.task.base.BaseTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Component("autoWriteOffBillTask")
public class AutoWriteOffBillTask extends BaseTask implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger logger = LogManager.getLogger(AutoWriteOffBillTask.class);

	private static final long SLEEP_TIME = 10 * 60 * 1000;
	// companyName/depict --> customerId
	private Map<String, String> cacheInvoiceMap;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private RefundTask refundTask;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IDepartmentService departmentService;

	private FlowNode flowNode;

	private String flowId;

	@Override
	public void run() {
		while (isRun()) {
			extracteTask();
		}
	}

	private void extracteTask() {
		try {
			// 先执行抵消任务
			refundTask.propertyTask();

			logger.info("自动匹配销账任务开始...");
			long startTime = System.currentTimeMillis();
			getBillWriteOffFlowId();
			// 存放 摘要 -> customerId
			cacheInvoiceMap = new HashMap<>();
			// 查剩余可关联金额>0的到款
			Map<String, List<InComeInfo>> fsExpenseIncomes = queryAllNeedWriteOffData();
			if (fsExpenseIncomes != null && fsExpenseIncomes.size() > 0 && flowNode != null) {
				// {customerId -> [IncomeInfo]}
				for (Map.Entry<String, List<InComeInfo>> entry : fsExpenseIncomes.entrySet()) {
					// 客户的所有到款，自动销账
					autoWriteOff(entry.getKey(), entry.getValue());
				}
				cacheInvoiceMap.clear();
				logger.info("自动匹配销账耗时：[" + (System.currentTimeMillis() - startTime) + "]毫秒");
			}
			try {
				logger.info("关联账单线程暂未查询到要关联的数据或者无销账流程，休眠：" + SLEEP_TIME + "毫秒");
				TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {

			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 获取销账流程
	 */
	private void getBillWriteOffFlowId() {
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowClass", Constants.ROP_EQ, Constants.BILL_WRITE_OFF_FLOW_CLASS));
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			List<ErpFlow> list = erpFlowService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(list)) {
				this.flowId = list.get(0).getFlowId();
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
				List<FlowNode> nodes = flowNodeService.queryAllBySearchFilter(filter);
				if (nodes != null && !nodes.isEmpty()) {
					this.flowNode = nodes.get(0);
				} else {
					logger.info("'" + Constants.BILL_WRITE_OFF_FLOW_CLASS + "'无节点，请重新设计");
				}
			} else {
				logger.info("系统无'" + Constants.BILL_WRITE_OFF_FLOW_CLASS + "'，请尽快创建该流程");
			}
		} catch (ServiceException e) {
			logger.error("获取" + Constants.BILL_WRITE_OFF_FLOW_CLASS + "异常", e);
		}
	}

	/**
	 * 查询剩余可关联金额>0的到款
	 *
	 * @return {customerId -> [IncomeInfo]}
	 */
	private Map<String, List<InComeInfo>> queryAllNeedWriteOffData() {
		try {
			// 查询剩余可关联金额 >0 的到款
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("isIncome", Constants.ROP_EQ, 0));
			filter.getRules().add(new SearchRule("remainRelatedCost", Constants.ROP_GT, BigDecimal.ZERO));
			List<FsExpenseIncome> list = fsExpenseIncomeService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(list)) {
				List<InComeInfo> result = new ArrayList<>();
				for (FsExpenseIncome income : list) {
					InComeInfo info = null;
					if (income.getRelateStatus() == RelateStatus.RELATED.ordinal() && StringUtils.isNotBlank(income.getCustomerId())) { // 已经匹配过的
						info = new InComeInfo();
						info.setCustomerId(income.getCustomerId());
						info.setFsExpenseIncome(income);
					} else {
						// 到款绑定到客户，设置客户id
						info = buildIncomeInfo(income);
					}
					if (info != null) {
						result.add(info);
					}
				}
				if (!result.isEmpty()) {
					Map<String, List<String>> msgMap = new HashMap<>();
					// 本次新绑定到客户的到款
					List<FsExpenseIncome> relatedData = new ArrayList<>();
					// 客户所属销售的部门名 deptId -> deptName
					Map<String, String> deptNameMap = new HashMap<>();
					// 按客户id分组
					Map<String, List<InComeInfo>> map = result.stream().peek(info -> {
						// 上一步只是InComeInfo设置了customerId，这里给FsExpenseIncome设置customerId
						if (info.getFsExpenseIncome().getRelateStatus() == RelateStatus.UNRELATE.ordinal()
								|| StringUtils.isBlank(info.getFsExpenseIncome().getCustomerId())) { // 未匹配或者关联customerId为空的需要更新
							info.getFsExpenseIncome().setRelateStatus(RelateStatus.RELATED.ordinal());
							info.getFsExpenseIncome().setCustomerId(info.getCustomerId());
							String deptId = info.getCustomer().getDeptId();
							info.getFsExpenseIncome().setDeptId(deptId);
							if (deptNameMap.containsKey(deptId)) {
								info.getFsExpenseIncome().setDeptName(deptNameMap.get(deptId));
							} else {
								String deptName = null;
								try {
									Department dept = departmentService.read(deptId);
									if (null != dept) {
										deptName = dept.getDeptname();
									}
								} catch (ServiceException e) {
									logger.error("", e);
								}
								info.getFsExpenseIncome().setDeptName(deptName);
								deptNameMap.put(deptId, deptName);
							}
							FsExpenseIncome fsExpenseIncome = info.getFsExpenseIncome();
							Customer customer = info.getCustomer();

							if (msgMap.get(customer.getOssuserId()) == null) {
								msgMap.put(customer.getOssuserId(), new ArrayList<>());
							}
							msgMap.get(customer.getOssuserId())
									.add("您有一笔到款：到款信息【" + fsExpenseIncome.getDepict() + "】，到款时间【"
											+ DateUtil.convert(fsExpenseIncome.getOperateTime(), DateUtil.format1) + "】，到款金额【" + fsExpenseIncome.getCost()
											+ "】，关联客户【" + customer.getCompanyName() + "】成功，请确认并尽快销账！");

							relatedData.add(info.getFsExpenseIncome());
						}
					}).collect(Collectors.groupingBy(InComeInfo::getCustomerId));
					// 更新匹配上客户的到款记录
					baseDao.updateByBatch(relatedData);

					// 发送消息
					if (!CollectionUtils.isEmpty(msgMap)) {
						msgMap.forEach((ossUserId, msgList) -> {
							if (!CollectionUtils.isEmpty(msgList)) {
								try {
									User user = userService.read(ossUserId);
									if (user != null) {
										msgList.forEach(msg -> {
											WeixinMessage.sendMessageByMobile(user.getContactMobile(), msg);
										});
									}
								} catch (Exception e) {
									logger.error("", e);
								}
							}
						});
					}

					return map;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 到款绑定到客户 优先匹配开票信息，无开票信息匹配公司名称
	 * 
	 * @return
	 */
	private InComeInfo buildIncomeInfo(FsExpenseIncome income) {
		try {
			String customerId = null;
			// 摘要 对应的 客户id
			if (!cacheInvoiceMap.containsKey(income.getDepict())) {
				// 查与 到款的摘要 一致的 客户开票信息
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("companyName", Constants.ROP_EQ, income.getDepict()));
				filter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, InvoiceType.OtherInvoice.ordinal()));
				List<InvoiceInformation> list = invoiceInformationService.queryAllBySearchFilter(filter);
				// 查询开票信息结果不为空时，保存 摘要 -> 客户id
				if (!CollectionUtils.isEmpty(list)) {
					Set<String> matchCustomerIdSet = new HashSet<>();
					for (InvoiceInformation invoiceInformation : list) {
						if (customerService.read(invoiceInformation.getBasicsId()) != null) { // 是客户，排除供应商
							matchCustomerIdSet.add(invoiceInformation.getBasicsId());
						}
					}
					// 开票信息对应的客户只有1个时，就是它
					if (matchCustomerIdSet.size() == 1) {
						customerId = matchCustomerIdSet.iterator().next();
					} else if (matchCustomerIdSet.size() > 1) {
						// 查出有相同开票信息的客户
						filter.getRules().clear();
						filter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, new ArrayList<>(matchCustomerIdSet)));
						List<Customer> custList = customerService.queryAllBySearchFilter(filter);
						if (!CollectionUtils.isEmpty(custList)) {
							// 每个客户绑定的我司银行信息 我司银行信息id -> 客户
							Map<String, List<Customer>> bankAccountCustMap = custList.stream().filter(cust -> StringUtils.isNoneBlank(cust.getBankAccountId())).collect(Collectors.groupingBy(Customer::getBankAccountId));
							if (bankAccountCustMap.size() > 0) {
								filter.getRules().clear();
								filter.getRules().add(new SearchRule("bankAccountId", Constants.ROP_IN, new ArrayList<>(bankAccountCustMap.keySet())));
								List<BankAccount> bankAccountList = bankAccountService.queryAllBySearchFilter(filter);
								if (!CollectionUtils.isEmpty(bankAccountList)) {
									// 排除 客户绑定的我司银行 与 到款记录的收款银行 不一致的客户
									bankAccountList.forEach(bankAccount -> {
										if (!StringUtils.equals(bankAccount.getAccountBank(), income.getBankName())) {
											List<Customer> nonBankCustList = bankAccountCustMap.get(bankAccount.getBankAccountId());
											matchCustomerIdSet.removeAll(nonBankCustList.stream().map(Customer::getCustomerId).collect(Collectors.toList()));
										}
									});
									if (matchCustomerIdSet.size() == 1) {
										customerId = matchCustomerIdSet.iterator().next();
									} else {
										customerId = null;
										logger.info("到款信息无法匹配，id：" + income.getId() + "！");
									}
								}
							}
						}
					}
				} else {
					// 查询开票信息的结果为空时，查与 摘要 一致的客户公司名称
					filter.getRules().clear();
					filter.getRules().add(new SearchRule("companyName", Constants.ROP_EQ, income.getDepict()));
					filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
					List<Customer> customerList = customerService.queryAllBySearchFilter(filter);
					// 保存 摘要 -> 客户id
					if (!CollectionUtils.isEmpty(customerList)) {
						customerId = customerList.get(0).getCustomerId();
						cacheInvoiceMap.put(income.getDepict(), customerList.get(0).getCustomerId());
					} else {
						cacheInvoiceMap.put(income.getDepict(), null);
					}
				}
			} else {
				customerId = cacheInvoiceMap.get(income.getDepict());
			}
			if (StringUtils.isNotBlank(customerId)) {
				InComeInfo inComeInfo = new InComeInfo();
				inComeInfo.setFsExpenseIncome(income);
				inComeInfo.setCustomerId(customerId);
				Customer cust = customerService.read(customerId);
				if (cust != null) {
					inComeInfo.setCustomer(cust);
				}
				return inComeInfo;
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 自动关联账单: 查询
	 * 
	 * @param customerId
	 *            客户id
	 * @param infos
	 *            关联上该客户的到款信息
	 * @return
	 */
	private int autoWriteOff(String customerId, List<InComeInfo> infos) {
		try {
			// 查询此客户的账单
			List<ProductBills> allBills = getAllBills(customerId);
			if (!CollectionUtils.isEmpty(allBills) && !CollectionUtils.isEmpty(infos)) {
				build(customerId, infos, allBills);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return -1;
	}

	/**
	 * 创建销账流程(优先按照金额匹配)
	 * 
	 * @param customerId
	 *            客户id
	 * @param infos
	 *            该客户的所有到款
	 * @param allBills
	 *            该客户的所有账单
	 */
	private void build(String customerId, List<InComeInfo> infos, List<ProductBills> allBills) {
		try {
			Customer customer = customerService.read(customerId);
			Map<String, MatchWriteOffInfo> matchData = matchByAmount(infos, allBills, customer);
			if (matchData.size() > 0) {
				buildFlowEnt(customer, matchData);
			} else {
				logger.info("按照金额没有匹配可以销账的账单！");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}

	/**
	 * @param customer
	 * @param matchData
	 */
	private void buildFlowEnt(Customer customer, Map<String, MatchWriteOffInfo> matchData) {
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, this.flowId));
			filter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
			List<FlowLabel> labelList = flowLabelService.queryAllBySearchFilter(filter);
			for (Map.Entry<String, MatchWriteOffInfo> entry : matchData.entrySet()) {
				MatchWriteOffInfo data = entry.getValue();
				List<ProductBills> productBills = data.getBillList();
				List<FsExpenseIncome> incomeInfoList = data.getIncomeList();
				JSONArray billsInfoArray = data.getBillInfoArr();
				JSONArray incomeInfoArray = data.getIncomeInfoArr();
				ApplyProcessReqDto reqDto = new ApplyProcessReqDto();
				reqDto.setFlowId(this.flowId);
				reqDto.setProductId(entry.getKey());
				reqDto.setSupplierId(customer.getCustomerId());
				reqDto.setPlatform(PlatformType.PC.ordinal());
				JSONObject flowMsg = new JSONObject();
				flowMsg.put(labelList.get(0).getName(), "1");
				flowMsg.put(labelList.get(1).getName(), billsInfoArray);
				flowMsg.put(labelList.get(2).getName(), incomeInfoArray);
				flowMsg.put("备注", "自动销账流程");
				reqDto.setFlowMsg(flowMsg.toString());
				fsExpenseIncomeService.saveWriteOffInfo(productBills, incomeInfoList, reqDto, customer.getOssuserId());
				logger.info("客户生成自动销账流程：" + JSON.toJSONString(reqDto));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private JSONObject getBillsJson(ProductBills bills, Customer customer, CustomerProduct product, BigDecimal billsUnreceivables) {
		JSONObject billsJson = new JSONObject();
		billsJson.put("productbillsid", bills.getId());
		billsJson.put("receivables", bills.getReceivables());
		billsJson.put("title", "账单-" + DateUtil.convert(bills.getWtime(), DateUtil.format4) + "-" + customer.getCompanyName() + "-" + product.getProductName());
		billsJson.put("thiscost", billsUnreceivables);
		return billsJson;
	}

	private JSONObject getIncomeJson(FsExpenseIncome income, BigDecimal thiscost) {
		JSONObject incomeInfoJson = new JSONObject();
		incomeInfoJson.put("fsexpenseincomeid", income.getId());
		incomeInfoJson.put("banckcustomername", income.getDepict());
		incomeInfoJson.put("cost", income.getCost());
		incomeInfoJson.put("operatetime", DateUtil.convert(income.getOperateTime(), DateUtil.format1));
		incomeInfoJson.put("thiscost", thiscost);
		return incomeInfoJson;
	}

	private JSONObject getRelateJson(FsExpenseIncome income, BigDecimal remain, BigDecimal thiscost) {
		JSONObject relateJson = new JSONObject();
		relateJson.put("fsExpenseIncomeId", income.getId());
		relateJson.put("remain", remain);
		relateJson.put("thisCost", thiscost);
		relateJson.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
		return relateJson;
	}

	/**
	 * 检查客户是否满足自动销账条件，所有产品必须只有一种付费类型，不能既有预付费又有后付费
	 * 
	 * @param customerId
	 *            客户id
	 * @param cacheCustomerProduct
	 *            该客户的产品 {产品id -> 产品}
	 * @return 是否满足自动销账条件
	 */
	private boolean verifyMatch(String customerId, Map<String, CustomerProduct> cacheCustomerProduct) {
		try {
			// 查客户的所有产品
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
			List<CustomerProduct> products = customerProductService.queryAllByFilter(filter);
			if (products != null && !products.isEmpty()) {
				int settleType = -1;
				// 判断该客户所有产品的产品类型是否一致
				for (CustomerProduct customerProduct : products) {
					cacheCustomerProduct.put(customerProduct.getProductId(), customerProduct);
					if (settleType == -1) {
						settleType = customerProduct.getSettleType();
					} else if (settleType != customerProduct.getSettleType()) {
						logger.info("customerId：" + customerId + "既包含预付费产品又包含未付费产品不匹配账单！");
						return false;
					}
				}
				return true;
			} else {
				logger.info("customerId：" + customerId + "未查询到需要销账的产品！");
			}
		} catch (ServiceException e) {
			logger.error("检查客户是否满足自动销账条件异常", e);
		}
		return false;
	}

	/**
	 * 优先匹配老账单
	 * 
	 * @param infos
	 *            该客户的所有到款（关联上该客户，且剩余可关联金额>0）
	 * @param allBills
	 *            该客户的所有账单（已对账账单）
	 * @param customer
	 *            客户
	 * @return 账单集合 收款集合 流程信息(账单信息：id + 本次销账金额；收款信息：id + 本次销账金额)
	 */
	private Map<String, MatchWriteOffInfo> matchByAmount(List<InComeInfo> infos, List<ProductBills> allBills, Customer customer) {
		Map<String, MatchWriteOffInfo> matchData = new HashMap<>();
		Map<String, CustomerProduct> cacheCustomerProduct = new HashMap<>();
		try {
			// 检查客户是否满足自动销账条件，所有产品必须只有一种付费类型，不能既有预付费又有后付费
			if (!verifyMatch(customer.getCustomerId(), cacheCustomerProduct)) {
				return matchData;
			}
			// 到款按 到款时间、记录创建时间 升序排序
			infos.sort((o1, o2) -> {
				if (o1.getFsExpenseIncome().getOperateTime().getTime() - o2.getFsExpenseIncome().getOperateTime().getTime() == 0) {
					return (o1.getFsExpenseIncome().getWtime().getTime() - o2.getFsExpenseIncome().getWtime().getTime()) > 0 ? 1 : -1;
				} else {
					return (o1.getFsExpenseIncome().getOperateTime().getTime() - o2.getFsExpenseIncome().getOperateTime().getTime()) > 0 ? 1 : -1;
				}
			});
			// 账单按 账单月份 升序排序
			allBills.sort((o1, o2) -> (o1.getWtime().getTime() - o2.getWtime().getTime()) > 0 ? 1 : -1);
			int settleType = cacheCustomerProduct.get(allBills.get(0).getProductId()).getSettleType();
			if (settleType == SettleType.Prepurchase.ordinal() || settleType == SettleType.Advance.ordinal()) {
				Date wtime = allBills.get(0).getWtime();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(wtime);
				calendar.add(Calendar.MONTH, 1);
				wtime = calendar.getTime();
				Iterator<InComeInfo> infoIterator = infos.iterator();
				while (infoIterator.hasNext()) {
					InComeInfo info = infoIterator.next();
					Date operateTime = info.getFsExpenseIncome().getOperateTime();
					if (operateTime.getTime() > wtime.getTime()) {
						logger.info("客户【" + customer.getCompanyName() + "】为预付费客户，最早账单时间为【" + DateUtil.convert(allBills.get(0).getWtime(), DateUtil.format1)
								+ "】，最迟到款时间为【" + DateUtil.convert(operateTime, DateUtil.format1) + "】不符合自动销账规则，ID【" + info.getFsExpenseIncome().getId()
								+ "】到款不参与销账！");
						infoIterator.remove();
					}
				}
			}
			if (settleType == SettleType.Prepurchase.ordinal() || settleType == SettleType.Advance.ordinal()) { // 预付费按照原来的逻辑
				advanceMatch(infos, allBills, customer, matchData, cacheCustomerProduct);
			} else { // 后付的匹配账单
				afterMatch(infos, allBills, customer, matchData, cacheCustomerProduct);
			}
		} catch (Exception e) {
			logger.error("", e);
			matchData.clear();
		}
		return matchData;
	}

	/**
	 *
	 * @param infos					客户所有到款（后付）
	 * @param allBills				客户所有产品的未销账账单
	 * @param customer				客户
	 * @param matchData				匹配信息	{}
	 * @param cacheCustomerProduct	客户产品
	 * @throws ServiceException
	 */
	private void afterMatch(List<InComeInfo> infos, List<ProductBills> allBills, Customer customer, Map<String, MatchWriteOffInfo> matchData,
			Map<String, CustomerProduct> cacheCustomerProduct) throws ServiceException {

		if (CollectionUtils.isEmpty(allBills) || CollectionUtils.isEmpty(infos)) {
			logger.info("账单或者到款为空，客户【" + customer.getCompanyName() + "】自动销账结束");
			return;
		}

		// 匹配到某一个月份的所有产品的账单
		// 所有产品的账单按月份分组，计算每月总的未收金额
		Map<String, MonthBillSumInfo> monthBillSumInfoMap = new HashMap<>();
		for (ProductBills productBills : allBills) {
			MonthBillSumInfo sumInfo = null;
			if (!monthBillSumInfoMap.containsKey(DateUtil.convert(productBills.getWtime(), DateUtil.format4))) {
				sumInfo = new MonthBillSumInfo(DateUtil.convert(productBills.getWtime(), DateUtil.format4));
				monthBillSumInfoMap.put(DateUtil.convert(productBills.getWtime(), DateUtil.format4), sumInfo);
			} else {
				sumInfo = monthBillSumInfoMap.get(DateUtil.convert(productBills.getWtime(), DateUtil.format4));
			}
			sumInfo.addBillSum(productBills.getReceivables().subtract(productBills.getActualReceivables()));
			sumInfo.getBillList().add(productBills);
		}
		// 月账单信息 按月份升序
		List<MonthBillSumInfo> monthBillSumInfoList = new ArrayList<>(monthBillSumInfoMap.values());
		monthBillSumInfoList.sort((info1, info2) -> {
			return info1.getMonth().compareTo(info2.getMonth());
		});

		// 匹配某个客户某个月的所有账单
		if (monthBillSumInfoList.size() >= 1) {
			combineBillsMatchIncome(infos, allBills, customer, matchData, cacheCustomerProduct, monthBillSumInfoList, 1);
		}

		// 匹配某两个月的所有账单（相邻的、不一定是8.9月，9月被销的情况有可能是8.10月）
		if (monthBillSumInfoList.size() >= 2) {
			combineBillsMatchIncome(infos, allBills, customer, matchData, cacheCustomerProduct, monthBillSumInfoList, 2);
		}

		// 匹配某三个月的所有账单（同上）
		if (monthBillSumInfoList.size() >= 3) {
			combineBillsMatchIncome(infos, allBills, customer, matchData, cacheCustomerProduct, monthBillSumInfoList, 3);
		}
	}

	/**
	 * 某笔到款匹配某N个月的所有账单
	 *
	 * @param infos					客户所有到款（后付）
	 * @param allBills				客户产品所有未销账账单
	 * @param customer				客户
	 * @param matchData				匹配信息
	 * @param cacheCustomerProduct	客户所有产品
	 * @param monthBillSumInfoList	客户月账单信息列表
	 * @param combineCount			合并月个数
	 */
	private void combineBillsMatchIncome(List<InComeInfo> infos, List<ProductBills> allBills, Customer customer, Map<String, MatchWriteOffInfo> matchData,
			Map<String, CustomerProduct> cacheCustomerProduct, List<MonthBillSumInfo> monthBillSumInfoList, int combineCount) {
		// 遍历每笔到款
		Iterator<InComeInfo> incomeInfoIterator = infos.iterator();
		while (incomeInfoIterator.hasNext()) {

			InComeInfo incomeInfo = incomeInfoIterator.next();

			int size = monthBillSumInfoList.size();

			// 外循环，遍历客户月账单信息列表，因为有个合并月个数，所以不能直接从0遍历到size-1
			for (int i = 0; i + combineCount - 1 < size; i++) {

				// 待匹配的最迟账单月份 2020-03 2020-04 2020-05 -> 2020-05
				Date wtime = monthBillSumInfoList.get(i + combineCount - 1).getBillList().get(0).getWtime();

				BigDecimal totalBillReceive = BigDecimal.ZERO;

				// 根据合并月个数，合并几个月的总未收金额
				for (int j = 0; j < combineCount; j++) {
					totalBillReceive = totalBillReceive.add(monthBillSumInfoList.get(i + j).getBillSum());
				}

				// 到款没被销过，且恰好等于几个月合并的金额，到款时间必须在账单时间之后
				if (incomeInfo.getFsExpenseIncome().getCost().subtract(incomeInfo.getFsExpenseIncome().getRemainRelatedCost()).signum() == 0
						&& totalBillReceive.subtract(incomeInfo.getFsExpenseIncome().getRemainRelatedCost()).signum() == 0
						&& incomeInfo.getFsExpenseIncome().getOperateTime().getTime() > DateUtil.getThisMonthFinalTime(wtime).getTime()) {

					MatchWriteOffInfo matchWriteOffInfo = null;

					BigDecimal remainRelatedCost = incomeInfo.getFsExpenseIncome().getRemainRelatedCost();

					// 寻找到没有匹配到账单和到款的产品作为流程发起点
					for (ProductBills productBills : monthBillSumInfoList.get(i).getBillList()) {
						if (!matchData.containsKey(productBills.getProductId())) {
							matchWriteOffInfo = new MatchWriteOffInfo();
							matchData.put(productBills.getProductId(), matchWriteOffInfo);
							break;
						}
					}
					// 取第一个产品
					if (matchWriteOffInfo == null) {
						for (ProductBills productBills : monthBillSumInfoList.get(i).getBillList()) {
							if (matchData.containsKey(productBills.getProductId())) {
								matchWriteOffInfo = matchData.get(productBills.getProductId());
								break;
							}
						}
					}

					for (int j = 0; j < combineCount; j++) {
						// 合并的起始月份，每次remove，下次循环就是取的下个月了
						MonthBillSumInfo billInfo = monthBillSumInfoList.remove(i);
						size--;

						// 修改该月所有账单的实收，修改当前到款的余额
						for (ProductBills productBills : billInfo.getBillList()) {

							String relatedInfo = productBills.getRelatedInfo();
							JSONArray relateInfoArr = null;
							if (StringUtils.isBlank(relatedInfo)) {
								relateInfoArr = new JSONArray();
							} else {
								relateInfoArr = JSON.parseArray(relatedInfo);
							}

							relateInfoArr.add(getRelateJson(incomeInfo.getFsExpenseIncome(), incomeInfo.getFsExpenseIncome().getRemainRelatedCost(),
									productBills.getReceivables().subtract(productBills.getActualReceivables())));
							// 流程的销账账单信息
							matchWriteOffInfo.getBillInfoArr().add(getBillsJson(productBills, customer, cacheCustomerProduct.get(productBills.getProductId()),
									productBills.getReceivables().subtract(productBills.getActualReceivables())));

							incomeInfo.getFsExpenseIncome().setRemainRelatedCost(incomeInfo.getFsExpenseIncome().getRemainRelatedCost()
									.subtract(productBills.getReceivables().subtract(productBills.getActualReceivables())));

							productBills.setActualReceivables(productBills.getReceivables());
							productBills.setBillStatus(BillStatus.WRITING_OFF.ordinal());
							productBills.setRelatedInfo(relateInfoArr.toString());
							matchWriteOffInfo.getBillList().add(productBills);

							allBills.remove(productBills);
						}

					}
					// 流程的到款信息
					matchWriteOffInfo.getIncomeInfoArr().add(getIncomeJson(incomeInfo.getFsExpenseIncome(), remainRelatedCost));

					FsExpenseIncome fsExpenseIncome = incomeInfo.getFsExpenseIncome();
					fsExpenseIncome.setRemainRelatedCost(BigDecimal.ZERO);
					matchWriteOffInfo.getIncomeList().add(fsExpenseIncome);
					incomeInfoIterator.remove();

					i--;

				}

			}

		}

	}

	// 预付费销账
	private void advanceMatch(List<InComeInfo> infos, List<ProductBills> allBills, Customer customer, Map<String, MatchWriteOffInfo> matchData,
			Map<String, CustomerProduct> cacheCustomerProduct) throws ServiceException {
		// 预付费到款总余额
		BigDecimal sumIncome = new BigDecimal(0);
		for (InComeInfo info : infos) {
			sumIncome = sumIncome.add(info.getFsExpenseIncome().getRemainRelatedCost());
		}
		for (ProductBills bills : allBills) {
			// 账单未收金额 > 0 ， 总余额 - 未收 > 0 ，余额可以销这个账单
			if (bills.getReceivables().subtract(bills.getActualReceivables()).signum() > 0
					&& sumIncome.subtract(bills.getReceivables()).add(bills.getActualReceivables()).signum() >= 0) {
				CustomerProduct product = null;
				if (cacheCustomerProduct.get(bills.getProductId()) == null) {
					cacheCustomerProduct.put(bills.getProductId(), customerProductService.read(bills.getProductId()));
				}
				product = cacheCustomerProduct.get(bills.getProductId());
				if (product == null) {
					logger.info("自动销账未获取产品，id：" + bills.getProductId());
					continue;
				}
				JSONArray billInfoArr = new JSONArray();
				JSONArray incomeInfoArr = new JSONArray();
				JSONArray relateInfoArr = new JSONArray();
				// 本次循环匹配到此账单的到款列表
				List<FsExpenseIncome> matchIncomeInfos = new ArrayList<>();
				// 总余额 - 未收
				sumIncome = sumIncome.subtract(bills.getReceivables()).add(bills.getActualReceivables());
				BigDecimal unpaid = bills.getReceivables().subtract(bills.getActualReceivables());
				Iterator<InComeInfo> infoIterator = infos.iterator();
				BigDecimal billsUnreceivables = bills.getReceivables().subtract(bills.getActualReceivables());
				// 每笔到款余额用于销这个账单
				while (infoIterator.hasNext()) {
					InComeInfo info = infoIterator.next();
					unpaid = unpaid.subtract(info.getFsExpenseIncome().getRemainRelatedCost());
					// 账单未收 > 这笔到款的余额
					if (unpaid.signum() > 0) {
						// 这笔到款的余额 全部用于销这个账单
						incomeInfoArr.add(getIncomeJson(info.getFsExpenseIncome(), info.getFsExpenseIncome().getRemainRelatedCost()));
						relateInfoArr.add(getRelateJson(info.getFsExpenseIncome(), info.getFsExpenseIncome().getRemainRelatedCost(),
								info.getFsExpenseIncome().getRemainRelatedCost()));
						bills.setActualReceivables(bills.getActualReceivables().add(info.getFsExpenseIncome().getRemainRelatedCost()));
						info.getFsExpenseIncome().setRemainRelatedCost(BigDecimal.ZERO);
						// 参与销账的到款
						matchIncomeInfos.add(info.getFsExpenseIncome());
						infoIterator.remove();
					} else {
						// 这笔到款的余额够销，余额减去剩余的要销金额
						incomeInfoArr.add(getIncomeJson(info.getFsExpenseIncome(), info.getFsExpenseIncome().getRemainRelatedCost().add(unpaid)));
						relateInfoArr.add(getRelateJson(info.getFsExpenseIncome(), info.getFsExpenseIncome().getRemainRelatedCost(),
								bills.getReceivables().subtract(bills.getActualReceivables())));
						// 销账账单信息中加上此账单
						billInfoArr.add(getBillsJson(bills, customer, product, billsUnreceivables));
						bills.setActualReceivables(bills.getReceivables());
						bills.setBillStatus(BillStatus.WRITING_OFF.ordinal());
						info.getFsExpenseIncome().setRemainRelatedCost(unpaid.abs());
						// 参与销账的到款
						matchIncomeInfos.add(info.getFsExpenseIncome());
						// 到款余额刚好用完
						if (unpaid.signum() == 0) {
							infoIterator.remove();
						}
						break;
					}
				}
				if (StringUtils.isBlank(bills.getRelatedInfo())) {
					bills.setRelatedInfo(relateInfoArr.toString());
				} else {
					JSONArray jsonArray = JSON.parseArray(bills.getRelatedInfo());
					jsonArray.addAll(relateInfoArr);
					bills.setRelatedInfo(jsonArray.toString());
				}
				if (matchData.get(bills.getProductId()) != null) {
					// 此产品在之前的循环匹配好的账单和到款
					MatchWriteOffInfo matchWriteOffInfo = matchData.get(bills.getProductId());
					matchWriteOffInfo.getBillList().add(bills);
					// 遍历之前匹配的到款，如果包含本次循环中匹配的到款，用本次的到款代替
					for (FsExpenseIncome fsExpenseIncome : matchWriteOffInfo.getIncomeList()) {
						Iterator<FsExpenseIncome> iterator = matchIncomeInfos.iterator();
						while (iterator.hasNext()) {
							FsExpenseIncome income = iterator.next();
							if (StringUtils.equals(income.getId(), fsExpenseIncome.getId())) {
								fsExpenseIncome = income;
								iterator.remove();
								break;
							}
						}
					}
					if (!matchIncomeInfos.isEmpty()) {
						matchWriteOffInfo.getIncomeList().addAll(matchIncomeInfos);
					}
					// 流程销账账单信息
					matchWriteOffInfo.getBillInfoArr().addAll(billInfoArr);
					// 流程到款信息
					for (Object obj : matchWriteOffInfo.getIncomeInfoArr()) {
						JSONObject json = (JSONObject) obj;
						// 本次循环生成的 到款信息 与 之前生成的 到款信息 是同一条到款时，把金额累计
						Iterator<Object> iterator = incomeInfoArr.iterator();
						while (iterator.hasNext()) {
							JSONObject incomeJson = (JSONObject) iterator.next();
							if (StringUtils.equals(json.getString("fsexpenseincomeid"), incomeJson.getString("fsexpenseincomeid"))) {
								json.put("thiscost", new BigDecimal(json.getString("thiscost")).add(new BigDecimal(incomeJson.getString("thiscost"))));
								iterator.remove();
							}
						}
					}
					// 本次循环生成的 到款信息 与之前的到款不重合的部分，全部加入
					if (!incomeInfoArr.isEmpty()) {
						matchWriteOffInfo.getIncomeInfoArr().addAll(incomeInfoArr);
					}
				} else {
					MatchWriteOffInfo billsWriteOffInfo = new MatchWriteOffInfo();
					List<ProductBills> matchBillsInfos = new ArrayList<>();
					matchBillsInfos.add(bills);
					billsWriteOffInfo.setBillList(matchBillsInfos);
					billsWriteOffInfo.setIncomeList(matchIncomeInfos);
					billsWriteOffInfo.setBillInfoArr(billInfoArr);
					billsWriteOffInfo.setIncomeInfoArr(incomeInfoArr);
					matchData.put(bills.getProductId(), billsWriteOffInfo);
				}
			} else {
				break;
			}
		}
	}

	/**
	 * 查询已对账账单
	 * 
	 * @param: customerId
	 * 
	 * @return: List
	 */
	private List<ProductBills> getAllBills(String customerId) {
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("billStatus", Constants.ROP_EQ, BillStatus.RECONILED.ordinal()));
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			filter.getRules().add(new SearchRule("entityId", Constants.ROP_EQ, customerId));
			filter.getRules().add(new SearchRule("needAuto", Constants.ROP_EQ, NeedAuto.TRUE.ordinal()));
			return productBillsService.queryAllBySearchFilter(filter);
		} catch (ServiceException e) {
			logger.error("查询已对账账单异常，customerId：" + customerId, e);
		}
		return null;
	}

	// 某个月的账单合计
	class MonthBillSumInfo {

		private String month;

		private BigDecimal billSum;

		private List<ProductBills> billList;

		public MonthBillSumInfo(String month) {
			this.month = month;
			this.billSum = BigDecimal.ZERO;
			this.billList = new ArrayList<>();
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public BigDecimal getBillSum() {
			return billSum;
		}

		public void addBillSum(BigDecimal receive) {
			this.billSum = this.billSum.add(receive);
		}

		public void setBillSum(BigDecimal billSum) {
			this.billSum = billSum;
		}

		public List<ProductBills> getBillList() {
			return billList;
		}

		public void setBillList(List<ProductBills> billList) {
			this.billList = billList;
		}

	}

	class MatchWriteOffInfo {

		private List<ProductBills> billList; // 待销账的账单

		private List<FsExpenseIncome> incomeList; // 待销账的到款

		private JSONArray billInfoArr; // 销账流程账单信息

		private JSONArray incomeInfoArr; // 销账流程到款信息

		public MatchWriteOffInfo() {
			this.billList = new ArrayList<>();
			this.incomeList = new ArrayList<>();
			this.billInfoArr = new JSONArray();
			this.incomeInfoArr = new JSONArray();
		}

		public List<ProductBills> getBillList() {
			return billList;
		}

		public void setBillList(List<ProductBills> billList) {
			this.billList = billList;
		}

		public List<FsExpenseIncome> getIncomeList() {
			return incomeList;
		}

		public void setIncomeList(List<FsExpenseIncome> incomeList) {
			this.incomeList = incomeList;
		}

		public JSONArray getBillInfoArr() {
			return billInfoArr;
		}

		public void setBillInfoArr(JSONArray billInfoArr) {
			this.billInfoArr = billInfoArr;
		}

		public JSONArray getIncomeInfoArr() {
			return incomeInfoArr;
		}

		public void setIncomeInfoArr(JSONArray incomeInfoArr) {
			this.incomeInfoArr = incomeInfoArr;
		}

	}

	class InComeInfo {

		private FsExpenseIncome fsExpenseIncome;

		private String customerId;

		private Customer customer;

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}

		public FsExpenseIncome getFsExpenseIncome() {
			return fsExpenseIncome;
		}

		public void setFsExpenseIncome(FsExpenseIncome fsExpenseIncome) {
			this.fsExpenseIncome = fsExpenseIncome;
		}

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		this.startRun();
	}

}