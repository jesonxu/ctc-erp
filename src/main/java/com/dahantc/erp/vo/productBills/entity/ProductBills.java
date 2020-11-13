package com.dahantc.erp.vo.productBills.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.NeedAuto;
import com.dahantc.erp.util.DateUtil;

/**
 * 产品账单表
 * 
 */
@Entity
@Table(name = "erp_bill", indexes = { @Index(name = "bill_wtime", columnList = "wtime") })
public class ProductBills implements Serializable {

	private static final long serialVersionUID = 4861317227871404400L;

	/**
	 * id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "entityid", length = 32)
	private String entityId;

	@Column(name = "productid", length = 32)
	private String productId;

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/**
	 * 供应商成功数
	 */
	@Column(name = "suppliercount", columnDefinition = "bigint default 0")
	private long supplierCount = 0;

	/**
	 * 平台成功数
	 */
	@Column(name = "platformcount", columnDefinition = "bigint default 0")
	private long platformCount = 0;
	/**
	 * 应收金额
	 */
	@Column(name = "receivables")
	private BigDecimal receivables = new BigDecimal(0);

	/**
	 * 实收金额
	 */
	@Column(name = "actualreceivables")
	private BigDecimal actualReceivables = new BigDecimal(0);

	/**
	 * 应付金额
	 */
	@Column(name = "payables")
	private BigDecimal payables = new BigDecimal(0);

	/**
	 * 实付金额
	 */
	@Column(name = "actualpayables")
	private BigDecimal actualPayables = new BigDecimal(0);

	@Column(name = "entitytype")
	private int entityType = EntityType.SUPPLIER.ordinal();

	/**
	 * 已开发票金额
	 */
	@Column(name = "actualinvoiceamount")
	private BigDecimal actualInvoiceAmount = new BigDecimal(0);

	/**
	 * 成本
	 */
	@Column(name = "cost")
	private BigDecimal cost = new BigDecimal(0);

	/**
	 * 平均销售单价
	 */
	@Column(name = "unitprice", columnDefinition = "decimal(19,6) default 0 comment '平均销售单价'", precision = 19, scale = 6)
	private BigDecimal unitPrice = new BigDecimal(0);

	/**
	 * 毛利润
	 */
	@Column(name = "grossprofit")
	private BigDecimal grossProfit = new BigDecimal(0);

	/**
	 * 财务收支表ID {@value JSONArray} {@link FsExpenseincomeInfo}
	 */
	@Column(name = "relatedinfo", columnDefinition = "TEXT")
	private String relatedInfo;

	/**
	 * 付款截至日期
	 */
	@Column(name = "finalpaytime")
	private Timestamp finalPayTime;

	/** 收款截止日期 **/
	@Column(name = "finalreceivetime")
	private Timestamp finalReceiveTime;

	/** 部门ID */
	@Column(name = "deptid", length = 32)
	private String deptId;

	@Column(name = "billstatus", columnDefinition = "int default 0")
	private Integer billStatus = BillStatus.NO_RECONCILE.ordinal();

	/** 销账流程归档时间 */
	@Column(name = "writeofftime")
	private Timestamp writeOffTime;

	@Column(name = "needauto", columnDefinition = "int default 0")
	private int needAuto = NeedAuto.TRUE.ordinal();

	/** 账单编号 */
	@Column(name = "billnumber")
	private String billNumber;

	/** 产品账号，考虑到产品出账单时是一批账号，后来又有账号从产品移除了，在重新计算账单页面时，可以对比账单的账号和产品当前账号的差别 */
	@Column(name = "loginname", length = 1000)
	private String loginName;

	/**
	 * 固定成本信息: {@value JSONObject}, {@link OperateCostDetail}
	 */
	@Column(name = "fixedcostinfo")
	private String fixedCostInfo;

	/**
	 * 账单附件json（excel, pdf）
	 */
	@Column(name = "files", length = 1000)
	private String files;

	/** 对应的对账流程的id */
	@Column(name = "flowentid", length = 32)
	private String flowEntId;

	// 备注
	@Column(name = "remark", length = 1000)
	private String remark;

	// 修改记录
	@Column(name = "operationlog", length = 2000)
	private String operationLog;

	public String getFixedCostInfo() {
		return fixedCostInfo;
	}

	public void setFixedCostInfo(String fixedCostInfo) {
		this.fixedCostInfo = fixedCostInfo;
	}

	public OperateCostDetail obtainOperateCost() {
		String fixedCostInfo = getFixedCostInfo();
		if (StringUtils.isBlank(fixedCostInfo)) {
			return null;
		} else {
			return JSON.parseObject(fixedCostInfo, OperateCostDetail.class);
		}
	}

	public String getId() {
		return id;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getProductId() {
		return productId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public BigDecimal getActualReceivables() {
		return actualReceivables;
	}

	public BigDecimal getPayables() {
		return payables;
	}

	public BigDecimal getActualPayables() {
		return actualPayables;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public void setActualReceivables(BigDecimal actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public void setPayables(BigDecimal payables) {
		this.payables = payables;
	}

	public void setActualPayables(BigDecimal actualPayables) {
		this.actualPayables = actualPayables;
	}

	public long getSupplierCount() {
		return supplierCount;
	}

	public long getPlatformCount() {
		return platformCount;
	}

	public void setSupplierCount(long supplierCount) {
		this.supplierCount = supplierCount;
	}

	public void setPlatformCount(long platformCount) {
		this.platformCount = platformCount;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public BigDecimal getActualInvoiceAmount() {
		return actualInvoiceAmount;
	}

	public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
		this.actualInvoiceAmount = actualInvoiceAmount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getRelatedInfo() {
		return relatedInfo;
	}

	public void setRelatedInfo(String relatedInfo) {
		this.relatedInfo = relatedInfo;
	}

	public Timestamp getFinalPayTime() {
		return finalPayTime;
	}

	public void setFinalPayTime(Timestamp finalPayTime) {
		this.finalPayTime = finalPayTime;
	}

	public Timestamp getFinalReceiveTime() {
		return finalReceiveTime;
	}

	public void setFinalReceiveTime(Timestamp finalReceiveTime) {
		this.finalReceiveTime = finalReceiveTime;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(int billStatus) {
		this.billStatus = billStatus;
	}

	public Timestamp getWriteOffTime() {
		return writeOffTime;
	}

	public void setWriteOffTime(Timestamp writeOffTime) {
		this.writeOffTime = writeOffTime;
	}

	public int getNeedAuto() {
		return needAuto;
	}

	public void setNeedAuto(int needAuto) {
		this.needAuto = needAuto;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public List<FsExpenseincomeInfo> getFsExpenseIncomeInfos() {
		if (StringUtils.isBlank(this.relatedInfo)) {
			return null;
		}
		return JSON.parseArray(this.relatedInfo, FsExpenseincomeInfo.class);
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(String operationLog) {
		this.operationLog = operationLog;
	}

	@Override
	public String toString() {
		return "ProductBills{" + "entityId='" + entityId + '\'' + ", productId='" + productId + '\'' + ", wtime="
				+ DateUtil.convert(wtime.getTime(), DateUtil.format2) + ", supplierCount=" + supplierCount + ", platformCount=" + platformCount
				+ ", receivables=" + receivables + ", actualReceivables=" + actualReceivables + ", payables=" + payables + ", actualPayables=" + actualPayables
				+ ", entityType=" + entityType + '}';
	}
}
