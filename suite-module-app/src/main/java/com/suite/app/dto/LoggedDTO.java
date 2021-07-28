package com.suite.app.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa um cliente websocket conectado
 * @author Wenceslau
 *
 */
public class LoggedDTO {

	/*
	 * ID da sessao websocket
	 */
	private String sessionId;

	/*
	 * Nome do usuario
	 */
	private String name;

	/*
	 * login do usuario
	 */
	private String username;

	/*
	 * Data da conexao
	 */
	private final LocalDateTime login = LocalDateTime.now();

	/*
	 * Setor do usuario conectado
	 */
	private Long codSector;

	/*
	 * Tempo logado
	 */
	private String timeLogged;

	public LoggedDTO(String sessionId, String username, Long codSector) {

		super();
		this.sessionId = sessionId;
		this.username = username;
		this.codSector = codSector;

	}

	public String getSessionId() {

		return sessionId;

	}

	public void setSessionId(String sessionId) {

		this.sessionId = sessionId;

	}

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

	public LocalDateTime getLogin() {

		return login;

	}

	public Long getCodSector() {

		return codSector;

	}

	public void setCodSector(Long codSector) {

		this.codSector = codSector;

	}

	public String getTimeLogged() {

		LocalTime time = LocalTime.ofNanoOfDay(Duration.between(login, LocalDateTime.now()).toNanos());
		timeLogged = time.plusSeconds(1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		return timeLogged;

	}

	public void setTimeLogged(String timeLogged) {

		this.timeLogged = timeLogged;

	}

	@Override
	public String toString() {

		return "LoggedTO [sessionId=" + sessionId + ", username=" + username + ", codSector=" + codSector + "]";

	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
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
		LoggedDTO other = (LoggedDTO) obj;

		if (sessionId == null) {

			if (other.sessionId != null)
				return false;

		} else if (!sessionId.equals(other.sessionId))
			return false;

		return true;

	}

}
