package com.dahantc.erp.controller.dsSupplier;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.resourceConsole.ResourceConsoleAction;
import com.dahantc.erp.dto.SupplierType.SupplierTypeRspDto;
import com.dahantc.erp.dto.supplier.DsSupplierRspDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;

@Controller
@RequestMapping("dianShangSupplier")
public class DsSupplierAction extends BaseAction{

	private static final Logger logger = LogManager.getLogger(ResourceConsoleAction.class);
	
	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ISupplierTypeService supplierTypeService;
	
	/**
	 * 跳转电商供应商工作台
	 */
	@RequestMapping("/toSupplierConsoleDs")
	public String toResourceConsole() {
		try {
			// 读取 用户页面的权限
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
		} catch (Exception e) {
			logger.error("获取用户页面权限的时候异常", e);
		}
		// 获取供应商信息（含未处理流程数）
		List<SupplierRspDto> suppliers = supplierService.querySuppliers(getOnlineUserAndOnther(), "", "", "", "", SearchType.SUPPLIER.ordinal());
		// 统计不同供应商类型对应 未处理流程 和 供应商 数量
		List<SupplierTypeRspDto> supplierCount = supplierTypeService.countSupplierTypes(suppliers);
		request.setAttribute("resourceFlowCount", supplierCount.stream().mapToLong(SupplierTypeRspDto::getFlowEntCount).sum());
		request.setAttribute("suppliers", suppliers);
		request.setAttribute("supplierTypes", supplierCount);
		request.setAttribute("defaultShow", true);
		return "/views/console/supplierConsoleDs";
	}

	/**
	 * 跳转去添加供应商页面
	 *
	 * @return String 页面路径
	 */
	@RequestMapping("/toAddDsSupperPage")
	public String toAddSupperPage() {
		return "/views/supplierDs/addSupplier";
	}
	
	/**
	 * 查找供应商
	 */
	@PostMapping("/querySupplier")
	@ResponseBody
	public BaseResponse<List<DsSupplierRspDto>> querySupplier() throws Exception {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		List<DsSupplierRspDto> dsSupplierRspDtos = new ArrayList<DsSupplierRspDto>();
		if (onlineUser == null) {
			return BaseResponse.error("未登录");
		}
		SearchFilter filter = new SearchFilter();
		List<Supplier> suppliers = supplierService.queryAllBySearchFilter(filter);
		for (Supplier supplier : suppliers) {
			DsSupplierRspDto dsSupplierRspDto = new DsSupplierRspDto();
			dsSupplierRspDto.setCompanyName(supplier.getCompanyName());
			dsSupplierRspDto.setSupplierId(supplier.getSupplierId());
			dsSupplierRspDtos.add(dsSupplierRspDto);
		}
		return BaseResponse.success(dsSupplierRspDtos);
	}
	
}

