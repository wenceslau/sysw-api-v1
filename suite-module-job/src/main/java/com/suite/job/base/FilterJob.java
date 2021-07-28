package com.suite.job.base;

import com.suite.app.base.Filter;

public class FilterJob extends Filter {

	private Long code;
	private Boolean status;
	private Long codeBusinessUnit;

	public void setCode(Long code) {
		this.code = code;
	}

	@Override
	public Long getCode() {
		return code;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public Boolean getStatus() {
		return status;
	}

	public Long getCodeBusinessUnit() {
		return codeBusinessUnit;
	}

	public void setCodeBusinessUnit(Long codeBusinessUnit) {
		this.codeBusinessUnit = codeBusinessUnit;
	}

}
