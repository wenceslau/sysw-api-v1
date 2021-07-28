
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.app.base.Model;
import com.suite.core.model.UserLogonHistory;

/**
 * Classe que representa a entidade UserLogonHistory
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_user_logon_history")
public class UserLogonHistory extends Model {

	/*
	 * PK da entidade
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	protected Long code;

	/*
	 * Endereco IP de onde o usuario realizou o login
	 */
	@NotNull
	@Column(name = "val_ip_address")
	private String ipAddress;

	/*
	 * Nome do browser de onde ele fez o login
	 */
	@Column(name = "val_browser")
	private String browser;

	/*
	 * Login do usuario
	 */
	@NotNull
	@Column(name = "val_user_logon")
	private String userLogon;

	/*
	 * Status do logon
	 * SUCCESS, FAILURE
	 * TODO: Avaliar usao de ENUM
	 */
	@NotNull
	@Column(name = "val_status_logon")
	private String statusLogon;

	@Column(name = "val_device")
	private String device;

	/*
	 * Usuario que tentou realizar o logon
	 * 
	 * @JsonIgnoreProperties ignora a prorpriedade profile do usuario no JSON
	 */
	@JsonIgnore
	@JsonIgnoreProperties({ "profile", "businessUnit", "sectors" })
	@ManyToOne
	@JoinColumn(nullable = true, name = "usr_record_fk")
	private User userRecord;

	/*
	 * Data Hora do registro
	 */
	@NotNull
	@Column(name = "dtt_record")
	private LocalDateTime dateRecord;

	/*
	 * data da tentativa de logon
	 */
	@Column(name = "dat_logon")
	private LocalDate dateLogon;

	@Column(name = "cod_sector")
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

	public User getUserRecord() {

		return userRecord;

	}

	public void setUserRecord(User userRecord) {

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

	public String getDevice() {

		return device;

	}

	public void setDevice(String device) {

		this.device = device;

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
		UserLogonHistory other = (UserLogonHistory) obj;

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

	public static UserLogonHistory build(UserLogonHistory source) {

		if (source == null)
			return null;

		UserLogonHistory target = new UserLogonHistory();
		// copyProperties(source, target);
		copyProperties(source, target, "userRecord");
		target.setUserRecord(User.build(source.getUserRecord()));

		return target;

	}

	public static List<UserLogonHistory> build(List<UserLogonHistory> sourceList) {

		List<UserLogonHistory> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}