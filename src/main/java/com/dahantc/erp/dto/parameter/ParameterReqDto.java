package com.dahantc.erp.dto.parameter;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * 参数分页查询 请求参数
 * 
 * @author 8520
 */
public class ParameterReqDto implements Serializable {
	private static final long serialVersionUID = 2054461789499163249L;

	/**
	 * 页大小
	 */
	@NotNull(message = "分页请求，页大小不能为空")
	private Integer pageSize;

	/**
	 * 当前页
	 */
	@NotNull(message = "分页请求,当前页不能为空")
	private Integer currentPage;

	private String parameterName;

	private Integer parameterType;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public Integer getParameterType() {
		return parameterType;
	}

	public void setParameterType(Integer parameterType) {
		this.parameterType = parameterType;
	}

	@Override
	public String toString() {
		return "ParameterReqDto{" + "pageSize=" + pageSize + ", currentPage=" + currentPage + ", parameterName='" + parameterName + '\'' + ", parameterType='"
				+ parameterType + '\'' + '}';
	}
}
