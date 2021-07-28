package com.suite.core.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_core_user_request")
public class UserRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	protected Long code;

	@NotNull
	@Column(name = "dtt_request")
	private LocalDateTime dateRequest;

	@NotNull
	@Column(name = "val_address")
	private String address;

	@NotNull
	@Column(name = "val_host")
	private String host;

	@NotNull
	@Column(name = "val_user_agent")
	private String userAgent;

	@Column(name = "val_app_agent")
	private String appAgent;

	@NotNull
	@Column(name = "val_verb")
	private String verb;

	@NotNull
	@Column(name = "val_path")
	private String path;

	@NotNull
	@Column(name = "val_url")
	private String url;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public LocalDateTime getDateRequest() {

		return dateRequest;

	}

	public void setDateRequest(LocalDateTime dateRequest) {

		this.dateRequest = dateRequest;

	}

	public String getAddress() {

		return address;

	}

	public void setAddress(String address) {

		this.address = address;

	}

	public String getHost() {

		return host;

	}

	public void setHost(String host) {

		this.host = host;

	}

	public String getUserAgent() {

		return userAgent;

	}

	public void setUserAgent(String userAgent) {

		this.userAgent = userAgent;

	}

	public String getAppAgent() {

		return appAgent;

	}

	public void setAppAgent(String appAgent) {

		this.appAgent = appAgent;

	}

	public String getVerb() {

		return verb;

	}

	public void setVerb(String verb) {

		this.verb = verb;

	}

	public String getPath() {

		return path;

	}

	public void setPath(String path) {

		this.path = path;

	}

	public String getUrl() {

		return url;

	}

	public void setUrl(String url) {

		this.url = url;

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
		UserRequest other = (UserRequest) obj;

		if (code == null) {

			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

}