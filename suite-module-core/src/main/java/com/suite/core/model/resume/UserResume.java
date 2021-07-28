package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.User;

public class UserResume {

	private Long code;

	private String name;

	private String username;

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

	public String getUsername() {
		return username;

	}

	public void setUsername(String username) {
		this.username = username;

	}

	public Boolean getStatus() {
		return status;

	}

	public void setStatus(Boolean status) {
		this.status = status;

	}

	public static UserResume build(User source) {
		if (source == null)
			return null;

		UserResume target = new UserResume();
		copyProperties(source, target);

		return target;

	}

	public static List<UserResume> build(List<User> sourceList) {
		List<UserResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserResume other = (UserResume) obj;

		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;

		return true;
	}

}
