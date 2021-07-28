package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.Permission;

public class PermissionResume {

	private Long code;
	private String display;
	private Boolean status;
	private String icon;
	private String key;
	private ApplicationResume application;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getDisplay() {

		return display;

	}

	public void setDisplay(String display) {

		this.display = display;

	}

	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public String getIcon() {

		return icon;

	}

	public void setIcon(String icon) {

		this.icon = icon;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public ApplicationResume getApplication() {

		return application;

	}

	public void setApplication(ApplicationResume application) {

		this.application = application;

	}

	public static PermissionResume build(Permission source) {

		if (source == null)
			return null;

		PermissionResume target = new PermissionResume();
		copyProperties(source, target, "application");
		target.setApplication(ApplicationResume.build(source.getApplication()));

		return target;

	}

	public static List<PermissionResume> build(List<Permission> sourceList) {

		List<PermissionResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}