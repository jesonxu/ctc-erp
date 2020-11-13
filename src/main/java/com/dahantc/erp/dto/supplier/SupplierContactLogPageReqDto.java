package com.dahantc.erp.dto.supplier;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.dahantc.erp.dto.BaseDto;

/**
 * 角色分页请求数据
 * 
 * @author wangyang
 *
 */
public class SupplierContactLogPageReqDto extends BaseDto implements Serializable {

	private static final long serialVersionUID = 4600188973865647999L;

	@NotBlank(message = "供应商id不能为空")
	private String supplierId;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
}
