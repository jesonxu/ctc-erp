package com.dahantc.erp.dto.fsExpenseIncome;

import java.io.Serializable;

/**
 * 分页查询的参数
 * 
 * @author 8520
 */
public class ExpenseIncomePageParam implements Serializable {
	
	private static final long serialVersionUID = 2427445714738161571L;
	
	/**
	 * 分页大小
	 */
	private Integer pageSize;
	
	/**
	 * 页数
	 */
	private Integer page;
	
	/**
	 * 年
	 */
	private String year;
	
	/**
	 * 开始时间 格式 2019-12-10 15:30:05
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 销账状态
	 */
	private String writeoffstatus ;
	
	/**
	 * 关联状态
	 */
	private String linkStatus;
	
	/**
	 * 客户还是供应商
	 */
	private String from;
	
	/**
	 * 内容描述
	 */
	private String depict;
	
	/**
	 * 时间类型 1-到账时间 2-导入时间
	 */
	private String timeType;

	/**
	 * 核销状态，只查未核销完的到款（未核销，部分核销）
	 */
	private String checkOut;

	private String companyName;
	
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getWriteoffstatus() {
		return writeoffstatus;
	}

	public void setWriteoffstatus(String writeoffstatus) {
		this.writeoffstatus = writeoffstatus;
	}

	public String getDepict() {
		return depict;
	}

	public void setDepict(String depict) {
		this.depict = depict;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public String getLinkStatus() {
		return linkStatus;
	}

	public void setLinkStatus(String linkStatus) {
		this.linkStatus = linkStatus;
	}

	public String getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(String checkOut) {
		this.checkOut = checkOut;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}