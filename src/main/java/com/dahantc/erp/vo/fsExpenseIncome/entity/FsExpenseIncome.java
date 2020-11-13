package com.dahantc.erp.vo.fsExpenseIncome.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.enums.CheckOutStatus;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.FeeType;
import com.dahantc.erp.enums.RelateStatus;

/** 财务收支表 */
@Entity
@Table(name = "erp_fs_expense_income")
@DynamicUpdate(true)
public class FsExpenseIncome implements Serializable {

	private static final long serialVersionUID = -7224750111184818603L;

	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/** 创建时间 */
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/** 时间(收付款时间,精确到日) */
	@Column(name = "operatetime")
	private Timestamp operateTime = new Timestamp(System.currentTimeMillis());

	@Column(name = "bankname")
	private String bankName;

	/** 部门名称 */
	@Column(name = "deptname")
	private String deptName;

	/** 到款的对应的部门id */
	@Column(name = "deptid")
	private String deptId;

	/** 地区名称 */
	@Column(name = "regionname")
	private String regionName;

	@Column(name = "feetype", columnDefinition = "int default 0")
	private int feeType = FeeType.SMS_INCOME.getCode();

	/** 姓名 */
	@Column(name = "operator")
	private String operator;

	/** 摘要: 广州某某信息科技有限公司 */
	private String depict;

	/** 是否是收入: 0--是,1--否(支出) */
	@Column(name = "isincome", columnDefinition = "int default 0")
	private int isIncome = 0;

	/** 金额 */
	private BigDecimal cost = new BigDecimal(0);

	private String remark;

	/** 剩余未关联金额 */
	@Column(name = "remainrelatedcost")
	private BigDecimal remainRelatedCost = new BigDecimal(0);

	/** 导入用户 */
	@Column(name = "userid")
	private String userId;

	/** 导入用户部门id */
	@Column(name = "userdeptid")
	private String userDeptId;

	// 本条银行收支的流水号，用于导入数据的去重
	@Column(name = "serialnumber", length = 32)
	private String serialNumber;

	@Column(name = "relatestatus", columnDefinition = "int default 0")
	private int relateStatus = RelateStatus.UNRELATE.ordinal();

	@Column(name = "customerid", length = 32)
	private String customerId;

	// （充值）核销状态，即是否与充值记录匹配
	@Column(name = "checkout", columnDefinition = "int default 0")
	private int checkOut = CheckOutStatus.NO_CHECKED.ordinal();

	// 剩余可用于核销的金额
	@Column(name = "remaincheckout", columnDefinition = "decimal(19,2) default 0")
	private BigDecimal remainCheckOut = new BigDecimal(0);

	// （充值）核销信息json
	@Column(name = "checkoutinfo", length = 1000)
	private String checkOutInfo;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getFeeType() {
		return feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDepict() {
		return depict;
	}

	public void setDepict(String depict) {
		this.depict = depict;
	}

	public int getIsIncome() {
		return isIncome;
	}

	public void setIsIncome(int isIncome) {
		this.isIncome = isIncome;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getRemainRelatedCost() {
		return remainRelatedCost;
	}

	public void setRemainRelatedCost(BigDecimal remainRelatedCost) {
		this.remainRelatedCost = remainRelatedCost;
	}

	public Timestamp getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Timestamp operateTime) {
		this.operateTime = operateTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserDeptId() {
		return userDeptId;
	}

	public void setUserDeptId(String userDeptId) {
		this.userDeptId = userDeptId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getRelateStatus() {
		return relateStatus;
	}

	public void setRelateStatus(int relateStatus) {
		this.relateStatus = relateStatus;
	}

	public int getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(int checkOut) {
		this.checkOut = checkOut;
	}

	public BigDecimal getRemainCheckOut() {
		return remainCheckOut;
	}

	public void setRemainCheckOut(BigDecimal remainCheckOut) {
		this.remainCheckOut = remainCheckOut;
	}

	public String getCheckOutInfo() {
		return checkOutInfo;
	}

	public void setCheckOutInfo(String checkOutInfo) {
		this.checkOutInfo = checkOutInfo;
	}
}
