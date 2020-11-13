package com.dahantc.erp.commom;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.jodconverter.DocumentConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.CtcErpApplication;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.accountbalance.entity.AccountBalance;
import com.dahantc.erp.vo.accountbalance.service.IAccountBalanceService;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;
import com.dahantc.erp.vo.balanceinterest.service.IBalanceInterestService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class ToPDFTest {
	
	@Autowired
	private IAccountBalanceService accountBalanceService;

	@Autowired
	private IBalanceInterestService balanceInterestService;

	@Autowired
	private DocumentConverter converter;

	@Test
	public void test() throws Exception {
		getStartDate();
	}
	
	private Date getStartDate() {
		try {

			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<BalanceInterest> list = balanceInterestService.findByFilter(1, 0, searchFilter);
			if (!CollectionUtils.isEmpty(list)) {
				return (Date) list.get(0).getWtime();
			}
			
			searchFilter.getOrders().clear();
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<AccountBalance> lis2 = accountBalanceService.findByFilter(1, 0, searchFilter);
			if (!CollectionUtils.isEmpty(lis2)) {
				return DateUtil.getNextDayStart((Date) lis2.get(0).getWtime());
			}
			
		} catch (ServiceException e) {
			
		}
		return null;
	}

}
