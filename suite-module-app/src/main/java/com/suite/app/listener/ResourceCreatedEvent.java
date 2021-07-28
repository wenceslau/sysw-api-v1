package com.suite.app.listener;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

/**
 * Classe evento de criação de recurso, usada no Listener que
 * adiciona o header do recurso criado
 * @author Wenceslau Neto
 *
 */
public class ResourceCreatedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	/*
	 * Response do HTTP
	 */
	private HttpServletResponse response;

	/*
	 * ID do resource criado. sera adicionado no URI para montar o path do header http
	 */
	private Object id;

	public ResourceCreatedEvent(Object source, HttpServletResponse response, Object id) {

		super(source);
		this.response = response;
		this.id = id;
	}

	public HttpServletResponse getResponse() {

		return response;

	}

	public Object getId() {

		return id;

	}

}