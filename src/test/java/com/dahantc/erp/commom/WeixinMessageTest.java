package com.dahantc.erp.commom;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.dahantc.erp.CtcErpApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtcErpApplication.class)
public class WeixinMessageTest {
	@Autowired
	protected WebApplicationContext wac;
	@Autowired
	private Environment ev;

	@Before
	public void test() {
		WeixinMessage.initwxParam(ev);
	}

	@Test
	public void testrun() throws Exception {
//		String res  = message.sendMessageByMobile("13621876969", "您好");
		String res = WeixinMessage.getDepartmentList("");
		System.out.println(res);
		assertNotNull(res);
		assertNotNull(WeixinMessage.getUserList());
		
	}

}
