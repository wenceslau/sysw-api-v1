package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o Profile
 * @author Wenceslau
 *
 */
public class ProfileFilter extends FilterCore {

	private String name;

	private String codPermissions;

	private Long codBusinessUnitProfile;

	private Long type;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getCodPermissions() {

		return codPermissions;

	}

	public void setCodPermissions(String codPermissions) {

		this.codPermissions = codPermissions;

	}

	public Long getCodBusinessUnitProfile() {

		return codBusinessUnitProfile;

	}

	public void setCodBusinessUnitProfile(Long codBusinessUnitProfile) {

		this.codBusinessUnitProfile = codBusinessUnitProfile;

	}

	public Long getType() {

		return type;

	}

	public void setType(Long type) {

		this.type = type;

	}

}
