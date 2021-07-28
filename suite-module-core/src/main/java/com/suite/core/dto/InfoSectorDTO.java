package com.suite.core.dto;

import com.suite.app.base.ModelDTO;

public class InfoSectorDTO extends ModelDTO {

	private Long code;
	private String name;
	private String bussinesName;
	private String appsName;
	private boolean buHasDc;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getBussinesName() {

		return bussinesName;

	}

	public void setBussinesName(String bussinesName) {

		this.bussinesName = bussinesName;

	}

	public String getAppsName() {

		return appsName;

	}

	public void setAppsName(String appsName) {

		this.appsName = appsName;

	}

	public boolean isBuHasDc() {

		return buHasDc;

	}

	public void setBuHasDc(boolean buHasDc) {

		this.buHasDc = buHasDc;

	}

}
