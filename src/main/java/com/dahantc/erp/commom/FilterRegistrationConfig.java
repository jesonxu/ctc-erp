package com.dahantc.erp.commom;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dahantc.erp.commom.interceptor.XssFilter;


@Configuration
public class FilterRegistrationConfig {

	@Bean
	public FilterRegistrationBean<Filter> xssFilterBean() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
		registration.setFilter(xssFilter());
		registration.addUrlPatterns("/*");
		registration.setName("xssFilter");
		registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
		return registration;
	}

	/**
	 * 创建一个bean
	 * 
	 * @return
	 */
	@Bean(name = "xssFilter")
	public Filter xssFilter() {
		return new XssFilter();
	}

}
