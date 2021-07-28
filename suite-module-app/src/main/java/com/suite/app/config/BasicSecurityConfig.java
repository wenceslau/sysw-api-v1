package com.suite.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Classe usada para Profile Basic Authenticate
 * @author Wenceslau Neto
 *
 */
@Service
@Profile("basic-security")
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailService;

	/**
	 * Metodo chamado no start da aplicacao no servidor Web Adiciona o
	 * UserDetailsService para ser usado pelas autenticação
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		System.out.println("BasicSecurityConfig.configure(AuthenticationManagerBuilder)");

		// Adiciona o UserDetailServer ao Objects AuthenticationManager,
		// O Spring ira usar o UserAccountDatailService que foi extendida de
		// UserDetailsService
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());

	}

	/**
	 * Metodo é chamado apos o configure(ResourceServerSecurityConfigurer) Define as
	 * propriedades do HttpSecurity, que faz a autenticacao para a autenticacao
	 * basica
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		System.out.println("BasicSecurityConfig.configure(HttpSecurity)");

		http.authorizeRequests().anyRequest().authenticated() // Qualquer requsisicao tem q autenticar
				// .permitAll() //permite qualquer request
				.and().httpBasic() // Autenticacao basica
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nao manten state
				.and().csrf().disable();

	}

	/**
	 * Cria instancia BCryptPasswordEncoder para ser usado pelo UserDetailService
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		System.out.println("BasicSecurityConfig.passwordEncoder()");
		return new BCryptPasswordEncoder();

	}

}
