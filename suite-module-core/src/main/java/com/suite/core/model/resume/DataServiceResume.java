package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.DataService;

public class DataServiceResume {

	private Long code;
	private String name;
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
	
	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public static DataServiceResume build(DataService source) {

		if (source == null)
			return null;

		DataServiceResume target = new DataServiceResume();		
		copyProperties(source, target);

		return target;

	}

	public static List<DataServiceResume> build(List<DataService> sourceList) {

		List<DataServiceResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}