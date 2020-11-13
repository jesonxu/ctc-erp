package com.dahantc.erp.dto.dsProduct;

import javax.validation.constraints.NotBlank;

public class DsSaveProductDto {
	
	private String productid;

	@NotBlank(message = "供应商id不能为空")
	private String supplierid;

	@NotBlank(message = "产品类型不能为空")
	private String producttype;

	@NotBlank(message = "产品名称不能为空")
	private String productname;

	@NotBlank(message = "产品规格不能为空")
	private String format;

	private String pcode;

	@NotBlank(message = "团购价格不能为空")
	private String groupprice;

	private int groupnumber;
	
	@NotBlank(message = "税率不能为空")
	private String rant;
	
	@NotBlank(message = "产品标准价不能为空")
	private String standardprice;
	
	private String wholesaleprice;
	
	private String picture;
	
	private String period;
	
	private String remark;
	
	private int onsale;
	
	private String ossuserid;

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(String supplierid) {
		this.supplierid = supplierid;
	}

	public String getProducttype() {
		return producttype;
	}

	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}

	public String getProductname() {
		return productname;
	}

	public int getOnsale() {
		return onsale;
	}

	public void setOnsale(int onsale) {
		this.onsale = onsale;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getGroupprice() {
		return groupprice;
	}

	public void setGroupprice(String groupprice) {
		this.groupprice = groupprice;
	}

	public int getGroupnumber() {
		return groupnumber;
	}

	public void setGroupnumber(int groupnumber) {
		this.groupnumber = groupnumber;
	}

	public String getRant() {
		return rant;
	}

	public void setRant(String rant) {
		this.rant = rant;
	}

	public String getStandardprice() {
		return standardprice;
	}

	public void setStandardprice(String standardprice) {
		this.standardprice = standardprice;
	}

	public String getWholesaleprice() {
		return wholesaleprice;
	}

	public void setWholesaleprice(String wholesaleprice) {
		this.wholesaleprice = wholesaleprice;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOssuserid() {
		return ossuserid;
	}

	public void setOssuserid(String ossuserid) {
		this.ossuserid = ossuserid;
	}

	@Override
	public String toString() {
		return "DsSaveProductDto [supplierid=" + supplierid + ", producttype=" + producttype + ", productname="
				+ productname + ", format=" + format + ", pcode=" + pcode + ", groupprice=" + groupprice
				+ ", groupnumber=" + groupnumber + ", rant=" + rant + ", standardprice=" + standardprice
				+ ", wholesaleprice=" + wholesaleprice + ", picture=" + picture + ", period=" + period + ", remark="
				+ remark + ", ossuserid=" + ossuserid + "]";
	}
	
}
