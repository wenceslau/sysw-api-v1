
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.BusinessUnitParameter;

public class BusinessUnitTO extends ModelCoreTO {

	private String name;

	private String description;

	private String uniqueId;

	private String license;

	private String valueLicense;

	private boolean hasDomainControl;

	private List<BusinessUnitParameter> businessUnitParameters;

	private List<ApplicationTO> applications;

	private String image;

	private BusinessUnitTO() {}

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

	public String getUniqueId() {

		return uniqueId;

	}

	public void setUniqueId(String uniqueId) {

		this.uniqueId = uniqueId;

	}

	public List<ApplicationTO> getApplications() {

		if (applications == null)
			applications = new ArrayList<>();
		return applications;

	}

	public void setApplications(List<ApplicationTO> applications) {

		this.applications = applications;

	}

	public String getLicense() {

		return license;

	}

	public void setLicense(String license) {

		this.license = license;

	}

	public boolean isHasDomainControl() {

		return hasDomainControl;

	}

	public void setHasDomainControl(boolean hasDomainControl) {

		this.hasDomainControl = hasDomainControl;

	}

	public List<BusinessUnitParameter> getBusinessUnitParameters() {

		if (businessUnitParameters == null)
			businessUnitParameters = new ArrayList<>();
		return businessUnitParameters;

	}

	public void setBusinessUnitParameters(List<BusinessUnitParameter> businessUnitParameters) {

		this.businessUnitParameters = businessUnitParameters;

	}

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public String getValueLicense() {

		return valueLicense;

	}

	public void setValueLicense(String valueLicense) {

		this.valueLicense = valueLicense;

	}

	public static BusinessUnitTO build(BusinessUnit source, String... ignoreProperties) {

		if (source == null)
			return null;

		BusinessUnitTO target = new BusinessUnitTO();
		copyProperties(source, target, "applications");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("applications"))
			target.getApplications().addAll(ApplicationTO.build(new ArrayList<>(source.getApplications()), ignoreProperties));

		System.out.println("BusinessUnitTO.build()" + source.getCode());
		return target;

	}

	public static List<BusinessUnitTO> build(List<BusinessUnit> sourceList, String... ignoreProperties) {

		List<BusinessUnitTO> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}