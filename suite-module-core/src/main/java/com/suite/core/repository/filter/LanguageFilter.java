package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o Parameter
 * @author Wenceslau
 *
 */
public class LanguageFilter extends FilterCore {

	private String key;

	private String portugues;

	private String english;

	private String spanish;

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getPortugues() {

		return portugues;

	}

	public void setPortugues(String portugues) {

		this.portugues = portugues;

	}

	public String getEnglish() {

		return english;

	}

	public void setEnglish(String english) {

		this.english = english;

	}

	public String getSpanish() {

		return spanish;

	}

	public void setSpanish(String spanish) {

		this.spanish = spanish;

	}

}
