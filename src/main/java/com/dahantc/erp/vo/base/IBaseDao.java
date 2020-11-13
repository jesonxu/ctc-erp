package com.dahantc.erp.vo.base;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.type.Type;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;

/**
 * 基础数据层接口
 * 
 * @author 8515
 */
public interface IBaseDao {

	boolean save(Object entity) throws BaseException;

	boolean saveOrUpdate(Object entity) throws BaseException;

	boolean delete(Object entity) throws BaseException;

	boolean update(Object enterprise) throws BaseException;

	Object get(Class<?> entity, Serializable id) throws BaseException;

	int getCountByCriteria(DetachedCriteria detachedCriteria) throws BaseException;

	<T> PageResult<T> findByPages(DetachedCriteria detachedCriteria, int pageSize, int currentPage, List<Order> orders) throws BaseException;

	List<?> findByFilter(DetachedCriteria detachedCriteria, int size, int start, List<Order> orders) throws BaseException;

	<T> List<T> findAllByCriteria(DetachedCriteria detachedCriteria) throws BaseException;

	void executeUpdateSQL(String sql) throws BaseException;

	void executeUpdateSQL(List<String> sqlList) throws BaseException;

	Object getEntityByProperty(String propertyName, Object propertyValue, Class<?> entity) throws BaseException;

	List<?> getEntitysByProperty(String propertyName, Object propertyValue, Class<?> entity) throws BaseException;

	boolean updateByBatch(List<?> objs) throws BaseException;

	boolean saveByBatch(List<?> objs) throws BaseException;

	boolean saveOrUpdateByBatch(List<?> objs) throws BaseException;

	boolean deleteByBatch(List<?> objs) throws BaseException;

	/**
	 * 
	 * @param hql
	 * @param values
	 * @param maxCount
	 *            hql语句不支持limit，使用maxCount参数传递最大限制 0 代表没有限制
	 * @return
	 * @throws BaseException
	 */
	<T> List<T> findByhql(final String hql, final Map<String, Object> values, int maxCount) throws BaseException;

	int executeSqlUpdte(String sql, Object[] values) throws BaseException;

	int executeSqlUpdte(String sql, Object[] values, Type[] typeValues) throws BaseException;

	List<?> findAll(Class<?> entity) throws BaseException;

	List<?> find(String query, int limit);

	List<?> findAllByCriteria(DetachedCriteria detachedCriteria, int lenght);

	/**
	 * 使用hql语句进行分页查询
	 * 
	 * @param hql
	 * @param params
	 * @param pageSize
	 * @param currentPage
	 * @return
	 * @throws BaseException
	 */
	<T> PageResult<T> findByhql(String hql, final String countHql, Map<String, Object> params, int pageSize, int currentPage) throws BaseException;

	Connection getConnection() throws BaseException;

	List<?> selectSQL(String var1) throws BaseException;

	<T> List<T> selectSQL(String var1, Object[] var2) throws BaseException;

	boolean updateByBatch(List<?> objs, boolean needClear) throws BaseException;

	boolean saveByBatch(List<?> objs, boolean needClear) throws BaseException;

	<T> List<T> selectSQL(String sql, Map<String, Object> params) throws BaseException;

}