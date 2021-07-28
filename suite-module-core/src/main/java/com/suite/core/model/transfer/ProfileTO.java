
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.Profile;

public class ProfileTO extends ModelCoreTO {

	private String name;

	private Integer type;

	private Integer hash;

	private List<PermissionTO> permissions;

	private BusinessUnitTO businessUnit;

	private ProfileTO() {}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public List<PermissionTO> getPermissions() {

		if (permissions == null)
			permissions = new ArrayList<>();
		return permissions;

	}

	public void setPermissions(List<PermissionTO> permissions) {

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

	public BusinessUnitTO getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitTO businessUnit) {

		this.businessUnit = businessUnit;

	}

	public static ProfileTO build(Profile source, String... ignoreProperties) {

		if (source == null)
			return null;

		ProfileTO target = new ProfileTO();
		copyProperties(source, target, "businessUnit", "permissions");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("businessUnit"))
			target.setBusinessUnit(BusinessUnitTO.build(source.getBusinessUnit(), ignoreProperties));
		if (ignoreList == null || !ignoreList.contains("permissions"))
			target.getPermissions().addAll(PermissionTO.build(new ArrayList<>(source.getPermissions()), ignoreProperties));

		System.out.println("ProfileTO.build()" + source.getCode());
		return target;

	}

	public static List<ProfileTO> build(List<Profile> sourceList, String... ignoreProperties) {

		List<ProfileTO> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}
