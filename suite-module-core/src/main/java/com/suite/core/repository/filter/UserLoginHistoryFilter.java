package com.suite.core.repository.filter;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o UserLoginHistory
 * @author Wenceslau
 *
 */
public class UserLoginHistoryFilter extends FilterCore {

	private String ipAddress;
	private String userLogon;
	private String statusLogon;
	private Long codUserRecord;
	private Integer typeProfile;
	private String deviceIn;
	private String device;

	/*
	 * @DateTimeFormat - eh o fomato de data que sera recebido no JSON
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateRecordStart;

	/*
	 * @DateTimeFormat - eh o fomato de data que sera recebido no JSON
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateRecordEnd;

	public String getIpAddress() {

		return ipAddress;

	}

	public void setIpAddress(String ipAddress) {

		this.ipAddress = ipAddress;

	}

	public String getUserLogon() {

		return userLogon;

	}

	public void setUserLogon(String userLogon) {

		this.userLogon = userLogon;

	}

	public String getStatusLogon() {

		return statusLogon;

	}

	public void setStatusLogon(String statusLogon) {

		this.statusLogon = statusLogon;

	}

	public Long getCodUserRecord() {

		return codUserRecord;

	}

	public void setCodUserRecord(Long userRecord) {

		this.codUserRecord = userRecord;

	}

	public Integer getTypeProfile() {

		return typeProfile;

	}

	public void setTypeProfile(Integer typeProfile) {

		this.typeProfile = typeProfile;

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

	public String getDeviceIn() {

		return deviceIn;

	}

	public void setDeviceIn(String deviceIn) {

		this.deviceIn = deviceIn;

	}

	public String getDevice() {

		return device;

	}

	public void setDevice(String device) {

		this.device = device;

	}

}
