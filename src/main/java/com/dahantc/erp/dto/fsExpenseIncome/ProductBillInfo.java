package com.dahantc.erp.dto.fsExpenseIncome;

import com.dahantc.erp.util.DateUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 产品账单信息
 * @author 8520
 */
public class ProductBillInfo implements Serializable {
	private static final long serialVersionUID = 479665653523323748L;
	/** 账单id */
	private String id;
	/** 应收账款 */
	private BigDecimal receivables;
	/** 实收账款 */
	private BigDecimal actualReceivables;
	/** 应付账款 */
	private BigDecimal payables;
	/** 实付账款 */
	private BigDecimal actualpayables;
	/** 已开发票金额 */
	private BigDecimal actualInvoiceAmount;
	/** 账单所属部门id */
	private String deptId;
	/** 账单所属部门名称 */
	private String deptName;
	/** 供应商/客户 */
	private String entityId;
	/** 供应商/客户名称 */
	private String companyName;
	/** 产品id */
	private String productId;
	/** 产品名称 */
	private String productName;
	/** 账单所属实体类型 */
	private String entityType;
	/** 账单时间 */
	private String wTime;

    public ProductBillInfo() {
    }


    public ProductBillInfo(Object[] billInfo) {
// id|actualpayables|actualreceivables|payables|productid|receivables|entityid|
        // entitytype|companyname|deptid|productname
        Object id = billInfo[0];
        setId(String.valueOf(id));
        // 实际支付
        Object actualPayable = billInfo[1];
        if (actualPayable != null) {
            setActualpayables(new BigDecimal(String.valueOf(actualPayable)));
        }
        // 实际收款
        Object actualReceivables = billInfo[2];
        if (actualReceivables != null) {
            setActualReceivables(new BigDecimal(String.valueOf(actualReceivables)));
        }
        // 应付
        Object payables = billInfo[3];
        if (payables != null) {
            setPayables(new BigDecimal(String.valueOf(payables)));
        }
        // 产品id
        Object productId = billInfo[4];
        if (productId != null) {
            setProductId(String.valueOf(productId));
        }
        // 应收
        Object receivables = billInfo[5];
        if (receivables != null) {
            setReceivables(new BigDecimal(String.valueOf(receivables)));
        }
        // 客户/供应商id
        Object entityId = billInfo[6];
        if (entityId != null) {
            setEntityId(String.valueOf(entityId));
        }
        // 实体类型
        Object entityType = billInfo[7];
        if (entityType != null) {
            setEntityType(String.valueOf(entityType));
        }
        // 公司名
        Object companyName = billInfo[8];
        if (companyName != null) {
            setCompanyName(String.valueOf(companyName));
        }
        // 订单部门
        Object billDeptId = billInfo[9];
        if (billDeptId != null) {
            setDeptId(String.valueOf(billDeptId));
        }
        // 产品名称
        Object productName = billInfo[10];
        if (productName != null) {
            setProductName(String.valueOf(productName));
        }
        // 产品名称
        Object time = billInfo[11];
		if (time instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) time;
			String timeStr = DateUtil.convert(new Date(timestamp.getTime()), DateUtil.format1);
			setwTime(timeStr);
		}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getReceivables() {
        return receivables;
    }

    public void setReceivables(BigDecimal receivables) {
        this.receivables = receivables;
    }

    public BigDecimal getActualReceivables() {
        return actualReceivables;
    }

    public void setActualReceivables(BigDecimal actualReceivables) {
        this.actualReceivables = actualReceivables;
    }

    public BigDecimal getPayables() {
        return payables;
    }

    public void setPayables(BigDecimal payables) {
        this.payables = payables;
    }

    public BigDecimal getActualpayables() {
        return actualpayables;
    }

    public void setActualpayables(BigDecimal actualpayables) {
        this.actualpayables = actualpayables;
    }

    public BigDecimal getActualInvoiceAmount() {
        return actualInvoiceAmount;
    }

    public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
        this.actualInvoiceAmount = actualInvoiceAmount;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getwTime() {
        return wTime;
    }

    public void setwTime(String wTime) {
        this.wTime = wTime;
    }


}
