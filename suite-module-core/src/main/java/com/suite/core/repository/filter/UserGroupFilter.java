package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

public class UserGroupFilter extends FilterCore {

	private String name;

	private String description;
	
	private String username;


	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	
	public String getUsername() {
	
		return username;
	
	}

	
	public void setUsername(String username) {
	
		this.username = username;
	
	}
	
	

}
