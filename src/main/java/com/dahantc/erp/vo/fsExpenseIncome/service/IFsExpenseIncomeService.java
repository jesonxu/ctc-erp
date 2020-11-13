package com.dahantc.erp.vo.fsExpenseIncome.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.FsExpenseIncomeUI;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

public interface IFsExpenseIncomeService {
	FsExpenseIncome read(Serializable id) throws ServiceException;

	boolean save(FsExpenseIncome entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(FsExpenseIncome enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<FsExpenseIncome> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<FsExpenseIncome> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<FsExpenseIncome> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<FsExpenseIncomeUI> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<FsExpenseIncome> objs) throws ServiceException;

	BaseResponse<Boolean> saveBindFsExpenseIncome(String productBillsId, String feiId, BigDecimal thisCost);

	void saveWriteOffInfo(List<ProductBills> bills, List<FsExpenseIncome> fsExpenseIncomes, ApplyProcessReqDto reqDto, String ossUserId);

	UploadFileRespDto exportFsExpenseIncome(SearchFilter searchFilter, String fileName);

	JSONObject querySaleIncome(Date startTime, Date endTime, String timeType, OnlineUser onlineUser);

	boolean updateByBatch(List<FsExpenseIncome> objs) throws ServiceException;

}
