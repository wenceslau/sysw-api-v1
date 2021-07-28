package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.Profile;

public class ProfileResume {

	private Long code;
	private String name;
	private Integer type;
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

	public Integer getType() {

		return type;

	}

	public void setType(Integer type) {

		this.type = type;

	}

	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public static ProfileResume build(Profile source) {

		if (source == null)
			return null;

		ProfileResume target = new ProfileResume();
		copyProperties(source, target);

		return target;

	}

	public static List<ProfileResume> build(List<Profile> sourceList) {

		List<ProfileResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}