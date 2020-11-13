package com.dahantc.erp.vo.dept.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dept.entity.Department;

public interface IDepartmentDao {
	Department read(Serializable id) throws DaoException;

	List<Department> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
}
