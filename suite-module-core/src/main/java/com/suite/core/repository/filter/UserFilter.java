package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o User
 * @author Wenceslau
 *
 */
public class UserFilter extends FilterCore {

	private String name;

	private String username;

	private String email;

	private Integer typeProfile;

	private Long codProfile;

	// Usado para filtro na list de setor do usuario
	private Long codSector;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getEmail() {

		return email;

	}

	public void setEmail(String email) {

		this.email = email;

	}

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public Integer getTypeProfile() {

		return typeProfile;

	}

	public void setTypeProfile(Integer typeProfile) {

		this.typeProfile = typeProfile;

	}

	public Long getCodProfile() {

		return codProfile;

	}

	public void setCodProfile(Long codProfile) {

		this.codProfile = codProfile;

	}

	public Long getCodSector() {

		return codSector;

	}

	public void setCodSector(Long codSector) {

		this.codSector = codSector;

	}

}
