package com.dahantc.erp.controller.search.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.region.entity.Region;
import com.dahantc.erp.vo.region.service.IRegionService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

public abstract class ISearchService {
	private static Logger logger = LogManager.getLogger(ISearchService.class);

	protected String downLoadFile = "exportFile/searchContent";

	@Autowired
	private IUserService userService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IRegionService regionService;

	@Autowired
	private ISupplierTypeService supplierTypeService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ISupplierService supplierService;

	/**
	 * 搜索类型
	 * 
	 * @return
	 */
	public abstract Integer getSearchType();

	public abstract String getSearchTypeName();

	/**
	 * 查询方法
	 * 
	 * @param onlineUser
	 * @param searchContent
	 * @param searchDate
	 * @param pageSize
	 * @param nowPage
	 * @return
	 */
	public abstract BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate,
			int pageSize, int nowPage);

	/**
	 * 获取导出title
	 * 
	 * @return
	 */
	protected abstract String[] getExportTitle();

	/**
	 * 获取需要导出的数据
	 * 
	 * @param onlineUser
	 * @param searchContent
	 * @param searchDate
	 * @return
	 */
	protected abstract List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId);

	/**
	 * 导出数据执行方法
	 * 
	 * @param onlineUser
	 * @param searchContent
	 * @param searchDate
	 * @return
	 */
	protected BaseResponse<UploadFileRespDto> export(OnlineUser onlineUser, String searchContent,  String searchStartDate, String searchDate, String flowId) {
		List<String[]> dataList = null;
		UploadFileRespDto result = new UploadFileRespDto();
		String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
		String resourceDir = Constants.RESOURCE + File.separator + downLoadFile + File.separator + datePath;
		String fileName = "";
		String filePath = "";
		try {
			dataList = getExportData(onlineUser, searchContent, searchStartDate, searchDate, flowId);
			// 导出文件路径
			fileName = getSearchTypeName() + "查询结果" + DateUtil.convert(new Date(), DateUtil.format8);
			File dir = new File(resourceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filePath = resourceDir + File.separator + fileName + ".xls";
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			String[] title = getExportTitle();
			ParseFile.exportDataToExcel(dataList, file, title);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("查询中心数据导出异常,请联系管理员");
		} finally {
			if (dataList != null && !dataList.isEmpty()) {
				dataList.clear();
				dataList = null;
			}
			result.setFilePath(filePath);
			result.setFileName(fileName + ".xls");
		}
		return BaseResponse.success(result);
	}

	protected String optCustomerName(Map<String, String> custMap, String custId) {
		String customerName = "";
		if (StringUtils.isNotBlank(custId)) {
			if (!custMap.containsKey(custId)) {
				try {
					Customer customer = customerService.read(custId);
					if (customer != null) {
						customerName = customer.getCompanyName();
					}
				} catch (ServiceException e) {
					logger.error("查询id为" + custId + "的客户失败", e);
				}
				custMap.put(custId, customerName);
			} else {
				customerName = custMap.get(custId);
			}
		}
		return customerName;
	}

	protected String optSupplierName(Map<String, String> supplierMap, String supplierId) {
		String supplierName = "";
		if (StringUtils.isNotBlank(supplierId)) {
			if (!supplierMap.containsKey(supplierId)) {
				try {
					Supplier supplier = supplierService.read(supplierId);
					if (supplier != null) {
						supplierName = supplier.getCompanyName();
					}
				} catch (ServiceException e) {
					logger.error("查询id为" + supplierId + "的供应商失败", e);
				}
				supplierMap.put(supplierId, supplierName);
			} else {
				supplierName = supplierMap.get(supplierId);
			}
		}
		return supplierName;
	}

	protected String optUserName(Map<String, String> userMap, String ossuserId) {
		String userName = "";
		if (StringUtils.isNotBlank(ossuserId)) {
			if (!userMap.containsKey(ossuserId)) {
				try {
					User user = userService.read(ossuserId);
					if (user != null) {
						userName = user.getRealName();
					}
				} catch (ServiceException e) {
					logger.error("查询userid为" + ossuserId + "的创建人失败", e);
				}
				userMap.put(ossuserId, userName);
			} else {
				userName = userMap.get(ossuserId);
			}
		}
		return userName;
	}

	protected String optFlowNode(Map<String, String> flowNodeMap, String nodeId) {
		String nodeName = "";
		if (StringUtils.isNotBlank(nodeId)) {
			if (!flowNodeMap.containsKey(nodeId)) {
				try {
					FlowNode flowNode = flowNodeService.read(nodeId);
					if (flowNode != null) {
						nodeName = flowNode.getNodeName();
					}
				} catch (ServiceException e) {
					logger.error("查询id为" + nodeId + "的流程节点异常", e);
				}
				flowNodeMap.put(nodeId, nodeName);
			} else {
				nodeName = flowNodeMap.get(nodeId);
			}
		}
		return nodeName;
	}

	protected String optDeptName(Map<String, String> deptMap, String deptId) {
		String deptName = "";
		if (StringUtils.isNotBlank(deptId)) {
			if (!deptMap.containsKey(deptId)) {
				try {
					Department department = departmentService.read(deptId);
					if (department != null) {
						deptName = department.getDeptname();
					}
				} catch (ServiceException e) {
					logger.error("查询deptId为" + deptId + "的部门失败", e);
				}
				deptMap.put(deptId, deptName);
			} else {
				deptName = deptMap.get(deptId);
			}
		}
		return deptName;
	}

	protected String optCustType(Map<String, String> custTypeMap, String customerTypeId) {
		String customerTypeName = "";
		if (StringUtils.isNotBlank(customerTypeId)) {
			if (!custTypeMap.containsKey(customerTypeId)) {
				try {
					CustomerType customerType = customerTypeService.read(customerTypeId);
					if (customerType != null) {
						customerTypeName = customerType.getCustomerTypeName();

					}
				} catch (ServiceException e) {
					logger.error("查询id为" + customerTypeId + "的客户类型失败", e);
				}
				custTypeMap.put(customerTypeId, customerTypeName);
			} else {
				customerTypeName = custTypeMap.get(customerTypeId);
			}
		}
		return customerTypeName;
	}

	protected String optRegion(Map<Integer, String> regionMap, Integer entityRegion) {
		String regionName = "";
		if (null != entityRegion) {
			if (!regionMap.containsKey(entityRegion)) {
				try {
					Region region = regionService.read(entityRegion);
					if (region != null) {
						regionName = region.getRegionName();
					}
				} catch (ServiceException e) {
					logger.error("查询id为" + entityRegion + "的地域失败", e);
				}
				regionMap.put(entityRegion, regionName);
			} else {
				regionName = regionMap.get(entityRegion);
			}
		}
		return regionName;
	}

	protected String optSupplierType(Map<String, String> custTypeMap, String supplierTypeId) {
		String customerTypeName = "";
		if (StringUtils.isNotBlank(supplierTypeId)) {
			if (!custTypeMap.containsKey(supplierTypeId)) {
				try {
					SupplierType supplierType = supplierTypeService.read(supplierTypeId);
					if (supplierType != null) {
						customerTypeName = supplierType.getSupplierTypeName();
					}
				} catch (ServiceException e) {
					logger.error("查询id为" + supplierTypeId + "的供应商类型失败", e);
				}
				custTypeMap.put(supplierTypeId, customerTypeName);
			} else {
				customerTypeName = custTypeMap.get(supplierTypeId);
			}
		}
		return customerTypeName;
	}
}
