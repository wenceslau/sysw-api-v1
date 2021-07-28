package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.Application;

public class ApplicationVwr extends ViewerCore {

	private String name;

	private String displayName;

	private String mainColor;

	private String image;

	private Boolean main = false;

	private LocalDateTime dateInitializer;

	private String nameModuleSource;

	private String licence;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getDisplayName() {

		return displayName;

	}

	public void setDisplayName(String displayName) {

		this.displayName = displayName;

	}

	public String getMainColor() {

		return mainColor;

	}

	public void setMainColor(String mainColor) {

		this.mainColor = mainColor;

	}

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public Boolean getMain() {

		return main;

	}

	public void setMain(Boolean main) {

		this.main = main;

	}

	public LocalDateTime getDateInitializer() {

		return dateInitializer;

	}

	public void setDateInitializer(LocalDateTime dateInitializer) {

		this.dateInitializer = dateInitializer;

	}

	public String getNameModuleSource() {

		return nameModuleSource;

	}

	public void setNameModuleSource(String nameModuleSource) {

		this.nameModuleSource = nameModuleSource;

	}

	public String getLicence() {

		return licence;

	}

	public void setLicence(String licence) {

		this.licence = licence;

	}

	public static ApplicationVwr build(Application source) {

		if (source == null)
			return null;

		ApplicationVwr target = new ApplicationVwr();
		copyProperties(source, target);

		return target;

	}

	public static List<ApplicationVwr> build(List<Application> sourceList) {

		List<ApplicationVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}