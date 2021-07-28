package com.suite.core.dto;

import java.time.LocalDateTime;

public class FileLogDTO {

	private String path;
	private String name;
	private LocalDateTime lastUpdate;

	public FileLogDTO() {

		super();

	}

	public FileLogDTO(String path, String name, LocalDateTime lastUpdate) {

		super();
		this.path = path;
		this.name = name;
		this.lastUpdate = lastUpdate;

	}

	public String getPath() {

		return path;

	}

	public void setPath(String path) {

		this.path = path;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public LocalDateTime getLastUpdate() {

		return lastUpdate;

	}

	public void setLastUpdate(LocalDateTime lastUpdate) {

		this.lastUpdate = lastUpdate;

	}

}
