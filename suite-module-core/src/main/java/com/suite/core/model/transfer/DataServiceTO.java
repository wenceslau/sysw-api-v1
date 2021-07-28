
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.DataService;
import com.suite.core.model.Property;

public class DataServiceTO extends ModelCoreTO {

	private DataServiceType type;

	private String identifier;

	private String scope;

	private String name;

	private String description;

	private BusinessUnitTO businessUnit;

	private List<Property> properties;

	private String codSectorsDataTask;
	
	private Long codSectorDataTaskParent;

	private DataServiceTO() {}

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

	public List<Property> getProperties() {

		return properties;

	}

	public void setProperties(List<Property> properties) {

		this.properties = properties;

	}

	public BusinessUnitTO getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitTO businessUnit) {

		this.businessUnit = businessUnit;

	}

	public String getScope() {

		return scope;

	}

	public void setScope(String scope) {

		this.scope = scope;

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

	@JsonIgnore
	public String getValueProperty(String nameProperty, String... valueDefault) {

		String name = getType() + "_" + nameProperty.toUpperCase();

		if (getProperties() == null)
			throw new RuntimeException("A lista de propriedades esta vazia");

		Property property = getProperties().stream().filter(x -> x.getName().equals(name))
				.findFirst().orElse(null);

		if (property == null && valueDefault.length != 0)
			return valueDefault[0];

		if (property == null)
			throw new RuntimeException("A propriedade [" + nameProperty + " ] não foi encontrada");

		return property.getValue();

	}

	public static DataServiceTO build(DataService source, String... ignoreProperties) {

		if (source == null)
			return null;

		DataServiceTO target = new DataServiceTO();
		copyProperties(source, target, "businessUnit");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("businessUnit"))
			target.setBusinessUnit(BusinessUnitTO.build(source.getBusinessUnit(), ignoreProperties));

		System.out.println("DataServiceTO.build()" + source.getCode());
		return target;

	}

	public static List<DataServiceTO> build(List<DataService> sourceList, String... ignoreProperties) {

		List<DataServiceTO> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}