package com.dahantc.erp.vo.multipleStatistics.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.multipleStatistics.IRegionStatisticsService;

@Service("regionStatisticsService")
public class RegionStatisticsServiceImpl implements IRegionStatisticsService {

	private static Logger logger = LogManager.getLogger(RegionStatisticsServiceImpl.class);

	private static final String QUERY_REGION_HQL = "SELECT deptId, statsDate, SUM(receivables), SUM(cost), SUM(grossProfit), businessType FROM CustomerStatistics GROUP BY deptId, statsDate HAVING businessType = :businessType and deptId in :deptIds ORDER BY statsDate ASC, deptId DESC";

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IBaseDao baseDao;

	/** 区域统计查询 */
	@Override
	public Map<String, Map<String, RegionStatisticsData[]>> queryRegionStatistics(OnlineUser user) {
		logger.info("查询[大汉三通云通讯各大区域经营状况表]开始");
		Map<String, Map<String, RegionStatisticsData[]>> regionStatistics = new LinkedHashMap<>();
		Map<String, Set<String>> regionDepts = new TreeMap<>();
		long startTime = System.currentTimeMillis();
		try {
			// 用户数据权限下能看到的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(user);
			if (ListUtils.isEmpty(deptIdList)) {
				return regionStatistics;
			}
			// 部门分区域
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("parentid", Constants.ROP_EQ, "1"));
			filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, deptIdList));
			List<Department> secondDepts = departmentService.queryAllBySearchFilter(filter);
			for (Department sDept : secondDepts) {
				String deptName = sDept.getDeptname();
				if (deptName.contains("(") && deptName.contains(")")) {
					Set<String> sonDepts = new HashSet<>(
							departmentService.getSubDept(sDept.getDeptid()).stream().map(Department::getDeptid).collect(Collectors.toList()));
					sonDepts.add(sDept.getDeptid()); // 自己部门+子部门
					String key = deptName.substring(deptName.indexOf("(") + 1, deptName.indexOf(")"));
					if (regionDepts.get(key) == null) {
						regionDepts.put(key, sonDepts);
					} else {
						regionDepts.get(key).addAll(sonDepts);
						sonDepts = null;
					}
					if (regionStatistics.get(key) == null) {
						regionStatistics.put(key, new LinkedHashMap<>());
					}
				}
			}
			secondDepts = null;

			// 客户统计表按照部门ID和统计月份聚合
			Map<String, Object> params = new HashMap<>();
			params.put("businessType", BusinessType.YTX.ordinal());
			params.put("deptIds", deptIdList);
			List<Object> queryData = baseDao.findByhql(QUERY_REGION_HQL, params, 0);
			// 解析数据
			for (Object obj : queryData) {
				Object[] datas = (Object[]) obj;
				String deptName = null;
				Calendar calender = Calendar.getInstance();
				calender.setTime((Date) datas[1]);
				String year = calender.get(Calendar.YEAR) + "";
				Integer month = calender.get(Calendar.MONTH);
				if (datas[0] != null) {
					for (Map.Entry<String, Set<String>> entry : regionDepts.entrySet()) {
						if (entry.getValue().contains((String) datas[0])) {
							deptName = entry.getKey();
						}
					}
				}
				if (StringUtils.isNotBlank(deptName)) {
					if (regionStatistics.get(deptName).get(year) == null) {
						regionStatistics.get(deptName).put(year, new RegionStatisticsData[12]);
					}
					if (regionStatistics.get(deptName).get(year)[month] == null) {
						regionStatistics.get(deptName).get(year)[month] = new RegionStatisticsData();
					}
					// 收入 成本 毛利润
					if (datas[2] != null) {
						regionStatistics.get(deptName).get(year)[month]
								.setReceivables(regionStatistics.get(deptName).get(year)[month].getReceivables().add((BigDecimal) datas[2]));
					}
					if (datas[3] != null) {
						regionStatistics.get(deptName).get(year)[month]
								.setCost(regionStatistics.get(deptName).get(year)[month].getCost().add((BigDecimal) datas[3]));
					}
					if (datas[4] != null) {
						regionStatistics.get(deptName).get(year)[month]
								.setGrossProfit(regionStatistics.get(deptName).get(year)[month].getGrossProfit().add((BigDecimal) datas[4]));
					}
				}
			}
			logger.info("重新统计[大汉三通云通讯各大区域经营状况表]耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.error("重新统计[大汉三通云通讯各大区域经营状况表]异常", e);
		} finally {
			if (regionDepts != null) {
				regionDepts.clear();
				regionDepts = null;
			}
		}
		return regionStatistics;
	}

	public class RegionStatisticsData implements Serializable {

		private static final long serialVersionUID = 6599844059358832492L;

		private BigDecimal receivables = new BigDecimal(0);

		private BigDecimal cost = new BigDecimal(0);

		private BigDecimal grossProfit = new BigDecimal(0);

		private String grossProfitRatio = "-"; // 78.09%

		public BigDecimal getReceivables() {
			return receivables;
		}

		public void setReceivables(BigDecimal receivables) {
			this.receivables = receivables;
		}

		public BigDecimal getCost() {
			return cost;
		}

		public void setCost(BigDecimal cost) {
			this.cost = cost;
		}

		public BigDecimal getGrossProfit() {
			return grossProfit;
		}

		public void setGrossProfit(BigDecimal grossProfit) {
			this.grossProfit = grossProfit;
		}

		public String getGrossProfitRatio() {
			if (receivables.compareTo(new BigDecimal(0)) > 0) {
				return String.format("%.2f", grossProfit.divide(receivables, 4, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100))) + "%";
			}
			return grossProfitRatio;
		}

		public void setGrossProfitRatio(String grossProfitRatio) {
			this.grossProfitRatio = grossProfitRatio;
		}

	}

}
