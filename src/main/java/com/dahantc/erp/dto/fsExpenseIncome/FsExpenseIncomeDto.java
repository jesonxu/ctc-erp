package com.dahantc.erp.dto.fsExpenseIncome;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.dahantc.erp.enums.FeeType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;

/**
 * 导入数据的展示信息
 * 
 * @author 8520
 */
public class FsExpenseIncomeDto implements Serializable {

	private static final long serialVersionUID = 2794571449666713798L;

	private String id;

	/** 时间(收付款时间,精确到日) */
	private String operateTime;

	/** 银行名称 */
	private String bankName;

	/** 部门名称 */
	private String deptName;

	/** 地区名称 */
	private String regionName;

	/** 费用类型 */
	private String feeType;

	/** 姓名 */
	private String optionName;

	/** 摘要: 广州某某信息科技有限公司 */
	private String depict;

	/** 是否是收入: 0--是,1--否(支出) */
	private Integer isIncome = 0;

	/** 金额 */
	private BigDecimal cost = new BigDecimal(0);

	private String remark;

	/** 剩余关联金额 */
	private BigDecimal remainRelatedCost;

	/** 记录创建者名称 */
	private String creatorName;

	/** 导入时间 */
	private String wTime;

	private String customerId;

	private String serialNumber;

	private int relateStatus;

	private String customerName;

	private String settleType;

	// 核销状态
	private int checkOut;

	// 剩余可核销金额
	private BigDecimal remainCheckOut;

	public FsExpenseIncomeDto() {

	}

	public FsExpenseIncomeDto(FsExpenseIncome fsExpenseIncome) {
		if (fsExpenseIncome != null) {
			this.id = fsExpenseIncome.getId();
			Timestamp operateTime = fsExpenseIncome.getOperateTime();
			this.operateTime = operateTime != null ? DateUtil.convert(operateTime, DateUtil.format1) : "";
			this.bankName = fsExpenseIncome.getBankName();
			this.deptName = fsExpenseIncome.getDeptName();
			this.regionName = fsExpenseIncome.getRegionName();
			if (FeeType.values().length > fsExpenseIncome.getFeeType()) {
				this.feeType = FeeType.values()[fsExpenseIncome.getFeeType()].getMsg();
			}
			this.optionName = fsExpenseIncome.getOperator();
			this.depict = fsExpenseIncome.getDepict();
			this.isIncome = fsExpenseIncome.getIsIncome();
			this.cost = fsExpenseIncome.getCost();
			this.remark = fsExpenseIncome.getRemark();
			this.remainRelatedCost = fsExpenseIncome.getRemainRelatedCost();
			Timestamp wtime = fsExpenseIncome.getWtime();
			this.wTime = wtime != null ? DateUtil.convert(wtime, DateUtil.format2) : "";
			this.serialNumber = fsExpenseIncome.getSerialNumber();
			this.relateStatus = fsExpenseIncome.getRelateStatus();
			this.customerId = fsExpenseIncome.getCustomerId();
			this.remainCheckOut = fsExpenseIncome.getRemainCheckOut();
			this.checkOut = fsExpenseIncome.getCheckOut();
		}
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
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

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public void setIsIncome(Integer isIncome) {
		this.isIncome = isIncome;
	}

	public BigDecimal getRemainRelatedCost() {
		return remainRelatedCost;
	}

	public void setRemainRelatedCost(BigDecimal remainRelatedCost) {
		this.remainRelatedCost = remainRelatedCost;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getwTime() {
		return wTime;
	}

	public void setwTime(String wTime) {
		this.wTime = wTime;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
}
