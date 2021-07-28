package com.suite.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("core")
public class ApiProperty {

	private String origin;

	private String host;

	private Integer port;

	private String username;

	private String password;

	public String getOrigin() {

		return origin;

	}

	public void setOrigin(String origin) {

		this.origin = origin;

	}

	public String getHost() {

		return host;

	}

	public void setHost(String host) {

		this.host = host;

	}

	public Integer getPort() {

		return port;

	}

	public void setPort(Integer port) {

		this.port = port;

	}

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public String getPassword() {

		return password;

	}

	public void setPassword(String password) {

		this.password = password;

	}

}
