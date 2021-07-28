package com.suite.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.suite.core.model.Notify;

/**
 * Service responsavel por enviar notificacoes aos clientes registrados no websocket
 * @author Wenceslau
 *
 */
@Service
public class WebSocketService {

	@Autowired
	private SimpMessagingTemplate template;

	public void notify(String message) {}

	/**
	 * @param notify
	 */
	public void notify(Notify notify) {
		this.template.convertAndSend("/chat/" + notify.getSectorCode(), notify);

		// O SA subscreve no canal 0. Envia toda notifiacao para o SA
		this.template.convertAndSend("/chat/0", notify);

	}

	/**
	 * @param notify
	 */
	public void notifyUser(Notify notify) {
		this.template.convertAndSend("/chat/" + notify.getUserName(), notify);

		// O SA subscreve no canal 0. Envia toda notifiacao para o SA
		// this.template.convertAndSend("/chat/0" , notify);
	}

}
