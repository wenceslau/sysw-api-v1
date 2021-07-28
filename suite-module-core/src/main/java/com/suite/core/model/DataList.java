package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

@Entity
@Table(name = "tb_core_data_list")
public class DataList extends ModelCore {

	@NotNull
	@Column(name = "nam_list")
	private String name;

	@NotNull
	@Column(name = "des_list")
	private String description;

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

	public static DataList build(DataList source) {

		if (source == null)
			return null;

		DataList target = new DataList();
		copyProperties(source, target);

		return target;

	}

	public static List<DataList> build(List<DataList> sourceList) {

		List<DataList> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}
}
