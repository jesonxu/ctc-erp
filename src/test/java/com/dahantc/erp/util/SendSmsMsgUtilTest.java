package com.dahantc.erp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dahantc.erp.CtcErpApplication;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.enums.ProductType;
import com.dahantc.erp.vo.base.IBaseDao;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class SendSmsMsgUtilTest {
	@Autowired
	private IBaseDao baseDao;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		try {
			Map<String, Object> params = new HashMap<>();
			String hql = "select channelId,bcustId,sum(successCount),productType from CustomerProductTj where productType = " + ProductType.SMS.ordinal()
					+ " and statsDate >= :startDate and statsDate < :endDate  group by channelId,productType,bcustId";
			params.put("startDate", getYesterdayStartDateTime());
			params.put("endDate", DateUtil.getCurrentStartDateTime());
		

			List<Object[]> smsCountList = baseDao.findByhql(hql, params, 0);
			System.out.println("查询统计：" + smsCountList.size());
		} catch (BaseException e) {
			e.printStackTrace();
		}
	}

	public static Date getYesterdayStartDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

}
