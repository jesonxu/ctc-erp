package com.dahantc.erp.vo.flowLabel.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.impl.FlowLabelServiceImpl.LabelValue;

public interface IFlowLabelService {
	FlowLabel read(Serializable id) throws ServiceException;

	boolean save(FlowLabel entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(FlowLabel enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<FlowLabel> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<FlowLabel> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<FlowLabel> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<FlowLabel> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<FlowLabel> objs) throws ServiceException;

	List<FlowLabel> readByIds(List<String> ids);

	List<LabelValue> getAllLabelValue(String flowMsg, String flowClass, String productId, Map<String, FlowLabel> cacheFlowLabelMap);

}
