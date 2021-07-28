package com.suite.core.repository.filter;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o StackError
 * @author Wenceslau
 *
 */
public class StackErrorFillter extends FilterCore {

	private String message;

	private String causes;

	private String stack;

	private String fullStack;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime dateTimeError;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime startDateTimeError;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime endDateTimeError;

	private Long codUser;

	private Long codeSector;

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

	public String getFullStack() {

		return fullStack;

	}

	public void setFullStack(String fullStack) {

		this.fullStack = fullStack;

	}

	public LocalDateTime getDateTimeError() {

		return dateTimeError;

	}

	public void setDateTimeError(LocalDateTime dateTimeError) {

		this.dateTimeError = dateTimeError;

	}

	public LocalDateTime getStartDateTimeError() {

		return startDateTimeError;

	}

	public void setStartDateTimeError(LocalDateTime startDateTimeError) {

		this.startDateTimeError = startDateTimeError;

	}

	public LocalDateTime getEndDateTimeError() {

		return endDateTimeError;

	}

	public void setEndDateTimeError(LocalDateTime endDateTimeError) {

		this.endDateTimeError = endDateTimeError;

	}

	public Long getCodUser() {

		return codUser;

	}

	public void setCodUser(Long codUser) {

		this.codUser = codUser;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}

}
