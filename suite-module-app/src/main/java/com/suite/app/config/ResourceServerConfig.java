package com.suite.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Classe usada para Profile Oauth
 * @author Wenceslau Neto
 *
 */
@Profile("oauth-security")
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * Metodo chamado apos o AutorizationServerConfig.configure(ClientDetailsServiceConfigurer)
	 * Adiciona true para stateless, mantem sem estado
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

		System.out.println("ResourceServerConfig.configure(ResourceServerSecurityConfigurer)");

		resources.stateless(true);

	}

	/**
	 * Metodo é chamado apos o ResourceServerConfig.configure(ResourceServerSecurityConfigurer)
	 * Define as propriedades do HttpSecurity, que faz a autenticacao para oauth2
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {

		System.out.println("ResourceServerConfig.configure(HttpSecurity)");

		http.authorizeRequests()
				.antMatchers("/core").permitAll()	
				.antMatchers("/simc").permitAll()				
				.antMatchers("/info/**").permitAll()
				//.antMatchers("/businessUnit/sysmonkey**").permitAll()
				//.antMatchers("/objetoValue/**").permitAll()
				//.antMatchers("/sector/byUsername").permitAll()
				//.antMatchers("/user/resetByEmail").permitAll()				
				//.antMatchers("/parameter/byKey/**").permitAll()
				//.antMatchers("/language/listByValue").permitAll()
				.antMatchers("/socket/*").permitAll()
				//.antMatchers("/swagger/**").permitAll()
				//.antMatchers("/info/system/**").permitAll()
				//.antMatchers("/info/license/**").permitAll()
				//.antMatchers("/template.html").permitAll()				
				//.antMatchers("/template").permitAll()				
				.anyRequest()
				.authenticated() // Qualquer requsisicao tem q autenticar
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // mesma funcao do metodo
																		// configure(ResourceServerSecurityConfigurer resources)
				.and()
				.csrf()
				.disable();

	}

	/**
	 * Metodo é chamado após o configure(AuthenticationManagerBuilder)
	 * Para autenticaçao com OAuth2
	 * @return
	 */
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {

		System.out.println("ResourceServerConfig.createExpressionHandler()");

		return new OAuth2MethodSecurityExpressionHandler();

	}
	

}
