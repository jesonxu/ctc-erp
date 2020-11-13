package com.dahantc.erp.vo.customerChangeRecord.vo;

/**
 * 客户变更表内容
 *
 * @author 8520
 */
public class CustomerChangeVo {
    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 合同客户总数
     */
    private Integer contractTotal = 0;
    /**
     * 测试客户总数
     */
    private Integer testTotal = 0;
    /**
     * 意向客户数量
     */
    private Integer intentionTotal = 0;
    /**
     * 沉默客户数量
     */
    private Integer silenceTotal = 0;
    /**
     * 当月改变总量
     */
    private Integer monthChangeCount = 0;

    //-------------合同客户变更------------------------
    /**
     * 合同客户下降数（f1->）
     */
    private Integer downFromContractCount = 0;
    /**
     * 升合同客户数量(->t1)
     */
    private Integer upToContractCount = 0;

    //-------------测试客户变更------------------------
    /**
     * 升到 测试客户(->t2)
     */
    private Integer upToTestCount = 0;

    /**
     * 降到 测试客户
     */
    private Integer downToTestCount = 0;

    /**
     * 测试客户降下去(f2->t3||f2->t4||f2->t5) 意向 沉默 公共池
     */
    private Integer downFromTestCount = 0;

    /**
     * 测试客户升上去（f2->t1）
     */
    private Integer upFromTestCount = 0;


    //-------------意向客户变更------------------------

    /**
     * 升到 意向客户(->t3)
     */
    private Integer upToIntentionCount = 0;

    /**
     * 降到 意向客户
     */
    private Integer downToIntentionCount = 0;

    /**
     * 意向客户降下去(f3->t4||f3->t5)  沉默 公共池
     */
    private Integer downFromIntentionCount = 0;

    /**
     * 意向客户升上去(f3->t1||f3->t2)
     */
    private Integer upFromIntentionCount = 0;


    //-------------沉默客户变更------------------------

    /**
     * 升到 沉默客户(->t4)
     */
    private Integer upToSilenceCount = 0;

    /**
     * 降到 沉默客户(->t4)
     */
    private Integer downToSilenceCount = 0;

    /**
     * 沉默客户降下去(f4->t5)
     */
    private Integer downFromSilenceCount = 0;

    /**
     * 沉默客户升上去(f4->t1||f4->t2||f4->t3) 意向 沉默 公共池
     */
    private Integer upFromSilenceCount = 0;

    // -------------公共池客户变更------------------------

    /**
     * 降到公共池
     * ->t5
     */
    private Integer downToPublic = 0;
    /**
     * 由公共池上升数
     * t5->
     */
    private Integer upFromPublic = 0;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getContractTotal() {
        return contractTotal;
    }

    public void setContractTotal(Integer contractTotal) {
        this.contractTotal = contractTotal;
    }

    public Integer getTestTotal() {
        return testTotal;
    }

    public void setTestTotal(Integer testTotal) {
        this.testTotal = testTotal;
    }

    public Integer getIntentionTotal() {
        return intentionTotal;
    }

    public void setIntentionTotal(Integer intentionTotal) {
        this.intentionTotal = intentionTotal;
    }

    public Integer getSilenceTotal() {
        return silenceTotal;
    }

    public void setSilenceTotal(Integer silenceTotal) {
        this.silenceTotal = silenceTotal;
    }

    public Integer getMonthChangeCount() {
        return monthChangeCount;
    }

    public void setMonthChangeCount(Integer monthChangeCount) {
        this.monthChangeCount = monthChangeCount;
    }

    public Integer getDownFromContractCount() {
        return downFromContractCount;
    }

    public void setDownFromContractCount(Integer downFromContractCount) {
        this.downFromContractCount = downFromContractCount;
    }

    public Integer getUpToContractCount() {
        return upToContractCount;
    }

    public void setUpToContractCount(Integer upToContractCount) {
        this.upToContractCount = upToContractCount;
    }

    public Integer getUpToTestCount() {
        return upToTestCount;
    }

    public void setUpToTestCount(Integer upToTestCount) {
        this.upToTestCount = upToTestCount;
    }

