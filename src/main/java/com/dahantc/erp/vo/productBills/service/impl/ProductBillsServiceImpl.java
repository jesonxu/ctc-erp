package com.dahantc.erp.vo.productBills.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.dahantc.erp.dto.bill.ProductBillsDto;
import com.dahantc.erp.enums.YysType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.bill.BillDataDetailDto;
import com.dahantc.erp.dto.bill.DateDetail;
import com.dahantc.erp.dto.bill.ProductBillsExtendDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.util.BillInfo;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.util.CreateBillExcelUtil;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.IText5Pdf4MergeUtil;
import com.dahantc.erp.util.IText5PdfUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.deductionPrice.service.IDeductionPriceService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productBills.dao.IProductBillsDao;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("productBillsService")
public class ProductBillsServiceImpl implements IProductBillsService {
	private static Logger logger = LogManager.getLogger(ProductBillsServiceImpl.class);

	private static String QUERY_COMPLETE_YEAR_BILL_SQL = "SELECT pb.deptid, cp.ossuserid, pb.entityid, SUM(pb.receivables), SUM(pb.actualreceivables), SUM(actualinvoiceamount), pb.wtime FROM erp_bill pb INNER JOIN erp_customer_product cp ON pb.productid = cp.productid WHERE pb.entitytype = "
			+ EntityType.CUSTOMER.ordinal();

	private static String QUERY_CURRENT_MONTH_BILL_SQL = "SELECT cs.deptid, cs.saleuserid, cs.customerid, SUM(cs.receivables) FROM erp_customer_statistics cs INNER JOIN erp_customer_product cp ON cs.productid = cp.productid WHERE TRUE";

	private static final String billFileDir = "billfiles";

	@Autowired
	private IProductBillsDao productBillsDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IDeductionPriceService deductionPriceService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public ProductBills read(Serializable id) throws ServiceException {
		try {
			return productBillsDao.read(id);
		} catch (Exception e) {
			logger.error("读取产品账单表失败", e);
			throw new ServiceException("读取产品账单表失败", e);
		}
	}

	@Override
	public boolean save(ProductBills entity) throws ServiceException {
		try {
			return productBillsDao.save(entity);
		} catch (Exception e) {
			logger.error("保存产品账单表失败", e);
			throw new ServiceException("保存产品账单表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<ProductBills> objs) throws ServiceException {
		try {
			return productBillsDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean updateByBatch(List<ProductBills> objs) throws ServiceException {
		try {
			return baseDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return productBillsDao.delete(id);
		} catch (Exception e) {
			logger.error("删除产品账单表失败", e);
			throw new ServiceException("删除产品账单表失败", e);
		}
	}

	@Override
	public boolean update(ProductBills enterprise) throws ServiceException {
		try {
			return productBillsDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新产品账单表失败", e);
			throw new ServiceException("更新产品账单表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return productBillsDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询产品账单表数量失败", e);
			throw new ServiceException("查询产品账单表数量失败", e);
		}
	}

	@Override
	public PageResult<ProductBills> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return productBillsDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询产品账单表分页信息失败", e);
			throw new ServiceException("查询产品账单表分页信息失败", e);
		}
	}

	@Override
	public List<ProductBills> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return productBillsDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询产品账单表失败", e);
			throw new ServiceException("查询产品账单表失败", e);
		}
	}

