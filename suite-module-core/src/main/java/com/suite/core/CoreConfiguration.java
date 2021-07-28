package com.suite.core;

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
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe de configuracao entre o modulo e o repositorio de dados
 * Relaciona O entityManager com o pacote repository
 * @author Wenceslau
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "coreEntityManagerFactory", basePackages = { "com.suite.core.repository" })
public class CoreConfiguration {

	@Autowired
	private Environment env;

	/**
	 * Constroi o objeto datasource
	 * Referencia a configuracao do banco no properties
	 * @return
	 */
	@Primary
	@Bean(name = "coreDataSourceV4")
	@ConfigurationProperties(prefix = "core.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();

	}

	/**
	 * Prove o objeto EntityManager JPA para acesso ao repositorio
	 * Referencia o pacote que contem as entidades do modulo
	 * O nome coreEntityManagerFactory identifica o objeto
	 * @param builder
	 * @param dataSource
	 * @return
	 */
	@Primary
	@Bean(name = "coreEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("coreDataSourceV4") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean lcFactoryBean = builder.dataSource(dataSource).packages("com.suite.core.model")
				.persistenceUnit("model").build();
		Properties properties = new Properties();
		properties.put("hibernate.dialect", env.getProperty("core.datasource.hibernate.dialect"));
		lcFactoryBean.setJpaProperties(properties);
		return lcFactoryBean;

	}

	/**
	 * Prove o objeto Transaction responsavel pelas transacoes
	 * O Qualifier identifica para qual EntityManager a transacao deve ser aplicada
	 * @param factory
	 * @return
	 */
	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("coreEntityManagerFactory") EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);

	}

}