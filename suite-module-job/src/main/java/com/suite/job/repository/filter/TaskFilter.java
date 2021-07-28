package com.suite.job.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * @author Wenceslau
 *
 */
public class TaskFilter extends FilterCore {

	private String name;

	private String description;

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

}