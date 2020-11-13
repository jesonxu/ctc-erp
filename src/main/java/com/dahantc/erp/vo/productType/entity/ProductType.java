package com.dahantc.erp.vo.productType.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.CostPriceType;
import com.dahantc.erp.enums.VisibleStatus;

@Entity
@Table(name = "erp_product_type")
public class ProductType implements Serializable {

    private static final long serialVersionUID = -3161515253542347558L;

    @Id
    @Column(length = 32, name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    // 产品类型名
    @Column(length = 255, name = "productTypeName")
    private String productTypeName;

    // 产品类型key，例如：SMS、MMS等等
    @Column(length = 255, name = "productTypeKey")
    private String productTypeKey;

    // 产品类型值
    @Column(name = "productTypeValue", columnDefinition = "int(11) default 0")
    private int productTypeValue;

    // 成本类型，0平台同步，1手动配置
    @Column(name = "costPriceType", columnDefinition = "int(11) default 0")
    private int costPriceType = CostPriceType.AUTO.ordinal();

    // 成本类型为1手动配置时的成本单价
    @Column(name = "costPrice", columnDefinition = "decimal(19,6) default 0 comment '产品类型成本'", precision = 19, scale = 6)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "wtime")
    private Timestamp wtime = new Timestamp(System.currentTimeMillis());

    // 创建者
    @Column(length = 32, name = "ossuserid")
    private String ossUserId;

    // 可见状态
    @Column(name = "visible", columnDefinition = "int(11) default 1")
    private int visible = VisibleStatus.SHOW.ordinal();

    // 备注
    @Column(length = 255, name = "remark")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeKey() {
        return productTypeKey;
    }

    public void setProductTypeKey(String productTypeKey) {
        this.productTypeKey = productTypeKey;
    }

    public int getProductTypeValue() {
        return productTypeValue;
    }

    public void setProductTypeValue(int productTypeValue) {
        this.productTypeValue = productTypeValue;
    }

    public int getCostPriceType() {
        return costPriceType;
    }

    public void setCostPriceType(int costPriceType) {
        this.costPriceType = costPriceType;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Timestamp getWtime() {
        return wtime;
    }

    public void setWtime(Timestamp wtime) {
        this.wtime = wtime;
    }

    public String getOssUserId() {
        return ossUserId;
    }

    public void setOssUserId(String ossUserId) {
        this.ossUserId = ossUserId;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
