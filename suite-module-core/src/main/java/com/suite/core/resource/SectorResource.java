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
import com.suite.core.model.resume.SectorResume;
import com.suite.core.model.Sector;
import com.suite.core.model.viewer.SectorVwr;
import com.suite.core.repository.filter.SectorFilter;
import com.suite.core.service.SectorService;

/**
 * EndPoint de acesso a recurso sector
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/sector")
public class SectorResource extends ResourceCore {

	@Autowired
	private SectorService sectorService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	@PreAuthorize("hasAuthority('CORE_SECTOR_LIST')")
	public List<SectorResume> listResume() {
		SectorFilter filter = new SectorFilter();
		filter.setStatus(true);
		return SectorResume.build(sectorService.filterBy(filter));

	}

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAnyAuthority('CORE_SECTOR_LIST','CORE_SECTOR_VIEWER')")
	public List<SectorVwr> filter(SectorFilter filter) {
		return SectorVwr.build(sectorService.filterBy(filter));

	}

	/**
	 * Retorna lista de setor pela unidade de negocio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/listResumeByBusinessUnit/{code}")
	@PreAuthorize("hasAuthority('CORE_SECTOR_LIST')")
	public List<SectorResume> listResumeByBusinessUnit(@PathVariable Long code) {
		return SectorResume.build(sectorService.listByBusinessUnit(code));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_SECTOR_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(SectorVwr.build(sectorService.findByCode(code)), HttpStatus.OK);

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/sectorForApi/{code}")
	@PreAuthorize("hasAuthority('CORE_SECTOR_API')")
	public ResponseEntity<?> sectorForApi(@PathVariable Long code) {
		return new ResponseEntity<>(SectorVwr.build(sectorService.findByCode(code)), HttpStatus.OK);

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
		sectorService.delete(code);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_a", formatTranslate("lbl_setor"), code));

	}

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param parameter
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_SECTOR_INSERT')")
	public ResponseEntity<SectorVwr> save(@RequestBody Sector sector, HttpServletResponse response) {
		sector = sectorService.insert(sector);

		// adiciona no header a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, sector.getCode()));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_setor"), sector.getName()));

		sectorService.createUserActionTable(sector);

		return new ResponseEntity<SectorVwr>(SectorVwr.build(sector), HttpStatus.CREATED);

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param parameter
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_SECTOR_UPDATE')")
	public ResponseEntity<SectorVwr> edit(@PathVariable Long code, @RequestBody Sector sector) {
		sector = sectorService.update(code, sector);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_setor"), sector.getName()));

		return new ResponseEntity<SectorVwr>(SectorVwr.build(sector), HttpStatus.OK);

	}

}
