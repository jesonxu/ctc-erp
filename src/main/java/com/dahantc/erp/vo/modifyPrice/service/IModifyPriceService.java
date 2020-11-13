package com.dahantc.erp.vo.modifyPrice.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;

public interface IModifyPriceService {
	ModifyPrice read(Serializable id) throws ServiceException;

	boolean save(ModifyPrice entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ModifyPrice enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<ModifyPrice> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<ModifyPrice> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<ModifyPrice> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<ModifyPrice> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	/**
	 * 查找产品的调价信息
	 * 
	 * @param productId
	 *            产品id
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @return 调价信息
	 */
	List<Map<String,Object>> findProductPriceInfo(String productId, Date start,Date end);
	
	Map<TimeQuantum, BigDecimal> findCurrentProductPriceInfo(String productId, Date startDate, Date endDate, int productType);
	
	Map<TimeQuantum, ModifyPrice> getModifyPrice(String productId, Date startDate, Date endDate);

	Map<TimeQuantum, Map<String, Double>> getInterPrices(List<ModifyPrice> modifyList, Date startDate, Date endDate);

	BigDecimal getDatePrice(Map<Date, Long> successCountMap, String productId, Date startDate, Date endDate);

	Map<String, Double> getInterCountryPrice(String productId, Date startDate, Date endDate);
}
