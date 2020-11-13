package com.dahantc.erp.vo.base.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.vo.base.IBaseDao;

@Repository("baseDao")
public class BaseDaoImpl extends HibernateDaoSupport implements IBaseDao, Serializable {

	private static final long serialVersionUID = -6927927160709243134L;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void setSessionFactoryOverride() {
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public Connection getConnection() throws BaseException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			logger.info("获取当前连接对象异常：", e);
		}
		return conn;
	}

	/**
	 * @param Enterprise
	 *            添加。
	 * @return
	 * @throws BaseException
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean save(Object entity) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().save(entity);
			result = true;
		} catch (Exception e) {
			throw new BaseException("实体保存失败", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean saveOrUpdate(Object entity) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().saveOrUpdate(entity);
			result = true;
		} catch (Exception e) {
			throw new BaseException("实体saveOrUpdate失败", e);
		}
		return result;

	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean delete(Object entity) throws BaseException {
		boolean result = false;
		try {
			this.getHibernateTemplate().delete(entity);
			result = true;
		} catch (Exception e) {
			throw new BaseException("实体删除失败", e);
		}
		return result;
	}

	/**
	 * @param Enterprise
	 *            修改。
	 * @return
	 * @throws BaseException
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean update(Object enterprise) throws BaseException {
		boolean result = false;
		try {
			this.getHibernateTemplate().update(enterprise);
			result = true;
		} catch (Exception e) {
			throw new BaseException("实体修改失败", e);
		}
		return result;

	}

	@Override
	public Object get(final Class<?> entity, final Serializable id) throws BaseException {
		try {
			return getHibernateTemplate().get(entity, id);
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}

	@Override
	public <T> PageResult<T> findByPages(final DetachedCriteria detachedCriteria, final int pageSize, final int currentPage, final List<Order> orders)
			throws BaseException {
		try {
			return (PageResult<T>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<PageResult<T>>() {
				@Override
				public PageResult<T> doInHibernate(Session session) throws HibernateException {
					Criteria criteria = detachedCriteria.getExecutableCriteria(session);
					long start = System.currentTimeMillis();
					int totalCount = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
					if (logger.isDebugEnabled()) {
						logger.info("Count(*) 耗时：[ " + (System.currentTimeMillis() - start) + " ]毫秒");
						start = System.currentTimeMillis();
					}
					// 解析order
					for (Order order : orders) {
						if (null != order) {
							detachedCriteria.addOrder(order);
						}
					}
					criteria.setProjection(null);
					criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
					List<?> items = criteria.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize).list();
					if (logger.isDebugEnabled()) {
						logger.info("查询分页数据 耗时：[ " + (System.currentTimeMillis() - start) + " ]毫秒");
					}
					PageResult<T> ps = new PageResult<>(items, totalCount);
					return ps;
				}
			});
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}

	@Override
	public List<?> findByFilter(final DetachedCriteria detachedCriteria, final int size, final int start, final List<Order> orders) throws BaseException {
		try {
			return (List<?>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<?>>() {
				@Override
				public List<?> doInHibernate(Session session) throws HibernateException {
					Criteria criteria = detachedCriteria.getExecutableCriteria(session);
					long startTime = System.currentTimeMillis();
					// 解析order
					for (Order order : orders) {
						if (null != order) {
							detachedCriteria.addOrder(order);
						}
					}
					criteria.setProjection(null);
					criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
					List<?> items = criteria.setFirstResult(start).setMaxResults(size).list();
					if (logger.isDebugEnabled()) {
						logger.info("查询分页数据 耗时：[ " + (System.currentTimeMillis() - startTime) + " ]毫秒");
					}
					return items;
				}
			});
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}

	@Override
	public <T> List<T> findAllByCriteria(final DetachedCriteria detachedCriteria) throws BaseException {
		try {
			return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
				@SuppressWarnings("unchecked")
				public List<T> doInHibernate(Session session) throws HibernateException {
					return detachedCriteria.getExecutableCriteria(session).list();
				}
			});
		} catch (Exception e) {
			logger.error("getHibernateTemplate().executeWithNativeSession(HibernateCallback) 执行异常：", e);
			throw new BaseException(e);
		}
	}

	@Override
	public int getCountByCriteria(final DetachedCriteria detachedCriteria) throws BaseException {
		try {
			Long count = (Long) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					Criteria criteria = detachedCriteria.getExecutableCriteria(session);
					return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
				}
			});
			return count.intValue();
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}

	@Override
	public List<?> findAllByCriteria(final DetachedCriteria detachedCriteria, final int lenght) {
		return (List<?>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Object>() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria criteria = detachedCriteria.getExecutableCriteria(session);
				criteria.setFirstResult(0);
				criteria.setMaxResults(lenght);
				return criteria.list();
			}
		});
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void executeUpdateSQL(final String sql) throws BaseException {
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Object>() {
				public Long doInHibernate(Session session) throws HibernateException {
					Query<?> query = null;
					try {
						query = session.createSQLQuery(sql);
						return new Long(query.executeUpdate());
					} catch (Exception e) {
						throw new HibernateException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		}
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void executeUpdateSQL(final List<String> sqlList) throws BaseException {
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Object>() {
				public Long doInHibernate(Session session) throws HibernateException {
					long result = 0l;
					Query<?> query = null;
					try {
						int count = 0;
						for (String sql : sqlList) {
							query = session.createSQLQuery(sql);
							result += query.executeUpdate();
							count++;
							if (count % 1000 == 0) {
								session.flush();
								session.clear();
							}
						}
					} catch (Exception e) {
						throw new HibernateException(e);
					} finally {
						session.flush();
						session.clear();
					}
					return result;
				}
			});
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		}
	}

	@Override
	public Object getEntityByProperty(String propertyName, Object propertyValue, Class<?> entity) throws BaseException {
		List<?> list = getEntitysByProperty(propertyName, propertyValue, entity);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<?> getEntitysByProperty(String propertyName, Object propertyValue, Class<?> entity) throws BaseException {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(entity);
		Field[] fields = entity.getDeclaredFields();
		for (Field field : fields) {
			if ("status".equals(field.getName())) {
				detachedCriteria.add(Restrictions.eq("status", EntityStatus.NORMAL.ordinal()));
			}
		}
		detachedCriteria.add(Restrictions.eq(propertyName, propertyValue));
		return this.findAllByCriteria(detachedCriteria);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean updateByBatch(final List<?> objs) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.update(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
								// session.clear();
							}
						}
					}
					session.flush();
					// session.clear();
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量修改失败", e);
			throw new BaseException("批量修改失败：", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean updateByBatch(final List<?> objs, boolean needClear) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.update(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
								if (needClear) {
									session.clear();
								}
							}
						}
					}
					session.flush();
					if (needClear) {
						session.clear();
					}
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量修改失败", e);
			throw new BaseException("批量修改失败：", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean saveByBatch(final List<?> objs) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.save(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
								// session.clear();
							}
						}
					}
					session.flush();
					// session.clear();
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量保存失败", e);
			throw new BaseException("批量保存异常", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean saveByBatch(final List<?> objs, boolean needClear) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.save(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
								if (needClear) {
									session.clear();
								}
							}
						}
					}
					session.flush();
					if (needClear) {
						session.clear();
					}
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量保存失败", e);
			throw new BaseException("批量保存异常", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean saveOrUpdateByBatch(List<?> objs) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.saveOrUpdate(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
								// session.clear();
							}
						}
					}
					session.flush();
					// session.clear();
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量保存失败", e);
			throw new BaseException("批量保存异常", e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean deleteByBatch(final List<?> objs) throws BaseException {
		boolean result = false;
		try {
			getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					int count = 0;
					for (Object obj : objs) {
						if (obj != null) {
							session.delete(obj);
							count++;
							if (count % 1000 == 0) {
								session.flush();
							}
						}
					}
					session.flush();
					return new Long(count);
				}
			});
			result = true;
		} catch (Exception e) {
			logger.error("批量删除执行失败", e);
			throw new BaseException("批量删除执行失败", e);
		}
		return result;
	}

	@Override
	public <T> List<T> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws BaseException {
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
			@SuppressWarnings("unchecked")
			public List<T> doInHibernate(Session session) throws HibernateException {
				Query<?> _query = session.createQuery(hql);
				if (maxCount > 0) {
					_query.setMaxResults(maxCount);
				}
				if (params != null) {
					for (Map.Entry<String, Object> entry : params.entrySet()) {
						if (entry.getValue() instanceof Date) {
							_query.setParameter(entry.getKey().toString(), (Date) entry.getValue(), TimestampType.INSTANCE);
						} else if (entry.getValue() instanceof Collection<?>) {
							_query.setParameterList(entry.getKey().toString(), (Collection<?>) entry.getValue());
						} else {
							_query.setParameter(entry.getKey().toString(), entry.getValue());
						}
					}
				}
				return (List<T>) _query.list();
			}
		});
	}

	@Override
	public <T> PageResult<T> findByhql(final String hql, final String countHql, final Map<String, Object> params, final int pageSize, final int currentPage)
			throws BaseException {
		return (PageResult<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<PageResult<T>>() {
			public PageResult<T> doInHibernate(Session session) throws HibernateException {
				long _start = System.currentTimeMillis();
				Query<?> _countquery = session.createQuery(countHql);
				if (params != null) {
					for (Map.Entry<String, Object> entry : params.entrySet()) {
						if (entry.getValue() instanceof Date) {
							_countquery.setParameter(entry.getKey().toString(), (Date) entry.getValue(), TimestampType.INSTANCE);
						} else if (entry.getValue() instanceof Collection<?>) {
							_countquery.setParameterList(entry.getKey().toString(), (Collection<?>) entry.getValue());
						} else {
							_countquery.setParameter(entry.getKey().toString(), entry.getValue());
						}
					}
				}
				Object obj = _countquery.uniqueResult();
				logger.info("Count耗时：" + (System.currentTimeMillis() - _start));
				if (null == obj) {
					return new PageResult<>();
				}
				long count = (long) obj;
				Query<?> _query = session.createQuery(hql).setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
				if (params != null) {
					for (Map.Entry<String, Object> entry : params.entrySet()) {
						if (entry.getValue() instanceof Date) {
							_query.setParameter(entry.getKey().toString(), (Date) entry.getValue(), TimestampType.INSTANCE);
						} else if (entry.getValue() instanceof Collection<?>) {
							_query.setParameterList(entry.getKey().toString(), (Collection<?>) entry.getValue());
						} else {
							_query.setParameter(entry.getKey().toString(), entry.getValue());
						}
					}
				}
				PageResult<T> page = new PageResult<>(_query.list(), count);
				return page;
			}
		});
	}

	@Override
	public List<?> find(final String query, final int limit) {
		return (List<?>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<?>>() {
			public List<?> doInHibernate(Session session) throws HibernateException {
				if (limit != -1) {
					Query<?> _query = session.createQuery(query).setMaxResults(limit);
					return _query.list();
				} else {
					Query<?> _query = session.createQuery(query);
					return _query.list();
				}
			}
		});
	}

	@Override
	public List<?> findAll(final Class<?> entity) throws BaseException {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(entity);
		return this.findAllByCriteria(detachedCriteria);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public int executeSqlUpdte(final String sql, final Object[] values) throws BaseException {
		long result = 0;
		try {
			result = getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {
					Query<?> query = session.createSQLQuery(sql);
					int len = values.length;
					for (int i = 0; i < len; i++) {
						if (values[i] instanceof Date) {
							query.setParameter(i + 1, (Date) values[i], TimestampType.INSTANCE);
						} else {
							query.setParameter(i + 1, values[i]);
						}
					}
					return new Long(query.executeUpdate());
				}
			});
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		}
		return (int) result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public int executeSqlUpdte(final String sql, final Object[] values, final Type[] typeValues) throws BaseException {
		long result = 0;
		long _start = System.currentTimeMillis();
		try {
			result = getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {
				public Long doInHibernate(Session session) throws HibernateException {

					Query<?> query = session.createSQLQuery(sql);
					int len = values.length;
					for (int i = 0; i < len; i++) {
						query.setParameter(i + 1, values[i], typeValues[i]);
					}
					logger.debug("executeSqlUpdte封装参数耗时：" + (System.currentTimeMillis() - _start));
					return new Long(query.executeUpdate());
				}
			});
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		} finally {
			logger.debug("executeSqlUpdte执行查询耗时：" + (System.currentTimeMillis() - _start));
		}
		return (int) result;
	}

	@Override
	public List<?> selectSQL(String sql) throws BaseException {
		return this.selectSQL(sql, (Object[]) null);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public <T> List<T> selectSQL(final String sql, final Object[] values) throws BaseException {
		List<T> list = null;

		try {
			list = (List<T>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
				@SuppressWarnings("unchecked")
				public List<T> doInHibernate(Session session) throws HibernateException {
					List<T> list = null;

					try {
						Query<?> query = session.createSQLQuery(sql);
						if (values != null) {
							for (int i = 0; i < values.length; ++i) {
								if (values[i] instanceof Date) {
									query.setParameter(i + 1, (Date) values[i], TimestampType.INSTANCE);
								} else {
									query.setParameter(i + 1, values[i]);
								}
							}
						}

						list = (List<T>) query.list();
					} catch (Exception e) {
						throw new HibernateException(e);
					}
					return list;
				}
			});
			return list;
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		}
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public <T> List<T> selectSQL(final String sql, final Map<String, Object> params) throws BaseException {
		List<T> list = null;

		try {
			list = (List<T>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
				@SuppressWarnings("unchecked")
				public List<T> doInHibernate(Session session) throws HibernateException {
					List<T> list = null;

					try {
						Query<?> query = session.createSQLQuery(sql);
						if (params != null) {
							for (Map.Entry<String, Object> entry : params.entrySet()) {
								if (entry.getValue() instanceof Date) {
									query.setParameter(entry.getKey().toString(), (Date) entry.getValue(), TimestampType.INSTANCE);
								} else if (entry.getValue() instanceof Collection<?>) {
									query.setParameterList(entry.getKey().toString(), (Collection<?>) entry.getValue());
								} else {
									query.setParameter(entry.getKey().toString(), entry.getValue());
								}
							}
						}
						list = (List<T>) query.list();
					} catch (Exception e) {
						throw new HibernateException(e);
					}
					return list;
				}
			});
			return list;
		} catch (Exception e) {
			throw new BaseException("脚本执行异常：", e);
		}
	}
}