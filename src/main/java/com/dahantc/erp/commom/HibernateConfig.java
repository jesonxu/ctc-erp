package com.dahantc.erp.commom;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 
 * @Description: hibernate的配置类
 * @author 8515
 * @date 2019年3月18日
 * @version V1.0
 */
@Configuration
public class HibernateConfig {
	@Autowired
	private JpaProperties jpaProperties;
	@Autowired
	private DataSource dataSource;

	@Bean(name = "entityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan(new String[] { "com.dahantc.erp.vo.*.entity", "com.dahantc.erp.vo.*.service", "com.dahantc.erp.vo.*.dao" });
		factory.setDataSource(dataSource);// 数据源
		factory.setJpaPropertyMap(jpaProperties.getProperties());
		factory.afterPropertiesSet();// 在完成了其它所有相关的配置加载以及属性设置后,才初始化
		return factory;
	}

	@Bean
	public SessionFactory sessionFactory(EntityManagerFactory emf) {
		SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
		return sessionFactory;
	}

	@Bean
	public PlatformTransactionManager txManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getNativeEntityManagerFactory());
		return transactionManager;
	}
}
