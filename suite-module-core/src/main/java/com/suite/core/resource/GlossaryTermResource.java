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
import com.suite.core.base.ResourceCore;
import com.suite.core.model.GlossaryTerm;
import com.suite.core.model.resume.GlossaryTermTO;
import com.suite.core.repository.filter.GlossaryTermFilter;
import com.suite.core.service.GlossaryTermService;

/**
 * EndPoint de acesso a recurso application
 * 
 * @author Fernando
 *
 */
@RestController
@RequestMapping("/TermosGlossario")
public class GlossaryTermResource extends ResourceCore {

	@Autowired
	private GlossaryTermService termosGlossarioService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* DEFAULT METHODS RESOURCE */

	/**
	 * Retorna Objeto pelo codigo Notacao @PreAuthorize - Apenas usuarios com tal
	 * permissao tem acesso ao metodo
	 * 
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('DPG_TERMOS_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(GlossaryTermTO.buildTO(termosGlossarioService.findByCode(code)), HttpStatus.OK);

	}

	/**
	 * Deleta o objeto pelo codigo Notacao @PreAuthorize - Apenas usuarios com tal
	 * permissao tem acesso ao metodo
	 * 
	 * @param code
	 */
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('DPG_TERMOS_DELETE')")
	public void deleteByCode(@PathVariable Long code) {
		termosGlossarioService.delete(code);

	}

	/**
	 * Insere objeto no repositorio Notacao @PreAuthorize - Apenas usuarios com tal
	 * permissao tem acesso ao metodo
	 * 
	 * @param termosGlossario
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('DPG_TERMOS_INSERT')")
	public ResponseEntity<GlossaryTermTO> save(@RequestBody GlossaryTermTO termosGlossario,
			HttpServletResponse response) {
		GlossaryTerm termosGlossarioSaved = termosGlossarioService.insert(GlossaryTermTO.buildEntity(termosGlossario));

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, termosGlossarioSaved.getCode()));

		notify(formatTranslate("msg_o_[%s]_['%s']_f_c", formatTranslate("lbl_termosGlossario"),
				termosGlossario.getName()));

		// Retora o status da requisição
		return new ResponseEntity<GlossaryTermTO>(GlossaryTermTO.buildTO(termosGlossarioSaved), HttpStatus.CREATED);

	}

	/**
	 * Atualiza objeto no repositorio Notacao @PreAuthorize - Apenas usuarios com
	 * tal permissao tem acesso ao metodo
	 * 
	 * @param code
	 * @param application
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('DPG_TERMOS_UPDATE')")
	public ResponseEntity<GlossaryTermTO> edit(@PathVariable Long code, @RequestBody GlossaryTermTO termosGlossario) {
		GlossaryTerm termosGlossarioSaved = termosGlossarioService.update(code, GlossaryTermTO.buildEntity(termosGlossario));

		notify(formatTranslate("msg_o_[%s]_['%s']_f_e", formatTranslate("lbl_objeto"), termosGlossarioSaved.getName()));

		// Atualiza o recurso e retorna status OK
		return new ResponseEntity<GlossaryTermTO>(GlossaryTermTO.buildTO(termosGlossarioSaved), HttpStatus.OK);

	}

	/* CUSTOM METHODS RESOURCE */

	@GetMapping("/pageable")
	@PreAuthorize("hasAuthority('DPG_TERMOS_VIEWER')")
	public Page<GlossaryTermTO> filter(GlossaryTermFilter filter, Pageable pageable) {
		Page<GlossaryTerm> listPg = termosGlossarioService.filter(filter, pageable);
		return new PageImpl<>(GlossaryTermTO.buildListTO(listPg.getContent()), listPg.getPageable(), listPg.getTotalElements());
	}

	/**
	 * Filtra lista de objetos
	 * 
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('DPG_TERMOS_VIEWER')")
	public List<GlossaryTermTO> filter(GlossaryTermFilter filter) {
		return GlossaryTermTO.buildListTO(termosGlossarioService.filter(filter));

	}

}
