package com.dahantc.erp.vo.supplierStatistics.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BusinessType;

@Entity
@Table(name = "erp_supplier_statistics")
@DynamicUpdate(true)
public class SupplierStatistics implements Serializable {

	private static final long serialVersionUID = -5863727538345122642L;
	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	// 所属商务id
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 供应商id
	@Column(name = "supplierid", length = 32)
	private String supplierId;

	// 供应商类型id
	@Column(name = "suppliertypeid", length = 32)
	private String supplierTypeId;

	// 产品id
	@Column(name = "productid", length = 32)
	private String productId;

	// 产品标识，即短信云的channelId
	@Column(name = "productmark", length = 32)
	private String productMark;

	// 产品类型
	@Column(name = "producttype", columnDefinition = "int default 0")
	private int productType = 0;

	// 商务所属销售的部门
	@Column(name = "deptid", length = 32)
	private String deptId;

	// 统计时间，yyyy
	@Column(name = "statsyear")
	private Date statsYear;

	// 统计时间，yyyy-MM
	@Column(name = "statsyearmonth")
	private Date statsYearMonth;

	// 统计时间，yyyy-MM-dd
	@Column(name = "statsdate")
	private Date statsDate;

	// 发送量
	@Column(name = "totalcount", columnDefinition = "bigint default 0")
	private long totalCount = 0;

	// 成功数
	@Column(name = "successcount", columnDefinition = "bigint default 0")
	private long successCount = 0;

	// 失败数
	@Column(name = "failcount", columnDefinition = "bigint default 0")
	private long failCount = 0;

	// 应付
	@Column(name = "payables")
	private BigDecimal payables = new BigDecimal(0);

	@Column(name = "businesstype")
	private int businessType = BusinessType.YTX.ordinal();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Date getStatsYear() {
		return statsYear;
	}

	public void setStatsYear(Date statsYear) {
		this.statsYear = statsYear;
	}

	public Date getStatsYearMonth() {
		return statsYearMonth;
	}

	public void setStatsYearMonth(Date statsYearMonth) {
		this.statsYearMonth = statsYearMonth;
	}

	public Date getStatsDate() {
		return statsDate;
	}

	public void setStatsDate(Date statsDate) {
		this.statsDate = statsDate;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	public void addSuccessCount(long successCount) {
		setSuccessCount(this.successCount + successCount);
	}

	public void addFailCount(long failCount) {
		setFailCount(this.failCount + failCount);
	}

	public void addTotalCount(long totalCount) {
		setTotalCount(this.totalCount + totalCount);
	}

	public void addPayables(BigDecimal payables) {
			setPayables(this.payables.add(payables));
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(String supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
	}

	public BigDecimal getPayables() {
		return payables;
	}

	public void setPayables(BigDecimal payables) {
		this.payables = payables;
	}

	public String getProductMark() {
		return productMark;
	}

	public void setProductMark(String productMark) {
		this.productMark = productMark;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public void setScale2() {
		setPayables(payables.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
