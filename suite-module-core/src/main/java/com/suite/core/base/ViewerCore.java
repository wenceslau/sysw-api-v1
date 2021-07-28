package com.suite.core.base;

import com.suite.app.base.Model;

public class ViewerCore extends Model {

	private Long code;

	private Boolean status;

	@Override
	public void setCode(Long code) {

		this.code = code;

	}

	@Override
	public Long getCode() {

		// TODO Auto-generated method stub
		return code;

	}

	@Override
	public Boolean getStatus() {

		// TODO Auto-generated method stub
		return status;

	}

	@Override
	public void setStatus(Boolean status) {

		this.status = status;

	}

}
