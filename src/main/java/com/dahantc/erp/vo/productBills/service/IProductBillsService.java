package com.dahantc.erp.vo.productBills.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.bill.BillDataDetailDto;
import com.dahantc.erp.dto.bill.ProductBillsDto;
import com.dahantc.erp.dto.bill.ProductBillsExtendDto;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

public interface IProductBillsService {
	ProductBills read(Serializable id) throws ServiceException;

	boolean save(ProductBills entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ProductBills enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	boolean updateByBatch(List<ProductBills> objs) throws ServiceException;

	PageResult<ProductBills> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<ProductBills> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<ProductBills> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<ProductBills> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<ProductBills> objs) throws ServiceException;

	String getBillNumber(String billDate, String billType);

	String getBillsNumber(ProductBills productBills);

	List<ProductBills> getTodoBills(int entityType, String entityId, String flowClass, String needOrder);

	List<ProductBillsExtendDto> queryCompleteYearBills(Date yearDate, List<String> userIds, String settleType);

	List<ProductBillsExtendDto> queryCurrMonthBills(List<String> userIds, String settleType);

	BaseResponse<ProductBills> buildCustomerBill(String productId, String yearMonth, Boolean redo, Boolean requireData);

	String createMergePdf(List<ProductBills> billList, String filePath, JSONObject billTotal, List<String> optionList);

	void buildCheckBillFlow(List<ProductBills> billList);

	Map<String, BillDataDetailDto> getBillDataDetail(List<ProductBills> billList, Map<String, CustomerProduct> productMap, Customer customer);

	boolean restoreBill(ProductBillsDto productBillsDto) throws Exception;
}
