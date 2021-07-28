package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;
import com.suite.core.base.EnumCore.DataServiceType;

/**
 * Classe usada como objeto de filtro para o DataService
 * @author Wenceslau
 *
 */
public class DataServiceFilter extends FilterCore {

	private DataServiceType type;

	private String name;

	private String scope;

	private Long codBusinessUnit;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getScope() {

		return scope;

	}

	public void setScope(String scope) {

		this.scope = scope;

	}

	public DataServiceType getType() {

		return type;

	}

	public void setType(DataServiceType type) {

		this.type = type;

	}

	public Long getCodBusinessUnit() {

		return codBusinessUnit;

	}

	public void setCodBusinessUnit(Long codBusinessUnit) {

		this.codBusinessUnit = codBusinessUnit;

	}

}
