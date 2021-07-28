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
import com.suite.core.base.FilterCore;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.resume.ApplicationResume;
import com.suite.core.model.Application;
import com.suite.core.model.viewer.ApplicationVwr;
import com.suite.core.service.ApplicationService;

/**
 * EndPoint de acesso a recurso application
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/application")
public class ApplicationResource extends ResourceCore {

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	public List<ApplicationResume> listResume() {
		return ApplicationResume.build(applicationService.list());

	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_APPLICATION_VIEWER')")
	public List<ApplicationVwr> filter(FilterCore filter) {
		return ApplicationVwr.build(applicationService.list());

	}

	/**
	 * Retorna uma lista de Application da Unidade de Negocio do usuario logado
	 * @return
	 */
	@GetMapping("/listByBusinesUnitLogged")
	public List<ApplicationResume> listByBusinesUnitLogged() {
		return ApplicationResume.build(applicationService.listApplicationBusinessUnitLogged());

	}

	/**
	 * Retorna uma lista de Application da Unidade de Negocio do usuario logado
	 * @return
	 */
	@GetMapping("/listByBusinessUnitOrSectorLogged/{codeBu}")
	public List<ApplicationResume> listByBusinessUnitOrSectorLogged(@PathVariable Long codeBu) {
		return ApplicationResume.build(applicationService.listByBusinessUnitOrSectorLogged(codeBu));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_APPLICATION_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(ApplicationVwr.build(applicationService.findByCode(code)), HttpStatus.OK);

	}

	/**
	 * Retorna uma lista de Application da Unidade de Negocio do usuario logado
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listNameApplications")
	public List<String> listNameApplications() {
		return applicationService.listNameApplications();

	}

	/* POST PUT DELETE */

	/**
	 * Deleta o objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 */
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('DELETE')")
	public void deleteByCode(@PathVariable Long code) {
		applicationService.delete(code);

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param application
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_APPLICATION_INSERT')")
	public ResponseEntity<ApplicationVwr> save(@RequestBody Application application, HttpServletResponse response) {
		ApplicationVwr applicationSaved = ApplicationVwr.build(applicationService.insert(application));

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, applicationSaved.getCode()));
		notify(formatTranslate("msg_a_aplicacao_[%s]_foi_c", applicationSaved.getName()));

		// Retora o status da requisição
		return new ResponseEntity<ApplicationVwr>(applicationSaved, HttpStatus.CREATED);

	}

	/**
	 * Atualiza objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param application
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_APPLICATION_UPDATE')")
	public ResponseEntity<ApplicationVwr> edit(@PathVariable Long code, @RequestBody Application application) {
		ApplicationVwr applicationSaved = ApplicationVwr.build(applicationService.update(code, application));

		notify(formatTranslate("msg_a_aplicacao_[%s]_foi_e", applicationSaved.getName()));

		// Atualiza o recurso e retorna status OK
		return new ResponseEntity<ApplicationVwr>(applicationSaved, HttpStatus.OK);

	}

	/* CUSTOM METHODS RESOURCE */

}
