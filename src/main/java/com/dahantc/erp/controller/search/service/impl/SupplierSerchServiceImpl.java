package com.dahantc.erp.controller.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.search.service.ISearchService;
import com.dahantc.erp.controller.search.service.SearchHandler;
import com.dahantc.erp.dto.search.SupplierSerchRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Service("supplierSerchService")
public class SupplierSerchServiceImpl extends ISearchService implements InitializingBean {
	private static Logger logger = LogManager.getLogger(SupplierSerchServiceImpl.class);
	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ISupplierService supplierService;

	@Override
	public Integer getSearchType() {
		return SearchType.SUPPLIER.getCode();
	}

	@Override
	public String getSearchTypeName() {
		return SearchType.SUPPLIER.getDesc();
	}

	@Override
	public BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, int pageSize, int nowPage) {
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchContent);

			PageResult<Supplier> customerPageResult = supplierService.queryByPages(pageSize, nowPage, filter);
			return BaseResponse.success(new PageResult<>(buildSupplierUI(customerPageResult.getData()), customerPageResult.getCount()));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success(new PageResult<Object>());
	}

	@Override
	protected String[] getExportTitle() {
		return new String[] { "供应商名称", "供应商类型", "业务联系人", "移动电话", "地址", "创建人", "创建时间" };
	}

	@Override
	protected List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId) {
		List<String[]> dataList = null;
		List<Supplier> suppliers = null;
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchContent);
			suppliers = supplierService.queryAllBySearchFilter(filter);
			dataList = buildExportData(suppliers, getExportTitle());
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (suppliers != null && !suppliers.isEmpty()) {
				suppliers.clear();
				suppliers = null;
			}
		}
		return dataList;
	}

	// "供应商名称", "供应商类型", "业务联系人", "移动电话", "地址", "创建人", "创建时间"
	private List<String[]> buildExportData(List<Supplier> suppliers, String[] title) {
		List<String[]> dataList = new ArrayList<>();
		Map<String, String> custTypeMap = null;
		Map<String, String> userMap = null;
		try {
			if (suppliers != null && !suppliers.isEmpty()) {
				custTypeMap = new HashMap<>();
				userMap = new HashMap<>();
				for (Supplier supplier : suppliers) {
					String[] data = new String[title.length];
					data[0] = supplier.getCompanyName();
					data[1] = optSupplierType(custTypeMap, supplier.getSupplierTypeId());
					data[2] = supplier.getContactName();
					data[3] = supplier.getContactPhone();
					data[4] = supplier.getPostalAddress();
					data[5] = optUserName(userMap, supplier.getOssUserId());
					data[6] = DateUtil.convert(supplier.getWtime(), DateUtil.format2);
					dataList.add(data);
				}
			}
		} finally {
			if (custTypeMap != null && !custTypeMap.isEmpty()) {
				custTypeMap.clear();
				custTypeMap = null;
			}
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
		}
		return dataList;
	}

	private List<SupplierSerchRespDto> buildSupplierUI(List<Supplier> data) {
		List<SupplierSerchRespDto> result = new ArrayList<>();
		Map<String, String> custTypeMap = null;
		Map<String, String> userMap = null;
		Map<String, String> deptMap = null;
		try {
			if (data != null && !data.isEmpty()) {
				custTypeMap = new HashMap<>();
				userMap = new HashMap<>();
				deptMap = new HashMap<>();
				for (Supplier supplier : data) {
					SupplierSerchRespDto dto = new SupplierSerchRespDto();
					dto.setCompanyName(supplier.getCompanyName());
					dto.setContactName(supplier.getContactName());
					dto.setContactPhone(supplier.getContactPhone());
					dto.setWitme(DateUtil.convert(supplier.getWtime(), DateUtil.format2));

					dto.setPostalAdress(supplier.getPostalAddress());

					dto.setSupplierType(optSupplierType(custTypeMap, supplier.getSupplierTypeId()));
					dto.setCreateUser(optUserName(userMap, supplier.getOssUserId()));
					dto.setCreateUserDept(optDeptName(deptMap, supplier.getDeptId()));
					result.add(dto);
				}
			}
		} finally {
			if (custTypeMap != null && !custTypeMap.isEmpty()) {
				custTypeMap.clear();
				custTypeMap = null;
			}
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
			if (deptMap != null && !deptMap.isEmpty()) {
				deptMap.clear();
				deptMap = null;
			}
		}
		return result;
	}

	private SearchFilter buildSearchFilter(OnlineUser onlineUser, String searchContent) throws ServiceException {
		SearchFilter searchFilter = new SearchFilter();
		String deptId = onlineUser.getUser().getDeptId();
		Role role = roleService.read(onlineUser.getRoleId());
		// 数据权限
		int dataPermission = role.getDataPermission();
		if (dataPermission == DataPermission.Dept.ordinal()) {
			// 部门权限
			List<String> subDeptIds = new ArrayList<>();
			subDeptIds.add(deptId);
			subDeptIds.addAll(departmentService.getSubDeptIds(deptId));
			searchFilter.getOrRules()
					.add(new SearchRule[] { new SearchRule("deptId", Constants.ROP_IN, subDeptIds), new SearchRule("deptId", Constants.ROP_EQ, null), });
		} else if (dataPermission == DataPermission.Self.ordinal()) {
			// 自己
			searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		} else if (dataPermission == DataPermission.Customize.ordinal()) {
			// 自定义
			String deptIdInfo = role.getDeptIds();
			List<String> deptIds = new ArrayList<>();
			if (StringUtil.isNotBlank(deptIdInfo)) {
				deptIds.addAll(Arrays.asList(deptIdInfo.split(",")));
			}
			searchFilter.getOrRules()
					.add(new SearchRule[] { new SearchRule("deptId", Constants.ROP_IN, deptIds), new SearchRule("deptId", Constants.ROP_EQ, null), });
		} else if (dataPermission == DataPermission.Flow.ordinal()) {
			// 流程权限
			searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		}
		if (StringUtils.isNotBlank(searchContent)) {
			searchFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, searchContent));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime",Constants.ROP_DESC));
		return searchFilter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SearchHandler.getInstance().registerSearchService(getSearchType(), this);
	}
}
