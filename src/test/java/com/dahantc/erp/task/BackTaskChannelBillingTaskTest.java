package com.dahantc.erp.task;

import com.dahantc.erp.CtcErpApplication;
import com.dahantc.erp.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class BackTaskChannelBillingTaskTest {

    @Autowired
    private BackTaskChannelBillingTask channelBillingTask;

    @Test
    public void tt(){
//       channelBillingTask.forTest();
    }

}
