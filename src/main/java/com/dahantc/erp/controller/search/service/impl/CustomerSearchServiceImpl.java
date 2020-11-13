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
import com.dahantc.erp.dto.search.CustomerSearchRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;

@Service("customerSearchService")
public class CustomerSearchServiceImpl extends ISearchService implements InitializingBean {
	private static Logger logger = LogManager.getLogger(CustomerSearchServiceImpl.class);
	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Override
	public Integer getSearchType() {
		return SearchType.CUSTOMER.getCode();
	}

	@Override
	public String getSearchTypeName() {
		return SearchType.CUSTOMER.getDesc();
	}

	@Override
	public BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, int pageSize,
			int nowPage) {
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchContent);

			PageResult<Customer> customerPageResult = customerService.queryByPages(pageSize, nowPage, filter);
			return BaseResponse.success(new PageResult<>(buildCustUI(customerPageResult.getData()), customerPageResult.getCount()));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success(new PageResult<Object>());
	}

	@Override
	protected String[] getExportTitle() {
		return new String[] { "客户名称", "城市", "电子邮件", "客户类型", "负责销售", "姓名", "移动电话", "创建时间" };
	}

	@Override
	protected List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId) {
		List<String[]> dataList = null;
		List<Customer> customers = null;
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchContent);
			customers = customerService.queryAllBySearchFilter(filter);
			dataList = buildExportData(customers, getExportTitle());
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (customers != null && !customers.isEmpty()) {
				customers.clear();
				customers = null;
			}
		}
		return dataList;
	}

	// "客户名称", "城市", "电子邮件", "客户类型", "负责销售", "姓名", "移动电话", "创建时间"
	private List<String[]> buildExportData(List<Customer> customers, String[] title) {
		List<String[]> dataList = new ArrayList<String[]>();
		Map<String, String> custTypeMap = null;
		Map<String, String> userMap = null;
		Map<Integer, String> regionMap = null;
		try {
			if (customers != null && !customers.isEmpty()) {
				custTypeMap = new HashMap<>();
				userMap = new HashMap<>();
				regionMap = new HashMap<>();
				for (Customer customer : customers) {
					String[] data = new String[title.length];
					data[0] = customer.getCompanyName();
					data[1] = optRegion(regionMap, customer.getCustomerRegion());
					data[2] = customer.getEmail();
					data[3] = optCustType(custTypeMap, customer.getCustomerTypeId());
					data[4] = optUserName(userMap, customer.getOssuserId());
					data[5] = customer.getContactName();
					data[6] = customer.getContactPhone();
					data[7] = DateUtil.convert(customer.getWtime(), DateUtil.format2);
					dataList.add(data);
				}
			}
		} finally {
			if (custTypeMap != null && !custTypeMap.isEmpty()) {
				custTypeMap.clear();
				custTypeMap = null;
			}
			if (regionMap != null && !regionMap.isEmpty()) {
				regionMap.clear();
				regionMap = null;
			}
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
		}
		return dataList;
	}

	private List<CustomerSearchRespDto> buildCustUI(List<Customer> data) {
		List<CustomerSearchRespDto> result = new ArrayList<>();
		Map<String, String> custTypeMap = null;
		Map<String, String> userMap = null;
		Map<String, String> deptMap = null;
		Map<Integer, String> regionMap = null;
		try {
			if (data != null && !data.isEmpty()) {
				custTypeMap = new HashMap<>();
				userMap = new HashMap<>();
				deptMap = new HashMap<>();
				regionMap = new HashMap<>();
				for (Customer customer : data) {
					CustomerSearchRespDto dto = new CustomerSearchRespDto();
					dto.setCompanyName(customer.getCompanyName());
					dto.setEmail(customer.getEmail());
					dto.setContactName(customer.getContactName());
					dto.setContactPhone(customer.getContactPhone());
					dto.setWitme(DateUtil.convert(customer.getWtime(), DateUtil.format2));
					dto.setPostalAdress(customer.getPostalAddress());
					dto.setCustomerType(optCustType(custTypeMap, customer.getCustomerTypeId()));
					dto.setCreateUser(optUserName(userMap, customer.getOssuserId()));
					dto.setCreateUserDept(optDeptName(deptMap, customer.getDeptId()));
					dto.setRegion(optRegion(regionMap, customer.getCustomerRegion()));
					result.add(dto);
				}
			}
		} finally {
			if (custTypeMap != null && !custTypeMap.isEmpty()) {
				custTypeMap.clear();
				custTypeMap = null;
			}
			if (regionMap != null && !regionMap.isEmpty()) {
				regionMap.clear();
				regionMap = null;
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
			searchFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
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
			searchFilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		}
		if (StringUtils.isNotBlank(searchContent)) {
			searchFilter.getRules().add(new SearchRule("keyWords", Constants.ROP_CN, searchContent));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		return searchFilter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SearchHandler.getInstance().registerSearchService(getSearchType(), this);
	}
}