	@Override
	public List<ProductBills> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return productBillsDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询产品账单表失败", e);
			throw new ServiceException("查询产品账单表失败", e);
		}
	}

	@Override
	public List<ProductBills> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return productBillsDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询产品账单表失败", e);
			throw new ServiceException("查询产品账单表失败", e);
		}
	}

	/**
	 * 生成账单编号
	 * 
	 * @param billDate
	 *            账单月份，yyyy-MM
	 * 
	 * @param billType
	 *            账单类型
	 * @return
	 */
	@Override
	public String getBillNumber(String billDate, String billType) {
		try {
			List<Object> result = baseDao.selectSQL("SELECT get_bill_num(?, ?)", new Object[] { billDate, billType });
			if (!CollectionUtils.isEmpty(result)) {
				return result.get(0).toString();
			}
		} catch (BaseException e) {
			logger.error("构建账单编号异常：", e);
		}
		return null;
	}

	@Override
	public String getBillsNumber(ProductBills productBills) {
		if (StringUtils.isNotBlank(productBills.getBillNumber())) {
			return productBills.getBillNumber();
		}
		return "Dhcc-Bill-" + DateUtil.convert(productBills.getWtime(), DateUtil.format4) + Constants.CUST_PRODUCT_BILL_NUM_KEY + "000000";
	}

	/**
	 * 按流程类别，获取客户/供应商下所有未处理完的账单。 eg: 开票流程，返回未开完票的流程； 付款流程，返回未付完款的账单； 收款流程，返回未收完款的流程；
	 *
	 * @param entityType
	 *            主体类型
	 * @param entityId
	 *            主体id
	 * @param flowClass
	 *            流程类别
	 * @param needOrder
	 *            是否需要按时间排序（T为需要，留空为不需要）
	 * @return
	 */
	@Override
	public List<ProductBills> getTodoBills(int entityType, String entityId, String flowClass, String needOrder) {
		logger.info("查询未处理完的账单开始，entityType：" + entityType + "，entityId：" + entityId + "，flowClass：" + flowClass);
		if (StringUtils.isAnyBlank(entityType + "", entityId, flowClass)) {
			logger.info("条件不完整无法获取账单");
			return null;
		}
		try {
			if (EntityType.CUSTOMER.getCode() == entityType) {
				Customer customer = customerService.read(entityId);
				if (null == customer) {
					logger.info("按客户id找不到对应的客户：" + entityId);
					return null;
				}
			} else if (EntityType.SUPPLIER.getCode() == entityType) {
				Supplier supplier = supplierService.read(entityId);
				if (null == supplier) {
					logger.info("按供应商id找不到对应的供应商：" + entityId);
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("按entityId查询客户/供应商异常：" + entityId + "，entityType：" + entityType, e);
			return null;
		}
		String hql = "select t From ProductBills t WHERE t.entityId = :entityId and billStatus not in (" + BillStatus.NO_RECONCILE.ordinal() + ", "
				+ BillStatus.RECONILING.ordinal() + ")";
		if (Constants.BILL_PAYMENT_FLOW_CLASS.equals(flowClass)) {
			// 账单付款流程，只显示未付清的账单
			hql += " and t.payables > t.actualPayables";
		} else if (Constants.REMUNERATION_FLOW_CLASS.equals(flowClass)) {
			// 酬金流程，只显示未收完的账单
			hql += " and t.receivables > t.actualReceivables";
		} else if (Constants.BILL_RECEIVABLES_FLOW_CLASS.equals(flowClass)) {
			// 销售账单收款流程，只显示未收完的账单
			hql += " and t.receivables > t.actualReceivables";
		} else if (Constants.INVOICE_CLASS.equals(flowClass)) {
			// 账单开票流程，只显示未开完的账单
			hql += " and t.receivables > t.actualInvoiceAmount";
		}
		if (StringUtils.equals(needOrder, "T")) {
			hql += " order by wtime asc";
		}

		Map<String, Object> params = new HashMap<>();
		params.put("entityId", entityId);
		List<ProductBills> billList = null;
		try {
			billList = findByhql(hql, params, 0);
		} catch (ServiceException e) {
			logger.error("查询未处理完的账单异常，entityType：" + entityType + "，entityId：" + entityId + "，flowClass：" + flowClass, e);
		}
		return billList;
	}

	@Override
	public List<ProductBillsExtendDto> queryCompleteYearBills(Date yearDate, List<String> userIds, String settleType) {
		try {
			String sql = QUERY_COMPLETE_YEAR_BILL_SQL;
			Map<String, Object> params = new HashMap<>();
			if (StringUtils.isNotBlank(settleType) && NumberUtils.isParsable(settleType)) {
				int sType = Integer.valueOf(settleType);
				if (sType > 0) {
					if (sType == SettleType.After.ordinal()) {
						sql += " AND cp.settletype = :settleType";
						params.put("settleType", SettleType.After.ordinal());
					} else {
						sql += " AND cp.settletype <> :settleType";
						params.put("settleType", SettleType.After.ordinal());
					}
				}
			}
			if (!CollectionUtils.isEmpty(userIds)) {
				sql += " AND cp.ossuserid IN (:ossUserIds)";
				params.put("ossUserIds", userIds);
			}
			sql += " AND pb.wtime >= :startDate AND pb.wtime < :endDate GROUP BY pb.entityid, pb.wtime";
			params.put("startDate", yearDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(yearDate);
			calendar.add(Calendar.YEAR, 1);
			params.put("endDate", calendar.getTime());
			List<Object[]> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				return list.stream().filter(obj -> obj != null && obj.length >= 7 && StringUtils.isNoneBlank((String) obj[0], (String) obj[1])).map(obj -> {
					ProductBillsExtendDto dto = new ProductBillsExtendDto();
					dto.setDeptId((String) obj[0]);
					dto.setOssUserId((String) obj[1]);
					dto.setEntityId((String) obj[2]);
					dto.setReceivables(obj[3] == null ? BigDecimal.ZERO : new BigDecimal(obj[3].toString()));
					dto.setActualReceivables(obj[4] == null ? BigDecimal.ZERO : new BigDecimal(obj[4].toString()));
					dto.setActualInvoiceAmount(obj[5] == null ? BigDecimal.ZERO : new BigDecimal(obj[5].toString()));
					dto.setWtime(new Timestamp(((Date) obj[6]).getTime()));
					return dto;
				}).filter(dto -> StringUtils.isNoneBlank(dto.getOssUserId(), dto.getDeptId())).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return null;
	}

	// 单月账单
	@Override
	public List<ProductBillsExtendDto> queryCurrMonthBills(List<String> userIds, String settleType) {
		try {
			Date startTime = DateUtil.getCurrentMonthFirstDay();
			Date endTime = DateUtil.getNextMonthFirst();
			String sql = QUERY_CURRENT_MONTH_BILL_SQL;
			Map<String, Object> params = new HashMap<>();
			if (StringUtils.isNotBlank(settleType) && NumberUtils.isParsable(settleType)) {
				int sType = Integer.valueOf(settleType);
				if (sType > 0) {
					if (sType == SettleType.After.ordinal()) {
						sql += " AND cp.settletype = :settleType";
						params.put("settleType", SettleType.After.ordinal());
					} else {
						sql += " AND cp.settletype <> :settleType";
						params.put("settleType", SettleType.After.ordinal());
					}
				}
			}
			if (!CollectionUtils.isEmpty(userIds)) {
				sql += " AND cs.saleuserid IN (:saleUserIds)";
				params.put("saleUserIds", userIds);
			}
			sql += " AND cs.statsDate >= :startDate AND cs.statsDate < :endDate GROUP BY cs.customerid";
			params.put("startDate", startTime);
			params.put("endDate", endTime);
			List<Object[]> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				return list.stream().filter(obj -> obj != null && obj.length >= 4 && StringUtils.isNoneBlank((String) obj[0], (String) obj[1])).map(obj -> {
					ProductBillsExtendDto dto = new ProductBillsExtendDto();
					dto.setDeptId((String) obj[0]);
					dto.setOssUserId((String) obj[1]);
					dto.setEntityId((String) obj[2]);
					dto.setReceivables(obj[3] == null ? BigDecimal.ZERO : new BigDecimal(obj[3].toString()));
					dto.setWtime(new Timestamp(DateUtil.getThisMonthFirst(new Date()).getTime()));
					return dto;
				}).filter(dto -> StringUtils.isNoneBlank(dto.getOssUserId(), dto.getDeptId())).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 生产产品某月的账单
	 *
	 * @param productId
	 *            产品id
	 * @param yearMonth
	 *            账单月
	 * @param redo
	 *            是否重新计算，true覆盖原来的，false已有就不生成
	 * @param requireData
	 *            是否必须有统计数据，true必须在统计表有数据，false可以没有统计数据
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public BaseResponse<ProductBills> buildCustomerBill(String productId, String yearMonth, Boolean redo, Boolean requireData) {
		logger.info("生成客户产品账单记录开始，productId：" + productId + "，账单月份：" + yearMonth);
		if (StringUtils.isAnyBlank(productId, yearMonth)) {
			logger.info("产品id和账单月份不能为空");
			return BaseResponse.error("生成失败：产品id和账单月份不能为空");
		}
		CustomerProduct customerProduct = null;
		Customer customer = null;
		try {
			customerProduct = customerProductService.read(productId);
			customer = customerService.read(customerProduct.getCustomerId());
		} catch (Exception e) {
			logger.error("查询客户/产品异常，生成账单失败，productId：" + productId);
			return BaseResponse.error("生成失败：查询客户/产品异常");
		}

		// 判断账单时间段是否有调价记录
		Map<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice> mpRecord = null;
		Date startDate = DateUtil.convert(yearMonth + "-01", DateUtil.format1);
		Date endDate = DateUtil.getMonthFinal(startDate);
		mpRecord = modifyPriceService.getModifyPrice(productId, startDate, endDate);
		// 没有调价记录，不能创建账单
		if (CollectionUtils.isEmpty(mpRecord)) {
			logger.info("产品在账单月份没有调价，生成账单失败，productId：" + productId + "，账单月份：" + yearMonth);
			return BaseResponse.error("生成失败：产品在账单月份没有调价");
		}

		boolean isUpdate = false;
		ProductBills bill = null;
		// 查询是否已有账单，根据是否重新计算条件进行后续动作
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endDate));
		filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
		try {
			List<ProductBills> billList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(billList)) {
				// 已有账单
				if (redo) {
					// 要重新统计，一般是手动点刷新
					bill = billList.get(0);
					isUpdate = true;
					logger.info("产品在账单月份已有账单，重新统计数据，productId：" + productId + "，账单月份：" + yearMonth);
				} else {
					// 不重新统计，自动或手动触发
					logger.info("产品在账单月份已有账单，本次不重新生成，productId：" + productId + "，账单月份：" + yearMonth);
					return BaseResponse.error("生成失败：产品在账单月份已有账单");
				}
			} else {
				// 全新生成
				bill = new ProductBills();
			}
		} catch (Exception e) {
			logger.error("查询产品在账单月份是否已有账单异常，productId：" + productId + "，账单月份：" + yearMonth);
			return BaseResponse.error("生成失败：查询产品在账单月份是否已有账单异常");
		}

		try {
			boolean isInter = false; // 是否国际账单
			long successCount = 0; // 平台成功数
			BigDecimal receivables = null; // 平台账单金额（应收）

			if (!isUpdate) {
				// 新生成的账单用新的账单编号，已存在的账单继续沿用之前的账单编号
				String billNumber = getBillNumber(yearMonth, Constants.CUST_PRODUCT_BILL_NUM_KEY);
				bill.setBillNumber(billNumber);
			}

			// 获取账单数据，包括文件
			String billData = getCustomerBillData(customerProduct, yearMonth, mpRecord, bill.getBillNumber(), requireData);
			if (StringUtil.isBlank(billData)) {
				logger.info("未获取到账单数据，不生成账单，productId：" + productId + "，账单月份：" + yearMonth);
				return BaseResponse.error("生成失败：未获取到账单数据");
			}
			JSONObject billDataJson = JSONObject.parseObject(billData, Feature.OrderedField);
			// 平台账单金额
			String receivablesStr = (String) billDataJson.getOrDefault(Constants.DAHAN_PAYMENT_AMOUNT_KEY, "0.00");
			receivables = new BigDecimal(receivablesStr);
			// 平台成功数
			if (billDataJson.containsKey(Constants.DAHAN_SUCCESS_COUNT_KEY)) {
				successCount = billDataJson.getLongValue(Constants.DAHAN_SUCCESS_COUNT_KEY);
			}
			// 备注
			if (billDataJson.containsKey(Constants.DAHAN_REMARK_KEY)) {
				String remark = billDataJson.getString(Constants.DAHAN_REMARK_KEY);
				bill.setRemark(remark);
			}

			// 自动生成账单时，未达到发送量阈值的客户不出账单
			String assertMsg = assertHasBill(productId, successCount);
			if (requireData && StringUtils.isNotBlank(assertMsg)) {
				return BaseResponse.error("生成失败：" + assertMsg);
			}

			Date billMonth = DateUtil.convert(yearMonth, DateUtil.format4);

			// 收/付款截止日期为 出账单月往后 账期个月 的月底，比如5月出4月账单，截止日期为5月31号23:59:59
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(billMonth);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, customerProduct.getBillPeriod() + 1);
			calendar.add(Calendar.SECOND, -1);

			Timestamp finalTime = new Timestamp(calendar.getTimeInMillis());
			bill.setFinalPayTime(finalTime);
			bill.setFinalReceiveTime(finalTime);

			bill.setEntityId(customerProduct.getCustomerId());
			bill.setProductId(productId);
			bill.setPlatformCount(successCount);
			bill.setSupplierCount(successCount);
			// 应付金额
			bill.setPayables(new BigDecimal(0));
			// 应收金额
			bill.setReceivables(receivables);
			// 实付金额
			bill.setActualPayables(new BigDecimal(0));
			// 实收金额
			bill.setActualReceivables(BigDecimal.ZERO);
			bill.setWtime(new Timestamp(billMonth.getTime()));
			bill.setEntityType(EntityType.CUSTOMER.ordinal()); // 客户
			bill.setDeptId(customer.getDeptId());

			List<String> loginNameList = Arrays.asList(customerProduct.getAccount().split("\\|"));
			loginNameList = loginNameList.stream().map(String::trim).filter(StringUtil::isNotBlank).collect(Collectors.toList());
			// 设置账单账号为产品有效账号
			bill.setLoginName(String.join(",", loginNameList));
			// 查产品在当月的总成本
			BigDecimal totalCost = customerProductService.queryCustomerProductCost(new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()),
					loginNameList, customerProduct.getProductType(), customerProduct.getYysType());
			bill.setCost(totalCost.setScale(2, BigDecimal.ROUND_HALF_UP));
			// 平均销售单价
			if (successCount > 0) {
				bill.setUnitPrice(receivables.divide(new BigDecimal(successCount), 6, BigDecimal.ROUND_HALF_UP));
			}
			// 账单毛利润 = 销售额 - 综合成本 （实际提成表的毛利润 = 账单毛利润 - 运营成本）
			bill.setGrossProfit(receivables.subtract(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP));

			// 账单电子文件，excel、pdf
			if (billDataJson.containsKey(Constants.DAHAN_BILL_FILE_KEY)) {
				String files = billDataJson.getString(Constants.DAHAN_BILL_FILE_KEY);
				bill.setFiles(files);
			}

			boolean result = false;
			String msg = "";
			if (isUpdate) {
				result = this.update(bill);
				msg = "更新账单记录" + (result ? "成功" : "失败");
				logger.info("重新统计账单数据" + (result ? "成功" : "失败") + "，productId：" + productId + "，账单月份：" + yearMonth);
			} else {
				result = this.save(bill);
				msg = "生成账单记录" + (result ? "成功" : "失败");
				logger.info("生成账单记录" + (result ? "成功" : "失败") + "，productId：" + productId + "，账单月份：" + yearMonth);
			}
			return result ? BaseResponse.success(msg, bill) : BaseResponse.error(msg);
		} catch (Exception e) {
			logger.error("生成客户产品账单记录异常，productId：" + productId + "，账单月份：" + yearMonth, e);
			return BaseResponse.error("生成失败：生成客户产品账单记录异常");
		}
	}

	// 判断是否可以出账单
	private String assertHasBill(String productId, long successCount) {
		try {
			CustomerProduct product = customerProductService.read(productId);
			String customerId = product.getCustomerId();
			Customer cust = customerService.read(customerId);
			String customerTypeId = cust.getCustomerTypeId();
			CustomerType custType = customerTypeService.read(customerTypeId);
			if (custType.getCustomerTypeValue() == CustomerTypeValue.CONTRACTED.ordinal()) { // 合同客户直接出账单
				return null;
			}

			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.BILL_SEND_COUNT_THRESHOLD);
			if (null == parameter) {
				return null;
			}
			// 未设置阈值，或发送量 >= 阈值 则生成账单
			if (StringUtils.isBlank(parameter.getParamvalue())
					|| (NumberUtils.isParsable(parameter.getParamvalue()) && Long.parseLong(parameter.getParamvalue()) <= successCount)) {
				return null;
			} else {
				String msg = "非合同客户发送量：" + successCount + "，阈值：" + parameter.getParamvalue() + "，未达到出账单条件";
				logger.info(msg);
				return msg;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return "账单生成异常";
	}

	/**
	 * 获取账单数据，同时生成单个账单的文件
	 * 
	 * @param product
	 *            产品
	 * @param billMonth
	 *            账单月份
	 * @param mpRecord
	 *            调价记录
	 * @param billNumber
	 *            账单编号（用于生成文件名）
	 * @param requireData
	 *            是否必须有统计数据，true必须在统计表有数据，false可以没有统计数据
	 * @return JSONObject
	 */
	private String getCustomerBillData(CustomerProduct product, String billMonth, Map<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice> mpRecord,
			String billNumber, Boolean requireData) {
		// 数据统计时间 账单月份的月初到月末
		Date startDate = DateUtil.convert(billMonth, DateUtil.format4);
		Date endDate = DateUtil.getMonthFinal(startDate);
		// 获取产品下的账号
		String accounts = product.getAccount();
		// 产品类型
		int productType = product.getProductType();
		// 每个账号的成功数，在账单文件内容中展示
		Map<String, Long> accountSuccessMap = new HashMap<>(); // 账号 --> 发送量
		String dataStr = null;
		// 支持运营商
		String yysType = product.getYysType();
		// 国际
		if (productType == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS)) {
			// 获取每天在各个国家的成功数，同时填充每个账号的成功数
			Map<Date, Map<String, Long>> countryCountMap = getPlatformSuccessDateCount4Inter(productType, accounts, startDate, endDate, accountSuccessMap);
			if (null == countryCountMap) {
				if (requireData) {
					// 必须要有统计数据
					return null;
				} else {
					// 允许没有统计数据
					JSONObject json = new JSONObject();
					json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, "0");
					json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, "0.00");
					return json.toJSONString();
				}
			}
			// 根据成功数和调价，统计账单基础数据（成功数、单价、总金额）
			dataStr = getCustomerBillBaseData4Inter(product, countryCountMap, startDate, endDate, mpRecord);
		} else {
			// 获取每天的成功数，同时填充每个账号的成功数
			Map<Date, Long> platformSuccessCountMap = getPlatformSuccessDateCount(productType, accounts, startDate, endDate, accountSuccessMap, yysType);
			if (null == platformSuccessCountMap) {
				if (requireData) {
					// 必须要有统计数据
					return null;
				} else {
					// 允许没有统计数据
					JSONObject json = new JSONObject();
					json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, "0");
					json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, "0.00");
					return json.toJSONString();
				}
			}
			// 根据成功数和调价，统计账单基础数据（成功数、单价、总金额）
			dataStr = getCustomerBillBaseData(product, platformSuccessCountMap, startDate, endDate, mpRecord);
		}
		// {BILL_PRICE_INFO_KEY: [{一个调价的所有日期区间, 成功数, 单价信息}],
		// 平台成功数: ,
		// 单价(元): ,
		// 平台账单金额: ,
		// 备注: }
		// 创建 pdf 和 excel文件
		dataStr = getCustomerBillFile(product, dataStr, startDate, endDate, accountSuccessMap, billNumber);
		return dataStr;
	}

	/**
	 * 生成单个账单的文件（excel、pdf）
	 *
	 * @param product
	 *            产品
	 * @param billData
	 *            账单数据
	 * @param startDate
	 *            开始
	 * @param endDate
	 *            结束
	 * @param accountSuccessMap
	 *            账号成功数map
	 * @param billNumber
	 *            账单编号（用于生成文件名）
	 * @return
	 */
	private String getCustomerBillFile(CustomerProduct product, String billData, Date startDate, Date endDate, Map<String, Long> accountSuccessMap,
			String billNumber) {
		try {
			if (StringUtils.isNotBlank(billData) && !CollectionUtils.isEmpty(accountSuccessMap)) {
				JSONObject json = JSON.parseObject(billData, Feature.OrderedField);
				BigDecimal count = new BigDecimal(json.get(Constants.DAHAN_SUCCESS_COUNT_KEY).toString());
				BigDecimal fee = new BigDecimal(json.get(Constants.DAHAN_PAYMENT_AMOUNT_KEY).toString());
				Customer customer = customerService.read(product.getCustomerId());
				User user = userService.read(product.getOssUserId());

				// 获取账单内容
				BillInfo billInfo = getBillFileContent(customer, user, product, accountSuccessMap,
						count.signum() == 0 ? BigDecimal.ZERO : fee.divide(count, 10, BigDecimal.ROUND_HALF_UP), startDate, billNumber);

				// 获取用于与客户对账的我司银行信息
				BankAccount bankAccount = bankAccountService.read(customer.getBankAccountId());
				if (bankAccount == null) {
					throw new Exception("客户：" + customer.getCompanyName() + "，没有关联我方银行信息");
				}

				// 文件存放路径
				String fileSavePath = Constants.RESOURCE + File.separator + billFileDir + File.separator + customer.getCustomerId() + File.separator
						+ product.getProductId() + File.separator + DateUtil.convert(startDate, "yyyyMM");
				String excelFile = fileSavePath + File.separator + billInfo.getBillNumber() + ".xls";
				String pdfFile = IText5PdfUtil.getNextCopyFileName(fileSavePath + File.separator + billInfo.getBillNumber() + ".pdf");
				// 对账单内容写入json
				File jsonFile = new File(fileSavePath + File.separator + billInfo.getBillNumber() + "json.txt");
				if (!jsonFile.exists()) {
					if (!jsonFile.getParentFile().exists()) {
						jsonFile.getParentFile().mkdirs();
					}
				}
				// 创建或覆盖文件，把对账单内容写到文件
				try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(jsonFile, false), "UTF-8");
						BufferedWriter buffer = new BufferedWriter(osw);) {
					buffer.write(JSON.toJSONString(billInfo));
					buffer.flush();
				} catch (Exception e) {
					logger.error("", e);
				}

				User saler = null;
				if (StringUtils.isNotBlank(customer.getOssuserId())) {
					saler = userService.read(customer.getOssuserId());
				}

				// 创建excel和pdf
				CreateBillExcelUtil.createBillExcel(billInfo, bankAccount, excelFile, customer, saler);
				IText5PdfUtil.createBillPdf(billInfo, bankAccount, pdfFile, customer, saler);

				JSONArray files = new JSONArray();
				JSONObject file = new JSONObject();
				file.put("fileName", billInfo.getBillNumber() + ".xls");
				file.put("filePath", excelFile);
				files.add(file);
				file = new JSONObject();
				file.put("fileName", billInfo.getBillNumber() + ".pdf");
				file.put("filePath", pdfFile);
				files.add(file);

				json.put(Constants.DAHAN_BILL_FILE_KEY, files);
				billData = json.toString();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return billData;
	}

	/**
	 * 根据成功数和调价，统计账单基础数据（成功数、单价、总金额）
	 *
	 * @param product
	 *            产品信息
	 * @param platformSuccessCountMap
	 *            每天的成功数map {Date -> 成功数}
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param mpRecord
	 *            调价记录
	 */
	private String getCustomerBillBaseData(CustomerProduct product, Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate,
			Map<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice> mpRecord) {
		// 账单金额
		BigDecimal totalAmount = new BigDecimal("0");
		// 结果的JSON对象
		JSONObject json = new JSONObject();
		// 获取调价记录
		if (CollectionUtils.isEmpty(mpRecord)) {
			mpRecord = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
		}
		Date lastEnd = null;
		// 无调价的时间段信息
		StringBuffer withOutModifyPriceMsg = new StringBuffer();
		if (CollectionUtils.isEmpty(mpRecord)) {
			withOutModifyPriceMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
		} else {
			lastEnd = startDate;
			// 遍历每个调价区间
			for (ModifyPriceServiceImpl.TimeQuantum timeQuantum : mpRecord.keySet()) {
				// TODO 日期判断不准确
				if (lastEnd != null) {
					// 如果 调价区间开始 在 检查日期的明天 之后，那么从 检查日期的明天 到 调价区间开始的昨天 这一段区间没有价格
					if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
						withOutModifyPriceMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
								+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
					}
				}
				// 更新检查日期为 调价区间的结束
				lastEnd = timeQuantum.getEndDate();
			}
			// 遍历完了所有调价区间，检查日期 还不是 统计结束日期，说明从 检查日期 到 统计结束日期 这一段区间都没有价格
			if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
				withOutModifyPriceMsg
						.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
			}
		}
		long successSum = 0L;
		// 调价区间按 调价记录id 分组
		Map<String, List<Map.Entry<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice>>> groupMap = mpRecord.entrySet().stream()
				.collect(Collectors.groupingBy(entry -> entry.getValue().getModifyPriceId()));
		String msg = "";
		JSONArray timeQuantumPriceArrInfo = new JSONArray();
		// 遍历每个调价记录
		for (Map.Entry<String, List<Map.Entry<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice>>> entry : groupMap.entrySet()) {
			// 该调价记录 被划分成的 调价区间列表
			List<ModifyPriceServiceImpl.TimeQuantum> list = entry.getValue().stream().map(Map.Entry::getKey).collect(Collectors.toList());
			ModifyPrice modifyPrice = entry.getValue().get(0).getValue();
			if (!CollectionUtils.isEmpty(list) && modifyPrice != null) {
				// 记录同一个调价记录的信息 （日期区间，成功数，价格信息）
				JSONObject timeQuantumJsonInfo = new JSONObject();
				// 列表中的所有调价区间 对应 同一个价格
				List<String> dates = list.stream().map(timeQuantum -> DateUtil.convert(timeQuantum.getStartDate(), DateUtil.format1) + "~"
						+ DateUtil.convert(timeQuantum.getEndDate(), DateUtil.format1)).collect(Collectors.toList());
				msg += StringUtils.join(dates.iterator(), "、") + "：";
				timeQuantumJsonInfo.put("timeQuantum", StringUtils.join(dates.iterator(), "、"));
				// 从 产品每天的成功数 中获取 每个调价区间的成功数 之和，即使用同一个调价记录的单价计算销售额的成功数
				long successCount = list.stream()
						.mapToLong(timeQuantum -> getSuccessCountByDate(platformSuccessCountMap, timeQuantum.getStartDate(), timeQuantum.getEndDate())).sum();
				successSum += successCount;
				timeQuantumJsonInfo.put("successCount", successCount);
				// 根据调价记录查找梯度价格
				List<DeductionPrice> deductionList = getDeductionPrice(modifyPrice.getModifyPriceId());
				if (!CollectionUtils.isEmpty(deductionList)) {
					DeductionPrice deductionPriceInfo = deductionList.get(0);
					// 统一价
					if (modifyPrice.getPriceType() == PriceType.UNIFORM_PRICE.getCode()) {
						// 单价
						BigDecimal price = deductionPriceInfo.getPrice();
						msg += "统一价：【" + String.format("%.6f", price.doubleValue()) + "元】，";
						timeQuantumJsonInfo.put("modifyPriceInfo", "统一价【" + String.format("%.6f", price.doubleValue()) + "元】");
						totalAmount = totalAmount.add(BigDecimal.valueOf(successCount).multiply(price));
					} else if (modifyPrice.getPriceType() == PriceType.STAGE_PRICE.getCode()) {
						// 阶段价
						String stagePriceMsg = "阶段价【";
						for (DeductionPrice deductionPrice : deductionList) {
							BigDecimal ladderPrice = deductionPrice.getPrice();
							// 成功数大于一个阶段的最大发送量，这一阶段的金额 = 阶段数量 * 阶段单价
							if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
								totalAmount = totalAmount
										.add(BigDecimal.valueOf(deductionPrice.getMaxSend() - deductionPrice.getMinSend()).multiply(ladderPrice));
								stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送：" + String.format("%d", (long) deductionPrice.getMaxSend())
										+ "，阶段价格：" + String.format("%.6f", ladderPrice.doubleValue()) + "元；";
							} else if (successCount > deductionPrice.getMinSend()) {
								// 成功数超过了一个阶段的最小发送量，但没超过该阶段最大发送量，这一阶段的金额 = 超出部分
								// * 阶段单价
								totalAmount = totalAmount.add(BigDecimal.valueOf(successCount - deductionPrice.getMinSend()).multiply(ladderPrice));
								stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送："
										+ (deductionPrice.getMaxSend() == 0L ? "∞" : String.format("%d", (long) deductionPrice.getMaxSend())) + "，阶段价格："
										+ String.format("%.6f", ladderPrice.doubleValue()) + "元";
							}
						}
						stagePriceMsg += "】";
						timeQuantumJsonInfo.put("modifyPriceInfo", stagePriceMsg);
						msg += stagePriceMsg + "，";
					} else if (modifyPrice.getPriceType() == PriceType.STEPPED_PRICE.getCode()) {
						String steppedMsg = "阶梯价：";
						// 阶梯价
						BigDecimal ladderPrice = new BigDecimal("0");
						for (DeductionPrice deductionPrice : deductionList) {
							if (successCount >= deductionPrice.getMinSend()
									&& (deductionPrice.getMinSend() >= deductionPrice.getMaxSend() || successCount < deductionPrice.getMaxSend())) {
								ladderPrice = deductionPrice.getPrice();
								totalAmount = totalAmount.add(BigDecimal.valueOf(successCount).multiply(ladderPrice));
								steppedMsg = "【符合第" + (deductionPrice.getGradient() + 1) + "阶梯，最小发送：" + String.format("%d", (long) deductionPrice.getMinSend())
										+ "，最大发送：" + (deductionPrice.getMaxSend() == 0L ? "∞" : String.format("%d", (long) deductionPrice.getMaxSend()))
										+ "，阶梯价：" + String.format("%.6f", ladderPrice.doubleValue()) + "元】";
							}
						}
						timeQuantumJsonInfo.put("modifyPriceInfo", steppedMsg);
						msg += steppedMsg + "，";
					}
					timeQuantumPriceArrInfo.add(timeQuantumJsonInfo);
				}
			}
		}
		if (withOutModifyPriceMsg.length() > 0) {
			msg += "无调价信息时间段：" + withOutModifyPriceMsg.toString();
		}
		json.put(Constants.BILL_PRICE_INFO_KEY, timeQuantumPriceArrInfo.toJSONString());
		json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successSum + "");
		// 平均单价
		json.put(Constants.DAHAN_PRICE_KEY,
				successSum == 0 ? "0" : totalAmount.divide(BigDecimal.valueOf(successSum), 10, BigDecimal.ROUND_HALF_UP).toPlainString());
		// 金额精确到10位
		json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, String.format("%.10f", totalAmount.doubleValue()));
		json.put(Constants.DAHAN_REMARK_KEY, msg);
		return json.toString();
	}

	/**
	 * 从 每天的成功数 中累计 开始到结束日期的成功数之和
	 * 
	 * @param platformSuccessCountMap
	 *            每天的成功数map
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	private long getSuccessCountByDate(Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate) {
		long successCount = 0L;
		for (; !startDate.after(endDate); startDate = DateUtil.getNextDayStart(startDate)) {
			for (Map.Entry<Date, Long> entry : platformSuccessCountMap.entrySet()) {
				if (StringUtils.equals(DateUtil.convert(entry.getKey(), DateUtil.format1), DateUtil.convert(startDate.getTime(), DateUtil.format1))
						&& entry.getValue() != null) {
					successCount += entry.getValue();
					break;
				}
			}
		}
		return successCount;
	}

	/**
	 * 获取梯度价格
	 *
	 * @param modifyPriceId
	 *            调价信息id
	 * @return DeductionPrice 梯度价格
	 */
	private List<DeductionPrice> getDeductionPrice(String modifyPriceId) {
		// 查询梯度价格表
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifyPriceId));
		filter.getOrders().add(new SearchOrder("gradient", Constants.ROP_ASC));
		try {
			return deductionPriceService.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("获取梯度价格时出现异常，", e);
		}
		return null;
	}

	/**
	 * 根据成功数和调价，统计账单基础数据（成功数、单价、总金额）
	 *
	 * @param product
	 *            产品信息
	 * @param countryCountMap
	 *            成功数
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 */
	public String getCustomerBillBaseData4Inter(CustomerProduct product, Map<Date, Map<String, Long>> countryCountMap, Date startDate, Date endDate,
			Map<ModifyPriceServiceImpl.TimeQuantum, ModifyPrice> mpRecord) {
		try {
			JSONArray timeQuantumPriceArrInfo = new JSONArray();
			// 获取调价记录
			if (CollectionUtils.isEmpty(mpRecord)) {
				mpRecord = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
			}
			if (!CollectionUtils.isEmpty(countryCountMap) && !CollectionUtils.isEmpty(mpRecord)) {
				StringBuffer markMsg = new StringBuffer();
				//
				Map<ModifyPriceServiceImpl.TimeQuantum, Map<String, Double>> interPricesMap = modifyPriceService
						.getInterPrices(new ArrayList<>(mpRecord.values()), startDate, endDate);
				Date lastEnd = null;
				if (CollectionUtils.isEmpty(mpRecord)) {
					markMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
				} else {
					lastEnd = startDate;
					// 遍历每个调价区间
					for (ModifyPriceServiceImpl.TimeQuantum timeQuantum : mpRecord.keySet()) {
						if (lastEnd != null) {
							// 如果 调价区间开始 在 检查日期的明天 之后，那么从 检查日期的明天 到 调价区间开始的昨天
							// 这一段区间没有价格
							if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
								markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
										+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
							}
						}
						// 更新检查日期为 调价区间的结束
						lastEnd = timeQuantum.getEndDate();
					}
					// 遍历完了所有调价区间，检查日期 还不是 统计结束日期，说明从 检查日期 到 统计结束日期 这一段区间都没有价格
					if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
						markMsg.append(
								DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
					}
				}
				if (markMsg.length() > 0) {
					markMsg.insert(0, "无调价信息时间段：");
				}
				JSONObject json = new JSONObject();
				long successCount = 0;
				BigDecimal paymentAmount = new BigDecimal(0);
				// 遍历每个调价记录
				for (Map.Entry<ModifyPriceServiceImpl.TimeQuantum, Map<String, Double>> entry : interPricesMap.entrySet()) {
					JSONObject timeQuantumJsonInfo = new JSONObject();
					timeQuantumJsonInfo.put("timeQuantum", DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1) + "~"
							+ DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1));
					timeQuantumJsonInfo.put("modifyPriceInfo", mpRecord.get(entry.getKey()).getRemark());
					markMsg.append(DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1)).append("~")
							.append(DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1)).append("：");
					Map<String, Double> priceMap = entry.getValue();
					Date sDate = entry.getKey().getStartDate();
					Date eDate = entry.getKey().getEndDate();
					List<String> withOutPriceInfo = new ArrayList<>();
					long sectionCount = 0;
					for (; !sDate.after(eDate); sDate = DateUtil.getNextDayStart(sDate)) {
						Map<String, Long> successCountMap = countryCountMap.get(sDate);
						if (!CollectionUtils.isEmpty(successCountMap)) {
							for (Map.Entry<String, Long> entry2 : successCountMap.entrySet()) {
								Double price = priceMap.get(entry2.getKey());
								if (price == null) {
									withOutPriceInfo.add(entry2.getKey() + "：" + entry2.getValue());
								} else {
									sectionCount += entry2.getValue();
									successCount += entry2.getValue();
									paymentAmount = paymentAmount.add(new BigDecimal(price).multiply(new BigDecimal(entry2.getValue())));
								}
							}
						}
					}
					timeQuantumJsonInfo.put("successCount", sectionCount);
					timeQuantumPriceArrInfo.add(timeQuantumJsonInfo);
					if (!CollectionUtils.isEmpty(withOutPriceInfo)) {
						markMsg.append("无单价国家发送量如下：【").append(StringUtils.join(withOutPriceInfo.iterator(), "、")).append("】，");
					} else {
						markMsg.append("发送量：【").append(sectionCount).append("】，");
					}
				}
				json.put(Constants.BILL_PRICE_INFO_KEY, timeQuantumPriceArrInfo.toJSONString());
				json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successCount + "");
				json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, paymentAmount.setScale(10, BigDecimal.ROUND_HALF_UP).toPlainString());
				if (markMsg.length() > 0) {
					json.put(Constants.DAHAN_REMARK_KEY, markMsg.toString());
				}
				return json.toJSONString();
			}
		} catch (Exception e) {
			logger.error("生成国际账单流程基础数据异常", e);
		}
		return null;
	}
