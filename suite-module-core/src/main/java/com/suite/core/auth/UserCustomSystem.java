package com.suite.core.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.suite.core.model.Sector;
import com.suite.core.model.User;

/**
 * Estende a classe User do supring que implementa o a classe UserDetails
 * Uma especializacao do objeto UserDetails para adicionar dados das entidades do repositorio da suite
 * 
 * @author Wenceslau Neto
 *
 */
public class UserCustomSystem extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = 1L;

	/*
	 * Entidade User do repositorio
	 */
	private User user;

	/*
	 * Entidade Sector do repositorio
	 */
	private Sector sector;

	/*
	 * Linguagem em que o usario logou;
	 */
	private String language;
	
	/*
	 * Linguagem em que o usario logou;
	 */
	private String device;

	/**
	 * Construtor que cria o objeto com as entidades e permissoes do repositorio
	 * @param user
	 * @param sector
	 * @param authorities
	 */
	public UserCustomSystem(User user, String pass, Sector sector, String language, String device, Collection<? extends GrantedAuthority> authorities) {

		super(user.getUsername(), pass, authorities);
		this.user = user;
		this.sector = sector;
		this.language = language;
		this.device = device;

	}

	public User getUser() {

		return user;

	}

	public Sector getSector() {

		return sector;

	}

	public String getLanguage() {

		return language;

	}

	
	public String getDevice() {
	
		return device;
	
	}
	
}
