
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.app.base.ModelTO;
import com.suite.core.model.UserLogonHistory;

public class UserLogonHistoryTO extends ModelTO {

	protected Long code;

	private String ipAddress;

	private String browser;

	private String userLogon;

	private String statusLogon;

	private UserTO userRecord;

	private LocalDateTime dateRecord;

	private LocalDate dateLogon;

	private Long codeSector;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getIpAddress() {

		return ipAddress;

	}

	public void setIpAddress(String ipAddress) {

		this.ipAddress = ipAddress;

	}

	public String getBrowser() {

		return browser;

	}

	public void setBrowser(String browser) {

		this.browser = browser;

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

	public UserTO getUserRecord() {

		return userRecord;

	}

	public void setUserRecord(UserTO userRecord) {

		this.userRecord = userRecord;

	}

	public LocalDateTime getDateRecord() {

		return dateRecord;

	}

	public void setDateRecord(LocalDateTime dateRecord) {

		this.dateRecord = dateRecord;

	}

	public LocalDate getDateLogon() {

		return dateLogon;

	}

	public void setDateLogon(LocalDate dateLogon) {

		this.dateLogon = dateLogon;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLogonHistoryTO other = (UserLogonHistoryTO) obj;

		if (code == null) {
			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

	@Override
	public Boolean getStatus() {

		return true;

	}

	@Override
	public void setStatus(Boolean status) {

	}

	public static UserLogonHistoryTO build(UserLogonHistory source, String... ignoreProperties) {

		if (source == null)
			return null;

		UserLogonHistoryTO target = new UserLogonHistoryTO();
		copyProperties(source, target, "userRecord");

		target.setUserRecord(UserTO.build(source.getUserRecord(), ignoreProperties));

		System.out.println("UserLogonHistoryTO.build()" + source.getCode());
		return target;

	}

	public static List<UserLogonHistoryTO> build(List<UserLogonHistory> sourceList, String... ignoreProperties) {

		List<UserLogonHistoryTO> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}