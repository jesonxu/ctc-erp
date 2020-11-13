package com.dahantc.erp.vo.multipleStatistics;

import java.util.Date;
import java.util.List;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.vo.multipleStatistics.impl.ComGroupYearStatisticsServiceImpl.RowData;

public interface IComGroupYearStatisticsService {
    
    List<RowData> queryComYearStatistics(Date year, OnlineUser user);
}
