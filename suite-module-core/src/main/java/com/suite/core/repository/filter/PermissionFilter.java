package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para Permission
 * @author Wenceslau
 *
 */
public class PermissionFilter extends FilterCore {

	private String application;

	private String strApplication;

	private String role;

	private String description;

	private String module;

	private String component;

	private String root;

	public String getApplication() {

		return application;

	}

	public void setApplication(String application) {

		this.application = application;

	}

	public String getStrApplication() {

		return strApplication;

	}

	public void setStrApplication(String strApplication) {

		this.strApplication = strApplication;

	}

	public String getRole() {

		return role;

	}

	public void setRole(String role) {

		this.role = role;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public String getModule() {

		return module;

	}

	public void setModule(String module) {

		this.module = module;

	}

	public String getComponent() {

		return component;

	}

	public void setComponent(String component) {

		this.component = component;

	}

	public String getRoot() {

		return root;

	}

	public void setRoot(String root) {

		this.root = root;

	}

}
