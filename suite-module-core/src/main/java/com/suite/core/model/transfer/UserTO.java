
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.User;
import com.suite.security.cryptography.SHACryptography;

public class UserTO extends ModelCoreTO {

	private String name;

	private String username;

	private String email;

	private String password;

	private ProfileTO profile;

	private Integer sectorHash;

	private String userHash;

	private Integer hashPassword;

	private Boolean receiveNotify = false;

	private Boolean sendNotify = false;

	private Boolean viewNotify = false;

	private List<SectorTO> sectors;

	private Boolean firstAccess;

	private Boolean locked;

	private Integer countLocked;

	private BusinessUnitTO businessUnit;

	private Boolean windowsAutentication = false;

	private UserTO() {}

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

	public String getPassword() {

		return password;

	}

	public void setPassword(String password) {

		this.password = password;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public ProfileTO getProfile() {

		return profile;

	}

	public void setProfile(ProfileTO profile) {

		this.profile = profile;

	}

	public List<SectorTO> getSectors() {

		if (sectors == null)
			sectors = new ArrayList<>();
		return sectors;

	}

	public void setSectors(List<SectorTO> sectors) {

		this.sectors = sectors;

	}

	public Boolean getFirstAccess() {

		return firstAccess;

	}

	public void setFirstAccess(Boolean firstAccess) {

		this.firstAccess = firstAccess;

	}

	public Integer getSectorHash() {

		return sectorHash;

	}

	public void setSectorHash(Integer sectorHash) {

		this.sectorHash = sectorHash;

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

	public Boolean getViewNotify() {

		return viewNotify;

	}

	public void setViewNotify(Boolean viewNotify) {

		this.viewNotify = viewNotify;

	}

	public Boolean getSendNotify() {

		return sendNotify;

	}

	public void setSendNotify(Boolean sendNotify) {

		this.sendNotify = sendNotify;

	}

	public BusinessUnitTO getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitTO businessUnit) {

		this.businessUnit = businessUnit;

	}

	public String getUserHash() {

		return userHash;

	}

	public void setUserHash(String userHash) {

		this.userHash = userHash;

	}

	public Boolean getLocked() {

		return locked;

	}

	public void setLocked(Boolean locked) {

		this.locked = locked;

	}

	public Integer getCountLocked() {

		return countLocked;

	}

	public void setCountLocked(Integer countLocked) {

		this.countLocked = countLocked;

	}

	public Boolean getWindowsAutentication() {

		return windowsAutentication;

	}

	public void setWindowsAutentication(Boolean windowsAutentication) {

		this.windowsAutentication = windowsAutentication;

	}

	@JsonIgnore
	public boolean checkUserHash() {

		try {
			return (getUserHash().equals(generateUserHash()));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return false;

		}

	}

	@JsonIgnore
	public String generateUserHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {

		String hash = getName() +
				getUsername() +
				getEmail() +
				getProfile().getCode() +
				getSectorHash() +
				getPassword() +
				getReceiveNotify() +
				getSendNotify() +
				getStatus() +
				getLocked();

		return SHACryptography.encrypt(hash);

	}

	public static UserTO build(User source, String... ignoreProperties) {

		if (source == null)
			return null;

		UserTO target = new UserTO();
		copyProperties(source, target, "profile", "businessUnit", "sectors");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("profile"))
			target.setProfile(ProfileTO.build(source.getProfile(), ignoreProperties));

		if (ignoreList == null || !ignoreList.contains("businessUnit"))
			target.setBusinessUnit(BusinessUnitTO.build(source.getBusinessUnit(), ignoreProperties));

		if (ignoreList == null || !ignoreList.contains("sectors"))
			target.getSectors().addAll(SectorTO.build(new ArrayList<>(source.getSectors()), ignoreProperties));

		System.out.println("UserTO.build()" + source.getCode());
		return target;

	}

	public static List<UserTO> build(List<User> sourceList, String... ignoreProperties) {

		List<UserTO> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}
