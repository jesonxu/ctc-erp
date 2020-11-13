package com.dahantc.erp.dto.bill;

import com.dahantc.erp.vo.flowEnt.entity.ProductBillsJSONObject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ProductBillsDto implements Serializable {
	private static final long serialVersionUID = -6834655035685045392L;

	/**
	 * 发票ID
	 */
	@NotBlank(message = "发票ID不能为空")
	private String id;

	/**
	 * 发票开票金额
	 */
	@NotNull(message = "发票开票金额不能为空")
	private BigDecimal receivables;

	/**
	 * 账单信息
	 */
	@Valid
	@NotEmpty(message = "账单信息不能为空")
	private List<ProductBillsJSONObject> productBillsJSONObjectList;

	private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public List<ProductBillsJSONObject> getProductBillsJSONObjectList() {
		return productBillsJSONObjectList;
	}

	public void setProductBillsJSONObjectList(List<ProductBillsJSONObject> productBillsJSONObjectList) {
		this.productBillsJSONObjectList = productBillsJSONObjectList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
