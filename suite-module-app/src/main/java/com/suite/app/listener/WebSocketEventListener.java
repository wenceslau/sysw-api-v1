package com.suite.app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.suite.app.base.Base;
import com.suite.app.dto.InfoLoggedDTO;
import com.suite.app.dto.LoggedDTO;

/**
 * Classe para ouvir os eventos de conexao websockets
 * @author Wenceslau
 *
 */
@Component
public class WebSocketEventListener extends Base {

	/*
	 * Objeto bean que armazena os usuarios logado. Injetado pelo spring
	 */
	@Autowired
	private InfoLoggedDTO infoLoggedDTO;

	@EventListener
	private void handleSessionConnect(SessionConnectEvent event) {

		debug("handleSessionConnect: " + event);

	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {

		debug("handleSessionDisconnect: " + event);
		infoLoggedDTO.removeLogged(event.getSessionId());

	}

	@EventListener
	private void handleSessionSubscribe(SessionSubscribeEvent event) {

		MessageHeaders mh = event.getMessage().getHeaders();

		String username = null;
		if (mh.containsKey("simpDestination"))
			username = mh.get("simpDestination").toString();

		if (username.contains("logged")) {

			username = username.substring(8);

			String sessionId = null;
			if (mh.containsKey("simpSessionId"))
				sessionId = mh.get("simpSessionId").toString();

			String[] arr = username.split("-");

			infoLoggedDTO.addLogged(new LoggedDTO(sessionId, arr[0], Long.parseLong(arr[1])));

		}

		info("handleSessionSubscribe: " + event);

	}

	@EventListener
	private void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {

		debug("handleSessionUnsubscribe: " + event);

	}

	@EventListener
	private void handleSessionConnected(SessionConnectedEvent event) {

		debug("handleSessionConnected: " + event);

	}

}
