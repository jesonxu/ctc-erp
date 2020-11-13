package com.dahantc.erp.task;

import com.dahantc.erp.CtcErpApplication;
import com.dahantc.erp.commom.WeixinMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class BackTaskCustomerChangeTaskTest {

    @Autowired
    private BackTaskCustomerChangeTask BackTaskCustomerChangeTask;

    @Autowired
    private Environment ev;

    @Before
    public void test() {
//        WeixinMessage.isTestModle = true;
//        WeixinMessage.initwxParam(ev);
    }

    @Test
    public void testmonthSaleAnalysisStatistics() throws Exception {
//        BackTaskCustomerChangeTask.analysisCustomer();
    }
}
