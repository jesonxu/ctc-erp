package com.dahantc.erp.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dahantc.erp.CtcErpApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class BackTaskMonthSaleAnalysisStatisticsTaskTest {
	@Autowired
	private BackTaskMonthSaleAnalysisStatisticsTask backTaskMonthSaleAnalysisStatisticsTask;


	@Test
	public void testmonthSaleAnalysisStatistics() throws Exception {
//		backTaskMonthSaleAnalysisStatisticsTask.monthSaleAnalysisStatistics();
	}
}
