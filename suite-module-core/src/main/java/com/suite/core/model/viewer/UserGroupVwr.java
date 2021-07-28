
package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.resume.UserResume;
import com.suite.core.model.UserGroup;

public class UserGroupVwr extends ViewerCore {

	private String name;

	private String description;

	private BusinessUnitResume businessUnit;

	@Transient
	private List<UserResume> users;

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

	public BusinessUnitResume getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitResume businessUnit) {

		this.businessUnit = businessUnit;

	}

	public List<UserResume> getUsers() {

		if (users == null)
			users = new ArrayList<>();
		return users;

	}

	public void setUsers(List<UserResume> users) {

		this.users = users;

	}

	public static UserGroupVwr build(UserGroup source) {

		if (source == null)
			return null;

		UserGroupVwr target = new UserGroupVwr();
		copyProperties(source, target, new String[] { "businessUnit", "users" });

		target.setBusinessUnit(BusinessUnitResume.build(source.getBusinessUnit()));
		target.getUsers().addAll(UserResume.build(new ArrayList<>(source.getUsers())));

		target.getUsers().sort((p1, p2) -> p1.getUsername().compareTo(p2.getUsername()));

		return target;

	}

	public static List<UserGroupVwr> build(List<UserGroup> sourceList) {

		List<UserGroupVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		targetList.sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));

		return targetList;

	}

}