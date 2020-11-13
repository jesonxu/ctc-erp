package com.dahantc.erp.commom.listener;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.vo.user.entity.User;

/**
 * @author 8515
 */
public class SessionListener implements HttpSessionListener {

	public static final Map<String, HttpSession> SESSION_TABLE = new Hashtable<String, HttpSession>();

	private final static Logger logger = LogManager.getLogger(SessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		logger.info("sessionCreated：" + event.getSession().getId());
		SESSION_TABLE.put(event.getSession().getId(), event.getSession());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		User user = (User) event.getSession().getAttribute(Constants.SESSION_KEY);
		if (user != null) {
			logger.error("用户 " + user.getLoginName() + " 登录超时.");
		}
		user = null;
		SESSION_TABLE.remove(event.getSession().getId());
	}

}
