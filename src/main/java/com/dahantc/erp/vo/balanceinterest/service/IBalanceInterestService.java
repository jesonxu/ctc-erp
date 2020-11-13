package com.dahantc.erp.vo.balanceinterest.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestDto;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestParam;
import com.dahantc.erp.dto.balanceaccount.InterestDetailDto;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;

public interface IBalanceInterestService {
    BalanceInterest read(Serializable id) throws ServiceException;

    boolean save(BalanceInterest entity) throws ServiceException;

    boolean delete(Serializable id) throws ServiceException;

    boolean update(BalanceInterest enterprise) throws ServiceException;

    int getCount(SearchFilter filter) throws ServiceException;

    PageResult<BalanceInterest> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

    List<BalanceInterest> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

    List<BalanceInterest> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

    List<BalanceInterest> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

    boolean saveByBatch(List<BalanceInterest> objs) throws ServiceException;


    /**
     * 根据条件分页查询统计数据
     *
     * @param user  登录用户
     * @param param 参数
     * @return 分页 计息数据
     */
    PageResult<BalanceInterestDto> queryBalanceInterestByPage(OnlineUser user, BalanceInterestParam param);

    /**
     * 查询 计息详情
     *
     * @param companyId 公司ID
     * @param month     月份
     * @param user      登录用户
     * @return 计息详情
     */
    List<InterestDetailDto> queryInterestDetail(String companyId, Date month, OnlineUser user);
}
