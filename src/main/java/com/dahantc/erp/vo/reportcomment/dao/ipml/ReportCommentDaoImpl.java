package com.dahantc.erp.vo.reportcomment.dao.ipml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.util.DetachedCriteriaUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.reportcomment.dao.IReportCommentDao;
import com.dahantc.erp.vo.reportcomment.entity.ReportComment;

@Repository("reportCommentDao")
public class ReportCommentDaoImpl implements IReportCommentDao {
	private static final Logger logger = LogManager.getLogger(ReportCommentDaoImpl.class);
	@Resource
	private IBaseDao baseDao;

	@Override
	public ReportComment read(Serializable id) {
		try {
			ReportComment ReportComment = (ReportComment) baseDao.get(ReportComment.class, id);
			return ReportComment;
		} catch (Exception e) {
			logger.error("读取评论信息失败", e);
		}
		return null;
	}

	@Override
	public boolean save(ReportComment entity) throws DaoException {
		try {
			return baseDao.save(entity);
		} catch (Exception e) {
			logger.error("保存评论信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws DaoException {
		ReportComment ReportComment = read(id);
		try {
			return baseDao.delete(ReportComment);
		} catch (Exception e) {
			logger.error("删除评论信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean update(ReportComment ReportComment) throws DaoException {
		try {
			return baseDao.update(ReportComment);
		} catch (Exception e) {
			logger.error("更新评论信息失败", e);
			throw new DaoException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(ReportComment.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(dc, filter, list);
			return baseDao.getCountByCriteria(dc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public PageResult<ReportComment> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ReportComment.class);
			List<Order> list = new ArrayList<>();
			DetachedCriteriaUtil.addSearchFilter(detachedCriteria, filter, list);
			return baseDao.findByPages(detachedCriteria, pageSize, currentPage, list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<ReportComment> queryAllBySearchFilter(SearchFilter filter) throws DaoException {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(ReportComment.class);
			DetachedCriteria detachedCriteria = DetachedCriteriaUtil.addSearchFilter(dc, filter, null);
			return baseDao.findAllByCriteria(detachedCriteria);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<ReportComment> objs) throws DaoException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<ReportComment> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<ReportComment> objs) throws DaoException {
		try {
			return baseDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}
}
