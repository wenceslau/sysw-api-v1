package com.suite.core.base;

import com.suite.app.base.Filter;

/**
 * Classe base para filtros das entidades do Core
 * estende a classe filter
 * @author Wenceslau
 *
 */
public class FilterCore extends Filter {

	/*
	 * PK do objeto a filtrar
	 */
	private Long code;

	/*
	 * Status do objeto a filtrar
	 */
	private Boolean status;

	/*
	 * Codigo da unidade de negocio do objeto a filtrar
	 */
	private Long codBusinessUnit;

	/*
	 * Metodo abstrato sobrescrito
	 */
	@Override
	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	/*
	 * Metodo abstrato sobrescrito
	 */
	@Override
	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public Long getCodBusinessUnit() {

		return codBusinessUnit;

	}

	public void setCodBusinessUnit(Long codBusinessUnit) {

		this.codBusinessUnit = codBusinessUnit;

	}

}
