
package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.UserActionCore;

public class UserActionVwr {

	protected Long code;

	private String application;

	private String action;

	private String nameObject;

	private Long idRecord;

	//private String valueRecord;

	private Long userRecordCode;

	private Long codeSector;

	private LocalDateTime dateRecord;

	private Long codeUser;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getApplication() {

		return application;

	}

	public void setApplication(String application) {

		this.application = application;

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

	public Long getUserRecordCode() {

		return userRecordCode;

	}

	public void setUserRecordCode(Long userRecordCode) {

		this.userRecordCode = userRecordCode;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}

	public LocalDateTime getDateRecord() {

		return dateRecord;

	}

	public void setDateRecord(LocalDateTime dateRecord) {

		this.dateRecord = dateRecord;

	}

	public Long getCodeUser() {

		return codeUser;

	}

	public void setCodeUser(Long codeUser) {

		this.codeUser = codeUser;

	}

	public static UserActionVwr build(UserActionCore source) {

		if (source == null)
			return null;

		UserActionVwr target = new UserActionVwr();
		copyProperties(source, target);

		return target;

	}

	public static List<UserActionVwr> build(List<UserActionCore> sourceList) {

		List<UserActionVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}