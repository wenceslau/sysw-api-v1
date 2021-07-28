package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.base.ViewerCore;
import com.suite.core.model.Property;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.DataService;

public class DataServiceVwr extends ViewerCore {

	private DataServiceType type;

	private String identifier;

	private String scope;

	private String name;

	private String description;

	private BusinessUnitResume businessUnit;

	@JsonIgnoreProperties("dataService")
	private List<Property> properties;

	private String codSectorsDataTask;

	private Long codSectorDataTaskParent;

	public DataServiceType getType() {
		return type;

	}

	public void setType(DataServiceType type) {
		this.type = type;

	}

	public String getIdentifier() {
		return identifier;

	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

	public String getScope() {
		return scope;

	}

	public void setScope(String scope) {
		this.scope = scope;

	}

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

	public List<Property> getProperties() {
		return properties;

	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;

	}

	public String getCodSectorsDataTask() {
		return codSectorsDataTask;

	}

	public void setCodSectorsDataTask(String codSectorsDataTask) {
		this.codSectorsDataTask = codSectorsDataTask;

	}

	public Long getCodSectorDataTaskParent() {
		return codSectorDataTaskParent;
	}

	public void setCodSectorDataTaskParent(Long codSectorDataTaskParent) {
		this.codSectorDataTaskParent = codSectorDataTaskParent;
	}

	public static DataServiceVwr build(DataService source) {
		if (source == null)
			return null;

		DataServiceVwr target = new DataServiceVwr();
		copyProperties(source, target, new String[] { "businessUnit" });
		target.setBusinessUnit(BusinessUnitResume.build(source.getBusinessUnit()));

		return target;

	}

	public static List<DataServiceVwr> build(List<DataService> sourceList) {
		List<DataServiceVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}