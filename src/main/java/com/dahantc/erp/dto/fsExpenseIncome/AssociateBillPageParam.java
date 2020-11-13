package com.dahantc.erp.dto.fsExpenseIncome;

import com.dahantc.erp.util.StringUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 关联的产品账单请求参数
 * @author 8520
 */
public class AssociateBillPageParam implements Serializable {
	private static final long serialVersionUID = 6089754891391108255L;
	/**
     * 当前页
     */
    Integer page;
    /**
     * 页大小
     */
    Integer pageSize;
    /**
     * 部门ID
     */
    String deptId;
    /**
     * 来源 supplier | customer
     */
    String from;

    /**
     * 开始时间 年月日
     */
    private String startTime ;

    /**
     * 结束时间 年月日
     */
    private String endTime;
    /**
     * 客户/供应商 id
     */
    private String entityName;
    /** 是否为收入 0-是，1-不是 */
    private Integer income;
    
    public String paramCheck(){
        if (page == null || page <=0){
            return "当前页数错误";
        }
        if (pageSize == null || pageSize <=0){
            return  "当前页大小错误";
        }
		Set<String> froms = new HashSet<>(Arrays.asList("supplier", "customer"));
        if (StringUtil.isBlank(from) || !froms.contains(from)){
            return "请求参数错误";
        }
        return null;
    }

    public Integer getPageStart(){
        return (page-1)*pageSize;
    }

    public boolean fromCustomer(){
        return "customer".equalsIgnoreCase(from);
    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }
}
