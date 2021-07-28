package com.suite.core.model.resume;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.EnumCore.Lang;
import com.suite.core.model.Language;

/**
 * @author Wenceslau
 *
 */
public class LanguageResume {

	private String key;

	private String value;

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getValue() {

		return value;

	}

	public void setValue(String value) {

		this.value = value;

	}

	public static LanguageResume build(Language language, Lang lang) {

		if (language == null)
			return null;

		LanguageResume to = new LanguageResume();
		to.key = language.getKey();

		if (lang.equals(Lang.PT))
			to.value = language.getPortugues();
		else if (lang.equals(Lang.EN))
			to.value = language.getEnglish();
		else if (lang.equals(Lang.ES))
			to.value = language.getSpanish();

		return to;

	}

	public static List<LanguageResume> buildResume(List<Language> list, Lang lang) {

		List<LanguageResume> listTO = new ArrayList<>();

		for (Language object : list)
			listTO.add(build(object, lang));

		return listTO;

	}

}