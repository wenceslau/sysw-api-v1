package com.suite.core.dto;

public class InfoConnectionDTO {

	private String key;
	private String status;

	
	
	public InfoConnectionDTO() {

		super();

	}

	public InfoConnectionDTO(String key, String status) {

		super();
		this.key = key;
		this.status = status;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getStatus() {

		return status;

	}

	public void setStatus(String status) {

		this.status = status;

	}

}