    public Integer getDownFromTestCount() {
        return downFromTestCount;
    }

    public void setDownFromTestCount(Integer downFromTestCount) {
        this.downFromTestCount = downFromTestCount;
    }

    public Integer getUpFromTestCount() {
        return upFromTestCount;
    }

    public void setUpFromTestCount(Integer upFromTestCount) {
        this.upFromTestCount = upFromTestCount;
    }

    public Integer getUpToIntentionCount() {
        return upToIntentionCount;
    }

    public void setUpToIntentionCount(Integer upToIntentionCount) {
        this.upToIntentionCount = upToIntentionCount;
    }

    public Integer getDownFromIntentionCount() {
        return downFromIntentionCount;
    }

    public void setDownFromIntentionCount(Integer downFromIntentionCount) {
        this.downFromIntentionCount = downFromIntentionCount;
    }

    public Integer getUpFromIntentionCount() {
        return upFromIntentionCount;
    }

    public void setUpFromIntentionCount(Integer upFromIntentionCount) {
        this.upFromIntentionCount = upFromIntentionCount;
    }

    public Integer getUpToSilenceCount() {
        return upToSilenceCount;
    }

    public void setUpToSilenceCount(Integer upToSilenceCount) {
        this.upToSilenceCount = upToSilenceCount;
    }

    public Integer getDownFromSilenceCount() {
        return downFromSilenceCount;
    }

    public void setDownFromSilenceCount(Integer downFromSilenceCount) {
        this.downFromSilenceCount = downFromSilenceCount;
    }

    public Integer getUpFromSilenceCount() {
        return upFromSilenceCount;
    }

    public void setUpFromSilenceCount(Integer upFromSilenceCount) {
        this.upFromSilenceCount = upFromSilenceCount;
    }

    public Integer getDownToPublic() {
        return downToPublic;
    }

    public void setDownToPublic(Integer downToPublic) {
        this.downToPublic = downToPublic;
    }

    public Integer getUpFromPublic() {
        return upFromPublic;
    }

    public void setUpFromPublic(Integer upFromPublic) {
        this.upFromPublic = upFromPublic;
    }

    public Integer getDownToTestCount() {
        return downToTestCount;
    }

    public void setDownToTestCount(Integer downToTestCount) {
        this.downToTestCount = downToTestCount;
    }

    public Integer getDownToIntentionCount() {
        return downToIntentionCount;
    }

    public void setDownToIntentionCount(Integer downToIntentionCount) {
        this.downToIntentionCount = downToIntentionCount;
    }

    public Integer getDownToSilenceCount() {
        return downToSilenceCount;
    }

    public void setDownToSilenceCount(Integer downToSilenceCount) {
        this.downToSilenceCount = downToSilenceCount;
    }

    @Override
    public String toString() {
        return "CustomerChangeVo{" +
                "deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", contractTotal=" + contractTotal +
                ", testTotal=" + testTotal +
                ", intentionTotal=" + intentionTotal +
                ", silenceTotal=" + silenceTotal +
                ", monthChangeCount=" + monthChangeCount +
                ", downFromContractCount=" + downFromContractCount +
                ", upToContractCount=" + upToContractCount +
                ", upToTestCount=" + upToTestCount +
                ", downToTestCount=" + downToTestCount +
                ", downFromTestCount=" + downFromTestCount +
                ", upFromTestCount=" + upFromTestCount +
                ", upToIntentionCount=" + upToIntentionCount +
                ", downToIntentionCount=" + downToIntentionCount +
                ", downFromIntentionCount=" + downFromIntentionCount +
                ", upFromIntentionCount=" + upFromIntentionCount +
                ", upToSilenceCount=" + upToSilenceCount +
                ", downToSilenceCount=" + downToSilenceCount +
                ", downFromSilenceCount=" + downFromSilenceCount +
                ", upFromSilenceCount=" + upFromSilenceCount +
                ", downToPublic=" + downToPublic +
                ", upFromPublic=" + upFromPublic +
                '}';
    }
}
