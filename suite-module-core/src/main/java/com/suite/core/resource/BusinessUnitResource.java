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

import com.suite.app.dto.BoolDTO;
import com.suite.app.listener.ResourceCreatedEvent;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.resume.BusinessUnitResume;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.viewer.BusinessUnitVwr;
import com.suite.core.service.BusinessUnitService;
import com.suite.security.cryptography.RSACryptography;
import com.suite.security.licence.Licence;

/**
 * EndPoint de acesso a recurso businessUnit
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/businessUnit")
public class BusinessUnitResource extends ResourceCore {

	@Autowired
	private BusinessUnitService businessUnitService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_LIST')")
	public List<BusinessUnitResume> listResume() {
		return BusinessUnitResume.build(businessUnitService.findAllEnabled());

	}

	/**
	 * Filtra lista de objetos
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_VIEWER')")
	public List<BusinessUnitVwr> filter() {
		return BusinessUnitVwr.build(businessUnitService.listByFilter());

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(businessUnitService.findByCode(code), HttpStatus.OK);

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
		businessUnitService.delete(code);

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param application
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_INSERT')")
	public ResponseEntity<?> save(@RequestBody BusinessUnit businessUnit, HttpServletResponse response) {
		BusinessUnit businessUnitSaved = businessUnitService.insert(businessUnit);

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, businessUnitSaved.getCode()));
		notify(formatTranslate("msg_a_unidade_de_negocio_[%s]_f_c", businessUnitSaved.getName()));

		// Retora o status da requisição
		return new ResponseEntity<BusinessUnit>(businessUnitSaved, HttpStatus.CREATED);

	}

	/**
	 * Atualiza objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param application
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_UPDATE')")
	public ResponseEntity<?> edit(@PathVariable Long code, @RequestBody BusinessUnit businessUnit) {
		BusinessUnit businessUnitSaved = businessUnitService.update(code, businessUnit);

		notify(formatTranslate("msg_a_unidade_de_negocio_[%s]_f_e", businessUnitSaved.getName()));

		// Atualiza o recurso e retorna status OK
		return new ResponseEntity<BusinessUnit>(businessUnitSaved, HttpStatus.OK);

	}

	@PostMapping("/sysmonkey")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sysMonkey(@RequestBody String value) {
		businessUnitService.setSysMonkey(value);

	}

	@PostMapping("/sysMonkeyForApi/{sectorCode}")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_API')")
	public ResponseEntity<?> sysMonkeyForApi(@PathVariable Long sectorCode, @RequestBody String value) {
		return new ResponseEntity<>(businessUnitService.sysMonkeyForApi(sectorCode, value), HttpStatus.OK);

	}

	@GetMapping("/sysmonkey/{sectorCode}")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_API')")
	public ResponseEntity<?> sysmonkey(@PathVariable Long sectorCode) {
		return new ResponseEntity<>(businessUnitService.sysmonkey(sectorCode), HttpStatus.OK);

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param application
	 * @param response
	 * @return
	 */
	@PutMapping("/sysmonkey/{code}")
	@PreAuthorize("hasAuthority('CORE_BUSINESS_UNIT_UPDATE')")
	public ResponseEntity<?> sysmonkey(@PathVariable Long code, @RequestBody Licence licence) {
		String json = Licence.toJson(licence);

		BoolDTO bool = new BoolDTO();
		bool.setObject(new RSACryptography().encrypt(json, ""));

		return new ResponseEntity<BoolDTO>(bool, HttpStatus.OK);

	}

	/* CUSTOM METHODS RESOURCE */

}
