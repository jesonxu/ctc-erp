package com.dahantc.erp.controller.dsproduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.product.ProductAction;
import com.dahantc.erp.dto.dsProduct.DsProductDto;
import com.dahantc.erp.dto.dsProduct.DsQueryProductDto;
import com.dahantc.erp.dto.dsProduct.DsSaveProductDto;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dianshangProduct.entity.DianShangProduct;
import com.dahantc.erp.vo.dianshangProduct.service.IDianShangProductService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;

/**
 * 
 * @Description: 产品控制
 * 
 */
@Controller
@RequestMapping(value = "/dsProduct")
public class DsProductAction extends BaseAction{
	
	private static final Logger logger = LogManager.getLogger(ProductAction.class);
	
	// 每页显示条数
	private int pageSize = 10;

	// 当前页
	private int currentPage = 1;

	@Autowired
	private IDianShangProductService dsProductService;
	
	@Autowired
	private ISupplierService supplierService;

	/**
	 * 跳转添加页面
	 */
	@RequestMapping("/toAddDsProduct")
	public String toAddProduct(Model model) {
		return "/views/productDs/addProduct";
	}

	/**
	 * 跳转修改页面
	 */
	@RequestMapping("/toEditDsProduct")
	public String toEditProduct(Model model) {
		try {
			String productid = request.getParameter("productid");
			DianShangProduct product = dsProductService.read(productid);
			model.addAttribute("product", product);
			return "/views/productDs/editProduct";
		} catch (Exception e) {
			logger.error("跳转修改产品页面异常：", e);
		}
		return "";
	}
	
	/**
	 * 添加/修改产品
	 */
	@PostMapping("/save")
	@ResponseBody
	public BaseResponse<String> save(@Valid DsSaveProductDto dto) throws Exception {
		try {
			User onlineUser = getOnlineUser();
			if (onlineUser == null) {
				return BaseResponse.error("未登录");
			}
			dto.setOssuserid(onlineUser.getOssUserId());
			return dsProductService.saveProduct(dto);
		} catch (Exception e) {
			logger.error("产品保存异常：", e);
			return BaseResponse.error("保存失败");
		}
	}
	
	/**
	 * 产品信息页面跳转
	 */
	@PostMapping("/toProducts")
	public String toProducts(Model model) {
		return "/views/productDs/productCollapseTemplate";
	}
	
	/**
	 * 查询产品信息
	 */
	@PostMapping("/queryProducts")
	@ResponseBody
	public BaseResponse<PageResult<DsProductDto>> queryProducts(@Valid DsQueryProductDto dto) {
		PageResult<DianShangProduct> dianShangProducts = null;
		List<DsProductDto> dsProductDtos = new ArrayList<DsProductDto>();
		PageResult<DsProductDto> dsPageResult = new PageResult<>();
		pageSize = Integer.parseInt(dto.getLimit());
		currentPage = Integer.parseInt(dto.getPage());
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtil.isNotBlank(dto.getProductname())) {
				filter.getRules().add(new SearchRule("productname", Constants.ROP_CN, dto.getProductname()));
			}
			if (StringUtil.isNotBlank(dto.getSupplierid())) {
				filter.getRules().add(new SearchRule("supplierid", Constants.ROP_EQ, dto.getSupplierid()));
			}
			if (StringUtil.isNotBlank(dto.getRant())&&StringUtils.isNumeric(dto.getRant())) {
				filter.getRules().add(new SearchRule("rant", Constants.ROP_EQ, Integer.valueOf(dto.getRant())));
			}
			if (StringUtil.isNotBlank(dto.getMinprice())&&StringUtils.isNumeric(dto.getMinprice())) {
				BigDecimal minprice =new BigDecimal(dto.getMinprice());
				filter.getRules().add(new SearchRule("standardprice", Constants.ROP_GE, minprice));
			}
			if (StringUtil.isNotBlank(dto.getMaxprice())&&StringUtils.isNumeric(dto.getMaxprice())) {
				BigDecimal maxprice =new BigDecimal(dto.getMaxprice());
				filter.getRules().add(new SearchRule("standardprice", Constants.ROP_LE, maxprice));
			}
			if (StringUtil.isNotBlank(dto.getOnsale())) {
				int onsale = Integer.parseInt(dto.getOnsale());
				filter.getRules().add(new SearchRule("onsale", Constants.ROP_EQ, onsale));
			}
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			dianShangProducts = dsProductService.queryByPages( pageSize, currentPage, filter);
			List<DianShangProduct> dianShangProduct = dianShangProducts.getData();
			for (DianShangProduct dsProduct : dianShangProduct) {
				DsProductDto dsProductDto = new DsProductDto();
				Supplier supplier = supplierService.read(dsProduct.getSupplierid());
				if (supplier != null) {
					BeanUtils.copyProperties(dsProduct,dsProductDto);
					dsProductDto.setSuppliername(supplier.getCompanyName());
					dsProductDtos.add(dsProductDto);
				}
			}
			dsPageResult.setData(dsProductDtos);
			dsPageResult.setCode(dianShangProducts.getCode());
			dsPageResult.setCount(dianShangProducts.getCount());
			dsPageResult.setMsg(dianShangProducts.getMsg());
			dsPageResult.setTotalPages(dianShangProducts.getTotalPages());
			dsPageResult.setCurrentPage(dianShangProducts.getCurrentPage());
			return BaseResponse.success(dsPageResult);
		} catch (ServiceException e) {
			return BaseResponse.success(dsPageResult);
		}
	}
			
}
