package com.dahantc.erp.controller.parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.dahantc.erp.vo.productType.entity.ProductType;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.parameter.ParameterReqDto;
import com.dahantc.erp.enums.ParamType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;

/**
 * 系统参数 action
 *
 * @author 8520
 */
@Controller
@RequestMapping(value = "/parameter")
public class ParameterAction extends BaseAction {

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IProductTypeService productTypeService;

	public static final Logger logger = LoggerFactory.getLogger(ParameterAction.class);

	@RequestMapping("/toParameter")
	public String toParameter() {
		return "/views/parameter/parameterPage";
	}

	/**
	 * 分页查询系统参数信息
	 *
	 * @return 流程分页
	 */
	@ResponseBody
	@RequestMapping(value = "/readPages")
	public BaseResponse<PageResult<Parameter>> readPages(@Valid ParameterReqDto reqDto) {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getOrders().add(new SearchOrder("paramType", Constants.ROP_ASC));
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
		if (StringUtil.isNotBlank(reqDto.getParameterName())) {
			searchFilter.getRules().add(new SearchRule("paramkey", Constants.ROP_CN, reqDto.getParameterName()));
		}
		if (reqDto.getParameterType() != null) {
			searchFilter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, reqDto.getParameterType()));
		}
		try {
			PageResult<Parameter> flows = parameterService.findByPages(reqDto.getPageSize(), reqDto.getCurrentPage(), searchFilter);
			return BaseResponse.success(flows);
		} catch (ServiceException e) {
			logger.error("分页查询系统参数信息错误", e);
		}
		return BaseResponse.error("系统参数查询异常");
	}

	/**
	 * 根据参数类型 加载参数
	 * @param parameterType 类型(对应 @code{@link com.dahantc.erp.enums.ParamType})
	 * @return 参数
	 */
	@ResponseBody
	@RequestMapping(value = "/readParameterByType")
	public BaseResponse<List<Parameter>> readParameterByType(Integer parameterType) {
		if (parameterType == null){
			return BaseResponse.error("请求参数错误");
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
		searchFilter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, parameterType));
		try {
			List<Parameter> flows = parameterService.findAllByCriteria(searchFilter);
			return BaseResponse.success(flows);
		} catch (ServiceException e) {
			logger.error("分页查询系统参数信息错误", e);
		}
		return BaseResponse.error("系统参数查询异常");
	}



	/**
	 * 获取系统参数类型(防止后台增加类型后，还需要改前端)
	 */
	@RequestMapping(value = "/readParameterTypes")
	@ResponseBody
	public BaseResponse<Map<Integer, String>> readParameterTypes() {
		Map<Integer, String> result = new HashMap<>();
		for (ParamType paramType : ParamType.values()) {
			result.put(paramType.ordinal(), paramType.getDesc());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 获取系统参数类型(防止后台增加类型后，还需要改前端)
	 */
	@ResponseBody
	@RequestMapping(value = "/readProductType")
	public BaseResponse<Map<Integer, String>> readProductType() {
		Map<Integer, String> result = new HashMap<>();
		try {
			List<ProductType> productTypeList = productTypeService.queryAllBySearchFilter(null);
			if (!CollectionUtils.isEmpty(productTypeList)) {
				for (ProductType productType : productTypeList) {
					result.put(productType.getProductTypeValue(), productType.getProductTypeName());
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.success(result);
	}

	/**
	 * 转向添加|编辑页面
	 *
	 * @param parameterId
	 *            id
	 * @return page
	 */
	@RequestMapping("/toParameterEdit")
	public String toParameterEdit(@RequestParam(value = "id", required = false) String parameterId) {
		Parameter parameter = null;
		if (StringUtil.isNotBlank(parameterId)) {
			try {
				parameter = parameterService.read(parameterId);
			} catch (ServiceException e) {
				logger.error("查询系统参数错误", e);
			}
		}
		request.setAttribute("parameter", parameter);
		return "/views/parameter/parameterEdit";
	}

	@ResponseBody
	@RequestMapping("/delParameter")
	public BaseResponse<Boolean> delParameter(String parameterId) {
		if (StringUtil.isBlank(parameterId)) {
			return BaseResponse.error("请求参数错误");
		}
		try {
			Boolean result = parameterService.delete(parameterId);
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error("删除系统参数出现异常", e);
		}
		return BaseResponse.error("删除系统参数异常");
	}

	@ResponseBody
	@RequestMapping("/addOrEdit")
	public BaseResponse<Boolean> addOrEditParameter(Parameter parameter) {
		if (StringUtil.isBlank(parameter.getDepict())) {
			return BaseResponse.error("参数描述不可为空");
		}
		if (StringUtil.isBlank(parameter.getParamvalue())) {
			return BaseResponse.error("参数值不可为空");
		}
		if (StringUtil.isBlank(parameter.getParamkey())) {
			return BaseResponse.error("参数名不可为空");
		}
		if (parameter.getParamType() == null) {
			return BaseResponse.error("参数类型不可为空");
		}
		String msgType = "";
		try {
			boolean result;
			if (StringUtil.isBlank(parameter.getEntityid())) {
				msgType = "添加";
				result = parameterService.save(parameter);
			} else {
				msgType = "编辑";
				result = parameterService.update(parameter);
			}
			return BaseResponse.success(result);
		} catch (ServiceException e) {
			logger.error(msgType + "系统参数异常", e);
		}
		return BaseResponse.error(msgType + "系统参数失败");
	}
	
}