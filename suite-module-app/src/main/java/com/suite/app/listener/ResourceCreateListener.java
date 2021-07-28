package com.suite.app.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Classe para ouvir os eventos disparados na criação de um resource
 * @author Wenceslau
 *
 */
@Component
public class ResourceCreateListener implements ApplicationListener<ResourceCreatedEvent> {

	/**
	 * Evento chamado para adicionar no head da requisição a url de get do recurso
	 * criado
	 */
	@Override
	public void onApplicationEvent(ResourceCreatedEvent resourceCreatedEvent) {

		HttpServletResponse response = resourceCreatedEvent.getResponse();
		Object codigo = resourceCreatedEvent.getId();

		addHeaderLocation(response, codigo);

	}

	/**
	 * Adiciona valor ao header do response recebido
	 * @param response
	 * @param id
	 */
	private void addHeaderLocation(HttpServletResponse response, Object id) {

		// Monta a uri com o path e o id. Ex: http://localhost:8080/Account/7
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(id).toUri();
		response.setHeader("Location", uri.toASCIIString());

	}

}
