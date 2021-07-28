package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.resume.ApplicationResume;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.resume.DataServiceResume;
import com.suite.core.model.Sector;

public class SectorVwr extends ViewerCore {

	private String name;

	private String description;

	private String uniqueId;

	private String image;

	private Boolean requiredDb = false;

	private ApplicationResume application;

	private DataServiceResume dataService;

	private String nameExternalDatabase;

	private LocalDateTime dateCreateDatabase;

	private BusinessUnitResume businessUnit;

	private String label;

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

	public Boolean getRequiredDb() {

		return requiredDb;

	}

	public void setRequiredDb(Boolean requiredDb) {

		this.requiredDb = requiredDb;

	}

	public ApplicationResume getApplication() {

		return application;

	}

	public void setApplication(ApplicationResume application) {

		this.application = application;

	}

	public DataServiceResume getDataService() {

		return dataService;

	}

	public void setDataService(DataServiceResume dataService) {

		this.dataService = dataService;

	}

	public String getNameExternalDatabase() {

		return nameExternalDatabase;

	}

	public void setNameExternalDatabase(String nameExternalDatabase) {

		this.nameExternalDatabase = nameExternalDatabase;

	}

	public LocalDateTime getDateCreateDatabase() {

		return dateCreateDatabase;

	}

	public void setDateCreateDatabase(LocalDateTime dateCreateDatabase) {

		this.dateCreateDatabase = dateCreateDatabase;

	}

	public BusinessUnitResume getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitResume businessUnit) {

		this.businessUnit = businessUnit;

	}

	public String getLabel() {

		return label;

	}

	public void setLabel(String label) {

		this.label = label;

	}

	public static SectorVwr build(Sector source) {

		if (source == null)
			return null;

		SectorVwr target = new SectorVwr();
		copyProperties(source, target, "businessUnit", "application", "dataService");
		target.setBusinessUnit(BusinessUnitResume.build(source.getBusinessUnit()));
		target.setApplication(ApplicationResume.build(source.getApplication()));
		target.setDataService(DataServiceResume.build(source.getDataService()));

		return target;

	}

	public static List<SectorVwr> build(List<Sector> sourceList) {

		List<SectorVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}
