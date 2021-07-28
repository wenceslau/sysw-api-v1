package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;
import com.suite.core.model.UserGroup;

@Entity
@Table(name = "tb_core_user_group")
public class UserGroup extends ModelCore {

	@NotNull
	@Column(name = "nam_user_group")
	private String name;

	@NotNull
	@Column(name = "des_user_group")
	private String description;

	@ManyToOne
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_core_user_group_user", joinColumns = @JoinColumn(name = "cod_user_group"), inverseJoinColumns = @JoinColumn(name = "cod_user"))
	private List<User> users;

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

	public BusinessUnit getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {

		this.businessUnit = businessUnit;

	}

	public List<User> getUsers() {
		//if (users == null)
			//users = new ArrayList<>();
		return users;

	}

	public void setUsers(List<User> users) {

		this.users = users;

	}

	public static UserGroup build(UserGroup source) {

		if (source == null)
			return null;

		UserGroup target = new UserGroup();
		//copyProperties(source, target);
		copyProperties(source, target, "businessUnit", "users");
		target.setBusinessUnit(BusinessUnit.build(source.getBusinessUnit()));
		source.getUsers().stream().forEach((object -> {
			target.getUsers().add(User.build(object));

		}));

		return target;

	}

	public static List<UserGroup> build(List<UserGroup> sourceList) {

		List<UserGroup> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

	
}