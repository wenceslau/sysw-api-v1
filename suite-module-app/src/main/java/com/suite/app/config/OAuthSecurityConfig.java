package com.suite.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Classe para injecao do AuthenticationManager para uso do spring
 * @author Wenceslau
 *
 */
@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAuthorizationServer
@EnableResourceServer
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Metodo overrride de WebSecurityConfigurerAdapter para prover o
	 * AuthenticationManager usando no Oauth2
	 */
	@Bean // notacao que faz o metodo ser provido automaticamente, o objeto
			// AuthenticationManager pode ser injetado em qq ponto da app
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {

		return super.authenticationManager();

	}

	/**
	 * Retorna um instancia de PasswordEncoder para ser usado pelo spring security
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();

	}

	/**
	 * Configura parametros de seguranca web
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {

		System.out.println("OAuthSecurityConfig.configure()");
		web.ignoring().antMatchers("/socket/**"); // Ignira path com o nome socket

	}

}
