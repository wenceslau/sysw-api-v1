package com.suite.core.dto;

import java.time.LocalDateTime;

import com.suite.core.model.resume.PermissionResume;

public class InfoModelDTO {

	private PermissionResume permission;
	private Long numRecords;
	private LocalDateTime lastUpdate;
	private String updatedBy;

	public PermissionResume getPermission() {

		return permission;

	}

	public void setPermission(PermissionResume permission) {

		this.permission = permission;

	}

	public Long getNumRecords() {

		return numRecords;

	}

	public void setNumRecords(Long numRecords) {

		this.numRecords = numRecords;

	}

	public LocalDateTime getLastUpdate() {

		return lastUpdate;

	}

	public void setLastUpdate(LocalDateTime lastUpdate) {

		this.lastUpdate = lastUpdate;

	}

	public String getUpdatedBy() {

		return updatedBy;

	}

	public void setUpdatedBy(String upateBy) {

		this.updatedBy = upateBy;

	}

}
