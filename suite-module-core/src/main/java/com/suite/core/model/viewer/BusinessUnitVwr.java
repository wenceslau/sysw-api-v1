package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.BusinessUnitParameter;
import com.suite.core.model.resume.ApplicationResume;
import com.suite.core.model.BusinessUnit;

/**
 * Classe que representa a entidade Unidade Negocio
 * @author Wenceslau
 *
 */
public class BusinessUnitVwr extends ViewerCore {

	private String name;

	private String description;

	private String uniqueId;

	private String valueLicense;

	private String image;

	private List<ApplicationResume> applications;

	private List<BusinessUnitParameter> businessUnitParameters;

	private boolean hasDomainControl;

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

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public void setValueLicense(String valueLicense) {

		this.valueLicense = valueLicense;

	}

	public String getValueLicense() {

		return valueLicense;

	}

	public List<ApplicationResume> getApplications() {

		return applications;

	}

	public void setApplications(List<ApplicationResume> applications) {

		this.applications = applications;

	}

	public List<BusinessUnitParameter> getBusinessUnitParameters() {

		return businessUnitParameters;

	}

	public void setBusinessUnitParameters(List<BusinessUnitParameter> businessUnitParameters) {

		this.businessUnitParameters = businessUnitParameters;

	}

	public boolean isHasDomainControl() {

		return hasDomainControl;

	}

	public void setHasDomainControl(boolean hasDomainControl) {

		this.hasDomainControl = hasDomainControl;

	}

	public static BusinessUnitVwr build(BusinessUnit source) {

		if (source == null)
			return null;

		BusinessUnitVwr target = new BusinessUnitVwr();
		copyProperties(source, target);

		return target;

	}

	public static List<BusinessUnitVwr> build(List<BusinessUnit> sourceList) {

		List<BusinessUnitVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}