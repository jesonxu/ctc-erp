package com.dahantc.erp.controller;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.commom.listener.SessionListener;
import com.dahantc.erp.vo.operationlog.entity.OperationLog;
import com.dahantc.erp.vo.operationlog.service.IOperationLogService;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;

/**
 * 
 * @Description: 基础控制器（所有action需要继承）
 * @author: 8519
 * @date: 2019年6月19日 上午10:17:00
 */
@Controller
public abstract class BaseAction {

	private static final Logger logger = LogManager.getLogger(BaseAction.class);

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected HttpServletResponse response;

	@Autowired
	protected IParameterService parameterService;

	@Autowired
	protected IOperationLogService operationLogService;

	@Autowired
	protected IRoleService roleService;

	// session的key（国际化）
	protected final String LANGUAGE = "lang";

	// 英文
	protected final String EN = "en_US";

	// 中文
	protected final String CH = "zh_CN";

	// 判断是否是销售
	public boolean isSale() {
		try {
			return StringUtils.equals(roleService.read(getOnlineUserAndOnther().getRoleId()).getRolename(), "销售");
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return false;
	}

	protected OnlineUser getOnlineUserAndOnther() {
		OnlineUser onlineUser = null;
		HttpSession httpSession = request.getSession();
		String jsessionid = request.getParameter("JSESSIONID");
		if (httpSession == null) {
			if (jsessionid != null) {
				httpSession = SessionListener.SESSION_TABLE.get(jsessionid);
			}
		}
		String roleId = null;
		User user = null;
		if (httpSession != null) {
			user = (User) httpSession.getAttribute(Constants.SESSION_KEY);
			roleId = (String) httpSession.getAttribute(Constants.ROLEID_KEY);
		}
		if (user == null && jsessionid != null) {
			user = (User) SessionListener.SESSION_TABLE.get(jsessionid).getAttribute(Constants.SESSION_KEY);
			roleId = (String) SessionListener.SESSION_TABLE.get(jsessionid).getAttribute(Constants.ROLEID_KEY);
		}
		if (user != null) {
			onlineUser = new OnlineUser();
			onlineUser.setUser(user);
			onlineUser.setRoleId(roleId);
		}
		return onlineUser;
	}

	/**
	 * 获取当前活跃用户信息
	 */
	protected User getOnlineUser() {
		User user = null;
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser != null) {
			user = onlineUser.getUser();
		}
		return user;
	}

	/**
	 * 获取当前请求的IP
	 *
	 * @return
	 */
	protected String getIp() {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 清理当前用户的相关session信息
	 */
	protected void cleanSession() {
		request.getSession().removeAttribute(Constants.SESSION_KEY);
	}

	protected boolean saveLog(String actionName, String entityClass, String logMsg, int logType) {
		boolean result = false;
		String userId = null;
		String deptId = null;
		User user = getOnlineUser();
		if (user != null) {
			userId = user.getOssUserId();
			deptId = user.getDeptId();
		}
		OperationLog log = new OperationLog(actionName, entityClass, logMsg, logType, userId, deptId, getIp(), new Timestamp(System.currentTimeMillis()));
		try {
			result = operationLogService.save(log);
		} catch (Exception e) {
			logger.error("保存系统日志异常，日志：" + JSON.toJSONString(log, SerializerFeature.WriteDateUseDateFormat), e);
		}
		return result;
	}

	/**
	 * * @desc 获取平台语言
	 *
	 * @author 8515
	 * @date 2019年3月7日 下午2:42:45
	 */
	protected String getLanguage() {
		String language = null;
		language = request.getParameter(LANGUAGE);
		if (StringUtils.isNotBlank(language)) {
			HttpSession session = request.getSession();
			session.setAttribute(LANGUAGE, language);
		} else {
			HttpSession session = request.getSession();
			if (session.getAttribute(LANGUAGE) != null) {
				language = (String) session.getAttribute(LANGUAGE);
			} else {
				language = CH;
				logger.info("session沒有默认语言使用简体中文zh_CN");
			}
		}
		return language;
	}
}
