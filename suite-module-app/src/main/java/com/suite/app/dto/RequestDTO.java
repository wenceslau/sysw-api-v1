package com.suite.app.dto;

import com.suite.app.base.Base;

public class RequestDTO extends Base {

	private Long userCode;
	private Long sectorCode;

	public RequestDTO() {
		super();
		this.userCode = getCodeUserContext();
		this.sectorCode = getCodeSectorContext();
	}

	public Long getUserCode() {
		return userCode;
	}

	public void setUserCode(Long userCode) {
		this.userCode = userCode;
	}

	public Long getSectorCode() {
		return sectorCode;
	}

	public void setSectorCode(Long sectorCode) {
		this.sectorCode = sectorCode;
	}

}
