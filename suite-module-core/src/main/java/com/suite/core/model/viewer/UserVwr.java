
package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.resume.ProfileResume;
import com.suite.core.model.resume.SectorResume;
import com.suite.core.model.User;

public class UserVwr extends ViewerCore {

	private String name;

	private String username;

	private String email;

	private ProfileResume profile;

	private Integer hash;

	private Integer hashPassword;

	private Boolean receiveNotify = false;

	private Boolean sendNotify = false;

	private Boolean viewNotify = false;

	private List<SectorResume> sectors;

	private Boolean firstAccess;

	private BusinessUnitResume businessUnit;

	private Boolean windowsAutentication = false;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public String getEmail() {

		return email;

	}

	public void setEmail(String email) {

		this.email = email;

	}

	public ProfileResume getProfile() {

		return profile;

	}

	public void setProfile(ProfileResume profile) {

		this.profile = profile;

	}

	public Integer getHash() {

		return hash;

	}

	public void setHash(Integer hash) {

		this.hash = hash;

	}

	public Integer getHashPassword() {

		return hashPassword;

	}

	public void setHashPassword(Integer hashPassword) {

		this.hashPassword = hashPassword;

	}

	public Boolean getReceiveNotify() {

		return receiveNotify;

	}

	public void setReceiveNotify(Boolean receiveNotify) {

		this.receiveNotify = receiveNotify;

	}

	public Boolean getSendNotify() {

		return sendNotify;

	}

	public void setSendNotify(Boolean sendNotify) {

		this.sendNotify = sendNotify;

	}

	public Boolean getViewNotify() {

		return viewNotify;

	}

	public void setViewNotify(Boolean viewNotify) {

		this.viewNotify = viewNotify;

	}

	public List<SectorResume> getSectors() {

		if (sectors == null)
			sectors = new ArrayList<>();

		return sectors;

	}

	public void setSectors(List<SectorResume> sectors) {

		this.sectors = sectors;

	}

	public Boolean getFirstAccess() {

		return firstAccess;

	}

	public void setFirstAccess(Boolean firstAccess) {

		this.firstAccess = firstAccess;

	}

	public BusinessUnitResume getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitResume businessUnit) {

		this.businessUnit = businessUnit;

	}

	public Boolean getWindowsAutentication() {

		return windowsAutentication;

	}

	public void setWindowsAutentication(Boolean windowsAutentication) {

		this.windowsAutentication = windowsAutentication;

	}

	public static UserVwr build(User source) {

		if (source == null)
			return null;

		UserVwr target = new UserVwr();
		copyProperties(source, target, new String[] { "businessUnit", "profile", "sectors" });

		target.setBusinessUnit(BusinessUnitResume.build(source.getBusinessUnit()));
		target.setProfile(ProfileResume.build(source.getProfile()));
		target.getSectors().addAll(SectorResume.build(new ArrayList<>(source.getSectors())));

		return target;

	}

	public static List<UserVwr> build(List<User> sourceList) {

		List<UserVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}
