package com.dahantc.erp.controller.dsDepot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.resourceConsole.ResourceConsoleAction;
import com.dahantc.erp.dto.dsDepot.DsDepotDto;
import com.dahantc.erp.dto.dsDepot.DsDepotItemDto;
import com.dahantc.erp.dto.dsDepot.DsQueryDepotDto;
import com.dahantc.erp.dto.dsDepot.DsSaveDepotDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dsDepotHead.entity.DsDepotHead;
import com.dahantc.erp.vo.dsDepotHead.service.IDsDepotHeadService;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;
import com.dahantc.erp.vo.dsDepotItem.service.IDsDepotItemService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping(value = "/dsDepot")
public class DsDepotAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ResourceConsoleAction.class);
	
	@Autowired
	private IDsDepotHeadService dsDepotHeadService;
	
	@Autowired
	private IDsDepotItemService dsDepotItemService;
	
	// 每页显示条数
	private int pageSize = 10;

	// 当前页
	private int currentPage = 1;
	
	@RequestMapping("/toDsDepotDetail")
	public String toDsDepotDetail() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/messageInfo/messageInfoDetail.html";
	}
	
	@RequestMapping("/toAddDsDepotPage")
	public String toAddDsDepotPage() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/stockProductDs/addStockProduct.html";
	}
	
	@RequestMapping("/matchStockProduct")
	public String toMatchStockProductPage() {
		String product = request.getParameter("productName");
		String id = request.getParameter("id");
		request.setAttribute("productName", product);
		request.setAttribute("id", id);
		return "/views/stockProductDs/matchStockProduct.html";
	}
	
	@RequestMapping("/toDsDepotInfoPage")
	public String toDsDepotInfoPage(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			DsSaveDepotDto dsDepotDto = new DsSaveDepotDto();
			String id = request.getParameter("id");
			String isEdit = request.getParameter("isEdit");
			DsDepotHead dsDepotHead = dsDepotHeadService.read(id);
			dsDepotDto.setWtime(dsDepotHead.getWtime().toString());
			dsDepotDto.setCreaterId(dsDepotHead.getCreaterId());
			dsDepotDto.setCreaterName(dsDepotHead.getCreaterName());
			dsDepotDto.setDepotCode(dsDepotHead.getDepotCode());
			dsDepotDto.setDepotCost(dsDepotHead.getDepotCost());
			dsDepotDto.setId(dsDepotHead.getId());
			dsDepotDto.setOtherCost(dsDepotHead.getOtherCost());
			dsDepotDto.setRemark(dsDepotHead.getRemark());
			dsDepotDto.setVerifyStatus(dsDepotHead.getVerifyStatus());
			dsDepotDto.setBuyTime(sdf.format(dsDepotHead.getBuyTime()));
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("depotHeadId", Constants.ROP_EQ, id));
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			List<DsDepotItem> dsDepotItems = dsDepotItemService.queryAllBySearchFilter(filter);
			List<DsDepotItemDto> dsDepotItemDtos = new ArrayList<>();
			for (DsDepotItem dsDepotItem : dsDepotItems) {
				DsDepotItemDto dsDepotItemDto = new DsDepotItemDto();
				BeanUtils.copyProperties(dsDepotItem,dsDepotItemDto);
				if (dsDepotItem.getValidTime()==null) {
					dsDepotItemDto.setValidTime("");
				}else {
					String validTime = sdf.format(dsDepotItem.getValidTime());
					dsDepotItemDto.setValidTime(validTime);
				}
				dsDepotItemDtos.add(dsDepotItemDto);
			}
			String dsDepotItem = JSON.toJSON(dsDepotItemDtos).toString();
			dsDepotDto.setDsDepotItems(dsDepotItem);
			model.addAttribute("DsSaveDepotDto", dsDepotDto);
			model.addAttribute("isEdit", isEdit);
			return "/views/stockProductDs/editStock.html";	
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "/views/stockProductDs/editStock.html";
	}
	
	@RequestMapping("/toPreviewStockPage")
	public String toPreviewStockPage(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			DsSaveDepotDto dsDepotDto = new DsSaveDepotDto();
			String id = request.getParameter("id");
			String isEdit = request.getParameter("isEdit");
			DsDepotHead dsDepotHead = dsDepotHeadService.read(id);
			dsDepotDto.setWtime(dsDepotHead.getWtime().toString());
			dsDepotDto.setCreaterId(dsDepotHead.getCreaterId());
			dsDepotDto.setCreaterName(dsDepotHead.getCreaterName());
			dsDepotDto.setDepotCode(dsDepotHead.getDepotCode());
			dsDepotDto.setDepotCost(dsDepotHead.getDepotCost());
			dsDepotDto.setId(dsDepotHead.getId());
			dsDepotDto.setOtherCost(dsDepotHead.getOtherCost());
			dsDepotDto.setRemark(dsDepotHead.getRemark());
			dsDepotDto.setVerifyStatus(dsDepotHead.getVerifyStatus());
			dsDepotDto.setBuyTime(sdf.format(dsDepotHead.getBuyTime()));
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("depotHeadId", Constants.ROP_EQ, id));
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			List<DsDepotItem> dsDepotItems = dsDepotItemService.queryAllBySearchFilter(filter);
			List<DsDepotItemDto> dsDepotItemDtos = new ArrayList<>();
			for (DsDepotItem dsDepotItem : dsDepotItems) {
				DsDepotItemDto dsdepotItemDto = new DsDepotItemDto();
				BeanUtils.copyProperties(dsDepotItem,dsdepotItemDto);
				if (dsDepotItem.getValidTime()==null) {
					dsdepotItemDto.setValidTime("");
				}else {
					String validTime = sdf.format(dsDepotItem.getValidTime());
					dsdepotItemDto.setValidTime(validTime);
				}
				dsDepotItemDtos.add(dsdepotItemDto);
			}
			String dsDepotItem = JSON.toJSON(dsDepotItemDtos).toString();
			dsDepotDto.setDsDepotItems(dsDepotItem);
			model.addAttribute("DsSaveDepotDto", dsDepotDto);
			model.addAttribute("isEdit", isEdit);
			return "/views/stockProductDs/previewStock.html";	
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "/views/stockProductDs/previewStock.html";
	}
	
	/**
	 * 查询电商入库记录
	 */
	@PostMapping("/queryDsDepot")
	@ResponseBody
	public BaseResponse<PageResult<DsDepotDto>> queryProducts(@Valid DsQueryDepotDto dto) {
		PageResult<DsDepotHead> dsDepotHeads = null;
		List<DsDepotDto> DsDepotDtos = new ArrayList<DsDepotDto>();
		PageResult<DsDepotDto> pageResult = new PageResult<>();
		pageSize = Integer.parseInt(dto.getLimit());
		currentPage = Integer.parseInt(dto.getPage());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.format1);
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtil.isNotBlank(dto.getDepotCode())) {
				filter.getRules().add(new SearchRule("depotCode", Constants.ROP_EQ, dto.getDepotCode()));
			}
			if (StringUtil.isNotBlank(dto.getSupplierId())) {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, dto.getSupplierId()));
			}
			if (StringUtil.isNotBlank(dto.getVerifyStatus())) {
				filter.getRules().add(new SearchRule("verifyStatus", Constants.ROP_EQ, Integer.parseInt(dto.getVerifyStatus())));
			}
			if (StringUtil.isNotBlank(dto.getProductName())) {
				filter.getRules().add(new SearchRule("productName", Constants.ROP_CN, dto.getProductName()));
			}
			if (StringUtil.isNotBlank(dto.getCreateName())) {
				filter.getRules().add(new SearchRule("createName", Constants.ROP_CN, dto.getCreateName()));
			}
			if (StringUtil.isNotBlank(dto.getStartTime())) {
				Date date = DateUtil.getDateStartDateTime(simpleDateFormat.parse(dto.getStartTime()));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, date));
			}
			if (StringUtil.isNotBlank(dto.getEndTime())) {
				Date date = DateUtil.getDateEndDateTime(simpleDateFormat.parse(dto.getStartTime()));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, date));
			}
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			dsDepotHeads = dsDepotHeadService.queryByPages( pageSize, currentPage, filter);
			List<DsDepotHead> dsDepotHeadList = dsDepotHeads.getData();
			for (DsDepotHead dsDepotHead : dsDepotHeadList) {
				DsDepotDto dsDepotDto = new DsDepotDto();
				SearchFilter dsDepotItemFilter = new SearchFilter();
				dsDepotItemFilter.getRules().add(new SearchRule("depotHeadId", Constants.ROP_EQ, dsDepotHead.getId()));
				List<DsDepotItem> dsDepotItems = dsDepotItemService.queryAllBySearchFilter(dsDepotItemFilter);
				if (!CollectionUtils.isEmpty(dsDepotItems)) {
					BeanUtils.copyProperties(dsDepotHead,dsDepotDto);
					String productName = "";
					for (DsDepotItem dsDepotItem : dsDepotItems) {
						productName = productName + dsDepotItem.getProductName() + ",";
					}
					String Date = simpleDateFormat.format(dsDepotHead.getBuyTime());
					dsDepotDto.setBuyTime(Date);
					dsDepotDto.setDepotCost(dsDepotHead.getDepotCost());
					productName = productName.substring(0, productName.length() -1);
					dsDepotDto.setProductName(productName);
					DsDepotDtos.add(dsDepotDto);
				}
			}
			pageResult.setData(DsDepotDtos);
			pageResult.setCode(dsDepotHeads.getCode());
			pageResult.setCount(dsDepotHeads.getCount());
			pageResult.setMsg(dsDepotHeads.getMsg());
			pageResult.setTotalPages(dsDepotHeads.getTotalPages());
			pageResult.setCurrentPage(dsDepotHeads.getCurrentPage());
			return BaseResponse.success(pageResult);
		} catch (Exception e) {
			logger.error("查询入库信息页面异常：", e);
			return BaseResponse.success(pageResult);
		}
	}
	
	/**
	 * 添加/修改入库记录
	 */
	@PostMapping("/save")
	@ResponseBody
	public BaseResponse<String> save(@Valid DsSaveDepotDto dto) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			return dsDepotHeadService.saveDepotHead(dto, onlineUser);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}
	
	/**
	 * 审核入库记录
	 */
	@PostMapping("/audit")
	@ResponseBody
	public BaseResponse<String> audit(@RequestParam String id) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			return dsDepotHeadService.auditDepotHead(id, onlineUser);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}
	
	/**
	 * 删除入库记录
	 */
	@PostMapping("/delete")
	@ResponseBody
	public BaseResponse<String> delete(@RequestParam String id) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			return dsDepotHeadService.deleteDepotHead(id, onlineUser);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}
	
}
