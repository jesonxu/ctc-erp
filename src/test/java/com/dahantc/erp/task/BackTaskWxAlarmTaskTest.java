package com.dahantc.erp.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dahantc.erp.CtcErpApplication;
import com.dahantc.erp.commom.WeixinMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class BackTaskWxAlarmTaskTest {
	@Autowired
	private BackTaskWxAlarmTask backTaskWxAlarmTask;

	@Before
	public void test() {
		WeixinMessage.isTestModle = true;
		backTaskWxAlarmTask.initwxParam();
	}

	@Test
	public void testDoFlowAlarm() throws Exception {
		backTaskWxAlarmTask.doFlowAlarm();
	}

	@Test
	public void testSyncWxUserAndOrg() throws Exception {
		backTaskWxAlarmTask.syncWxUserAndOrg();
	}

}
