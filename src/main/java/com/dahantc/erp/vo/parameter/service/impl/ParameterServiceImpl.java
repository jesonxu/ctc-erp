package com.dahantc.erp.vo.parameter.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.parameter.dao.IParameterDao;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;

@Service("parameterService")
public class ParameterServiceImpl implements IParameterService {
	private static Logger logger = LogManager.getLogger(ParameterServiceImpl.class);

	@Resource
	private IBaseDao baseDao;

	@Autowired
	private IParameterDao parameterDao;

	@Override
	public Parameter read(Serializable id) throws ServiceException {
		try {
			return parameterDao.read(id);
		} catch (Exception e) {
			logger.error("读取系统参数失败", e);
			throw new ServiceException("读取系统参数失败", e);
		}
	}

	@Override
	public Parameter readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return parameterDao.readOneByProperty(property, value);
		} catch (DaoException e) {
			throw new ServiceException(e);
		} catch (Exception e) {
			logger.error("查询系统参数失败", e);
			throw new ServiceException("查询系统参数失败", e);
		}
	}

	@Override
	public boolean save(Parameter Parameter) throws ServiceException {
		try {
			return parameterDao.save(Parameter);
		} catch (Exception e) {
			logger.error("保存系统参数失败", e);
			throw new ServiceException("保存系统参数失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return parameterDao.delete(id);
		} catch (Exception e) {
			logger.error("删除系统参数失败", e);
			throw new ServiceException("删除系统参数失败", e);
		}
	}

	@Override
	public boolean update(Parameter enterprise) throws ServiceException {
		try {
			return parameterDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新系统参数失败", e);
			throw new ServiceException("更新系统参数失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return parameterDao.getCount(filter);
		} catch (Exception e) {
			logger.error("查询系统参数数量失败", e);
			throw new ServiceException("查询系统参数数量失败", e);
		}
	}

	@Override
	public PageResult<Parameter> findByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return parameterDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询系统参数分页信息失败", e);
			throw new ServiceException("查询系统参数分页信息失败", e);
		}
	}

	@Override
	public List<Parameter> findAllByCriteria(SearchFilter filter) throws ServiceException {
		try {
			return parameterDao.findAllByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询系统参数数量失败", e);
			throw new ServiceException("查询系统参数数量失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Parameter> objs) throws DaoException {
		try {
			return baseDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoException(e);
		}
	}

	@Override
	public List<Parameter> readByIds(List<String> entitieIds) {
		if (entitieIds == null || entitieIds.isEmpty()) {
			return null;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("entityid", Constants.ROP_IN, entitieIds));
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		try {
			return findAllByCriteria(searchFilter);
		} catch (ServiceException e) {
			logger.error("批量查询系统参数数量失败", e);
		}
		return null;
	}

	@Override
	public Parameter getOneParameterByProperty(String propertyName, Object propertyValue) throws ServiceException {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule(propertyName, Constants.ROP_EQ, propertyValue));
		List<Parameter> list = findAllByCriteria(searchFilter);
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public BigDecimal getPenaltyInterestRatio() {
		try {
			Parameter parameter = readOneByProperty("paramkey", "penalty_Interest_ration");
			if (parameter != null) {
				String paramValue = parameter.getParamvalue();
				if (StringUtils.isNotBlank(paramValue) && NumberUtils.isParsable(paramValue)) {
					return new BigDecimal(paramValue);
				}
			}
		} catch (ServiceException e) {
			logger.error("查询罚息利率异常", e);
		}
		return null;
	}

	@Override
	public String getSysParam(String key) {
		String value = null;
		try {
			Parameter parameter = getOneParameterByProperty("paramkey", key);
			if (parameter != null) {
				value = parameter.getParamvalue();
			}
			if (StringUtils.isNotBlank(value)) {
				value = value.trim();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return value;
	}

}
