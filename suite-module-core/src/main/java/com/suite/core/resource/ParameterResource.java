package com.suite.core.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.listener.ResourceCreatedEvent;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.Parameter;
import com.suite.core.repository.filter.ParameterFilter;
import com.suite.core.service.ParameterService;

/**
 * EndPoint de acesso a recurso parameter
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/parameter")
public class ParameterResource extends ResourceCore {

	@Autowired
	private ParameterService parameterService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* DEFAULT METHODS RESOURCE */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/list")
	@PreAuthorize("hasAuthority('CORE_PARAMETER_LIST')")
	public List<Parameter> list() {
		return parameterService.list();

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PARAMETER_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(parameterService.findByCode(code));

	}

	/**
	 * Deleta o objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 */
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('DELETE')")
	public void deleteByCode(@PathVariable Long code) {
		parameterService.delete(code);

	}

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param parameter
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_PARAMETER_INSERT')")
	public ResponseEntity<Parameter> save(@RequestBody Parameter parameter, HttpServletResponse response) {
		Parameter parameterSaved = parameterService.insert(parameter);

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, parameterSaved.getCode()));
		notify("O Parâmetro '" + parameterSaved.getKey() + "' foi criado.");

		notify(formatTranslate("msg_o_parametro_[%s]_foi_c", parameterSaved.getKey()));

		return ResponseEntity.status(HttpStatus.CREATED).body(parameterSaved);

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param parameter
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PARAMETER_UPDATE')")
	public ResponseEntity<Parameter> edit(@PathVariable Long code, @RequestBody Parameter parameter) {
		parameter = parameterService.update(code, parameter);

		notify(formatTranslate("msg_o_parametro_[%s]_foi_e", parameter.getKey()));

		return ResponseEntity.status(HttpStatus.OK).body(parameter);

	}

	/* CUSTOM METHODS RESOURCE */

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@RequestMapping
	@PreAuthorize("hasAuthority('CORE_PARAMETER_VIEWER')")
	public List<Parameter> filter(ParameterFilter filter) {
		return parameterService.filter(filter);

	}

	/**
	 * Retorna objeto parametro pela chave
	 * Nao tem restricao de permissao para esse metodo
	 * @param key
	 * @return
	 */
	@PostMapping("/byKey")
	public ResponseEntity<?> findByKey(@RequestBody String key) {
		return ResponseEntity.status(HttpStatus.OK).body(parameterService.findByKeyOrNull(key));

	}

}
