package com.suite.job;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "jobEntityManagerFactory", transactionManagerRef = "jobTransactionManager", basePackages = {
		"com.suite.job.repository" })
public class JobConfiguration {

	@Autowired
	private Environment env;

	@Bean(name = "jobDataSource")
	@ConfigurationProperties(prefix = "job.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "jobEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean jobEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("jobDataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean lcFactoryBean = builder.dataSource(dataSource)
				.packages("com.suite.job.model").persistenceUnit("model").build();

		Properties properties = new Properties();
		properties.put("hibernate.dialect", env.getProperty("job.datasource.hibernate.dialect"));
		lcFactoryBean.setJpaProperties(properties);

		return lcFactoryBean;
	}

	@Bean(name = "jobTransactionManager")
	public PlatformTransactionManager jobTransactionManager(
			@Qualifier("jobEntityManagerFactory") EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

}