package com.dahantc.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * 
 * @Description: 启动类
 * @author 8515
 * @date 2019年3月18日
 * @version V1.0
 */
@EnableTransactionManagement
@EnableScheduling
@ServletComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = { "com.dahantc.erp" })
public class CtcErpApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext run = SpringApplication.run(CtcErpApplication.class, args);
		try{
			String host = InetAddress.getLocalHost().getHostAddress();
			TomcatServletWebServerFactory tomcatServletWebServerFactory= (TomcatServletWebServerFactory) run.getBean("tomcatServletWebServerFactory");
			int port = tomcatServletWebServerFactory.getPort();
			String contextPath = tomcatServletWebServerFactory.getContextPath();
			System.out.println("http://"+host+":"+port+contextPath+"/");
		}catch (Exception e){

		}
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.setThreadNamePrefix("BackTask-");
		return taskScheduler;
	}
}
