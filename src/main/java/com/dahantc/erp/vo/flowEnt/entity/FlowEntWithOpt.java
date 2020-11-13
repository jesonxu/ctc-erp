package com.dahantc.erp.vo.flowEnt.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程信息携带是否可以编辑信息
 * @author 8520
 */
public class FlowEntWithOpt implements Serializable {

    private static final long serialVersionUID = 3611902188935482244L;

    private String id;

    /** 所属流程id */
    private String flowId;

    /** 所属流程类型 */
    private int flowType;

    /** 标题 */
    private String flowTitle;

    /** 发起人 */
    private String ossUserId;

    /** 供应商id、客户id */
    private String supplierId;

    /** 产品id */
    private String productId;

    private String foreignId;

    /** json格式的流程内容 */
    private String flowMsg;

    /** 节点id*/
    private String nodeId;

    /** 流程状态 */
    private Integer flowStatus ;

    /** 备注 */
    private String remark;

    /** 发起时间 */
    private Timestamp wtime;

    /** 实体类型 */
    private Integer entityType;

    /** 可以看的角色id */
    private String viewerRoleId;

    /**
     * 用户是否能编辑
     */
    private Boolean canOpt = false;

    private String deptId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
    }

    public String getFlowTitle() {
        return flowTitle;
    }

    public void setFlowTitle(String flowTitle) {
        this.flowTitle = flowTitle;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public String getFlowMsg() {
        return flowMsg;
    }

    public void setFlowMsg(String flowMsg) {
        this.flowMsg = flowMsg;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getWtime() {
        return wtime;
    }

    public void setWtime(Timestamp wtime) {
        this.wtime = wtime;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public String getViewerRoleId() {
        return viewerRoleId;
    }

    public void setViewerRoleId(String viewerRoleId) {
        this.viewerRoleId = viewerRoleId;
    }

    public Boolean getCanOpt() {
        return canOpt;
    }

    public void setCanOpt(Boolean canOpt) {
        this.canOpt = canOpt;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * 设置数据
     *
     * @param data 数据
     * @return 是否成功
     */
    public boolean setDataInfo(Object[] data){
        if (data.length >= 16) {
            if (data[0] != null && data[0] instanceof String) {
                this.id = (String) data[0];
            }
            if (data[1] != null && data[1] instanceof String) {
                this.flowId = (String) data[1];
            }
            if (data[2] != null && data[2] instanceof String) {
                this.flowMsg = (String) data[2];
            }
            if (data[3] != null && data[3] instanceof Integer) {
                this.flowStatus = (Integer) data[3];
            }
            if (data[4] != null && data[4] instanceof String) {
                this.foreignId = (String) data[4];
            }
            if (data[5] != null && data[5] instanceof String) {
                this.nodeId = (String) data[5];
            }
            if (data[6] != null && data[6] instanceof String) {
                this.ossUserId = (String) data[6];
            }
            if (data[7] != null && data[7] instanceof String) {
                this.remark = (String) data[7];
            }
            if (data[8] != null && data[8] instanceof Timestamp) {
                this.wtime = (Timestamp)data[8];
            }
            if (data[9] != null) {
                this.productId = String.valueOf(data[9]);
            }
            if (data[10] != null) {
                this.supplierId = String.valueOf(data[10]);
            }
            if (data[11] != null) {
                this.flowTitle = String.valueOf(data[11]);
            }
            if (data[12] != null && data[12] instanceof Integer) {
                this.flowType = (Integer) data[12];
            }
            if (data[13] != null && data[13] instanceof Integer) {
                this.entityType = (Integer)data[13];
            }
            if (data[14] != null) {
                this.viewerRoleId = String.valueOf(data[14]);
            }
            Object opt = data[15];
            this.canOpt = opt != null && "1".equals(String.valueOf(opt));
            if (data[16] != null) {
                this.deptId = String.valueOf(data[16]);
            }
            return true;
        }
        return false;
    }
}
