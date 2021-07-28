package com.suite.app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 * Classe abstrata a ser implementada pode todos os UserActionService de cada modulo
 * Realiza todas as operacoes requeridas pelo objetivo da entidade UserAction
 * @author wbane
 *
 */
@Service
public abstract class UserRequestService extends com.suite.app.base.Service {

	public abstract void saveRequest(LocalDateTime time, String addr,
			String host,
			String userAgent,
			String path,
			String url,
			String verb,
			String userRequest);

}