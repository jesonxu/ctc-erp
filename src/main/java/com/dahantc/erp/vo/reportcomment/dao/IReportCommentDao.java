package com.dahantc.erp.vo.reportcomment.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.reportcomment.entity.ReportComment;

public interface IReportCommentDao {

	ReportComment read(Serializable id) throws DaoException;

	boolean save(ReportComment entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ReportComment enterprise) throws DaoException;

	int getCount(SearchFilter filter) throws DaoException;

	PageResult<ReportComment> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<ReportComment> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<ReportComment> objs) throws DaoException;

	boolean saveByBatch(List<ReportComment> objs) throws DaoException;

	boolean deleteByBatch(List<ReportComment> objs) throws DaoException;

}
