package com.dahantc.erp.dto.balanceaccount;

import com.dahantc.erp.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分页查询参数
 *
 * @author 8520
 */
public class BalanceInterestParam {

    private Integer pageSize;

    private Integer currentPage;

    private String userId;

    private String queryDate;

    private String deptId;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }


    public List<String> getDeptIds(){
        if (StringUtil.isNotBlank(this.deptId)){
            return new ArrayList<>(Arrays.asList(this.deptId.split(",")));
        }
        return null;
    }

    public List<String> getUserIds(){
        if (StringUtil.isNotBlank(this.userId)){
            return new ArrayList<>(Arrays.asList(this.userId.split(",")));
        }
        return null;
    }

    public Integer getPageStart() {
        if (pageSize == null) {
            pageSize = 10;
        }
        if (currentPage == null) {
            currentPage = 1;
        }
        return pageSize * (currentPage - 1);
    }
}
