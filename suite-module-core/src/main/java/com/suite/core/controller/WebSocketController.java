package com.suite.core.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;

import com.suite.app.base.Controller;

/**
 * Controller que permite receber mensagens dos clientes Web Socket
 * @author 4931pc_neto
 *
 */
@org.springframework.stereotype.Controller
public class WebSocketController extends Controller {

	/**
	 * End point de entrada de mensagens
	 * @param message
	 */
	@MessageMapping("/send/message")
	public void onReceiveMessage(String message) {

		new Thread(new Runnable() {

			@Override
			public void run() {}

		}).start();

	}

	@Override
	protected String formatTranslate(String key, Object... args) {

		return null;

	}

}
