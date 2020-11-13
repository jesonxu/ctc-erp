package com.dahantc.erp.dto.customerChangeRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户变更统计数据（以 客户、变更类型 为主）、
 *
 * @author 8520
 */
public class ChangeCountDto {

    public static final Logger logger = LoggerFactory.getLogger(ChangeCountDto.class);

    /**
     * 销售ID
     */
    private String userId;

    /**
     * 原始客户类型
     */
    private Integer from;

    /**
     * 改变后的客户类型
     */
    private Integer to;

    /**
     * 改变类型0升，1降
     */
    private Integer changeType;

    /**
     * 数量
     */
    private Integer count = 0;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ChangeCountDto() {
    }

    /**
     * 设置数据
     *
     * @return 是否成功
     */
    public boolean setObjectInfo(Object row) {
        try {
            if (row != null && row.getClass().isArray()) {
                Object[] rowInfo = (Object[]) row;
                if (rowInfo.length >= 5) {
                    this.userId = String.valueOf(rowInfo[0]);
                    this.from = ((Number) rowInfo[1]).intValue();
                    this.to = ((Number) rowInfo[2]).intValue();
                    this.changeType = ((Number) rowInfo[3]).intValue();
                    this.count = ((Number) rowInfo[4]).intValue();
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }
}
