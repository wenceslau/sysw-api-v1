package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.Sector;

public class SectorResume {

	private Long code;
	private String name;
	private String label;
	private String nameExternalDatabase;

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

	public String getLabel() {

		return label;

	}

	public void setLabel(String label) {

		this.label = label;

	}

	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public String getNameExternalDatabase() {

		return nameExternalDatabase;

	}

	public void setNameExternalDatabase(String nameExternalDatabase) {

		this.nameExternalDatabase = nameExternalDatabase;

	}

	public static SectorResume build(Sector source) {

		if (source == null)
			return null;

		SectorResume target = new SectorResume();
		copyProperties(source, target);

		return target;

	}

	public static List<SectorResume> build(List<Sector> sourceList) {

		List<SectorResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}