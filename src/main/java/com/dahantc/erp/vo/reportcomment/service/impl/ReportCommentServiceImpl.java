package com.dahantc.erp.vo.reportcomment.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.reportcomment.dao.IReportCommentDao;
import com.dahantc.erp.vo.reportcomment.entity.ReportComment;
import com.dahantc.erp.vo.reportcomment.service.IReportCommentService;

@Service("reportCommentService")
public class ReportCommentServiceImpl implements IReportCommentService {
	private static Logger logger = LogManager.getLogger(ReportCommentServiceImpl.class);

	@Autowired
	private IReportCommentDao reportCommentDao;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public ReportComment read(Serializable id) throws ServiceException {
		try {
			return reportCommentDao.read(id);
		} catch (Exception e) {
			logger.error("读取评论信息失败", e);
			throw new ServiceException("读取评论信息失败", e);
		}
	}

	@Override
	public boolean save(ReportComment entity) throws ServiceException {
		try {
			return reportCommentDao.save(entity);
		} catch (Exception e) {
			logger.error("保存评论信息失败", e);
			throw new ServiceException("保存评论信息失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return reportCommentDao.delete(id);
		} catch (Exception e) {
			logger.error("删除评论信息失败", e);
			throw new ServiceException("删除评论信息失败", e);
		}
	}

	@Override
	public boolean update(ReportComment enterprise) throws ServiceException {
		try {
			return reportCommentDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新评论信息失败", e);
			throw new ServiceException("更新评论信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return reportCommentDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询评论信息数量失败", e);
			throw new ServiceException("查询评论信息数量失败", e);
		}
	}

	@Override
	public PageResult<ReportComment> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return reportCommentDao.queryByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询评论分页信息失败", e);
			throw new ServiceException("查询评论分页信息失败", e);
		}
	}

	@Override
	public List<ReportComment> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return reportCommentDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询评论信息数量失败", e);
			throw new ServiceException("查询评论信息数量失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<ReportComment> objs) throws ServiceException {
		try {
			return reportCommentDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<ReportComment> objs) throws ServiceException {
		try {
			return reportCommentDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<ReportComment> objs) throws ServiceException {
		try {
			return reportCommentDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(String commentId) throws ServiceException {
		try {
			baseDao.executeSqlUpdte("delete from erp_report_comment where id = ? or replyid = ?", new Object[] { (Object) commentId, (Object) commentId });
			return true;
		} catch (BaseException e) {
			logger.error("", e);
		}
		return false;
	}

}
