package com.dahantc.erp.dto.customer;

import java.io.Serializable;
import java.util.Date;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;

/**
 * 客户校验返回信息
 *
 * @author 8520
 */
public class CustomerInfoDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7172688471777146230L;

	/**
	 * 公司id
	 */
	private String companyId;

	/**
	 * 客户名称
	 */
	private String companyName;

	/**
	 * 客户类别
	 */
	private String customerType;

	/**
	 * 销售名称
	 */
	private String saleName;

	/**
	 * 建立时间
	 */
	private Date createTime;

	/**
	 * 创建时间string
	 */
	private String createTimeStr;

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public CustomerInfoDto() {

	}

	public CustomerInfoDto(Customer customer, String customerType, String saleName) {
		this.companyId = customer.getCustomerId();
		this.companyName = customer.getCompanyName();
		this.customerType = customerType;
		this.saleName = saleName;
		// 创建时间
		this.createTime = customer.getWtime();
		if (this.createTime != null) {
			this.createTimeStr = DateUtil.convert(this.createTime, DateUtil.format2);
		}
	}

	@Override
	public String toString() {
		return "CustomerInfoDto{" + "companyId='" + companyId + '\'' + ", companyName='" + companyName + '\'' + ", customerType='" + customerType + '\''
				+ ", saleName='" + saleName + '\'' + ", createTime=" + createTime + '}';
	}
}
