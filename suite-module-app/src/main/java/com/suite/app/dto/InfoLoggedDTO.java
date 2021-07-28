package com.suite.app.dto;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Objeto DTO usado para armazenar em tempo real os clientes web socket conectados
 * @author Wenceslau
 *
 */
@Component
public class InfoLoggedDTO {

	/*
	 * Set de objets que representa um cliente websocket conectado
	 */
	private final Set<LoggedDTO> usersLogged;

	public InfoLoggedDTO() {

		usersLogged = new HashSet<>();

	}

	/**
	 * Retorna o set com os user logados
	 * @return
	 */
	public Set<LoggedDTO> getUsersLogged() {

		return usersLogged;

	}

	/**
	 * Adiciona o cliente no Set
	 * @param logged
	 */
	public void addLogged(LoggedDTO logged) {

		removeLogged(logged.getSessionId());
		usersLogged.add(logged);

	}

	/**
	 * Remove o cliente do Set
	 * @param sessionId
	 */
	public void removeLogged(String sessionId) {

		usersLogged.removeIf(x -> x.getSessionId().equals(sessionId));

	}

}
