package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.Application;

public class ApplicationResume {

	private Long code;
	private String name;
	private String nameModuleSource;

	private Boolean status;

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

	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public String getNameModuleSource() {

		return nameModuleSource;

	}

	public void setNameModuleSource(String nameModuleSource) {

		this.nameModuleSource = nameModuleSource;

	}

	public static ApplicationResume build(Application source) {

		if (source == null)
			return null;

		ApplicationResume target = new ApplicationResume();
		copyProperties(source, target);

		return target;

	}

	public static List<ApplicationResume> build(List<Application> sourceList) {

		List<ApplicationResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}