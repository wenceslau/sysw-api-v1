package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.resume.PermissionResume;
import com.suite.core.model.Profile;

public class ProfileVwr extends ViewerCore {

	private String name;

	private Integer type;

	private Integer hash;

	private List<PermissionResume> permissions;

	private BusinessUnitResume businessUnit;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public List<PermissionResume> getPermissions() {

		if (permissions == null)
			permissions = new ArrayList<>();
		return permissions;

	}

	public void setPermissions(List<PermissionResume> permissions) {

		this.permissions = permissions;

	}

	public Integer getType() {

		return type;

	}

	public void setType(Integer type) {

		this.type = type;

	}

	public Integer getHash() {

		return hash;

	}

	public void setHash(Integer hash) {

		this.hash = hash;

	}

	public BusinessUnitResume getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitResume businessUnit) {

		this.businessUnit = businessUnit;

	}

	public static ProfileVwr build(Profile source) {

		if (source == null)
			return null;

		ProfileVwr target = new ProfileVwr();
		copyProperties(source, target, "businessUnit", "permissions");
		target.setBusinessUnit(BusinessUnitResume.build(source.getBusinessUnit()));
		target.getPermissions().addAll(PermissionResume.build(new ArrayList<>(source.getPermissions())));

		return target;

	}

	public static List<ProfileVwr> build(List<Profile> sourceList) {

		List<ProfileVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));
		return targetList;

	}

}
