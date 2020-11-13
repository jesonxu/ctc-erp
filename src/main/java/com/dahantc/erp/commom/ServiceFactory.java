package com.dahantc.erp.commom;

import org.springframework.context.ApplicationContext;

/**
 * 
 * 类描述：service工厂
 * 
 * @version: 1.0
 * @author: 8516
 * @date: 2016年2月24日 下午2:19:17
 */
public class ServiceFactory {

	private ApplicationContext appContext;
	private static ServiceFactory factory = new ServiceFactory();

	private ServiceFactory() {

	}

	public static ServiceFactory getFactory() {
		return factory;
	}

	public void init(ApplicationContext wc) {
		if (appContext == null) {
			appContext = wc;
		}
	}

	/**
	 * 获取接口服务
	 * 
	 * @param serviceName
	 * @return
	 */
	public Object getService(String serviceName) {
		if (appContext == null || !appContext.containsBean(serviceName)) {
			return null;
		}
		return appContext.getBean(serviceName);
	}

	public ApplicationContext getApplicationContext() {
		return appContext;
	}
}
