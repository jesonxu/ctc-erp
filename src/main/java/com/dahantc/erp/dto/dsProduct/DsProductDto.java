package com.dahantc.erp.dto.dsProduct;

import java.math.BigDecimal;
import java.util.Date;

public class DsProductDto {

	private String dsproductid;

	private String supplierid;

	private String producttype;

	private String productname;

	private String format;

	private String pcode;

	private BigDecimal groupprice;

	private int groupnumber;
	
	private int rant;
	
	private BigDecimal standardprice;
	 
	private BigDecimal wholesaleprice;
	
	private String picture;
	
	private int period;
	
	private String remark;
	
	private String ossuserid;
	
	private Date wtime;
	
	private String suppliername;
	
	private int onsale;
	
	private int stock;

	public String getDsproductid() {
		return dsproductid;
	}

	public void setDsproductid(String dsproductid) {
		this.dsproductid = dsproductid;
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

	public BigDecimal getGroupprice() {
		return groupprice;
	}

	public void setGroupprice(BigDecimal groupprice) {
		this.groupprice = groupprice;
	}

	public int getGroupnumber() {
		return groupnumber;
	}

	public void setGroupnumber(int groupnumber) {
		this.groupnumber = groupnumber;
	}

	public int getRant() {
		return rant;
	}

	public void setRant(int rant) {
		this.rant = rant;
	}

	public BigDecimal getStandardprice() {
		return standardprice;
	}

	public void setStandardprice(BigDecimal standardprice) {
		this.standardprice = standardprice;
	}

	public BigDecimal getWholesaleprice() {
		return wholesaleprice;
	}

	public void setWholesaleprice(BigDecimal wholesaleprice) {
		this.wholesaleprice = wholesaleprice;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
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

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public int getOnsale() {
		return onsale;
	}

	public void setOnsale(int onsale) {
		this.onsale = onsale;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return "DsProductDto [dsproductid=" + dsproductid + ", supplierid=" + supplierid + ", producttype="
				+ producttype + ", productname=" + productname + ", format=" + format + ", pcode=" + pcode
				+ ", groupprice=" + groupprice + ", groupnumber=" + groupnumber + ", rant=" + rant + ", standardprice="
				+ standardprice + ", wholesaleprice=" + wholesaleprice + ", picture=" + picture + ", period=" + period
				+ ", remark=" + remark + ", ossuserid=" + ossuserid + ", wtime=" + wtime + ", suppliername="
				+ suppliername + ", onsale=" + onsale + ", stock=" + stock + "]";
	}
	
}
