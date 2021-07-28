
package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.StackError;

public class StackErrorVwr {

	private Long code;

	private String message;

	private String causes;

	private String stack;

	private LocalDateTime dateTimeError;

	private Long codeUser;

	private Long codeSector;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getMessage() {

		return message;

	}

	public void setMessage(String message) {

		this.message = message;

	}

	public String getCauses() {

		return causes;

	}

	public void setCauses(String causes) {

		this.causes = causes;

	}

	public String getStack() {

		return stack;

	}

	public void setStack(String stack) {

		this.stack = stack;

	}

	public LocalDateTime getDateTimeError() {

		return dateTimeError;

	}

	public void setDateTimeError(LocalDateTime dateTimeError) {

		this.dateTimeError = dateTimeError;

	}

	public Long getCodeUser() {

		return codeUser;

	}

	public void setCodeUser(Long codeUser) {

		this.codeUser = codeUser;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}

	public static StackErrorVwr build(StackError source) {

		if (source == null)
			return null;

		StackErrorVwr target = new StackErrorVwr();
		copyProperties(source, target, new String[] { "fullStack" });

		return target;

	}

	public static List<StackErrorVwr> build(List<StackError> sourceList) {

		List<StackErrorVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}
