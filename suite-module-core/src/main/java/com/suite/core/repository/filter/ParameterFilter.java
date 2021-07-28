package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o Parameter
 * @author Wenceslau
 *
 */
public class ParameterFilter extends FilterCore {

	private String group;

	private String key;

	public String getGroup() {

		return group;

	}

	public void setGroup(String group) {

		this.group = group;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

}
