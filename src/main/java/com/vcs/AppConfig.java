package com.vcs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ComponentScan
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
//@EnableSwagger2
public class AppConfig {

	@Value("${spring.datasource.url}") String url;
	@Value("${spring.datasource.username}") String username;
	@Value("${spring.datasource.password}") String password;

	@Bean
	public DataSource getDataSource() { 
		DataSource dataSource = DataSourceBuilder 
				.create() 
				.username(username)
				.password(password) 
				.url(url) 
				.build();
		//.driverClassName(driverClassName) .build();
		
		return dataSource; 
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) { 
		return new JdbcTemplate(dataSource); 
	}
	
	@Bean
    public Docket docket() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }

	/*
	 * @Bean(name = "sessionFactory") public SessionFactory
	 * getSessionFactory(DataSource dataSource) { LocalSessionFactoryBuilder
	 * sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
	 * sessionBuilder.scanPackages("com.vflux.rbot.services.account.domain"); return
	 * sessionBuilder.buildSessionFactory(); }
	 * 
	 * @Bean(name = "transactionManager") public HibernateTransactionManager
	 * getTransactionManager( SessionFactory sessionFactory) {
	 * HibernateTransactionManager transactionManager = new
	 * HibernateTransactionManager( sessionFactory); return transactionManager; }
	 * 
	 * @Bean public DataSourceInitializer dataSourceInitializer(final DataSource
	 * dataSource) { final DataSourceInitializer initializer = new
	 * DataSourceInitializer(); initializer.setDataSource(dataSource); return
	 * initializer; }
	 */

}
