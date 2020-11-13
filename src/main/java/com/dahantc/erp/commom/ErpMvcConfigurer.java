package com.dahantc.erp.commom;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.dahantc.erp.commom.interceptor.LoginInterceptor;
import com.dahantc.erp.commom.listener.SessionListener;
import com.dahantc.erp.vo.role.service.IRoleService;

/**
 * @author 8515
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ErpMvcConfigurer implements WebMvcConfigurer {

	@Resource
	private IRoleService roleService;

	/**
	 * 不需要拦截的url:登录注册和验证码 以及静态资源
	 */
	final String[] notLoginInterceptPaths = { "/**/*.pdf", "/**/*.xls", "/**/*.csv", "/**/*.txt", "/**/*.xlsx", "/**/*.css", "/**/*.js", "/**/*.woff",
			"/**/*.ttf", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.properties", "/init/*.action", "/login/*.action", "/erp", "/**/views/faq/faq.html", "/**/common/help/ERP_HELP.pdf", "/**/common/help/test.html", "/**/views/userLeave/rule.html" };

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseRegisteredSuffixPatternMatch(true);
	}

	/**
	 * 添加静态资源文件，外部可以直接访问地址
	 *
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		// 本地 file:/D:/*
		// 服务器 file:/home/*
		String prePath = StringUtils.startsWith(Constants.RESOURCE, "/") ? "file:" : "file:/";
		String endPath = StringUtils.endsWith(Constants.RESOURCE, "/") ? "upFile/" : "/upFile/";
		registry.addResourceHandler("/upFile/**").addResourceLocations(prePath + Constants.RESOURCE + endPath);
	}

	/**
	 * 注册拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		LoginInterceptor loginInterceptor = new LoginInterceptor();
		loginInterceptor.setRoleService(roleService);
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(notLoginInterceptPaths);
	}

	// 注册session监听器;
	@Bean
	public ServletListenerRegistrationBean<SessionListener> servletListenerRegistrationBean() {
		ServletListenerRegistrationBean<SessionListener> slrBean = new ServletListenerRegistrationBean<SessionListener>();
		slrBean.setListener(new SessionListener());
		return slrBean;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		Locale.setDefault(Locale.CHINESE);
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("static/i18n/messages");// name of the resource
		source.setUseCodeAsDefaultMessage(true);
		source.setDefaultEncoding("UTF-8");
		return source;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		// 默认语言
		slr.setDefaultLocale(Locale.CHINESE);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		// 参数名
		lci.setParamName("lang");
		return lci;
	}

}
