package com.dahantc.erp.controller.resourceConsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.commom.interceptor.XssHttpServletRequestWrapper;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.dto.SupplierType.SupplierTypeRspDto;
import com.dahantc.erp.dto.supplier.SupplierRspDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;

@Controller
@RequestMapping("resourceConsole")
public class ResourceConsoleAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ResourceConsoleAction.class);

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ISupplierTypeService supplierTypeService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IRoleService roleService;

	/**
	 * 供应商按名称搜索
	 */
	@RequestMapping("/queryByKeyWord")
	public String queryByKeyWord() {
		String entityType = request.getParameter("entityType");

		try {
			// 读取 用户页面的权限
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
		} catch (Exception e) {
			logger.error("获取用户页面权限的时候异常", e);
		}
		// 获取供应商信息（含未处理流程数）
		List<SupplierRspDto> suppliers = supplierService.querySuppliers(getOnlineUserAndOnther(), "", "", "",
				XssHttpServletRequestWrapper.xssDecode(request.getParameter("keyWord")), SearchType.FLOW.ordinal());
		// 统计不同供应商类型对应 未处理流程 和 供应商 数量
		List<SupplierTypeRspDto> supplierCount = supplierTypeService.countSupplierTypes(suppliers);
		request.setAttribute("suppliers", suppliers);
		request.setAttribute("supplierTypes", supplierCount);
		request.setAttribute("defaultShow", true);
		if (StringUtil.isNotBlank(entityType)) {
			int entitytype = Integer.parseInt(entityType);
			if (entitytype == EntityType.SUPPLIER_DS.ordinal()) {
				return "/views/supplierDs/supplierInfo";
			}
		}
		return "/views/supplier/supplierInfo";
	}

	/**
	 * 跳转资源工作台
	 */
	@RequestMapping("/toResourceConsole")
	public String toResourceConsole() {
		try {
			// 读取 用户页面的权限
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
		} catch (Exception e) {
			logger.error("获取用户页面权限的时候异常", e);
		}
		// 获取供应商信息（含未处理流程数）
		List<SupplierRspDto> suppliers = supplierService.querySuppliers(getOnlineUserAndOnther(), "", "", "", "", SearchType.FLOW.ordinal());
		// 统计不同供应商类型对应 未处理流程 和 供应商 数量
		List<SupplierTypeRspDto> supplierCount = supplierTypeService.countSupplierTypes(suppliers);
		request.setAttribute("resourceFlowCount", supplierCount.stream().mapToLong(SupplierTypeRspDto::getFlowEntCount).sum());
		request.setAttribute("suppliers", suppliers);
		request.setAttribute("supplierTypes", supplierCount);
		request.setAttribute("defaultShow", true);
		return "/views/console/resourceConsole";
	}

	/**
	 * 刷新待审核数
	 *
	 * @return
	 */
	@RequestMapping("/queryFlowEntCount")
	@ResponseBody
	public JSONObject queryFlowEntCount() {
		JSONObject result = new JSONObject();
		try {
			Map<String, Long> supplierTypeCount = new HashMap<>(); // 供应商类别
			Map<String, Long> supplierCount = new HashMap<>(); // 供应商
			Map<String, Long> productCount = new HashMap<>(); // 产品
			Map<String, Long> operateYearCount = new HashMap<>(); // 运营(年)
			Map<String, Map<String, Long>> operateCount = new HashMap<>(); // 运营(月)
			Map<String, Long> settleYearCount = new HashMap<>(); // 结算(年)
			Map<String, Map<String, Long>> settleCount = new HashMap<>(); // 结算(月)

			String productId = request.getParameter("productId");
			String supplierId = request.getParameter("supplierId");

			// 待处理流程数
			List<FlowEntDealCount> countList = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
			// 获取当前用户的所有供应商
			List<SupplierRspDto> supplierList = supplierService.querySuppliers(getOnlineUserAndOnther(), request.getParameter("deptIds"), "", "",
					request.getParameter("keyWord"), SearchType.FLOW.ordinal());
			List<String> supplierIds = new ArrayList<String>();
			if (supplierList != null && !supplierList.isEmpty()) {
				supplierIds = supplierList.stream().map(SupplierRspDto::getSupplierId).collect(Collectors.toList());
			}
			// 过滤出当前用户供应商的流程
			List<String> finalSupplierIds = supplierIds;
			countList = countList.stream().filter(flow -> finalSupplierIds.contains(flow.getSupplierId())).collect(Collectors.toList());
			List<Product> productList = null;
			if (StringUtils.isNotBlank(supplierId)) {
				SearchFilter productFilter = new SearchFilter();
				productFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
				productList = productService.queryAllBySearchFilter(productFilter);
			}

			sumSupplierFlowCount(supplierList, supplierCount);
			sumSupplierTypeFlowCount(supplierList, supplierTypeCount);
			sumProductFlowCount(productList, supplierId, productCount, countList);
			// 运营、结算
			sumFlowCountByFlowType(supplierId, productId, operateYearCount, operateCount, countList, FlowType.OPERATE.ordinal());
			sumFlowCountByFlowType(supplierId, productId, settleYearCount, settleCount, countList, FlowType.FINANCE.ordinal());

			result.put("supplierCount", supplierCount);
			result.put("supplierTypeCount", supplierTypeCount);
			result.put("productCount", productCount);
			result.put("operateYearCount", operateYearCount);
			result.put("operateCount", operateCount);
			result.put("settleYearCount", settleYearCount);
			result.put("settleCount", settleCount);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * 刷新待审核数
	 *
	 * @return
	 */
	@RequestMapping("/queryConsoleFlowEntCount")
	@ResponseBody
	public JSONObject queryConsoleFlowEntCount() {
		JSONObject result = new JSONObject();
		try {
			Map<String, Long> supplierTypeCount = new HashMap<>(); // 供应商类别
			// 获取当前用户的所有供应商
			List<SupplierRspDto> supplierList = supplierService.querySuppliers(getOnlineUserAndOnther(), request.getParameter("deptIds"), "", "",
					request.getParameter("keyWord"), SearchType.FLOW.ordinal());
			// 获取供应商类型的待处理流程数
			sumSupplierTypeFlowCount(supplierList, supplierTypeCount);
			result.put("resourceFlowCount", supplierTypeCount.values().stream().mapToLong(Long::longValue).sum());
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * 流程类型查询待处理数
	 */
	private void sumFlowCountByFlowType(String supplierId, String productId, Map<String, Long> yearCountMap, Map<String, Map<String, Long>> countMap,
			List<FlowEntDealCount> countList, int flowType) {
		Stream<FlowEntDealCount> stream = null;
		if (StringUtils.isNotBlank(productId)) {
			stream = countList.stream().filter(count -> StringUtils.equals(count.getProductId(), productId) && count.getFlowType() == flowType);
		} else if (StringUtils.isNotBlank(supplierId)) {
			stream = countList.stream().filter(count -> StringUtils.equals(count.getSupplierId(), supplierId) && count.getFlowType() == flowType);
		} else {
			stream = countList.stream().filter(count -> count.getEntityType() == EntityType.SUPPLIER.ordinal() && count.getFlowType() == flowType);
		}
		stream.forEach(count -> {
			Long lastCount = yearCountMap.get(count.getYear() + "");
			lastCount = lastCount == null ? 0L : lastCount;
			yearCountMap.put(count.getYear() + "", lastCount + count.getFlowEntCount());
			Map<String, Long> monthCount = countMap.get(count.getYear() + "");
			if (monthCount == null) {
				countMap.put(count.getYear() + "", new HashMap<>());
			}
			Long lastMCount = countMap.get(count.getYear() + "").get(count.getMonth() + "");
			lastMCount = lastMCount == null ? 0L : lastMCount;
			countMap.get(count.getYear() + "").put(count.getMonth() + "", lastMCount + count.getFlowEntCount());
		});
	}

	/**
	 * 统计产品的待处理数
	 */
	private void sumProductFlowCount(List<Product> productList, String supplierId, Map<String, Long> productCount, List<FlowEntDealCount> countList) {
		if (!ListUtils.isEmpty(productList)) {
			// 统计数据
			Map<String, IntSummaryStatistics> countMap = null;
			if (countList != null && !countList.isEmpty()) {
				// 根据供应商id过滤，再按产品id分类统计
				countMap = countList.stream().filter(c -> StringUtils.equals(c.getSupplierId(), supplierId))
						.collect(Collectors.groupingBy(FlowEntDealCount::getProductId, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
			}
			for (Product product : productList) {
				long flowCount = 0L;
				if (countMap != null) {
					IntSummaryStatistics statistics = countMap.get(product.getProductId());
					if (statistics != null) {
						flowCount = statistics.getSum();
					}
				}
				productCount.put(product.getProductId(), flowCount);
			}
		}
	}

	/**
	 * 统计供应商的待处理数
	 */
	private void sumSupplierFlowCount(List<SupplierRspDto> supplierList, Map<String, Long> supplierCount) {
		for (SupplierRspDto dto : supplierList) {
			Long flowEntCount = dto.getFlowEntCount();
			if (flowEntCount != null && flowEntCount > 0) {
				supplierCount.put(dto.getSupplierId(), dto.getFlowEntCount());
			}
		}
	}

	/**
	 * 统计供应商类别的待处理数
	 */
	private void sumSupplierTypeFlowCount(List<SupplierRspDto> supplierList, Map<String, Long> supplierTypeCount) {
		List<SupplierTypeRspDto> dtos = supplierTypeService.countSupplierTypes(supplierList);
		for (SupplierTypeRspDto dto : dtos) {
			Long flowEntCount = dto.getFlowEntCount();
			if (flowEntCount != null && flowEntCount > 0) {
				supplierTypeCount.put(dto.getSupplierTypeId(), dto.getFlowEntCount());
			}
		}
	}

}
