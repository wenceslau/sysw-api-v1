
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.UserGroup;

public class UserGroupTO extends ModelCoreTO {

	private String name;

	private String description;

	private BusinessUnitTO businessUnit;

	private List<UserTO> users;

	private UserGroupTO() {}

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

	public BusinessUnitTO getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitTO businessUnit) {

		this.businessUnit = businessUnit;

	}

	public List<UserTO> getUsers() {

		if (users == null)
			users = new ArrayList<>();
		return users;

	}

	public void setUsers(List<UserTO> users) {

		this.users = users;

	}

	public static UserGroupTO build(UserGroup source, String... ignoreProperties) {

		if (source == null)
			return null;

		UserGroupTO target = new UserGroupTO();
		copyProperties(source, target, "businessUnit", "users");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
		if (ignoreList == null || !ignoreList.contains("businessUnit"))
			target.setBusinessUnit(BusinessUnitTO.build(source.getBusinessUnit(), ignoreProperties));
		if (ignoreList == null || !ignoreList.contains("users"))
			target.getUsers().addAll(UserTO.build(new ArrayList<>(source.getUsers()), ignoreProperties));

		target.getUsers().sort((p1, p2) -> p1.getUsername().compareTo(p2.getUsername()));

		System.out.println("UserGroupTO.build()" + source.getCode());
		return target;

	}

	public static List<UserGroupTO> build(List<UserGroup> sourceList, String... ignoreProperties) {

		List<UserGroupTO> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		targetList.sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));

		return targetList;

	}

}