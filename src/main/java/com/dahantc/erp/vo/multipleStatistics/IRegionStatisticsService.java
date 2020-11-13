package com.dahantc.erp.vo.multipleStatistics;

import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.vo.multipleStatistics.impl.RegionStatisticsServiceImpl;

public interface IRegionStatisticsService {

    Map<String, Map<String, RegionStatisticsServiceImpl.RegionStatisticsData[]>> queryRegionStatistics(OnlineUser user);

}
