package com.dahantc.erp.commom.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.vo.role.service.IRoleService;

/**
 * 
 * @Description: 登录的拦截器
 * @author 8515
 * @date 2019年3月5日
 * @version V1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

	public static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	private IRoleService roleService;

	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		String vistUri = request.getRequestURI();
		if ((vistUri.startsWith("/receiveDataAnalysis") || vistUri.startsWith("/receiveFlowDataAnalysis"))
				&& (StringUtils.isBlank(Constants.DATA_ANALYSIS_CALL_BACK_IP) || StringUtils.equals(Constants.DATA_ANALYSIS_CALL_BACK_IP, getIp(request)))) {
			return true;
		} else if (vistUri.endsWith("/faq")) {
			return true;
		} else if (!vistUri.endsWith(".html")) {
			// 登录用户
			Object user = request.getSession().getAttribute(Constants.SESSION_KEY);
			// 当前角色id
			String roleId = (String) request.getSession().getAttribute(Constants.ROLEID_KEY);
			if (user != null) {
				if (!roleService.checkRoleMenuUrl(roleId, vistUri)) {
					response.sendRedirect(request.getContextPath() + "/erp");
				}
				return true;
			} else {
				// 为AJAX提交
				if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
					// session 超时
					response.setStatus(600);
				} else {
					// 没有登录进入登录页面
					response.sendRedirect(request.getContextPath() + "/erp");
				}
			}
		} else {
			// 不拦截主题
			if (vistUri.contains("system/theme")) {
				return true;
			}
			response.sendRedirect(request.getContextPath() + "/erp");
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView)
			throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

	}

	protected String getIp(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		return ip;
	}
}
