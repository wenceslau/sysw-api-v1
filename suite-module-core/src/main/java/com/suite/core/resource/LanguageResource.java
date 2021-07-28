package com.suite.core.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import com.suite.core.base.EnumCore.Lang;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.Language;
import com.suite.core.model.resume.LanguageResume;
import com.suite.core.repository.filter.LanguageFilter;
import com.suite.core.service.LanguageService;

/**
 * EndPoint de acesso a recurso application
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/language")
public class LanguageResource extends ResourceCore {

	@Autowired
	private LanguageService languageService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* DEFAULT METHODS RESOURCE */

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_LANGUAGE_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(languageService.findByCode(code), HttpStatus.OK);

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
		languageService.delete(code);

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param language
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_LANGUAGE_INSERT')")
	public ResponseEntity<Language> save(@RequestBody Language language, HttpServletResponse response) {
		Language languageSaved = languageService.insert(language);

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, languageSaved.getCode()));

		notify(formatTranslate("msg_os_dados_de_l_[%s]_f_c", languageSaved.getKey()));

		// Retora o status da requisição
		return new ResponseEntity<Language>(languageSaved, HttpStatus.CREATED);

	}

	/**
	 * Atualiza objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param application
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_LANGUAGE_UPDATE')")
	public ResponseEntity<Language> edit(@PathVariable Long code, @RequestBody Language application) {
		Language languageSaved = languageService.update(code, application);

		notify(formatTranslate("msg_os_dados_de_l_[%s]_f_e", languageSaved.getKey()));

		// Atualiza o recurso e retorna status OK
		return new ResponseEntity<Language>(languageSaved, HttpStatus.OK);

	}

	/* CUSTOM METHODS RESOURCE */

	@GetMapping("/filter/pageable")
	@PreAuthorize("hasAuthority('CORE_LANGUAGE_VIEWER')")
	public Page<Language> filter(LanguageFilter filter, Pageable pageable) {
		Page<Language> listPg = languageService.filter(filter, pageable);
		return new PageImpl<>(listPg.getContent(), listPg.getPageable(), listPg.getTotalElements());
	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_LANGUAGE_VIEWER')")
	public List<Language> filter(LanguageFilter filter) {
		return languageService.filter(filter);

	}

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@PostMapping("/listByValue")
	public List<LanguageResume> listByValue(@RequestBody String lang) {
		return LanguageResume.buildResume(languageService.list(), Lang.valueOf(lang));

	}

}
