package com.dahantc.erp.controller.dsOutDepot;

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
import com.dahantc.erp.dto.dsDepot.DsDepotItemDto;
import com.dahantc.erp.dto.dsDepot.DsQueryDepotDto;
import com.dahantc.erp.dto.dsOutDepot.DsOutDepotDetialDto;
import com.dahantc.erp.dto.dsOutDepot.DsOutDepotDto;
import com.dahantc.erp.dto.dsOutDepot.DsQueryOutDepotDto;
import com.dahantc.erp.dto.dsOutDepot.DsSaveOutDepotDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;
import com.dahantc.erp.vo.dsDepotItem.service.IDsDepotItemService;
import com.dahantc.erp.vo.dsOutDepot.entity.DsOutDepot;
import com.dahantc.erp.vo.dsOutDepot.service.IDsOutDepotService;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;
import com.dahantc.erp.vo.dsOutDepotDetail.service.IDsOutDepotDetailService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping(value = "/dsOutDepot")
public class DsOutDepotAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ResourceConsoleAction.class);
	
	@Autowired
	private IDsOutDepotService dsOutDepotService;
	
	@Autowired
	private IDsOutDepotDetailService dsOutDepotDetialService;
	
	@Autowired
	private IDsDepotItemService dsDepotItemService;
	
	// 每页显示条数
	private int pageSize = 10;

	// 当前页
	private int currentPage = 1;
	
	@RequestMapping("/toAddStockOutProductPage")
	public String toAddStockOutProductPage() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/stockOutProductDs/addStockOutProduct.html";
	}
	
	@RequestMapping("/matchOutDepotProduct")
	public String toMatchStockProductPage() {
		String product = request.getParameter("productName");
		String id = request.getParameter("id");
		request.setAttribute("productName", product);
		request.setAttribute("id", id);
		return "/views/stockProductDs/matchStockProduct.html";
	}
	
	@RequestMapping("/toDsOutDepotInfoPage")
	public String toDsDepotInfoPage(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			DsSaveOutDepotDto dsSaveOutDepotDto = new DsSaveOutDepotDto();
			String id = request.getParameter("id");
			String isEdit = request.getParameter("isEdit");
			DsOutDepot dsOutDepot = dsOutDepotService.read(id);
			dsSaveOutDepotDto.setWtime(dsOutDepot.getWtime().toString());
			dsSaveOutDepotDto.setCreaterId(dsOutDepot.getCreaterId());
			dsSaveOutDepotDto.setCreaterName(dsOutDepot.getCreaterName());
			dsSaveOutDepotDto.setOutDepotCode(dsOutDepot.getOutDepotCode());
			dsSaveOutDepotDto.setOutDepotTotal(dsOutDepot.getOutDepotTotal());
			dsSaveOutDepotDto.setCustomerId(dsOutDepot.getCustomerId());
			dsSaveOutDepotDto.setCustomerName(dsOutDepot.getCustomerName());
			dsSaveOutDepotDto.setUserId(dsOutDepot.getOutDepotPersonId());
			dsSaveOutDepotDto.setUserName(dsOutDepot.getOutDepotPersonName());
			dsSaveOutDepotDto.setId(dsOutDepot.getId());
			dsSaveOutDepotDto.setOtherCost(dsOutDepot.getOtherCost());
			dsSaveOutDepotDto.setRemark(dsOutDepot.getRemark());
			dsSaveOutDepotDto.setVerifyStatus(dsOutDepot.getVerifyStatus());
			dsSaveOutDepotDto.setOutTime(sdf.format(dsOutDepot.getOutTime()));
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("outDepotId", Constants.ROP_EQ, id));
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			List<DsOutDepotDetail> dsOutDepotDetials = dsOutDepotDetialService.queryAllBySearchFilter(filter);
			List<DsOutDepotDetialDto> dsOutDepotDetialDtos = new ArrayList<>();
			for (DsOutDepotDetail dsOutDepotDetial : dsOutDepotDetials) {
				DsOutDepotDetialDto dsOutDepotDetialDto = new DsOutDepotDetialDto();
				BeanUtils.copyProperties(dsOutDepotDetial,dsOutDepotDetialDto);
				if (dsOutDepotDetial.getValidTime()==null) {
					dsOutDepotDetialDto.setValidTime("");
				}else {
					String validTime = sdf.format(dsOutDepotDetial.getValidTime());
					dsOutDepotDetialDto.setValidTime(validTime);
				}
				dsOutDepotDetialDtos.add(dsOutDepotDetialDto);
			}
			String dsOutDepotDetial = JSON.toJSON(dsOutDepotDetialDtos).toString();
			dsSaveOutDepotDto.setDsOutDepotDetials(dsOutDepotDetial);
			model.addAttribute("dsOutDepotDto", dsSaveOutDepotDto);
			model.addAttribute("isEdit", isEdit);
			return "/views/stockOutProductDs/editStockOut.html";	
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "/views/stockOutProductDs/editStockOut.html";
	}
	
	@RequestMapping("/toMatchStockOutProductPage")
	public String toMatchStockOutProductPage(Model model) {
		String productName = request.getParameter("productName");
		String id = request.getParameter("id");
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		model.addAttribute("productName", productName);
		model.addAttribute("id", id);
		return "/views/stockOutProductDs/matchStockOutProduct.html";
	}
	
	@RequestMapping("/queryStockOutProductInfo")
	@ResponseBody
	public BaseResponse<PageResult<DsDepotItemDto>> QueryStockOutProductInfo(@Valid DsQueryDepotDto dto) {
		PageResult<DsDepotItem> dsDepotItemResult = new PageResult<>();
		PageResult<DsDepotItemDto> pageResult = new PageResult<>();
		pageSize = Integer.parseInt(dto.getLimit());
		currentPage = Integer.parseInt(dto.getPage());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtil.isNotBlank(dto.getDepotCode())) {
				filter.getRules().add(new SearchRule("depotCode", Constants.ROP_EQ, dto.getDepotCode()));
			}
			if (StringUtil.isNotBlank(dto.getSupplierId())) {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, dto.getSupplierId()));
			}
			if (StringUtil.isNotBlank(dto.getProductName())) {
				filter.getRules().add(new SearchRule("productName", Constants.ROP_CN, dto.getProductName()));
			}
			if (StringUtil.isNotBlank(dto.getCreateName())) {
				filter.getRules().add(new SearchRule("createrName", Constants.ROP_EQ, dto.getCreateName()));
			}
			if (StringUtil.isNotBlank(dto.getStartTime())) {
				Date date = DateUtil.getDateStartDateTime(sdf.parse(dto.getStartTime()));
				filter.getRules().add(new SearchRule("validTime", Constants.ROP_GE, date));
			}
			if (StringUtil.isNotBlank(dto.getEndTime())) {
				Date date = DateUtil.getDateEndDateTime(sdf.parse(dto.getStartTime()));
				filter.getRules().add(new SearchRule("validTime", Constants.ROP_LE, date));
			}
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			filter.getOrders().add(new SearchOrder("validTime", Constants.ROP_DESC));
			dsDepotItemResult = dsDepotItemService.queryByPages(pageSize, currentPage, filter);
			List<DsDepotItem> dsDepotItems = dsDepotItemResult.getData();
			List<DsDepotItemDto> dsDepotItemDtos = new ArrayList<>();
			for (DsDepotItem dsDepotItem : dsDepotItems) {
				DsDepotItemDto dsDepotItemDto = new DsDepotItemDto();
				BeanUtils.copyProperties(dsDepotItem,dsDepotItemDto);
				SearchFilter depotFilter = new SearchFilter();
				depotFilter.getRules().add(new SearchRule("depotItemId", Constants.ROP_EQ, dsDepotItem.getId()));
				depotFilter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
				Integer amount = dsDepotItem.getAmount();
				List<DsOutDepotDetail> dsOutDepotDetails = dsOutDepotDetialService.queryAllBySearchFilter(depotFilter);
				for (DsOutDepotDetail dsOutDepotDetail : dsOutDepotDetails) {
					amount = amount - dsOutDepotDetail.getAmount();
				}
				dsDepotItemDto.setAmount(amount);
				if (dsDepotItem.getValidTime()==null) {
					dsDepotItemDto.setValidTime("");
				}else {
					String validTime = sdf.format(dsDepotItem.getValidTime());
					dsDepotItemDto.setValidTime(validTime);
				}
				dsDepotItemDtos.add(dsDepotItemDto);
			}
			pageResult.setData(dsDepotItemDtos);
			pageResult.setCode(dsDepotItemResult.getCode());
			pageResult.setCount(dsDepotItemResult.getCount());
			pageResult.setMsg(dsDepotItemResult.getMsg());
			pageResult.setTotalPages(dsDepotItemResult.getTotalPages());
			pageResult.setCurrentPage(dsDepotItemResult.getCurrentPage());
		} catch (Exception e) {
			logger.error("跳转库存详细信息页面异常：", e);
			return BaseResponse.success(pageResult);
		}
		return BaseResponse.success(pageResult);
	}
	
	@RequestMapping("/toPreviewStockOutPage")
	public String toPreviewStockOutPage(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			DsSaveOutDepotDto dsOutDepotDto = new DsSaveOutDepotDto();
			String id = request.getParameter("id");
			String isEdit = request.getParameter("isEdit");
			DsOutDepot dsOutDepot = dsOutDepotService.read(id);
			dsOutDepotDto.setWtime(dsOutDepot.getWtime().toString());
			dsOutDepotDto.setCustomerId(dsOutDepot.getCustomerId());
			dsOutDepotDto.setCustomerName(dsOutDepot.getCustomerName());
			dsOutDepotDto.setUserId(dsOutDepot.getOutDepotPersonId());
			dsOutDepotDto.setUserName(dsOutDepot.getOutDepotPersonName());
			dsOutDepotDto.setCreaterId(dsOutDepot.getCreaterId());
			dsOutDepotDto.setCreaterName(dsOutDepot.getCreaterName());
			dsOutDepotDto.setOutDepotCode(dsOutDepot.getOutDepotCode());
			dsOutDepotDto.setOutDepotTotal(dsOutDepot.getOutDepotTotal());
			dsOutDepotDto.setId(dsOutDepot.getId());
			dsOutDepotDto.setOtherCost(dsOutDepot.getOtherCost());
			dsOutDepotDto.setRemark(dsOutDepot.getRemark());
			dsOutDepotDto.setVerifyStatus(dsOutDepot.getVerifyStatus());
			dsOutDepotDto.setOutTime(sdf.format(dsOutDepot.getOutTime()));
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("outDepotId", Constants.ROP_EQ, id));
			filter.getRules().add(new SearchRule("isDelete", Constants.ROP_EQ, 1));
			List<DsOutDepotDetail> dsOutDepotDetials = dsOutDepotDetialService.queryAllBySearchFilter(filter);
			List<DsOutDepotDetialDto> dsOutDepotDetialDtos = new ArrayList<>();
			for (DsOutDepotDetail dsOutDepotDetial : dsOutDepotDetials) {
				DsOutDepotDetialDto dsOutDepotDetialDto = new DsOutDepotDetialDto();
				BeanUtils.copyProperties(dsOutDepotDetial,dsOutDepotDetialDto);
				if (dsOutDepotDetial.getValidTime()==null) {
					dsOutDepotDetialDto.setValidTime("");
				}else {
					String validTime = sdf.format(dsOutDepotDetial.getValidTime());
					dsOutDepotDetialDto.setValidTime(validTime);
				}
				dsOutDepotDetialDtos.add(dsOutDepotDetialDto);
			}
			String dsOutDepotDetial = JSON.toJSON(dsOutDepotDetialDtos).toString();
			dsOutDepotDto.setDsOutDepotDetials(dsOutDepotDetial);
			model.addAttribute("dsOutDepotDto", dsOutDepotDto);
			model.addAttribute("isEdit", isEdit);
			return "/views/stockOutProductDs/previewStockOut.html";	
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "/views/stockOutProductDs/previewStockOut.html";
	}
	
	/**
	 * 查询电商出库记录
	 */
	@PostMapping("/queryDsOutDepot")
	@ResponseBody
	public BaseResponse<PageResult<DsOutDepotDto>> queryProducts(@Valid DsQueryOutDepotDto dto) {
		PageResult<DsOutDepot> dsOutDepots = null;
		List<DsOutDepotDto> DsOutDepotDtos = new ArrayList<DsOutDepotDto>();
		PageResult<DsOutDepotDto> pageResult = new PageResult<>();
		pageSize = Integer.parseInt(dto.getLimit());
		currentPage = Integer.parseInt(dto.getPage());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.format1);
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtil.isNotBlank(dto.getOutDepotCode())) {
				filter.getRules().add(new SearchRule("outDepotCode", Constants.ROP_EQ, dto.getOutDepotCode()));
			}
			if (StringUtil.isNotBlank(dto.getCustomerId())) {
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, dto.getCustomerId()));
			}
			if (StringUtil.isNotBlank(dto.getUserId())) {
				filter.getRules().add(new SearchRule("outDepotPersonId", Constants.ROP_EQ, dto.getUserId()));
			}
			if (StringUtil.isNotBlank(dto.getVerifyStatus())) {
				filter.getRules().add(new SearchRule("verifyStatus", Constants.ROP_EQ, Integer.parseInt(dto.getVerifyStatus())));
			}
			if (StringUtil.isNotBlank(dto.getProductName())) {
				filter.getRules().add(new SearchRule("productName", Constants.ROP_CN, dto.getProductName()));
			}
			if (StringUtil.isNotBlank(dto.getCreateName())) {
				filter.getRules().add(new SearchRule("createName", Constants.ROP_EQ, dto.getCreateName()));
			}
			if (StringUtil.isNotBlank(dto.getVerifyStatus())) {
				filter.getRules().add(new SearchRule("verifyStatus", Constants.ROP_EQ, Integer.parseInt(dto.getVerifyStatus())));
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
			dsOutDepots = dsOutDepotService.queryByPages( pageSize, currentPage, filter);
			List<DsOutDepot> dsOutDepotList = dsOutDepots.getData();
			for (DsOutDepot dsOutDepot : dsOutDepotList) {
				DsOutDepotDto dsOutDepotDto = new DsOutDepotDto();
				SearchFilter dsDepotItemFilter = new SearchFilter();
				dsDepotItemFilter.getRules().add(new SearchRule("outDepotId", Constants.ROP_EQ, dsOutDepot.getId()));
				List<DsOutDepotDetail> dsOutDepotDetials = dsOutDepotDetialService.queryAllBySearchFilter(dsDepotItemFilter);
				if (!CollectionUtils.isEmpty(dsOutDepotDetials)) {
					BeanUtils.copyProperties(dsOutDepot,dsOutDepotDto);
					String productName = "";
					for (DsOutDepotDetail dsOutDepotDetial : dsOutDepotDetials) {
						productName = productName + dsOutDepotDetial.getProductName() + ",";
					}
					String Date = simpleDateFormat.format(dsOutDepot.getOutTime());
					dsOutDepotDto.setCustomerName(dsOutDepot.getCustomerName());
					dsOutDepotDto.setUserName(dsOutDepot.getOutDepotPersonName());
					dsOutDepotDto.setOutTime(Date);
					dsOutDepotDto.setOutDepotTotal(dsOutDepot.getOutDepotTotal());
					productName = productName.substring(0, productName.length() -1);
					dsOutDepotDto.setProductName(productName);
					DsOutDepotDtos.add(dsOutDepotDto);
				}
			}
			pageResult.setData(DsOutDepotDtos);
			pageResult.setCode(dsOutDepots.getCode());
			pageResult.setCount(dsOutDepots.getCount());
			pageResult.setMsg(dsOutDepots.getMsg());
			pageResult.setTotalPages(dsOutDepots.getTotalPages());
			pageResult.setCurrentPage(dsOutDepots.getCurrentPage());
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
	public BaseResponse<String> save(@Valid DsSaveOutDepotDto dto) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			return dsOutDepotService.saveDsOutDepot(dto, onlineUser);
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
			return dsOutDepotService.auditDsOutDepot(id, onlineUser);
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
			return dsOutDepotService.deleteDsOutDepot(id, onlineUser);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}
	
}
