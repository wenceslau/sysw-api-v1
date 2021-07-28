package com.suite.core.dto;

import java.util.List;

import com.suite.core.model.resume.LanguageResume;

public class InfoStartDTO {

	private List<LanguageResume> languages;
	private InfoSystemDTO infoSystem;

	public List<LanguageResume> getLanguages() {

		return languages;

	}

	public void setLanguages(List<LanguageResume> languages) {

		this.languages = languages;

	}

	public InfoSystemDTO getInfoSystem() {

		return infoSystem;

	}

	public void setInfoSystem(InfoSystemDTO infoSystem) {

		this.infoSystem = infoSystem;

	}

}
