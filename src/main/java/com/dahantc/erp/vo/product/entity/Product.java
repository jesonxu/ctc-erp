package com.dahantc.erp.vo.product.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.CurrencyType;
import com.dahantc.erp.enums.SettleType;

/**
 * 产品
 * 
 */
@Entity
@Table(name = "erp_product")
public class Product implements Serializable {

	private static final long serialVersionUID = 124364485978138983L;

	/**
	 * 产品id
	 */
	@Id
	@Column(length = 32, name = "productid")
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String productId;

	/**
	 * 供应商id
	 */
	@Column(length = 32, name = "supplierid")
	private String supplierId;

	/**
	 * 产品名称
	 */
	@Column(length = 255, name = "productname")
	private String productName;

	/**
	 * 产品类型
	 */
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	/**
	 * 通道参数
	 */
	@Column(length = 2000, name = "productparam")
	private String productParam;

	/**
	 * 产品标识
	 */
	@Column(length = 255, name = "productmark")
	private String productMark;

	/**
	 * 计费单位
	 */
	@Column(name = "voiceunit", columnDefinition = "int default 0")
	private int voiceUnit;

	/**
	 * 套餐最低消费金额
	 */
	private double lowdissipation;

	/**
	 * 套餐最低消费条数
	 */
	private long unitvalue;
	
	/**
	 * 结算币种
	 */
	@Column(name = "currencytype", columnDefinition = "int default 0")
	private int currencyType = CurrencyType.CNR.getCode();

	/** 可达省份，32位2进制的10进制值，每一位代表一个省。 */
	@Column(name = "reachprovince", columnDefinition = "int default 2147483647")
	private int reachProvince = 2147483647;

	/** 落地省份，32位2进制的10进制值，每一位代表一个省。 */
	@Column(name = "baseprovince", columnDefinition = "int default 0")
	private int baseProvince = 0;
	
	/**
	 * 结算方式
	 */
	@Column(name = "settletype", columnDefinition = "int default 0")
	private int settleType = SettleType.Prepurchase.ordinal();

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/** 是否直连产品 */
	@Column(name = "directconnect", columnDefinition = "tinyint(1) default 0")
	private Boolean directConnect = false;

	public Product(String productId, String supplierId, String productName, int productType, String productParam, String productMark, double lowdissipation,
			int unitvalue, int reachProvince, int baseProvince, int settleType, String ossUserId, Timestamp wtime) {
		super();
		this.productId = productId;
		this.supplierId = supplierId;
		this.productName = productName;
		this.productType = productType;
		this.productParam = productParam;
		this.productMark = productMark;
		this.lowdissipation = lowdissipation;
		this.unitvalue = unitvalue;
		this.reachProvince = reachProvince;
		this.baseProvince = baseProvince;
		this.settleType = settleType;
		this.ossUserId = ossUserId;
		this.wtime = wtime;
	}

	public Product() {

	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getProductParam() {
		return productParam;
	}

	public void setProductParam(String productParam) {
		this.productParam = productParam;
	}

	public String getProductMark() {
		return productMark;
	}

	public void setProductMark(String productMark) {
		this.productMark = productMark;
	}

	public int getVoiceUnit() {
		return voiceUnit;
	}

	public void setVoiceUnit(int voiceUnit) {
		this.voiceUnit = voiceUnit;
	}

	public double getLowdissipation() {
		return lowdissipation;
	}

	public void setLowdissipation(double lowdissipation) {
		this.lowdissipation = lowdissipation;
	}

	public long getUnitvalue() {
		return unitvalue;
	}

	public void setUnitvalue(long unitvalue) {
		this.unitvalue = unitvalue;
	}

	public int getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(int currencyType) {
		this.currencyType = currencyType;
	}

	public int getReachProvince() {
		return reachProvince;
	}

	public void setReachProvince(int reachProvince) {
		this.reachProvince = reachProvince;
	}

	public int getBaseProvince() {
		return baseProvince;
	}

	public void setBaseProvince(int baseProvince) {
		this.baseProvince = baseProvince;
	}

	public int getSettleType() {
		return settleType;
	}

	public void setSettleType(int settleType) {
		this.settleType = settleType;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public Boolean getDirectConnect() {
		return directConnect;
	}

	public void setDirectConnect(Boolean directConnect) {
		this.directConnect = directConnect;
	}
}
