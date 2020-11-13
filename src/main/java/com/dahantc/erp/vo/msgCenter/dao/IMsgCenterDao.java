package com.dahantc.erp.vo.msgCenter.dao;

import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;

public interface IMsgCenterDao {

	PageResult<MsgCenter> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	int getCountByDate(SearchFilter filter) throws DaoException;

	List<MsgCenter> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean save(MsgCenter entity) throws DaoException;

	boolean saveByBatch(List<MsgCenter> objs) throws DaoException;
}
