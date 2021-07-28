package com.suite.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.app.base.Resource;
import com.suite.app.service.EncodeService;
import com.suite.core.service.NotifyService;

/**
 * Classe base Resource do Modulo Core
 * Estende a classe base Resource
 * @author Wenceslau
 *
 */
@Service
public abstract class ResourceCore extends Resource {

	@Autowired
	private NotifyService notifyService;
	
	@Autowired
	protected EncodeService rsaService;

	/**
	 * Envia uma notificacao usando o NotifyService
	 * @param message
	 */
	protected void notify(String message) {

		notifyService.notify(message);

	}
	
	/**
	 * Envia uma notificacao tipada usando o NotifyService
	 * @param message
	 * @param type
	 */
	protected void notifyUser(String message, String type) {

		notifyService.notifyUser(message, type);

	}

	/**
	 * Procura o valor da chave e formata se possuir parametros
	 * @param key
	 * @param args
	 * @return
	 */
	@Override
	protected String formatTranslate(String key, Object... args) {

		return notifyService.formatTranslate(key, args);

	}

}
