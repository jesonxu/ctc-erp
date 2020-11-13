package com.dahantc.erp.controller.contract;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.contract.ContractDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.ContractFlowStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PayType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.contract.entity.Contract;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/contract")
public class ContractAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ContractAction.class);

	private static final String downLoadFile = "exportFile/contract";

	@Autowired
	private IUserService userService;

	@Autowired
	private IContractService contractService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IRegionService regionService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IProductTypeService productTypeService;

	/**
	 * 跳转到合同报表页面
	 */
	@RequestMapping("/toContractSheet")
	public String toContractSheet() {
		return "/views/manageConsole/contractSheet";
	}

	/**
	 * 分页查询合同表记录
	 *
	 * @return
	 */
	@RequestMapping(value = "/readContractByPage")
	@ResponseBody
	public BaseResponse<PageResult<ContractDto>> readContractByPage() {
		PageResult<Contract> pageResult = new PageResult<Contract>();
		PageResult<ContractDto> result = new PageResult<ContractDto>();
		int pageSize = 15;
		int nowPage = 1;
		logger.info("查询合同记录开始");
		try {
			long _start = System.currentTimeMillis();
			if (StringUtils.isNotBlank(request.getParameter("limit"))) {
				pageSize = Integer.parseInt(request.getParameter("limit"));
			}
			if (StringUtils.isNotBlank(request.getParameter("page"))) {
				nowPage = Integer.parseInt(request.getParameter("page"));
			}
			String contractName = request.getParameter("contractName");
			String contractId = request.getParameter("contractId");
			String ossUserId = request.getParameter("ossUserId");
			String entityName = request.getParameter("entityName");
			String applyDateStr = request.getParameter("applyDate");
			String applyDateEndStr = request.getParameter("applyDateEnd");
			String validStatusStr = request.getParameter("validStatus");
			// 封装搜索filter
			SearchFilter filter = buildSearchFilter(contractName, contractId, ossUserId, entityName, applyDateStr, applyDateEndStr, validStatusStr);
			if (null != filter) {
				pageResult = contractService.queryByPages(pageSize, nowPage, filter);
			}
			// 封装返回对象
			List<ContractDto> dtoList = buildContractDto(pageResult.getData());
			if (!CollectionUtils.isEmpty(dtoList)) {
				result.setData(dtoList);
				result.setCount(pageResult.getCount());
				result.setTotalPages(pageResult.getTotalPages());
				result.setCurrentPage(pageResult.getCurrentPage());
				result.setCode(pageResult.getCode());
			}
			logger.info("查询合同记录结束，查询到" + result.getCount() + "条记录，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询合同记录异常", e);
			result.setData(new ArrayList<ContractDto>());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 创建SearchFilter
	 * 
	 * @param contractName
	 *            搜索条件 合同名称
	 * @param contractId
	 *            搜索条件 合同编号
	 * @param searchUserId
	 *            搜索条件 申请人id
	 * @param entityName
	 *            搜索条件 客户名称
	 * @param applyDateStr
	 *            搜索条件 申请日期区间开始
	 * @param applyDateEndStr
	 *            搜索条件 申请日期区间结束
	 * @param validStatusStr
	 *            搜索条件 是否在有效期
	 * @return
	 */
	private SearchFilter buildSearchFilter(String contractName, String contractId, String searchUserId, String entityName, String applyDateStr,
			String applyDateEndStr, String validStatusStr) {
		SearchFilter filter = new SearchFilter();
		// 数据权限查用户，带用户id过滤
		OnlineUser onlineUser = getOnlineUserAndOnther();
		List<User> userList = userService.readUsers(onlineUser, searchUserId, "", "");
		List<String> userIdList = null;
		if (CollectionUtils.isEmpty(userList)) {
			logger.info("当前用户数据权限下没有用户");
			return null;
		} else {
			userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
		}
		if (StringUtils.isNotBlank(contractName)) {
			filter.getRules().add(new SearchRule("contractName", Constants.ROP_CN, contractName));
		}
		if (StringUtils.isNotBlank(contractId)) {
			filter.getRules().add(new SearchRule("contractId", Constants.ROP_EQ, contractId));
		}
		if (StringUtils.isNotBlank(entityName)) {
			filter.getRules().add(new SearchRule("entityName", Constants.ROP_CN, entityName));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isBlank(applyDateStr)) {
			applyDateStr = sdf.format(new Date()) + " 00:00:00";
		}
		if (StringUtils.isBlank(applyDateEndStr)) {
			applyDateEndStr = sdf.format(new Date()) + " 23:59:59";
		}
		if (StringUtils.isNotBlank(validStatusStr)) {
			String[] validStatus = validStatusStr.split(",");
			if (validStatus.length == 1) {
				if (Integer.parseInt(validStatus[0]) == 1) {
					// 只查在有效期的，开始有效期 < 今天 < 结束有效期
					filter.getRules().add(new SearchRule("validityDateStart", Constants.ROP_LE, DateUtil.getCurrentStartDateTime()));
					filter.getRules().add(new SearchRule("validityDateEnd", Constants.ROP_GT, DateUtil.getCurrentEndDateTime()));
				} else if (Integer.parseInt(validStatus[0]) == 0) {
					// 只查不在有效期的，结束有效期 < 今天，或 今天 < 开始有效期
					filter.getOrRules().add(new SearchRule[] { new SearchRule("validityDateEnd", Constants.ROP_LE, DateUtil.getCurrentStartDateTime()),
							new SearchRule("validityDateStart", Constants.ROP_GT, DateUtil.getCurrentEndDateTime()) });
				} else {
					logger.info("错误的[是否在合同有效期]选项：" + validStatusStr);
					return null;
				}
			} // 长度为2说明同时勾了是和否，同时查在有效期和不在有效期，就是查所有
		}
		filter.getRules().add(new SearchRule("applyDate", Constants.ROP_GE, DateUtil.convert2(applyDateStr)));
		filter.getRules().add(new SearchRule("applyDate", Constants.ROP_LE, DateUtil.convert2(applyDateEndStr)));
		filter.getOrders().add(new SearchOrder("wtime", "desc"));
		return filter;
	}

	/**
	 * 封装dto
	 * 
	 * @param contractList
	 *            合同对象列表
	 * @return 合同对象dto列表
	 */
	private List<ContractDto> buildContractDto(List<Contract> contractList) {
		List<ContractDto> dtoList = new ArrayList<>();
		try {
			if (!CollectionUtils.isEmpty(contractList)) {
				// 缓存map
				Map<Integer, String> statusMap = new HashMap<>();
				Map<String, String> deptMap = new HashMap<>();
				Map<Integer, String> productTypeMap = new HashMap<>();
				Map<Integer, String> settleTypeMap = new HashMap<>();
				Map<String, String> userMap = new HashMap<>();
				Map<Integer, String> regionMap = new HashMap<>();
				// 遍历查出的合同列表，封装
				for (Contract contract : contractList) {
					ContractDto dto = new ContractDto();
					dto.setContractId(contract.getContractId());
					dto.setContractName(contract.getContractName());
					// 申请人、申请人部门
					if (userMap.containsKey(contract.getOssUserId())) {
						dto.setRealName(userMap.get(contract.getOssUserId()));
						dto.setDeptName(deptMap.get(contract.getDeptId()));
					} else {
						User user = userService.read(contract.getOssUserId());
						userMap.put(user.getOssUserId(), user.getRealName());
						dto.setRealName(user.getRealName());
						Department dept = departmentService.read(contract.getDeptId());
						String deptName = dept != null ? dept.getDeptname() : "未知";
						deptMap.put(contract.getDeptId(), deptName);
						dto.setDeptName(deptName);
					}
					// 合同评审状态
					if (statusMap.containsKey(contract.getStatus())) {
						dto.setStatus(statusMap.get(contract.getStatus()));
					} else {
						Optional<ContractFlowStatus> statusOpt = ContractFlowStatus.getEnumsByCode(contract.getStatus());
						if (statusOpt.isPresent()) {
							statusMap.put(statusOpt.get().getCode(), statusOpt.get().getMsg());
							dto.setStatus(statusOpt.get().getMsg());
						} else {
							statusMap.put(contract.getStatus(), "未知");
							dto.setStatus("未知");
						}
					}
					// 申请日期
					if (null != contract.getApplyDate()) {
						dto.setApplyDate(DateUtil.convert(contract.getApplyDate(), DateUtil.format2));
					} else {
						dto.setApplyDate("");
					}
					// 创建日期
					if (null != contract.getWtime()) {
						dto.setWtime(DateUtil.convert(contract.getWtime(), DateUtil.format2));
					} else {
						dto.setWtime("");
					}
					dto.setWtime(DateUtil.convert(contract.getWtime(), DateUtil.format2));
					dto.setEntityName(contract.getEntityName());
					if (regionMap.containsKey(contract.getEntityRegion())) {
						dto.setEntityRegion(regionMap.get(contract.getEntityRegion()));
					} else {
						Region region = regionService.read(contract.getEntityRegion());
						String regionName = region != null ? region.getRegionName() : "未知";
						regionMap.put(contract.getEntityRegion(), regionName);
						dto.setEntityRegion(regionName);
					}
					dto.setContractRegion(contract.getContractRegion());
					dto.setContactName(contract.getContactName());
					dto.setContactPhone(contract.getContactPhone());
					dto.setAddress(contract.getAddress());
					dto.setContractType(contract.getContractType());
					// 产品类型
					if (productTypeMap.containsKey(contract.getProductType())) {
						dto.setProductType(productTypeMap.get(contract.getProductType()));
					} else {
						String productTypeName = productTypeService.getProductTypeNameByValue(contract.getProductType());
						productTypeMap.put(contract.getProductType(), productTypeName);
						dto.setProductType(productTypeName);
					}
					// 付费方式
					if (settleTypeMap.containsKey(contract.getSettleType())) {
						dto.setSettleType(settleTypeMap.get(contract.getSettleType()));
					} else {
						Optional<PayType> settleTypeOpt = PayType.getEnumsByCode(contract.getSettleType());
						if (settleTypeOpt.isPresent()) {
							settleTypeMap.put(settleTypeOpt.get().ordinal(), settleTypeOpt.get().getMsg());
							dto.setSettleType(settleTypeOpt.get().getMsg());
						} else {
							settleTypeMap.put(contract.getSettleType(), "未知");
							dto.setSettleType("未知");
						}
					}
					dto.setMonthCount(contract.getMonthCount());
					dto.setContractAmount(contract.getContractAmount());
					dto.setPrice(contract.getPrice());
					dto.setProjectLeader(contract.getProjectLeader());
					dto.setDescription(contract.getDescription());
					// 有效期开始
					if (null != contract.getValidityDateStart()) {
						dto.setValidityDateStart(DateUtil.convert(contract.getValidityDateStart(), DateUtil.format2));
					} else {
						dto.setValidityDateStart("");
					}
					// 有效期结束
					if (null != contract.getValidityDateEnd()) {
						dto.setValidityDateEnd(DateUtil.convert(contract.getValidityDateEnd(), DateUtil.format2));
					} else {
						dto.setValidityDateEnd("");
					}
					dto.setContractFilesScan(contract.getContractFilesScan());
					dtoList.add(dto);
				}
			}
		} catch (Exception e) {
			logger.error("封装合同返回对象异常", e);
		}
		return dtoList;
	}

	/**
	 * 查询合同流程，并转换成合同表记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/readContractInFlow")
	@ResponseBody
	public BaseResponse<PageResult<ContractDto>> readContractInFlow() {
		List<Contract> savedContract = new ArrayList<>();
		PageResult<ContractDto> result = new PageResult<ContractDto>();
		String flowId;
		logger.info("查询合同流程生成或更新合同表记录开始");
		try {
			// 查询 销售合同评审流程 的流程设计
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.SALE_CONTRACT_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
			} else {
				logger.info("系统无" + Constants.SALE_CONTRACT_FLOW_NAME);
				return BaseResponse.success(result);
			}
			// 查出所有 销售合同评审流程
			SearchFilter flowFilter = new SearchFilter();
			flowFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			List<FlowEnt> contractFlowList = flowEntService.queryAllBySearchFilter(flowFilter);
			if (CollectionUtils.isEmpty(contractFlowList)) {
				logger.info("未查询到" + Constants.SALE_CONTRACT_FLOW_NAME + "的流程实体");
				return BaseResponse.success(result);
			}
			// 遍历合同流程生成合同表记录
			for (FlowEnt flowEnt : contractFlowList) {
				String flowMsg = flowEnt.getFlowMsg();
				if (StringUtil.isNotBlank(flowMsg)) {
					JSONObject json = JSONObject.parseObject(flowMsg);
					if (json != null) {
						Contract contract = saveContract(flowEnt, json);
						if (contract != null) {
							savedContract.add(contract);
						}
					}
				}

			}
			// 封装返回对象
			List<ContractDto> dtoList = buildContractDto(savedContract);
			result.setData(dtoList);
			result.setCount(dtoList.size());
			logger.info("查询合同流程结束，生成或更新" + savedContract.size() + "条记录");
		} catch (Exception e) {
			logger.error("查询合同流程生成或更新合同记录异常", e);
			result.setData(new ArrayList<ContractDto>());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 根据流程标签内容生成合同对象
	 * 
	 * @param flowEnt
	 *            流程实体
	 * @param json
	 *            流程标签内容
	 * @return
	 */
	private Contract saveContract(FlowEnt flowEnt, JSONObject json) {
		Contract contract = null;
		try {
			String contractNo;
			boolean isUpdate = false;
			// 合同编号
			if (json.containsKey(Constants.CONTRACT_NUMBER) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_NUMBER))) {
				contractNo = json.getString(Constants.CONTRACT_NUMBER);
				contract = contractService.read(contractNo);
				if (contract != null) {
					isUpdate = true;
					logger.info("合同编号对应的记录已存在：" + contractNo + "，对其进行更新");
				} else {
					contract = new Contract();
					contract.setContractId(contractNo);
					logger.info("合同编号对应的记录不存在：" + contractNo + "，对其进行创建");
				}
			} else {
				logger.info(Constants.CONTRACT_NUMBER + "不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}
			// 根据流程审核状态，设置合同记录的评审状态
			int flowStatus = flowEnt.getFlowStatus();
			if (FlowStatus.FILED.ordinal() == flowStatus) {
				// 已归档
				contract.setStatus(ContractFlowStatus.FILED.getCode());
			} else if (FlowStatus.NO_PASS.ordinal() == flowStatus || FlowStatus.NOT_AUDIT.ordinal() == flowStatus) {
				// 申请中
				contract.setStatus(ContractFlowStatus.APPLYING.getCode());
			} else if (FlowStatus.CANCLE.ordinal() == flowStatus) {
				// 已取消
				contract.setStatus(ContractFlowStatus.CANCLE.getCode());
			}

			// 合同名称
			if (json.containsKey(Constants.CONTRACT_NAME) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_NAME))) {
				contract.setContractName(json.getString(Constants.CONTRACT_NAME));
			}
			// 申请人信息
			User user = userService.read(flowEnt.getOssUserId());
			contract.setOssUserId(user.getOssUserId());
			contract.setDeptId(user.getDeptId());
			contract.setProjectLeader(user.getRealName());
			// 申请日期
			if (json.containsKey(Constants.APPLY_DATE) && StringUtil.isNotBlank(json.getString(Constants.APPLY_DATE))) {
				String applyDateStr = json.getString(Constants.APPLY_DATE);
				Date applyDate = DateUtil.convert(applyDateStr, DateUtil.format1);
				contract.setApplyDate(new Timestamp(applyDate.getTime()));
			}
			// 创建日期
			contract.setWtime(flowEnt.getWtime());
			// 实体信息，客户/供应商
			contract.setEntityType(flowEnt.getEntityType());
			int entityType = flowEnt.getEntityType();
			if (EntityType.CUSTOMER.ordinal() == entityType) {
				Customer customer = customerService.read(flowEnt.getSupplierId());
				contract.setEntityId(customer.getCustomerId());
				contract.setEntityName(customer.getCompanyName());
				contract.setEntityRegion(customer.getCustomerRegion());
				contract.setContactName(customer.getContactName());
				contract.setContactPhone(customer.getContactPhone());
				contract.setAddress(customer.getPostalAddress());

				CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
				contract.setProductType(customerProduct.getProductType());
			} else if (EntityType.SUPPLIER.ordinal() == entityType) {
				Supplier supplier = supplierService.read(flowEnt.getSupplierId());
				contract.setEntityId(supplier.getSupplierId());
				contract.setEntityName(supplier.getCompanyName());
				contract.setContactName(supplier.getContactName());
				contract.setContactPhone(supplier.getContactPhone());
				contract.setAddress(supplier.getPostalAddress());

				Product product = productService.read(flowEnt.getProductId());
				contract.setProductType(product.getProductType());
			}
			// 付费方式
			if (json.containsKey(Constants.PAY_TYPE) && StringUtil.isNotBlank(json.getString(Constants.PAY_TYPE))) {
				String payTypeStr = json.getString(Constants.PAY_TYPE);
				Optional<PayType> payTypeOpt = PayType.getEnumsByMsg(payTypeStr);
				if (payTypeOpt.isPresent()) {
					contract.setSettleType(payTypeOpt.get().ordinal());
				}
			}
			// 合同归属
			if (json.containsKey(Constants.CONTRACT_REGION) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_REGION))) {
				contract.setContractRegion(json.getString(Constants.CONTRACT_REGION));
			}
			// 合同类型
			if (json.containsKey(Constants.CONTRACT_TYPE) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_TYPE))) {
				contract.setContractType(json.getString(Constants.CONTRACT_TYPE));
			}
			// 月发送量，可能会写 “xxx万”
			if (json.containsKey(Constants.MONTH_COUNT) && StringUtil.isNotBlank(json.getString(Constants.MONTH_COUNT))) {
				contract.setMonthCount(json.getString(Constants.MONTH_COUNT));
			}
			// 合同金额
			if (json.containsKey(Constants.CONTRACT_AMOUNT) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_AMOUNT))) {
				String contractAmountStr = json.getString(Constants.CONTRACT_AMOUNT);
				contract.setContractAmount(contractAmountStr);
			}
			// 单价
			if (json.containsKey(Constants.UNIT_PRICE) && StringUtil.isNotBlank(json.getString(Constants.UNIT_PRICE))) {
				String unitPriceStr = json.getString(Constants.UNIT_PRICE);
				contract.setPrice(unitPriceStr);
			}
			// 开始有效期
			if (json.containsKey(Constants.VALIDITY_DATE_START) && StringUtil.isNotBlank(json.getString(Constants.VALIDITY_DATE_START))) {
				String dateStartStr = json.getString(Constants.VALIDITY_DATE_START);
				Date dateStart = DateUtil.convert(dateStartStr, DateUtil.format1);
				contract.setValidityDateStart(new Timestamp(dateStart.getTime()));
			} else {
				contract.setValidityDateStart(new Timestamp(DateUtil.getCurrentStartDateTime().getTime()));
			}
			// 结束有效期
			if (json.containsKey(Constants.VALIDITY_DATE_END) && StringUtil.isNotBlank(json.getString(Constants.VALIDITY_DATE_END))) {
				String dateEndStr = json.getString(Constants.VALIDITY_DATE_END);
				Date dateEnd = DateUtil.convert(dateEndStr, DateUtil.format1);
				dateEnd = DateUtil.getDateEndDateTime(dateEnd);
				contract.setValidityDateEnd(new Timestamp(dateEnd.getTime()));
			} else {
				contract.setValidityDateEnd(new Timestamp(DateUtil.getCurrentEndDateTime().getTime()));
			}
			// 项目情况说明
			if (json.containsKey(Constants.PROJECT_DESCRIPTION) && StringUtil.isNotBlank(json.getString(Constants.PROJECT_DESCRIPTION))) {
				contract.setDescription(json.getString(Constants.PROJECT_DESCRIPTION));
			}
			// 合同扫描件
			if (json.containsKey(Constants.CONTRACT_FILES_SCAN) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_FILES_SCAN))) {
				contract.setContractFilesScan(json.getString(Constants.CONTRACT_FILES_SCAN));
			} else if (json.containsKey(Constants.CONTRACT_FILE) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_FILE))) {
				// 没有扫描件，从合同附件取
				contract.setContractFilesScan(json.getString(Constants.CONTRACT_FILE));
			}
			boolean result = isUpdate ? contractService.update(contract) : contractService.save(contract);
			logger.info("生成或更新合同表记录" + (result ? "成功" : "失败") + "，合同编号：" + contractNo);
		} catch (Exception e) {
			logger.error("生成或更新合同表记录异常，flowEntId：" + flowEnt.getId(), e);
			return null;
		}
		return contract;
	}

	/**
	 * 按主体id查合同记录
	 * 
	 * @param entityId
	 *            主体id
	 * @return
	 */
	@RequestMapping(value = "/readContractByEntityId")
	@ResponseBody
	public BaseResponse<JSONArray> readContractByEntityId(@RequestParam String entityId) {
		JSONArray result = new JSONArray();
		logger.info("查询合同记录开始，entityId：" + entityId);
		try {
			if (StringUtil.isBlank(entityId)) {
				logger.info("主体id不能为空");
				return BaseResponse.success(result);
			}
			long _start = System.currentTimeMillis();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("entityId", Constants.ROP_EQ, entityId));
			filter.getOrders().add(new SearchOrder("wtime", "desc"));
			List<Contract> contractList = contractService.queryAllBySearchFilter(filter);
			// 封装返回对象
			List<ContractDto> dtoList = buildContractDto(contractList);
			result = JSONArray.parseArray(JSON.toJSONString(dtoList));
			logger.info("查询合同记录结束，查询到" + result.size() + "条记录，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询合同记录异常，entityId：" + entityId, e);
			return BaseResponse.success(new JSONArray());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 导出合同，与搜索条件一致
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportContract")
	public BaseResponse<UploadFileRespDto> exportContract() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		List<String[]> dataList = new ArrayList<>();
		UploadFileRespDto result = new UploadFileRespDto();
		String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
		String resourceDir = Constants.RESOURCE + File.separator + downLoadFile + File.separator + datePath;
		String fileName = "";
		String filePath = "";
		String[] title = new String[] { "合同编号", "合同名称", "合同评审状态", "申请日期", "申请人", "申请人部门", "客户地域", "客户", "客户联系人", "合同归属", "客户联系方式", "客户联系地址", "合同类型", "产品类型",
				"付费方式", "月发送量", "合同金额", "单价", "项目负责人", "有效期开始", "有效期结束", "项目情况说明" };
		try {
			String contractName = request.getParameter("contractName");
			String contractId = request.getParameter("contractId");
			String ossUserId = request.getParameter("ossUserId");
			String entityName = request.getParameter("entityName");
			String applyDateStr = request.getParameter("applyDate");
			String applyDateEndStr = request.getParameter("applyDateEnd");
			String validStatusStr = request.getParameter("validStatus");
			// 封装搜索filter
			SearchFilter filter = buildSearchFilter(contractName, contractId, ossUserId, entityName, applyDateStr, applyDateEndStr, validStatusStr);
			if (null == filter) {
				return BaseResponse.error("导出合同报表失败");
			}
			List<Contract> contractList = contractService.queryAllBySearchFilter(filter);
			// 封装返回对象
			List<ContractDto> dtoList = buildContractDto(contractList);
			if (!CollectionUtils.isEmpty(dtoList)) {
				dataList = dtoList.stream().map(dto -> dto.toExportData(title.length)).collect(Collectors.toList());
			}
			// 导出文件名
			fileName = "合同报表" + DateUtil.convert(new Date(), DateUtil.format8);
			File dir = new File(resourceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filePath = resourceDir + File.separator + fileName + ".xls";
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			ParseFile.exportDataToExcel(dataList, file, title);
		} catch (Exception e) {
			logger.error("导出合同异常", e);
			return BaseResponse.error("导出合同异常");
		} finally {
			if (dataList != null) {
				dataList.clear();
				dataList = null;
			}
			result.setFilePath(filePath);
			result.setFileName(fileName + ".xls");
		}
		logger.info("导出合同结束" + result.toString());
		return BaseResponse.success(result);
	}
}
