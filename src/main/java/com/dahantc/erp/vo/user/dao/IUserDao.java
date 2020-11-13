package com.dahantc.erp.vo.user.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.user.entity.User;

/**
 * @author 8514
 * @date 2019年3月19日 上午9:15:39
 */
public interface IUserDao {
	User read(Serializable id) throws DaoException;

	boolean save(User entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(User enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<User> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<User> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	User readOneByProperty(String property, Object value) throws BaseException;

	List<User> queryByHql(String hql, Map<String,Object> params , Integer max) throws DaoException;

	boolean updateByBatch(List<User> objs) throws DaoException;

	boolean saveByBatch(List<User> objs) throws DaoException;

	boolean deleteByBatch(List<User> objs) throws DaoException;

}
