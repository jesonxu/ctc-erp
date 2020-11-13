package com.dahantc.erp.controller.statistics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.statistics.StatisticsRspDto;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/statistics")
public class StatisticsAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(StatisticsAction.class);

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IUnitPriceService unitPriceService;

	/**
	 * 获取统计时间
	 */
	@RequestMapping("/getStatisticsTime")
	public String getStatisticsTime(@RequestParam String keyWord, @RequestParam String supplierTypeId, @RequestParam String supplierId,
			@RequestParam String productId) {
		try {
			Date startTime = null;
			Date nowTime = new Timestamp(System.currentTimeMillis());
			logger.info("查询统计时间开始");
			String entityType = request.getParameter("entityType");
			int entitytype = EntityType.SUPPLIER.ordinal();
			// 判断是否是电商的供应商
			if (StringUtil.isNotBlank(entityType)) {
				if (StringUtils.isNumeric(entityType)) {
					Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entityType));
					if (entityTypeOpt.isPresent()) {
						entitytype = entityTypeOpt.get().ordinal();
					} else {
						logger.info("错误的实体类型：" + entityType);
						return "";
					}
				} else {
					logger.info("错误的实体类型：" + entityType);
					return "";
				}
			}
			if (StringUtil.isNotBlank(productId)) { // 点击产品
				Product product = productService.read(productId);
				if (product != null) {
					Supplier supplier = supplierService.read(product.getSupplierId());
					if (supplier != null) {
						startTime = supplier.getWtime();
					}
				}
			} else if (StringUtil.isNotBlank(supplierId)) { // 点击供应商
				Supplier supplier = supplierService.read(supplierId);
				if (supplier != null) {
					startTime = supplier.getWtime();
				}
			} else { // 未点击产品或者供应商，按数据权限查询供应商
				List<Supplier> supplierList = supplierService.readSuppliers(getOnlineUserAndOnther(), "", supplierId, supplierTypeId, keyWord, SearchType.SUPPLIER.ordinal());
				if (!ListUtils.isEmpty(supplierList)) {
					supplierList = supplierList.stream().sorted((o1, o2) -> o1.getWtime().compareTo(o2.getWtime())).collect(Collectors.toList());
					startTime = supplierList.get(0).getWtime();
				}
			}

			List<Integer> years = new ArrayList<>();
			if (startTime != null && startTime.before(nowTime)) {
				Calendar startCal = Calendar.getInstance();
				Calendar nowCal = Calendar.getInstance();
				startCal.setTime(startTime);
				nowCal.setTime(nowTime);
				years = Stream.iterate(startCal.get(Calendar.YEAR), item -> item + 1).limit(nowCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR) + 1)
						.collect(Collectors.toList());
			}
			request.setAttribute("pagePermission", roleService.getPagePermission(getOnlineUserAndOnther().getRoleId()));
			request.setAttribute("years", years);
			if (entitytype == EntityType.SUPPLIER_DS.ordinal()) {
				return "/views/statisticsDs/statisticsTime";
			}
			return "/views/statistics/statisticsTime";
		} catch (ServiceException e) {
			logger.error("查询统计时间异常", e);
		}
		return "";
	}

	@RequestMapping("/toStatisticsSheet")
	public String toStatisticsSheet() {
		JSONObject params = new JSONObject();
		params.put("productId", request.getParameter("productId"));
		params.put("productName", request.getParameter("productName"));
		params.put("supplierId", request.getParameter("supplierId"));
		params.put("supplierName", request.getParameter("supplierName"));
		params.put("customerId", request.getParameter("customerId"));
		params.put("customerName", request.getParameter("customerName"));
		params.put("supplierTypeId", request.getParameter("supplierTypeId"));
		params.put("supplierTypeName", request.getParameter("supplierTypeName"));
		params.put("customerTypeId", request.getParameter("customerTypeId"));
		params.put("customerTypeName", request.getParameter("customerTypeName"));
		params.put("companyName", request.getParameter("companyName"));
		params.put("deptIds", request.getParameter("deptIds"));
		params.put("searchCustomerId", request.getParameter("searchCustomerId"));
		params.put("keyWord", request.getParameter("keyWord"));
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("title", request.getParameter("title"));
		return "/views/sheet/newStatisticsSheet";
	}

	/**
	 * 获取统计详情
	 * 
	 * @param keyWord
	 *            搜索关键词
	 * @param supplierTypeId
	 *            供应商类型
	 * @param supplierId
	 *            供应商id
	 * @param productId
	 *            产品id
	 * @param reqYear
	 *            查询年份
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getStatisticsDetail")
	public BaseResponse<JSONObject> getStatisticsDetail(@RequestParam String keyWord, @RequestParam String supplierTypeId, @RequestParam String supplierId,
			@RequestParam String productId, @RequestParam int reqYear) {
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，查询统计记录开始，年份：" + reqYear);

			long __start = System.currentTimeMillis();

			// 查账单表的过滤器
			SearchFilter billFilter = new SearchFilter();

			// 查询产品账单记录
			List<String> supplierIdList = new ArrayList<>();
			if (StringUtils.isNotBlank(productId)) {
				// 点击产品
				Product product = productService.read(productId);
				if (product == null) {
					logger.info("未查询到产品，产品id：" + productId);
					return BaseResponse.success(new JSONObject());
				}
				supplierIdList.add(product.getSupplierId());
				billFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (StringUtils.isNotBlank(supplierId)) {
				// 点击供应商
				Supplier supplier = supplierService.read(supplierId);
				if (supplier == null) {
					logger.info("未查询到供应商，供应商id：" + supplierId);
					return BaseResponse.success(new JSONObject());
				}
				supplierIdList.add(supplierId);
				billFilter.getRules().add(new SearchRule("entityId", Constants.ROP_EQ, supplierId));
			} else {
				// 未点击产品或者供应商，按数据权限查询
				logger.info("未点击供应商或产品，按权限和条件查询供应商开始");
				List<Supplier> suppliers = supplierService.readSuppliers(onlineUser, "", "", supplierTypeId, keyWord, SearchType.SUPPLIER.ordinal());
				if (ListUtils.isEmpty(suppliers)) {
					logger.info("按权限和条件未查询到客户");
					return BaseResponse.success(new JSONObject());
				}
				logger.info("按权限和条件查询到" + suppliers.size() + "条供应商记录，耗时：" + (System.currentTimeMillis() - __start));
				supplierIdList = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
				billFilter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, supplierIdList));
			}

			// 根据供应商查产品
			List<String> productIdList = new ArrayList<>();
			logger.info("查询供应商产品开始");
			__start = System.currentTimeMillis();
			SearchFilter productFilter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) { // 产品条件
				productFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (!ListUtils.isEmpty(supplierIdList)) { // 有产品的情况下不需要供应商条件
				productFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, supplierIdList));
			}
			List<Product> productList = productService.queryAllBySearchFilter(productFilter);
			if (productList != null && productList.size() > 0) {
				logger.info("查询到" + productList.size() + "条供应商产品记录，耗时：" + (System.currentTimeMillis() - __start));
				productIdList = productList.stream().map(Product::getProductId).collect(Collectors.toList());
			} else {
				logger.info("未查询到供应商产品");
			}

			// 查询账单记录
			__start = System.currentTimeMillis();
			logger.info("查询供应商产品账单开始");
			billFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert4(reqYear + "-01")));
			billFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.convert4((reqYear + 1) + "-01")));
			billFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.SUPPLIER.ordinal()));
			List<ProductBills> billList = productBillsService.queryAllBySearchFilter(billFilter);
			logger.info("查询到" + (billList == null ? 0 : billList.size()) + "条产品账单记录，耗时：" + (System.currentTimeMillis() - __start));

			// 获取从开始到结束的每月
			Map<String, ProductBills> dateBillMap = DateUtil.getYearMonth(reqYear);

			// 存放每个月有账单记录的产品id，在后面用来排除，以获取未出账单的产品
			Map<String, Set<String>> hasBillProductIdMap = new HashMap<>();

			// 遍历查询出的账单记录，将每个账单的数据累计到每月中
			if (!ListUtils.isEmpty(billList)) {
				logger.info("将账单数据累计到每月中开始");
				__start = System.currentTimeMillis();
				for (ProductBills bill : billList) {
					String date = DateUtil.convert(bill.getWtime(), DateUtil.format4);
					if (dateBillMap.get(date) == null) {
						dateBillMap.put(date, new ProductBills());
						hasBillProductIdMap.put(date, new HashSet<>());
					}
					// 记录在某月此产品出了账单
					hasBillProductIdMap.get(date).add(bill.getProductId());

					// 将每条账单记录的数据累加到对应月份中
					ProductBills temp = dateBillMap.get(date);
					temp.setSupplierCount(temp.getSupplierCount() + bill.getSupplierCount());
					temp.setPlatformCount(temp.getPlatformCount() + bill.getPlatformCount());
					temp.setPayables(temp.getPayables().add(bill.getPayables()));
					temp.setActualPayables(temp.getActualPayables().add(bill.getActualPayables()));
					temp.setActualReceivables(temp.getActualReceivables().add(bill.getActualReceivables()));
					temp.setReceivables(temp.getReceivables().add(bill.getReceivables()));
				}
				logger.info("将账单数据累计到每月中结束，耗时：" + (System.currentTimeMillis() - __start));
			}

			// 查询供应商统计详情表，获取每个通道的统计数据
			List<Map<String, Object>> statisticsList = queryStatistics(reqYear, supplierTypeId, supplierId, productId, "", keyWord, supplierIdList);
			DateFormat df = new SimpleDateFormat("yyyy-MM");
			// 以 统计日期,产品id 为key 生成统计map
			Map<String, List<Map<String, Object>>> statisticsMap = statisticsList.stream().collect(Collectors.groupingBy(item -> {
				Map<String, Object> infoMap = (Map<String, Object>) item;
				return df.format(infoMap.get("date")) + "," + infoMap.get("productId");
			}));

			List<StatisticsRspDto> proResultList = new ArrayList<>();

			// 遍历每月的账单
			logger.info("遍历生成每月的统计对象开始");
			__start = System.currentTimeMillis();
			for (Entry<String, ProductBills> entry : dateBillMap.entrySet()) {
				logger.info("生成" + entry.getKey() + "统计对象开始");
				long ___start = System.currentTimeMillis();

				StatisticsRspDto dto = null;
				// 分两步: 账单 + 未出账单 (未出账单的产品，用统计详情表的成功数、应付)，

				// 第一步，账单
				if (null != entry.getValue()) {
					dto = new StatisticsRspDto(entry.getValue());
				} else {
					dto = new StatisticsRspDto();
				}

				dto.setYear(entry.getKey().substring(0, 4));
				dto.setMonth(entry.getKey().substring(5));

				// 第二步，未出账单的产品，用统计表详情表的成功数、应付
				// =============================第二步开始==============================
				BigDecimal payables = new BigDecimal(0); // 当月应付

				// 获取未出账单的产品，先获取所有产品的信息，排除当月出了账单的产品，剩下的就是未出账单的产品
				List<String> productIdColne = new ArrayList<String>(Arrays.asList(new String[productIdList.size()]));
				Collections.copy(productIdColne, productIdList);
				Set<String> set = hasBillProductIdMap.get(entry.getKey());
				if (null != set && !set.isEmpty()) {
					productIdColne.removeAll(set);
				}

				// 遍历所有未出账单的产品
				logger.info("第二步，统计" + entry.getKey() + "未出账单的产品的数据开始，未出账单产品数：" + productIdColne.size());
				long ____start = System.currentTimeMillis();
				// 遍历所有未出账单的产品，获取该产品在当月的统计记录
				for (String productid : productIdColne) {
					// 获取该产品在当月的统计记录
					List<Map<String, Object>> dataList = statisticsMap.get(entry.getKey() + "," + productid);
					if (dataList != null && dataList.size() > 0) {
						for (Map<String, Object> data : dataList) {
							long successCount = (long) data.get("successCount");
							BigDecimal productPayables = (BigDecimal) data.get("payables");
							payables = payables.add(productPayables); // 累计到当月应付
							dto.setPlatformCount(dto.getPlatformCount() + successCount);
						}
					}
				}
				payables.setScale(2, BigDecimal.ROUND_CEILING);
				logger.info("第二步，统计" + entry.getKey() + "未出账单的产品的数据结束，耗时：" + (System.currentTimeMillis() - ____start));
				// =============================第二步结束==============================

				// 将计算出的应收加到统计对象中
				String prePayables = dto.getPayables();
				if (NumberUtils.isParsable(prePayables)) {
					dto.setPayables(payables.add(new BigDecimal(prePayables)).toString());
				} else {
					dto.setPayables(String.format("%.2f", payables));
				}

				proResultList.add(dto);
				logger.info("生成" + entry.getKey() + "统计对象结束，耗时：" + (System.currentTimeMillis() - ___start));
			}
			logger.info("遍历生成每月的统计对象开始，总耗时：" + (System.currentTimeMillis() - __start));

			proResultList.sort((o1, o2) -> {
				if (o1.getYear().equals(o2.getYear())) {
					return o1.getMonth().compareTo(o2.getMonth());
				} else {
					return o1.getYear().compareTo(o2.getYear());
				}
			});
			// 按周、月份或者季度等合并记录
			logger.info("合并每月的统计对象到每月开始");
			__start = System.currentTimeMillis();
			Map<String, ArrayList<StatisticsRspDto>> resultMap = new HashMap<>();
			String lastDate = "";
			for (StatisticsRspDto rsp : proResultList) {
				String year = rsp.getYear();
				if (!resultMap.containsKey(year)) {
					resultMap.put(year, new ArrayList<>());
				}
				ArrayList<StatisticsRspDto> yearList = resultMap.get(year);
				if (!yearList.isEmpty()) {
					lastDate = yearList.get(yearList.size() - 1).getMonth();
				}
				// 获取本对象的日期对应的时间标题，比如 2019-12-25 对应是 第四周（12-23到12-29）
				String thisDate = rsp.getMonth();
				if (lastDate.equals(thisDate)) {
					StatisticsRspDto temp = yearList.get(yearList.size() - 1);
					temp.addSupplierCount(rsp.getSupplierCount());
					temp.addPlatformCount(rsp.getPlatformCount());
					temp.addReceivables(rsp.getReceivables());
					temp.addActualReceivables(rsp.getActualReceivables());
					temp.addPayables(rsp.getPayables());
					temp.addActualPayables(rsp.getPayables());
				} else {
					yearList.add(rsp);
				}
			}
			logger.info("合并每月的统计对象到每月结束，耗时：" + (System.currentTimeMillis() - __start));

			JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(resultMap));
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，查询统计记录结束，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("查询统计记录异常", e);
		}
		return BaseResponse.success("未查询到数据", new JSONObject());
	}

	/**
	 * 查询客户统计详情表获取成功数
	 * 
	 * @param reqYear
	 *            查询年份
	 * @param supplierTypeId
	 *            供应商类型（点击条件）
	 * @param supplierId
	 *            供应商id（点击条件）
	 * @param productId
	 *            产品id（点击条件）
	 * @param deptIds
	 *            部门id（过滤条件）
	 * @param keyWord
	 *            关键词（过滤条件）
	 * @param supplierIdList
	 *            由过滤条件查出的供应商id
	 * @throws BaseException
	 */
	private List<Map<String, Object>> queryStatistics(int reqYear, String supplierTypeId, String supplierId, String productId, String deptIds, String keyWord,
			List<String> supplierIdList) throws BaseException {
		logger.info("查询供应商统计表获取成功数开始");
		long _start = System.currentTimeMillis();
		List<Map<String, Object>> result = new ArrayList<>();
		String sqlStart = "SELECT statsYearMonth,supplierId,productId,sum(successCount),sum(payables) FROM SupplierStatistics WHERE statsYearMonth >= :startTime AND statsYearMonth <= :endTime AND businessType = "
				+ BusinessType.YTX.ordinal();
		String sqlMiddle = "";
		String sqlEnd = " GROUP BY statsYearMonth,supplierId,productId";
		Map<String, Object> params = new HashMap<>();
		params.put("startTime", DateUtil.convert4(reqYear + "-01"));
		params.put("endTime", DateUtil.convert4((reqYear + 1) + "-01"));
		if (StringUtil.isNotBlank(productId)) {
			// 点击产品
			logger.info("查询统计，点击产品id：" + productId);
			sqlMiddle += " AND productId = :productId";
			params.put("productId", productId);
		} else if (StringUtil.isNotBlank(supplierId)) {
			// 点击供应商
			logger.info("查询统计，点击供应商id：" + supplierId);
			sqlMiddle += " AND supplierId = :supplierId";
			params.put("supplierId", supplierId);
		} else {
			// 未点击产品或供应商，按数据权限和过滤条件
			OnlineUser onlineUser = getOnlineUserAndOnther();
			User user = onlineUser.getUser();
			Role role = roleService.read(onlineUser.getRoleId());
			int dataPermission = role.getDataPermission();
			logger.info("查询统计，未点击供应商或产品，按数据权限和过滤条件查询");
			// 数据权限
			if (DataPermission.Self.ordinal() == dataPermission) {
				sqlMiddle += " AND ossUserId = :ossUserId";
				params.put("ossUserId", user.getOssUserId());
			} else if (DataPermission.Dept.ordinal() == dataPermission) {
				sqlMiddle += " AND deptId = :deptId";
				params.put("deptId", user.getDeptId());
			} else if (DataPermission.Customize.ordinal() == dataPermission) {
				sqlMiddle += " AND deptId IN :deptIds";
				// 用户数据权限下的部门
				List<String> userDeptIds = new ArrayList<>(Arrays.asList(role.getDeptIds().split(",")));
				if (StringUtil.isNotBlank(deptIds)) {
					// 部门过滤条件
					List<String> searchdeptIds = new ArrayList<>(Arrays.asList(deptIds.split(",")));
					searchdeptIds = searchdeptIds.stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
					if (searchdeptIds.size() > 0) {
						// 取交集
						userDeptIds.retainAll(searchdeptIds);
					}
				}
				params.put("deptIds", userDeptIds);
			} else if (DataPermission.All.ordinal() == dataPermission || DataPermission.Flow.ordinal() == dataPermission) {
				// 全部、流程 权限
				if (StringUtil.isNotBlank(deptIds)) {
					// 部门过滤条件
					List<String> searchdeptIds = new ArrayList<>(Arrays.asList(deptIds.split(",")));
					searchdeptIds = searchdeptIds.stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
					if (searchdeptIds.size() > 0) {
						sqlMiddle += " AND deptId IN :deptIds";
						params.put("deptIds", searchdeptIds);
					}
				}
			}

			if (StringUtil.isNotBlank(supplierTypeId)) {
				// 点击供应商类型
				sqlMiddle += " AND supplierTypeId = :supplierTypeId";
				params.put("supplierTypeId", supplierTypeId);
			}

			if (StringUtil.isNotBlank(keyWord) || DataPermission.Flow.ordinal() == dataPermission) {
				// 关键词搜索得到的供应商id，或者按流程权限查到的供应商id
				sqlMiddle += " AND supplierId IN :supplierIdList";
				params.put("supplierIdList", supplierIdList);
			}
		}
		String sql = sqlStart + sqlMiddle + sqlEnd;
		List<Object> statisticsList = baseDao.findByhql(sql, params, 0);
		if (ListUtils.isEmpty(statisticsList)) {
			logger.info("未查询到记录");
			return result;
		}
		logger.info("查询到" + statisticsList.size() + "条统计记录");
		for (Object obj : statisticsList) {
			Map<String, Object> map = new HashMap<>();
			Object[] ob = (Object[]) obj;
			map.put("date", (java.sql.Date) ob[0]);
			map.put("supplierId", (String) ob[1]);
			map.put("productId", (String) ob[2]);
			map.put("successCount", ((Long) ob[3]));
			map.put("payables", ((BigDecimal) ob[4]));
			result.add(map);
		}
		logger.info("查询供应商统计详情表获取成功数结束，耗时：" + (System.currentTimeMillis() - _start));
		return result;
	}
}