/*
	@SuppressWarnings("unused")
	private long getPlatformSuccessCount(int productType, String accounts, Date startDate, Date endDate) {
		Map<Date, Long> platformSuccessDateCount = getPlatformSuccessDateCount(productType, accounts, startDate, endDate, null);
		if (CollectionUtils.isEmpty(platformSuccessDateCount)) {
			return 0L;
		}
		return platformSuccessDateCount.values().stream().mapToLong(Long::longValue).sum();
	}

	@SuppressWarnings("unused")
	private Map<String, Long> getPlatformSuccessCount4Inter(int productType, String accounts, Date startDate, Date endDate) {
		Map<Date, Map<String, Long>> platformSuccessDateCount4Inter = getPlatformSuccessDateCount4Inter(productType, accounts, startDate, endDate, null);
		if (CollectionUtils.isEmpty(platformSuccessDateCount4Inter)) {
			return null;
		}
		Map<String, Long> result = new HashMap<>();
		platformSuccessDateCount4Inter.values().forEach(map -> mergeSccuessCount(result, map));
		return result;
	}*/

	/**
	 * 根据产品类型、账号、时间段 获取每天的成功数
	 *
	 * @param productType
	 *            产品类型
	 * @param accounts
	 *            产品下的账号
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param accountSuccessMap
	 *            账号的成功数map {loginName -> 成功数}
	 * @param yysType
	 *			  支持运营商类型 逗号分隔
	 * @return 每天的成功数map {Date -> 成功数}
	 */
	public Map<Date, Long> getPlatformSuccessDateCount(int productType, String accounts, Date startDate, Date endDate, Map<String, Long> accountSuccessMap, String yysType) {
		logger.info("查询统计数据开始，产品类型：" + productType + "，开始日期：" + DateUtil.convert(startDate, DateUtil.format2) + "，结束日期："
				+ DateUtil.convert(endDate, DateUtil.format2) + "，运营商类型：" + yysType + "，账号：" + accounts);
		Map<Date, Long> platformSuccessCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		List<String> loginNameList = StringUtil.isBlank(accounts) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(accounts.split("\\|")));
		loginNameList = loginNameList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		if (loginNameList.size() == 0) {
			logger.info("该产品下没有有效账号");
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();

		String whereHql = " where loginName in :loginNameList and statsDate>=:startDate and statsDate<:endDate and productType=:productType";
		params.put("loginNameList", loginNameList);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("productType", productType);
		// 不是支持运营商所有运营商，那么只查指定运营商的统计
		if (StringUtil.isNotBlank(yysType) && !yysType.equals(YysType.ALL.getValue() + "")) {
			whereHql += " and yysType in (:yysTypeList)";
			List<String> yysTypeList = new ArrayList<>(Arrays.asList(yysType.split(",")));
			params.put("yysTypeList", yysTypeList.stream().map(Integer::parseInt).collect(Collectors.toList()));
		}

		String hql = "select sum(successCount), statsDate, loginName from CustomerProductTj " + whereHql + " group by statsDate, loginName";

		// 查询短信统表数据
		List<Object[]> smsCountList = null;
		try {
			smsCountList = baseDao.findByhql(hql, params, 0);
			if (CollectionUtils.isEmpty(smsCountList)) {
				logger.info("未查询到统计数据");
				return null;
			}
			smsCountList.forEach(objects -> {
				// 累计每天的成功数
				Date date = (Date) objects[1];
				long dateCount = platformSuccessCountMap.getOrDefault(date, 0L);
				platformSuccessCountMap.put(date, dateCount + ((Number) objects[0]).longValue());

				if (accountSuccessMap != null) {
					// 累计每个账号的成功数
					String loginName = (String) objects[2];
					long accountCount = accountSuccessMap.getOrDefault(loginName, 0L);
					accountSuccessMap.put(loginName, accountCount + ((Number) objects[0]).longValue());
				}
			});
		} catch (BaseException e) {
			logger.error("查询统计数据异常", e);
		}
		return platformSuccessCountMap;
	}

	/**
	 * 根据产品类型、账号、时间段 获取每天在各个国家的成功数
	 * 
	 * @param productType
	 *            产品类型
	 * @param accounts
	 *            产品下的账号
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param accountSuccessMap
	 *            账号的成功数map {loginName -> 成功数}
	 * @return 每天的成功数map {Date -> {国别号 -> 成功数}}
	 */
	private Map<Date, Map<String, Long>> getPlatformSuccessDateCount4Inter(int productType, String accounts, Date startDate, Date endDate,
			Map<String, Long> accountSuccessMap) {

		logger.info("查询统计数据开始，产品类型：" + productType + "，开始日期：" + DateUtil.convert(startDate, DateUtil.format2) + "，结束日期："
				+ DateUtil.convert(endDate, DateUtil.format2) + "，账号：" + accounts);
		Map<Date, Map<String, Long>> platformSuccessCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		List<String> loginNameList = StringUtil.isBlank(accounts) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(accounts.split("\\|")));
		loginNameList = loginNameList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		if (loginNameList.size() == 0) {
			logger.info("该产品下没有有效账号");
			return platformSuccessCountMap;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select statsDate, countryCode, sum(successCount), loginName from CustomerProductTj "
				+ " where loginName in :loginNameList and statsDate >=: startDate and statsDate <: endDate and productType = :productType "
				+ " group by statsDate, countryCode, loginName";
		params.put("loginNameList", loginNameList);
		params.put("productType", productType);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// 查询短信统表数据
		List<Object[]> smsCountList = null;
		try {
			smsCountList = baseDao.findByhql(hql, params, 0);
			if (CollectionUtils.isEmpty(smsCountList)) {
				logger.info("未查询到统计数据");
				return null;
			}
			smsCountList.forEach(objects -> {
				Date statsDate = (Date) objects[0];
				// 这一天在各个国家的成功数map
				Map<String, Long> countryCountMap = platformSuccessCountMap.getOrDefault(statsDate, new HashMap<>());
				String countryCode = (String) objects[1];
				long lastCountryCount = countryCountMap.getOrDefault(countryCode, 0L);
				countryCountMap.put(countryCode, lastCountryCount + ((Number) objects[2]).longValue());
				platformSuccessCountMap.put(statsDate, countryCountMap);

				if (accountSuccessMap != null) {
					// 累计每个账号的成功数
					String loginName = (String) objects[3];
					long lastAccountCount = accountSuccessMap.getOrDefault(loginName, 0L);
					accountSuccessMap.put(loginName, lastAccountCount + ((Number) objects[2]).longValue());
				}
			});
		} catch (BaseException e) {
			logger.error("查询统计数据异常", e);
		}
		return platformSuccessCountMap;
	}

	private void mergeSccuessCount(Map<String, Long> result1, Map<String, Long> result2) {
		result2.entrySet().stream().forEach(entry -> {
			Long old = result1.get(entry.getKey());
			old = old == null ? 0 : old;
			Long value = entry.getValue();
			value = value == null ? 0 : value;
			result1.put(entry.getKey(), old + value);
		});
	}

	/**
	 * 获取账单文件内容
	 *
	 * @param customer
	 *            客户
	 * @param user
	 *            对应销售
	 * @param product
	 *            产品
	 * @param accountSuccessMap
	 *            账号成功数map {loginName -> 成功数}
	 * @param unitPrice
	 *            产品平均单价
	 * @param billDate
	 *            账单月份
	 * @return
	 */
	private BillInfo getBillFileContent(Customer customer, User user, CustomerProduct product, Map<String, Long> accountSuccessMap, BigDecimal unitPrice,
			Date billDate, String billNumber) {
		List<String> accountList = new ArrayList<>();
		if (StringUtils.isNotBlank(product.getAccount())) {
			accountList.addAll(Arrays.asList(product.getAccount().split("\\|")));
		}
		BillInfo billInfo = new BillInfo();
		billInfo.setBillDate(billDate);
		billInfo.setBillNumber(billNumber);
		billInfo.setCompanyName(customer.getCompanyName());
		billInfo.setContactsName(customer.getContactName());
		billInfo.setPhone(customer.getContactPhone());
		billInfo.setCreateDate(DateUtil.getDateStartDateTime(new Date()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(billDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, product.getBillPeriod() + 1);
		cal.add(Calendar.SECOND, -1);
		billInfo.setFinalPayDate(cal.getTime());
		billInfo.setSaleName(user.getRealName());
		String phone = null;
		if (StringUtils.isNotBlank(user.getContactMobile())) {
			phone = user.getContactMobile().split(",")[0];
		}
		if (StringUtils.isBlank(phone) && StringUtils.isNotBlank(user.getContactPhone())) {
			phone = user.getContactMobile().split(",")[0];
		}
		billInfo.setSalePhone(phone);
		billInfo.setAccountInfos(new ArrayList<>());
		// 总计信息
		DetailInfo realFeeInfo = new DetailInfo();
		realFeeInfo.setFee(new BigDecimal(0));
		realFeeInfo.setFeeCount(new BigDecimal(0));
		realFeeInfo.setUnitPrice(unitPrice);
		// 每个账号的成功数等信息
		accountSuccessMap.forEach((account, successCount) -> {
			if (!CollectionUtils.isEmpty(accountList)) {
				accountList.remove(account);
			}
			DetailInfo detail = new DetailInfo();
			detail.setAccountName(account);
			detail.setFeeCount(new BigDecimal(successCount));
			// 账号金额 = 账号成功数 x 当月产品平均单价
			detail.setFee(unitPrice.multiply(detail.getFeeCount()).setScale(2, BigDecimal.ROUND_HALF_UP));
			detail.setUnitPrice(unitPrice.setScale(6, BigDecimal.ROUND_HALF_UP));
			realFeeInfo.setFee(realFeeInfo.getFee().add(detail.getFee()));
			realFeeInfo.setFeeCount(realFeeInfo.getFeeCount().add(detail.getFeeCount()));
			billInfo.getAccountInfos().add(detail);
		});
		// 成功数为0的账号也展示
		if (!CollectionUtils.isEmpty(accountList)) {
			accountList.forEach(account -> {
				DetailInfo detail = new DetailInfo();
				detail.setAccountName(account);
				detail.setFeeCount(BigDecimal.ZERO);
				detail.setFee(BigDecimal.ZERO);
				detail.setUnitPrice(unitPrice.setScale(6, BigDecimal.ROUND_HALF_UP));
				billInfo.getAccountInfos().add(detail);
			});
		}
		realFeeInfo.setFee(realFeeInfo.getFee().setScale(2, BigDecimal.ROUND_HALF_UP));
		billInfo.setRealFeeInfo(realFeeInfo);
		return billInfo;
	}

	/**
	 * 合并账单成一个pdf，账号数据用账单的，实际数据用对账总计
	 * 
	 * @param billList
	 *            待合并的账单列表
	 * @param filePath
	 *            合并后文件存放路径
	 * @param billTotal
	 *            对账总计（自动账单发起时默认用我司数据，审核时可修改）
	 * @param optionList
	 *            对账单内容
	 * @return 成功返回空字符串，失败返回失败原因
	 */
	@Override
	public String createMergePdf(List<ProductBills> billList, String filePath, JSONObject billTotal, List<String> optionList) {
		String msg = "";
		try {
			// 从流程中获取本次对账的账单异常
			if (CollectionUtils.isEmpty(billList)) {
				logger.info("获取到的账单为空");
				msg = "获取到的账单为空";
				return msg;
			}
			Customer customer = customerService.read(billList.get(0).getEntityId());
			if (customer == null) {
				logger.info("客户不存在，customerId：" + billList.get(0).getEntityId());
				msg = "客户不存在";
				return msg;
			}
			User saler = null;
			if (StringUtils.isNotBlank(customer.getOssuserId())) {
				saler = userService.read(customer.getOssuserId());
			}
			BankAccount bankAccount = bankAccountService.read(customer.getBankAccountId());
			if (bankAccount == null) {
				logger.info("客户：" + customer.getCompanyName() + "，没有关联我方银行信息");
				msg = "客户：" + customer.getCompanyName() + "，没有关联我方银行信息";
				return msg;
			}
			// 获取每个账单的json文件
			Map<String, File> jsonMap = billList.stream().collect(Collectors.toMap(ProductBills::getId, this::getBillJsonFile, (v1, v2) -> v2));
			SearchFilter filter = new SearchFilter();
			List<String> productIds = billList.stream().map(ProductBills::getProductId).collect(Collectors.toList());
			filter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productIds));
			List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
			if (CollectionUtils.isEmpty(productList)) {
				logger.info("产品不存在，productId：" + String.join(",", productIds));
				msg = "产品不存在";
				return msg;
			}
			Map<String, CustomerProduct> productMap = productList.stream()
					.collect(Collectors.toMap(CustomerProduct::getProductId, product -> product, (v1, v2) -> v2));
			// 产品类型和时间排序
			billList.sort((bill1, bill2) -> {
				CustomerProduct p1 = productMap.get(bill1.getProductId());
				CustomerProduct p2 = productMap.get(bill2.getProductId());
				if (p1 == null && p2 != null) {
					return 1;
				} else if (p1 != null && p2 == null) {
					return -1;
				}
				if (p1.getProductType() != p2.getProductType()) {
					return p1.getProductType() - p2.getProductType();
				}
				return bill2.getWtime().getTime() - bill1.getWtime().getTime() > 0L ? 1 : -1;
			});
			Map<String, BillInfo> infoMap = new HashMap<>();
			billList.forEach(bill -> {
				File file = jsonMap.get(bill.getId());
				if (file == null || !file.exists()) {
					logger.info("账单的json文件不存在，账单Id：" + bill.getId());
					return;
				}
				try (InputStreamReader osr = new InputStreamReader(new FileInputStream(file), "UTF-8"); BufferedReader buffer = new BufferedReader(osr);) {
					String content = buffer.readLine();
					if (StringUtils.isNotBlank(content)) {
						BillInfo billInfo = JSON.parseObject(content, BillInfo.class);
						if (productMap.get(bill.getProductId()) == null) {
							billInfo.setProductType("未知");
						} else {
							int productType = productMap.get(bill.getProductId()).getProductType();
							billInfo.setProductType(productTypeService.getProductTypeNameByValue(productType));
						}
						infoMap.put(bill.getId(), billInfo);
					}
				} catch (Exception e) {
					logger.error("读取账单详情文件异常", e);
				}
			});
			if (infoMap.isEmpty()) {
				logger.info("获取到的账单详情为空");
				msg = "获取到的账单详情为空";
				return msg;
			}
			// 对账单中的实际计费，优先使用页面算出的对账总计（默认等于我司数据），页面没有数据时使用每个账单的总计
			DetailInfo realFeeInfo = null;
			if (null == billTotal) {
				// 累计多个账单的条数、金额
				getBillTotalByInfo(new ArrayList<BillInfo>(infoMap.values()));
			} else {
				realFeeInfo = new DetailInfo();
				realFeeInfo.setFeeCount(new BigDecimal(billTotal.getString("checkedSuccessCount")));
				realFeeInfo.setFee(new BigDecimal(billTotal.getString("checkedAmount")));
			}
			if (realFeeInfo == null) {
				logger.info("未获取到对账总计");
				msg = "未获取到对账总计";
				return msg;
			}
			boolean result = false;
			// 获取账单的数据详情
			Map<String, BillDataDetailDto> billDetailMap = null;
			if (optionList.contains(Constants.BILL_OPTION_DATA_DETAIL)) {
				billDetailMap = getBillDataDetail(billList, productMap, customer);
			}
			result = IText5Pdf4MergeUtil.createBillPdf(billList, infoMap, realFeeInfo, bankAccount, filePath, optionList, billDetailMap, customer, saler);
			if (!result) {
				msg = "生成对账单pdf失败";
			}
			return msg;
		} catch (Exception e) {
			logger.error("生成对账单异常", e);
			msg = "生成对账单异常";
			return msg;
		}
	}

	/**
	 * 累计多个账单的条数、金额
	 * @param billInfoList
	 * @return
	 */
	private DetailInfo getBillTotalByInfo(List<BillInfo> billInfoList) {
		DetailInfo totalInfo = null;
		BigDecimal unitPrice = new BigDecimal("0");
		BigDecimal feeCount = new BigDecimal("0");
		BigDecimal fee = new BigDecimal("0");
		try {
			for (BillInfo billInfo : billInfoList) {
				DetailInfo realFeeInfo = billInfo.getRealFeeInfo();
				feeCount = feeCount.add(realFeeInfo.getFeeCount());
				fee = fee.add(realFeeInfo.getFee());
			}
			if (feeCount.compareTo(BigDecimal.ZERO) > 0) {
				unitPrice = fee.divide(feeCount, 4, BigDecimal.ROUND_HALF_UP);
			}
			totalInfo = new DetailInfo();
			totalInfo.setFee(fee);
			totalInfo.setFeeCount(feeCount);
			totalInfo.setUnitPrice(unitPrice);
		} catch (Exception e) {
			logger.error("从账单中获取对账总计异常");
		}
		return totalInfo;
	}

	/**
	 * 获取对账流程的账单金额总计
	 * 
	 * @param flowEnt
	 * @return
	 */
	@SuppressWarnings("unused")
	private DetailInfo getBillTotalByFlowEnt(FlowEnt flowEnt) {
		DetailInfo realFeeInfo = null;
		if (flowEnt != null && StringUtils.isNotBlank(flowEnt.getFlowMsg())) {
			JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
			if (json.get(Constants.UNCHECKED_BILL_KEY) != null && StringUtils.isNotBlank(json.get(Constants.UNCHECKED_BILL_KEY).toString())) {
				JSONObject allBillJson = JSONObject.parseObject(json.get(Constants.UNCHECKED_BILL_KEY).toString());
				if (allBillJson.get("billTotal") != null && StringUtils.isNotBlank(allBillJson.get("billTotal").toString())) {
					JSONObject billTotal = JSONObject.parseObject(allBillJson.get("billTotal").toString());
					realFeeInfo = new DetailInfo();
					realFeeInfo.setFee(new BigDecimal(billTotal.get("checkedAmount").toString()));
					realFeeInfo.setFeeCount(new BigDecimal(billTotal.get("checkedSuccessCount").toString()));
					realFeeInfo.setUnitPrice(new BigDecimal(billTotal.get("checkedUnitPrice").toString()));
				}
			}
		}
		return realFeeInfo;
	}

	// 获取账单的json文件，在自动生成账单的时候写入的，其中有产品账号数据
	private File getBillJsonFile(ProductBills bill) {
		if (bill != null) {
			try {
				String fileSavePath = Constants.RESOURCE + File.separator + billFileDir + File.separator + bill.getEntityId() + File.separator
						+ bill.getProductId() + File.separator + DateUtil.convert(bill.getWtime(), "yyyyMM");
				return new File(fileSavePath + File.separator + bill.getBillNumber() + "json.txt");
			} catch (Exception e) {
				logger.error("获取账单数据文件异常", e);
			}
		}
		return null;
	}

	// 从流程中获取本次对账的账单
	@SuppressWarnings("unused")
	private List<ProductBills> getBillIdsByFlowEnt(FlowEnt flowEnt) {
		logger.info("从流程中获取本次对账的账单开始");
		try {
			if (flowEnt != null && StringUtils.isNotBlank(flowEnt.getFlowMsg())) {
				JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
				// 获取 未对账账单标签的值，里面有本次对账勾选的账单
				if (json.get(Constants.UNCHECKED_BILL_KEY) != null && StringUtils.isNotBlank(json.get(Constants.UNCHECKED_BILL_KEY).toString())) {
					JSONObject allBillJson = JSONObject.parseObject(json.get(Constants.UNCHECKED_BILL_KEY).toString());
					if (allBillJson.get("billInfos") != null && StringUtils.isNotBlank(allBillJson.get("billInfos").toString())) {
						// 勾选的账单
						JSONArray billInfos = JSONObject.parseArray(allBillJson.get("billInfos").toString());
						List<String> productBillsIds = new ArrayList<>();
						billInfos.forEach(obj -> productBillsIds.add(((JSONObject) obj).getString("id")));
						if (!CollectionUtils.isEmpty(productBillsIds)) {
							SearchFilter filter = new SearchFilter();
							filter.getRules().add(new SearchRule("id", Constants.ROP_IN, productBillsIds));
							return productBillsService.queryAllBySearchFilter(filter);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("从流程中获取本次对账的账单异常", e);
		}
		return null;
	}

	/**
	 * 发起账单流程
	 *
	 * @param billList
	 *            要对账的账单
	 */
	@Override
	public void buildCheckBillFlow(List<ProductBills> billList) {
		long _start = System.currentTimeMillis();
		try {
			if (CollectionUtils.isEmpty(billList)) {
				logger.info("待发起对账流程的账单列表为空");
				return;
			}

			String flowId;
			int flowType;
			String viewerRoleId;

			// 获取对账流程的id及类型
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.CUSTOMER_CHECK_BILL_FLOW));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
				// 查找流程节点表中nodeid
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
				if (CollectionUtils.isEmpty(nodeList)) {
					logger.info("'" + Constants.CUSTOMER_CHECK_BILL_FLOW + "'无节点，请重新设计");
					return;
				}
			} else {
				logger.info("系统无'" + Constants.CUSTOMER_CHECK_BILL_FLOW + "'，请尽快创建该流程");
				return;
			}

			logger.info("发起对账流程开始");
			Customer customer = customerService.read(billList.get(0).getEntityId());
			filter = new SearchFilter();
			filter.getRules()
					.add(new SearchRule("productId", Constants.ROP_IN, billList.stream().map(ProductBills::getProductId).collect(Collectors.toList())));
			List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
			CustomerProduct product = productList.get(0);
			String companyName = customer.getCompanyName();
			// 新建流程实体
			String flowTitle = Constants.CUSTOMER_CHECK_BILL_FLOW + "(" + companyName + ")";
			FlowEnt flowEnt = flowEntService.buildFlowEnt(flowTitle, flowId, flowType, customer.getOssuserId(), product.getProductId(),
					customer.getCustomerId(), viewerRoleId, EntityType.CUSTOMER.getCode());

			// 流程标签内容
			String flowMsg = "";
			JSONObject flowMsgJson = new JSONObject();
			JSONObject labelValue = new JSONObject();
			JSONArray billInfos = new JSONArray();
			JSONObject billTotal = new JSONObject();
			long platformSuccessCountTotal = 0L;
			BigDecimal platformAmountTotal = new BigDecimal(0);
			long customerSuccessCountTotal = 0L;
			BigDecimal customerAmountTotal = new BigDecimal(0);
			long checkedSuccessCountTotal = 0L;
			BigDecimal checkedAmountTotal = new BigDecimal(0);
			for (ProductBills bill : billList) {
				Optional<CustomerProduct> productOpt = productList.stream().filter(item -> StringUtils.equals(bill.getProductId(), item.getProductId()))
						.findAny();
				product = productOpt.orElse(null);
				if (null == product) {
					logger.info("产品不存在，productId：" + bill.getProductId());
					continue;
				}
				JSONObject billInfo = new JSONObject();
				billInfo.put("id", bill.getId());
				billInfo.put("productId", bill.getProductId());
				billInfo.put("entityId", bill.getEntityId());
				billInfo.put("billNumber", bill.getBillNumber());
				billInfo.put("billMonth", DateUtil.convert(bill.getWtime(), DateUtil.format4));
				billInfo.put("title", "账单-" + DateUtil.convert(bill.getWtime(), DateUtil.format4) + "-" + product.getProductName());
				billInfo.put("remark", bill.getRemark());

				billInfo.put("platformSuccessCount", bill.getPlatformCount());
				billInfo.put("platformAmount", bill.getReceivables());
				platformSuccessCountTotal += bill.getPlatformCount();
				platformAmountTotal = platformAmountTotal.add(bill.getReceivables());
				if (bill.getPlatformCount() > 0) {
					billInfo.put("platformUnitPrice", bill.getReceivables().divide(new BigDecimal(bill.getPlatformCount()), 6, BigDecimal.ROUND_HALF_UP));
				} else {
					billInfo.put("platformUnitPrice", "0.000000");
				}

				billInfo.put("customerSuccessCount", bill.getSupplierCount());
				billInfo.put("customerAmount", bill.getReceivables());
				customerSuccessCountTotal += bill.getSupplierCount();
				customerAmountTotal = customerAmountTotal.add(bill.getReceivables());
				if (bill.getSupplierCount() > 0) {
					billInfo.put("customerUnitPrice", bill.getReceivables().divide(new BigDecimal(bill.getSupplierCount()), 6, BigDecimal.ROUND_HALF_UP));
				} else {
					billInfo.put("customerUnitPrice", "0.000000");
				}

				billInfo.put("checkedSuccessCount", bill.getSupplierCount());
				billInfo.put("checkedAmount", bill.getReceivables());
				checkedSuccessCountTotal += bill.getSupplierCount();
				checkedAmountTotal = checkedAmountTotal.add(bill.getReceivables());
				if (bill.getSupplierCount() > 0) {
					billInfo.put("checkedUnitPrice", bill.getReceivables().divide(new BigDecimal(bill.getSupplierCount()), 6, BigDecimal.ROUND_HALF_UP));
				} else {
					billInfo.put("checkedUnitPrice", "0.000000");
				}

				billInfos.add(billInfo);
			}

			// 更新账单状态为对账中
			billList = billList.stream().peek(bill -> bill.setBillStatus(BillStatus.RECONILING.ordinal())).collect(Collectors.toList());
			productBillsService.updateByBatch(billList);

			// 总计
			billTotal.put("platformSuccessCount", platformSuccessCountTotal);
			billTotal.put("platformAmount", platformAmountTotal);
			if (platformSuccessCountTotal > 0) {
				billTotal.put("platformUnitPrice", platformAmountTotal.divide(new BigDecimal(platformSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
			} else {
				billTotal.put("platformUnitPrice", "0.000000");
			}
			billTotal.put("customerSuccessCount", customerSuccessCountTotal);
			billTotal.put("customerAmount", customerAmountTotal);
			if (customerSuccessCountTotal > 0) {
				billTotal.put("customerUnitPrice", customerAmountTotal.divide(new BigDecimal(customerSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
			} else {
				billTotal.put("customerUnitPrice", "0.000000");
			}
			billTotal.put("checkedSuccessCount", checkedSuccessCountTotal);
			billTotal.put("checkedAmount", checkedAmountTotal);
			if (checkedSuccessCountTotal > 0) {
				billTotal.put("checkedUnitPrice", checkedAmountTotal.divide(new BigDecimal(checkedSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
			} else {
				billTotal.put("checkedUnitPrice", "0.000000");
			}

			labelValue.put("billInfos", billInfos);
			labelValue.put("billTotal", billTotal);

			/*long ___start = System.currentTimeMillis();
			Date today = new Date();
			String fileName = companyName + "-已选中账单的对账单-" + DateUtil.convert(today, DateUtil.format10) + ".pdf";
			String filePath = Constants.RESOURCE + File.separator + billFileDir + File.separator + customer.getCustomerId() + File.separator
					+ DateUtil.convert(today, "yyyyMM") + File.separator + fileName;
			// 默认只生成电子账单
			List<String> defaultOptions = new ArrayList<>();
			defaultOptions.add(Constants.BILL_OPTION_BILL_FILE);
			// 生成对账单pdf
			String msg = productBillsService.createMergePdf(billList, filePath, billTotal, defaultOptions);
			if (StringUtil.isBlank(msg)) {
				logger.info("合并账单完成，耗时：" + (System.currentTimeMillis() - ___start));
				JSONObject billFile = new JSONObject();
				billFile.put("fileName", fileName);
				billFile.put("filePath", filePath);
				billFile.put("options", JSON.toJSONString(defaultOptions));
				labelValue.put("billFile", billFile);
			}*/

			flowMsgJson.put(Constants.UNCHECKED_BILL_KEY, labelValue);
			flowMsg = flowMsgJson.toJSONString();
			flowEnt.setFlowMsg(flowMsg);
			flowEnt.setDeptId(customer.getDeptId());
			flowEnt.setRemark("上月账单已出，自动发起流程");
			boolean result = flowEntService.save(flowEnt);
			if (result) {
				logger.info("创建" + Constants.CUSTOMER_CHECK_BILL_FLOW + "的流程实体成功，flowEntId：" + flowEnt.getId());
			} else {
				logger.info("创建" + Constants.CUSTOMER_CHECK_BILL_FLOW + "的流程实体失败，customerId：" + customer.getCustomerId());
			}
			logger.info("自动发起对账流程完成，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("自动发起对账流程异常", e);
		}
	}

	/**
	 * 获取产品账单在账单月每天的数据详情
	 *
	 * @param billList
	 *            账单列表
	 * @param productMap
	 *            产品
	 * @return 账单的数据详情 {billId -> 账单详情}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, BillDataDetailDto> getBillDataDetail(List<ProductBills> billList, Map<String, CustomerProduct> productMap, Customer customer) {
		logger.info("获取账单的数据详情开始");
		if (CollectionUtils.isEmpty(billList)) {
			logger.info("账单列表为空");
			return null;
		}
		logger.info("账单数：" + billList.size());
		Map<String, BillDataDetailDto> billDetailMap = new HashMap<>();
		// 获取每个账单在账单月每天的数据详情
		for (ProductBills bill : billList) {
			String loginName = bill.getLoginName();
			List<String> loginNameList = null;
			CustomerProduct product = productMap.get(bill.getProductId());
			if (StringUtil.isBlank(loginName)) {
				loginName = product.getAccount();
				if (StringUtil.isBlank(loginName)) {
					logger.info("产品下没有账号，productId：" + bill.getProductId());
					continue;
				}
				loginNameList = Arrays.asList(loginName.split("\\|"));
			} else {
				loginNameList = Arrays.asList(loginName.split(","));
			}
			loginName = loginNameList.stream().map(str -> "'" + str + "'").collect(Collectors.joining(","));
			Date startDate = new Date(bill.getWtime().getTime());
			String startTime = DateUtil.convert(startDate, DateUtil.format2);
			String endTime = DateUtil.convert(DateUtil.getNextMonthFirst(startDate), DateUtil.format2);

			String whereSql = " where statsDate >= '" + startTime + "'" + " and statsDate < '" + endTime + "'" + " and loginname in (" + loginName + ")"
					+ " and producttype = " + product.getProductType();
			String yysType = product.getYysType();
			// 不是支持运营商所有运营商，那么只查指定运营商的统计
			if (StringUtil.isNotBlank(yysType) && !yysType.equals(YysType.ALL.getValue() + "")) {
				whereSql += " and yystype in (" + yysType + ")";
			}

			// 账号 + 产品类型 可以唯一确定一个产品
			String sql = "select DATE_FORMAT(statsDate, '%Y-%m-%d') as date, loginname, sum(totalcount), sum(successcount), sum(failcount)" + " from erp_customerproducttj"
					+ whereSql + " group by date, loginname order by date";

			BillDataDetailDto billData = new BillDataDetailDto();
			billData.setBillMonth(DateUtil.convert(bill.getWtime(), DateUtil.format4));
			billData.setProductId(product.getProductId());
			billData.setProductName(product.getProductName());
			billData.setProductType(product.getProductType());

			List<DateDetail> dateDetailList = new ArrayList<>();
			try {
				List<Object[]> dataList = (List<Object[]>) baseDao.selectSQL(sql);
				if (!CollectionUtils.isEmpty(dataList)) {
					for (Object[] data : dataList) {
						DateDetail detail = new DateDetail();
						detail.setProductName(customer.getCompanyName() + "-" + product.getProductName());
						detail.setDate((String) data[0]);
						detail.setLoginName((String) data[1]);
						detail.setTotalCount(((Number) data[2]).longValue());
						detail.setSuccessCount(((Number) data[3]).longValue());
						detail.setFailCount(((Number) data[4]).longValue());
						detail.setSuccessRatio(
								new BigDecimal(detail.getSuccessCount()).divide(new BigDecimal(detail.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
						dateDetailList.add(detail);
					}
				}
			} catch (BaseException e) {
				logger.error("", e);
			}
			billData.setDateDetailList(dateDetailList);
			billDetailMap.put(bill.getId(), billData);
		}
		return billDetailMap;
	}

	/**
	 * 作废开票流程时，根据填入的扣减金额还原账单数据
	 *
	 * @param productBillsDto
	 *            发票DTO，存有所有设计的账单和扣减金额
	 * @throws Exception
	 */
	@Override
	public boolean restoreBill(ProductBillsDto productBillsDto) throws Exception {
		final boolean[] res = { false };
		List<ProductBills> productBillsList = new ArrayList<>();
		Optional.ofNullable(productBillsDto.getProductBillsJSONObjectList()).orElse(new ArrayList<>()).forEach(productBill -> {
			try {
				ProductBills productBills = productBillsService.read(productBill.getId());
				if (productBills == null) {
					logger.error("作废开票流程：账单查询失败，对应账单ID为：" + productBill.getId());
					return;
				}
				productBills.setActualInvoiceAmount(productBills.getActualInvoiceAmount().setScale(4).subtract(productBill.getDeductionAmount().setScale(4)));
				productBillsList.add(productBills);
				res[0] = true;
			} catch (Exception e) {
				logger.error("作废流程：扣减账单失败", e);
				res[0] = false;
			}
		});
		productBillsService.updateByBatch(productBillsList);
		return res[0];
	}

}
