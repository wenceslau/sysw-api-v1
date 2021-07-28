
package com.suite.core.model.resume;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.BusinessUnit;

public class BusinessUnitResume {

	private Long code;

	private String name;

	private Boolean status;

	private boolean hasDomainControl;

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

	public boolean isHasDomainControl() {

		return hasDomainControl;

	}

	public void setHasDomainControl(boolean hasDomainControl) {

		this.hasDomainControl = hasDomainControl;

	}

	public static BusinessUnitResume build(BusinessUnit source) {

		if (source == null)
			return null;

		BusinessUnitResume target = new BusinessUnitResume();
		copyProperties(source, target);

		return target;

	}

	public static List<BusinessUnitResume> build(List<BusinessUnit> sourceList) {

		List<BusinessUnitResume> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}