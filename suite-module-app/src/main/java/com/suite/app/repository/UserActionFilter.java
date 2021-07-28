
package com.suite.app.repository;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.suite.app.base.Filter;

/**
 * Classe filter para o UserAction. Usada para filtrar qualquer UserAction de qualquer modulo
 * @author Wenceslau
 */
public class UserActionFilter extends Filter {

	private Long code;

	private String action;

	private String nameObject;

	private Long idRecord;

	private Long idObject;

	private String hashObject;

	private Long codUserRecord;

	private Long codeSectorUserLogged;

	private Long codBusinessUnit;

	private boolean resume;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateRecordStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateRecordEnd;

	@Override
	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getAction() {

		return action;

	}

	public void setAction(String action) {

		this.action = action;

	}

	public String getNameObject() {

		return nameObject;

	}

	public void setNameObject(String nameObject) {

		this.nameObject = nameObject;

	}

	public Long getIdRecord() {

		return idRecord;

	}

	public void setIdRecord(Long idRecord) {

		this.idRecord = idRecord;

	}

	public Long getIdObject() {

		return idObject;

	}

	public void setIdObject(Long idObject) {

		this.idObject = idObject;

	}

	public String getHashObject() {

		return hashObject;

	}

	public void setHashObject(String hashObject) {

		this.hashObject = hashObject;

	}

	public Long getCodUserRecord() {

		return codUserRecord;

	}

	public void setCodUserRecord(Long idUserRecord) {

		this.codUserRecord = idUserRecord;

	}

	public LocalDateTime getDateRecordStart() {

		return dateRecordStart;

	}

	public void setDateRecordStart(LocalDateTime dateRecordStart) {

		this.dateRecordStart = dateRecordStart;

	}

	public LocalDateTime getDateRecordEnd() {

		return dateRecordEnd;

	}

	public void setDateRecordEnd(LocalDateTime dateRecordEnd) {

		this.dateRecordEnd = dateRecordEnd;

	}

	public Long getCodeSectorUserLogged() {

		return codeSectorUserLogged;

	}

	public void setCodeSectorUserLogged(Long codeSectorUserLogged) {

		this.codeSectorUserLogged = codeSectorUserLogged;

	}

	public Long getCodBusinessUnit() {

		return codBusinessUnit;

	}

	public void setCodBusinessUnit(Long codBusinessUnit) {

		this.codBusinessUnit = codBusinessUnit;

	}

	@Override
	public Boolean getStatus() {

		return true;

	}

	public boolean isResume() {

		return resume;

	}

	public void setResume(boolean resume) {

		this.resume = resume;

	}

}
