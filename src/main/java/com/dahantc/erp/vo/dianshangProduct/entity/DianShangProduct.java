package com.dahantc.erp.vo.dianshangProduct.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_dianshang_product")
@DynamicUpdate(true)
public class DianShangProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7131297707217393257L;

	@Id
	@Column(name = "dsproductid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String dsproductid;

	@Column(name = "supplierid", length = 32)
	private String supplierid;

	@Column(name = "producttype", length = 32)
	private String producttype;

	@Column(name = "productname", length = 128)
	private String productname;

	@Column(name = "format", length = 255)
	private String format;

	@Column(name = "pcode", length = 32)
	private String pcode;

	@Column(name = "groupprice", length = 11)
	private BigDecimal groupprice;

	@Column(name = "groupnumber", length = 10)
	private int groupnumber;
	
	@Column(name = "rant", length = 10)
	private int rant;
	
	@Column(name = "standardprice", length = 11)
	private BigDecimal standardprice;
	 
	@Column(name = "wholesaleprice", length = 11)
	private BigDecimal wholesaleprice;
	
	@Column(name = "picture", length = 255)
	private String picture;
	
	@Column(name = "period", length = 3)
	private int period;
	
	@Column(name = "remark", length = 255)
	private String remark;
	
	@Column(name = "ossuserid", length = 255)
	private String ossuserid;
	
	@Column(name = "stock", columnDefinition = "int(11) default 0")
	private int stock;
	
	@Column(name = "onsale", columnDefinition = "int default 1")
	private int onsale;
	
	@Column(name = "wtime", length = 255)
	private Date wtime;

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

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
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

	public int getOnsale() {
		return onsale;
	}

	public void setOnsale(int onsale) {
		this.onsale = onsale;
	}

	@Override
	public String toString() {
		return "DianShangProduct [dsproductid=" + dsproductid + ", supplierid=" + supplierid + ", producttype="
				+ producttype + ", productname=" + productname + ", format=" + format + ", pcode=" + pcode
				+ ", groupprice=" + groupprice + ", groupnumber=" + groupnumber + ", rant=" + rant + ", standardprice="
				+ standardprice + ", wholesaleprice=" + wholesaleprice + ", picture=" + picture + ", period=" + period
				+ ", remark=" + remark + ", ossuserid=" + ossuserid + ", stock=" + stock + ", onsale=" + onsale
				+ ", wtime=" + wtime + "]";
	}
	
}
