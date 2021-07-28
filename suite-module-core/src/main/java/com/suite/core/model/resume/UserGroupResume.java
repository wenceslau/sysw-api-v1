package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.UserGroup;

public class UserGroupResume {

	private Long code;

	private String name;

//	@Transient
//	private List<UserResume> users;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}
//
//	public List<UserResume> getUsers() {
//		if (users == null)
//			users = new ArrayList<>();
//		return users;
//
//	}
//
//	public void setUsers(List<UserResume> users) {
//
//		this.users = users;
//
//	}

	public static UserGroupResume build(UserGroup source) {

		if (source == null)
			return null;

		UserGroupResume target = new UserGroupResume();
		copyProperties(source, target, new String[] { "users"});
			
		//target.getUsers().addAll(UserResume.build(new ArrayList<>(source.getUsers())));		

		return target;

	}

	public static List<UserGroupResume> build(List<UserGroup> sourceList) {

		List<UserGroupResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}