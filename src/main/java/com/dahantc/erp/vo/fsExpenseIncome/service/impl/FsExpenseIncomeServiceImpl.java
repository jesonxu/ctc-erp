package com.dahantc.erp.vo.fsExpenseIncome.service.impl;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.FsExpenseIncomeUI;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FeeType;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerOperate.service.ICustomerOperateService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.fsExpenseIncome.dao.IFsExpenseIncomeDao;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.dao.IProductBillsDao;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.dao.IUserDao;

@Service("fsExpenseIncomeService")
public class FsExpenseIncomeServiceImpl implements IFsExpenseIncomeService {
	private static Logger logger = LogManager.getLogger(FsExpenseIncomeServiceImpl.class);

	@Autowired
	private IFsExpenseIncomeDao fsExpenseIncomeDao;

	@Autowired
	private IProductBillsDao productBillsDao;

	@Autowired
	private ICashFlowService cashflowService;

	@Autowired
	private IChargeRecordService chargeRecordService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerOperateService customerOperateService;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IDepartmentService departmentService;

	@Override
	public FsExpenseIncome read(Serializable id) throws ServiceException {
		try {
			return fsExpenseIncomeDao.read(id);
		} catch (Exception e) {
			logger.error("读取财务收支信息表失败", e);
			throw new ServiceException("读取财务收支信息表失败", e);
		}
	}

	@Override
	public boolean save(FsExpenseIncome entity) throws ServiceException {
		try {
			return fsExpenseIncomeDao.save(entity);
		} catch (Exception e) {
			logger.error("保存财务收支信息表失败", e);
			throw new ServiceException("保存财务收支信息表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<FsExpenseIncome> objs) throws ServiceException {
		try {
			// 先查询后保存
			if (objs != null && !objs.isEmpty()) {
				List<String> serialNumbers = objs.stream().map(FsExpenseIncome::getSerialNumber).collect(Collectors.toList());
				List<String> existSerialNumber = new ArrayList<>();
				int searchCnt = (int) Math.ceil(serialNumbers.size() / 1000.00);
				SearchFilter filter = new SearchFilter();
				for (int i = 0; i < searchCnt; i++) {
					filter.getRules().add(
							new SearchRule("serialNumber", Constants.ROP_IN, serialNumbers.subList(i * 1000, Math.min((i + 1) * 1000, serialNumbers.size()))));
					List<FsExpenseIncome> list = queryAllBySearchFilter(filter);
					if (!CollectionUtils.isEmpty(list)) {
						existSerialNumber.addAll(list.stream().map(FsExpenseIncome::getSerialNumber).collect(Collectors.toList()));
					}
				}
				if (!CollectionUtils.isEmpty(existSerialNumber)) {
					Iterator<FsExpenseIncome> iterator = objs.iterator();
					while (iterator.hasNext()) {
						FsExpenseIncome income = iterator.next();
						if (existSerialNumber.contains(income.getSerialNumber())) {
							iterator.remove();
						}
					}
				}
			}
			return fsExpenseIncomeDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return fsExpenseIncomeDao.delete(id);
		} catch (Exception e) {
			logger.error("删除财务收支信息表失败", e);
			throw new ServiceException("删除财务收支信息表失败", e);
		}
	}

	@Override
	public boolean update(FsExpenseIncome enterprise) throws ServiceException {
		try {
			return fsExpenseIncomeDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新财务收支信息表失败", e);
			throw new ServiceException("更新财务收支信息表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return fsExpenseIncomeDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询财务收支信息表数量失败", e);
			throw new ServiceException("查询财务收支信息表数量失败", e);
		}
	}

	@Override
	public PageResult<FsExpenseIncome> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return fsExpenseIncomeDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询财务收支信息表分页信息失败", e);
			throw new ServiceException("查询财务收支信息表分页信息失败", e);
		}
	}

