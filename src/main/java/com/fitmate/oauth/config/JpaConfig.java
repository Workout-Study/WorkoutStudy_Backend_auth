package com.fitmate.oauth.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

	// @Bean
	// public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
	// 	LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
	// 	factoryBean.setDataSource(dataSource);
	// 	factoryBean.setPackagesToScan("com.fitmate.oauth.jpa.entity"); // 엔티티 클래스 패키지
	// 	factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
	// 	return factoryBean;
	// }

	@Bean
	@Primary
	public EntityManagerFactory entityManagerFactoryBean(LocalContainerEntityManagerFactoryBean factoryBean) {
		return factoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
