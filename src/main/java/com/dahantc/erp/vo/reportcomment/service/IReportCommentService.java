package com.dahantc.erp.vo.reportcomment.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.reportcomment.entity.ReportComment;

public interface IReportCommentService {

	ReportComment read(Serializable id) throws ServiceException;

	boolean save(ReportComment entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ReportComment enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<ReportComment> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<ReportComment> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	boolean updateByBatch(List<ReportComment> objs) throws ServiceException;

	boolean saveByBatch(List<ReportComment> objs) throws ServiceException;

	boolean deleteByBatch(List<ReportComment> objs) throws ServiceException;
	
	boolean deleteByBatch(String commentId) throws ServiceException;

}