	@Override
	public List<FsExpenseIncome> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return fsExpenseIncomeDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询财务收支信息表失败", e);
			throw new ServiceException("查询财务收支信息表失败", e);
		}
	}

	@Override
	public List<FsExpenseIncome> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return fsExpenseIncomeDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询财务收支信息表失败", e);
			throw new ServiceException("查询财务收支信息表失败", e);
		}
	}

	private FsExpenseIncomeUI buildUI(FsExpenseIncome fei) {
		FsExpenseIncomeUI ui = new FsExpenseIncomeUI();
		BeanUtils.copyProperties(fei, ui);
		ui.setWtime(DateUtil.convert(fei.getWtime(), DateUtil.format2));
		ui.setOperateTime(DateUtil.convert(fei.getOperateTime(), DateUtil.format2));
		if (fei.getIsIncome() == 0) { // 收入
			ui.setIncome(String.format("%.2f", fei.getCost()));
		} else if (fei.getIsIncome() == 1) { // 支出
			ui.setExpense(String.format("%.2f", fei.getCost()));
		}
		Optional<FeeType> type = FeeType.getEnumsByCode(fei.getFeeType());
		ui.setFeeType(type.isPresent() ? type.get().getMsg() : "其他");
		return ui;
	}

	@Override
	public List<FsExpenseIncomeUI> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		List<FsExpenseIncomeUI> result = new ArrayList<>();
		try {
			List<FsExpenseIncome> list = fsExpenseIncomeDao.findByhql(hql, params, maxCount);
			for (FsExpenseIncome fei : list) {
				result.add(buildUI(fei));
			}
		} catch (Exception e) {
			logger.error("查询财务收支信息表失败", e);
			throw new ServiceException("查询财务收支信息表失败", e);
		}
		return result;
	}

	@Override
	public BaseResponse<Boolean> saveBindFsExpenseIncome(String productBillsId, String feiId, BigDecimal thisCost) {
		// 判断是否可以关联; 条件1: 账单: 应收金额 - 实收金额 - thisCost > 0, 条件2: 财务收支表: 剩余关联费用 -
		// thisCost > 0
		try {
			BigDecimal zero = new BigDecimal("0");
			ProductBills bill = productBillsDao.read(productBillsId);
			if (bill == null) {
				return BaseResponse.error("账单信息错误");
			}
			FsExpenseIncome expenseIncome = read(feiId);
			if (expenseIncome == null) {
				return BaseResponse.error("收入导入信息错误");
			}
			if (zero.compareTo(expenseIncome.getRemainRelatedCost()) == 0) {
				return BaseResponse.error("导入收支已经关联");
			}
			// 收入
			if (expenseIncome.getIsIncome() == 0) {
				// 剩余应关联
				BigDecimal left = bill.getReceivables().subtract(bill.getActualReceivables());
				if (zero.compareTo(left) >= 0) {
					// 账单已经关联完成
					return BaseResponse.error("账单已经关联完成，不能再关联");
				}
				if (thisCost.compareTo(left) > 0) {
					return BaseResponse.error("关联账单金额比实际剩余应关联金额高");
				}
				bill.setActualReceivables(bill.getActualReceivables().add(thisCost));
			} else {
				// 支出
				BigDecimal left = bill.getPayables().subtract(bill.getActualPayables());
				if (zero.compareTo(left) >= 0) {
					return BaseResponse.error("账单已经关联完成，不能再关联");
				}
				if (thisCost.compareTo(left) > 0) {
					return BaseResponse.error("关联账单金额比实际剩余应关联金额高");
				}
				bill.setActualPayables(bill.getActualPayables().add(thisCost));
			}

			Map<String, Object> map = new HashMap<>();
			map.put("fsExpenseIncomeId", feiId);
			map.put("thisCost", String.format("%.2f", thisCost));
			map.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			JSONArray infos = new JSONArray();
			try {
				if (StringUtil.isNotBlank(bill.getRelatedInfo())) {
					infos = JSON.parseArray(bill.getRelatedInfo());
				}
			} catch (Exception e) {
				logger.error("数据解析异常", e);
			}
			infos.add(new JSONObject(map));
			bill.setRelatedInfo(infos.toJSONString());
			expenseIncome.setRemainRelatedCost(expenseIncome.getRemainRelatedCost().subtract(thisCost));
			productBillsDao.update(bill);
			update(expenseIncome);
			buildChargeRecord(bill, expenseIncome, thisCost);
			addCashFlow(bill, expenseIncome, thisCost);
		} catch (Exception e) {
			logger.error("", e);
			return BaseResponse.error("修改失败", false);
		}
		return BaseResponse.success(true);
	}

	/**
	 * 增加收支记录
	 * 
	 * @param bill
	 *            账单
	 * @param expenseIncome
	 *            收支导入
	 * @param money
	 *            关联金额
	 */
	private void buildChargeRecord(ProductBills bill, FsExpenseIncome expenseIncome, BigDecimal money) {
		try {
			// 收支记录
			ChargeRecord record = new ChargeRecord();
			// 是否为收入
			Timestamp time = null;
			if (expenseIncome.getIsIncome() == 0) {
				// 收入
				time = bill.getFinalReceiveTime();
			} else {
				// 支出
				time = bill.getFinalPayTime();
			}
			record.setInvoiceTime(time);
			// 实际付款时间
			record.setActualPayTime(expenseIncome.getOperateTime());
			// 没有发票
			record.setActualInvoiceTime(time);
			record.setChargeType(IncomeExpenditureType.BILL.getCode());
			record.setProductId(bill.getProductId());
			record.setSupplierId(bill.getEntityId());
			Supplier supplier = supplierService.read(bill.getEntityId());
			if (supplier != null) {
				record.setDeptId(supplier.getDeptId());
			}
			record.setWtime(new Timestamp(System.currentTimeMillis()));
			record.setCreaterId(expenseIncome.getUserId());
			record.setFlowEntId(null);
			record.setChargePrice(money);
			record.setRemark("导入关联收入");
			chargeRecordService.save(record);
		} catch (Exception e) {
			logger.error("保存收支记录异常", e);
		}
	}

	/**
	 * 增加现金流
	 * 
	 * @param bill
	 *            账单信息
	 * @param expenseIncome
	 *            导入信息
	 * @param money
	 *            关联金额
	 */
	private void addCashFlow(ProductBills bill, FsExpenseIncome expenseIncome, BigDecimal money) {
		try {
			SearchFilter filter = new SearchFilter();
			Timestamp time = null;
			if (expenseIncome.getIsIncome() == 0) {
				// 收入
				time = bill.getFinalReceiveTime();
			} else {
				// 支出
				time = bill.getFinalPayTime();
			}
			Date date = new Date(time.getTime());
			Timestamp startTime = new Timestamp(DateUtil.getLastMonthFinal(date).getTime());
			Timestamp endTime = new Timestamp(DateUtil.getMonthFinal(date).getTime());
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startTime));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, bill.getProductId()));
			List<CashFlow> cacheFlowList = cashflowService.queryAllBySearchFilter(filter);
			// 现金流
			CashFlow cashFlow = null;
			boolean iscreat = true;
			if (cacheFlowList != null && !cacheFlowList.isEmpty()) {
				cashFlow = cacheFlowList.get(0);
				iscreat = false;
			} else {
				cashFlow = new CashFlow();
				cashFlow.setWtime(new Timestamp(DateUtil.getThisMonthFirst(date).getTime()));
				cashFlow.setEntityType(bill.getEntityType());
				cashFlow.setEntityId(bill.getEntityId());
				cashFlow.setProductId(bill.getProductId());
				if (bill.getEntityType() == EntityType.SUPPLIER.ordinal()) {
					Product product = productService.read(bill.getProductId());
					if (product != null) {
						cashFlow.setProductType(product.getProductType());
					}
					Supplier supplier = supplierService.read(bill.getEntityId());
					if (supplier != null) {
						cashFlow.setDeptId(supplier.getDeptId());
					}
				} else if (bill.getEntityType() == EntityType.CUSTOMER.ordinal()) {
					CustomerProduct customerProduct = customerProductService.read(bill.getProductId());
					if (customerProduct != null) {
						cashFlow.setProductType(customerProduct.getProductType());
					}
					Customer customer = customerService.read(bill.getEntityId());
					if (customer != null) {
						cashFlow.setDeptId(customer.getDeptId());
					}
				}
			}
			if (expenseIncome.getIsIncome() == 0) {
				cashFlow.setActualReceivables(cashFlow.getActualReceivables().add(money));
				cashFlow.setReceivables(cashFlow.getReceivables().add(money));
			} else {
				cashFlow.setActualPayables(cashFlow.getActualPayables().add(money));
				cashFlow.setPayables(cashFlow.getPayables().add(money));
			}
			if (iscreat) {
				cashflowService.save(cashFlow);
			} else {
				cashflowService.update(cashFlow);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 更新账单和到款，自动发起销账流程
	 * @param bills				参与销账的账单
	 * @param fsExpenseIncomes	参与销账的到款
	 * @param reqDto			流程信息对象
	 * @param ossUserId			用户id
	 */
	@Override
	public void saveWriteOffInfo(List<ProductBills> bills, List<FsExpenseIncome> fsExpenseIncomes, ApplyProcessReqDto reqDto, String ossUserId) {
		try {
			baseDao.updateByBatch(bills);
			baseDao.updateByBatch(fsExpenseIncomes);
			User user = userDao.read(ossUserId);
			OnlineUser onlineUser = new OnlineUser();
			onlineUser.setUser(user);
			// 查询下一个节点
			customerOperateService.applyProcess(reqDto, onlineUser);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	// 单个线程处理最大数量
	private final Integer threadDeal = 65535;

	// 最大线程数
	private final Integer maxThread = 10;

	private String downLoadFile = "exportFile/fsExpenseIncome";

	@Override
	public UploadFileRespDto exportFsExpenseIncome(SearchFilter searchFilter, String fileName) {
		List<File> fileList = new ArrayList<File>();
		UploadFileRespDto result = new UploadFileRespDto();
		// 导出文件路径
		String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
		String resourceDir = Constants.RESOURCE + File.separator + downLoadFile + File.separator + datePath;
		String zipName = "";
		try {
			File dir = new File(resourceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			int count = this.getCount(searchFilter);
			int threadSize = count % threadDeal == 0 ? count / threadDeal : (count / threadDeal + 1);
			int dealSize = threadDeal;
			if (threadSize > maxThread) {
				threadSize = maxThread;
				dealSize = count % threadSize == 0 ? count / threadSize : (count / threadSize + 1);
			}
			CountDownLatch coDownLatch = new CountDownLatch(threadSize);
			for (int i = 1; i <= threadSize; i++) {
				int dealStart = (i - 1) * dealSize;
				int dealEnd = i * dealSize < count ? i * dealSize : count;
				int dealCount = dealEnd - dealStart;
				new ExportFsExpenseIncomeThread(searchFilter, coDownLatch, fileList, i, dealCount, resourceDir, fileName).start();
			}
			coDownLatch.await();
			zipName = resourceDir + File.separator + fileName + ".zip";
			ParseFile.zipFile(fileList, zipName);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			result.setFilePath(zipName);
			result.setFileName(fileName + ".zip");
		}
		return result;
	}

	class ExportFsExpenseIncomeThread extends Thread {
		private CountDownLatch dealThreadNum = null;
		private List<File> fileList = new ArrayList<File>();
		private int page;
		private int pageSize;
		private String fileName;
		private String filePath;
		private SearchFilter searchFilter;

		private int selectSize = 1000;
		DecimalFormat df = new DecimalFormat("###,###.00");

		public ExportFsExpenseIncomeThread(SearchFilter searchFilter, CountDownLatch dealThreadNum, List<File> fileList, int page, int pageSize,
				String filePath, String fileName) {
			this.dealThreadNum = dealThreadNum;
			this.fileList = fileList;
			this.page = page;
			this.pageSize = pageSize;
			this.fileName = fileName;
			this.filePath = filePath;
			this.searchFilter = searchFilter;
		}

		@Override
		public void run() {
			logger.info("收支数据导出任务处理器-" + page + "启动");
			long startTime = System.currentTimeMillis();
			List<FsExpenseIncome> list = new ArrayList<FsExpenseIncome>();
			List<String[]> dataList = new ArrayList<String[]>();
			Map<String, Department> deptsMap = new HashMap<String, Department>();
			try {
				int pages = (pageSize % selectSize == 0 ? (pageSize / selectSize) : (pageSize / selectSize + 1));
				int start = 0;
				int dealStart = (page - 1) * pageSize;
				for (int i = 0; i < pages; i++) {
					start = dealStart + i * selectSize;
					int size = ((pageSize - (start - dealStart)) > selectSize) ? selectSize : (pageSize - (start - dealStart));
					list.addAll(findByFilter(size, start, searchFilter));
				}
				String[] title = new String[] { "到账时间", "流水号", "银行名称", "事业部", "部门", "区域", "分类", "姓名", "内容摘要", "收入", "支出", "剩余消账金额", "备注", "导入时间", "客户名称",
						"付费类型" };

				// 读取所有客户名称
				List<Customer> allCustomerList = customerService.queryAllBySearchFilter(null);
				Map<String, String> cacheCustomerMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(allCustomerList)) {
					allCustomerList.forEach(customer -> {
						cacheCustomerMap.put(customer.getCustomerId(), customer.getCompanyName());
					});
				}

				// 客户的付费类型
				List<CustomerProduct> allProductList = customerProductService.queryAllByFilter(null);
				Map<String, List<CustomerProduct>> groupMap = null;
				if (!CollectionUtils.isEmpty(allProductList)) {
					groupMap = allProductList.stream().collect(Collectors.groupingBy(CustomerProduct::getCustomerId));
				}
				Map<String, String> cacheSettleTypeMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(groupMap)) {
					groupMap.forEach((customerId, products) -> {
						if (CollectionUtils.isEmpty(products)) {
							cacheSettleTypeMap.put(customerId, "暂无产品");
						} else {
							int lastSettleType = -1;
							for (CustomerProduct product : products) {
								int settleType = product.getSettleType();
								settleType = settleType == 0 ? 0 : (settleType - 1);
								if (lastSettleType == -1) {
									lastSettleType = product.getSettleType();
								} else if (lastSettleType != product.getSettleType()) {
									cacheSettleTypeMap.put(customerId, "混合");
									return;
								}
							}
							cacheSettleTypeMap.put(customerId, lastSettleType == 0 ? "预付" : "后付");
						}
					});
				}

				for (FsExpenseIncome expenseIncome : list) {
					dataList.add(buildExportData(deptsMap, title, expenseIncome, cacheCustomerMap, cacheSettleTypeMap));
				}

				File file = new File(filePath + File.separator + fileName + "-" + page + ".xls");
				if (!file.exists()) {
					file.createNewFile();
				}
				ParseFile.exportDataToExcel(dataList, file, title);
				fileList.add(file);
			} catch (ServiceException e) {
				logger.error("导出线程，导出第" + page + "页," + pageSize + "条数据异常", e);
			} catch (Exception e) {
				logger.error("导出线程，导出第" + page + "页," + pageSize + "条数据异常", e);
			} finally {
				if (list != null && !list.isEmpty()) {
					list.clear();
					list = null;
				}
				if (dataList != null && !dataList.isEmpty()) {
					dataList.clear();
					dataList = null;
				}
				if (deptsMap != null && !deptsMap.isEmpty()) {
					deptsMap.clear();
					deptsMap = null;
				}
				dealThreadNum.countDown();
				logger.info("收支数据导出任务处理器-" + page + "执行结束,共耗时:" + (System.currentTimeMillis() - startTime));
			}
		}

		/**
		 * 封装导出数据
		 * 
		 * @param deptsMap
		 * @param title
		 * @param expenseIncome
		 * @param cacheCustomerMap
		 * @param cacheSettleTypeMap
		 * @return
		 * @throws ServiceException
		 */
		private String[] buildExportData(Map<String, Department> deptsMap, String[] title, FsExpenseIncome expenseIncome, Map<String, String> cacheCustomerMap,
				Map<String, String> cacheSettleTypeMap) throws ServiceException {
			String[] data = new String[title.length];
			Timestamp operateTime = expenseIncome.getOperateTime();
			data[0] = operateTime == null ? "" : DateUtil.convert(expenseIncome.getOperateTime(), DateUtil.format1);
			data[1] = expenseIncome.getSerialNumber();
			data[2] = expenseIncome.getBankName();
			String deptId = expenseIncome.getDeptId();
			String parentid = "";
			Department optDept = optDept(deptsMap, deptId);
			if (optDept != null) {
				parentid = optDept.getParentid();
			}
			Department dept = optDept(deptsMap, parentid);
			if (dept != null) {
				data[4] = expenseIncome.getDeptName();
			}
			data[5] = expenseIncome.getRegionName();
			Optional<FeeType> feeType = FeeType.getEnumsByCode(expenseIncome.getFeeType());
			if (feeType.isPresent()) {
				data[6] = feeType.get().getMsg();
			}
			data[7] = expenseIncome.getOperator();
			data[8] = expenseIncome.getDepict();
			String cost = thousand(expenseIncome.getCost());
			if (0 == expenseIncome.getIsIncome()) {
				data[9] = cost;
			} else if (1 == expenseIncome.getIsIncome()) {
				data[10] = cost;
			}

			data[11] = thousand(expenseIncome.getRemainRelatedCost());
			data[12] = expenseIncome.getRemark();
			Timestamp wtime = expenseIncome.getWtime();
			data[13] = wtime == null ? "" : DateUtil.convert(wtime, DateUtil.format2);
			if (StringUtils.isBlank(cacheCustomerMap.get(expenseIncome.getCustomerId()))) {
				data[14] = "";
			} else {
				data[14] = cacheCustomerMap.get(expenseIncome.getCustomerId());
			}
			if (StringUtils.isBlank(expenseIncome.getCustomerId())) {
				data[15] = "";
			} else if (StringUtils.isBlank(cacheCustomerMap.get(expenseIncome.getCustomerId()))) {
				data[15] = "暂无产品";
			} else {
				data[15] = cacheSettleTypeMap.get(expenseIncome.getCustomerId());
			}
			return data;
		}

		private Department optDept(Map<String, Department> deptsMap, String deptId) throws ServiceException {
			Department department = null;
			if (StringUtils.isNotBlank(deptId)) {
				if (deptsMap.containsKey(deptId)) {
					department = deptsMap.get(deptId);
				} else {
					department = departmentService.read(deptId);
				}
			}
			return department;
		}

		private String thousand(BigDecimal num) {
			return df.format(num);
		}
	}

	/**
	 * 查询到款
	 * 
	 * @param startTime
	 *            开始时间 <=
	 * @param endTime
	 *            结束时间 <
	 * @param timeType
	 *            到款时间/导入时间
	 * @param onlineUser
	 *            当前用户
	 * @return
	 */
	@Override
	public JSONObject querySaleIncome(Date startTime, Date endTime, String timeType, OnlineUser onlineUser) {
		List<Customer> customerList = customerService.readCustomers(onlineUser, "", "", "", "");
		if (CollectionUtils.isEmpty(customerList)) {
			logger.info("销售数据权限下没有客户");
			return null;
		}
		JSONObject incomeInfo = new JSONObject();
		List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		String hql = "select sum(cost), sum(cost-remainRelatedCost) from FsExpenseIncome where customerId in :customerIdList";
		Map<String, Object> params = new HashMap<>();
		params.put("customerIdList", customerIdList);
		if (StringUtil.isNotBlank(timeType) || "1".equals(timeType)) {
			hql += " and operateTime >= :startTime and operateTime < :endTime";
			params.put("startTime", startTime);
			params.put("endTime", endTime);
		} else if ("2".equals(timeType)) {
			hql += " and wtime >= :startTime and wtime < :endTime";
			params.put("startTime", startTime);
			params.put("endTime", endTime);
		}
		try {
			BigDecimal totalIncome = new BigDecimal(0);
			BigDecimal totalWriteOff = new BigDecimal(0);
			List<Object[]> result = baseDao.findByhql(hql, params, 0);
			if (!CollectionUtils.isEmpty(result)) {
				Object[] data = result.get(0);
				if (null != data[0]) {
					totalIncome = (BigDecimal) data[0];
				}
				if (null != data[0]) {
					totalWriteOff = (BigDecimal) data[1];
				}
			}
			incomeInfo.put("totalIncome", totalIncome.toPlainString());
			incomeInfo.put("totalWriteOff", totalWriteOff.toPlainString());
			return incomeInfo;
		} catch (Exception e) {
			logger.error("查询财务收支记录异常", e);
			return null;
		}
	}

	@Override
	public boolean updateByBatch(List<FsExpenseIncome> objs) throws ServiceException {
		try {
			return fsExpenseIncomeDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}}
