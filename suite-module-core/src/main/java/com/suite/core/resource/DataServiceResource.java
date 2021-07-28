package com.suite.core.resource;

import java.util.Base64;
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
import com.suite.core.model.resume.DataServiceResume;
import com.suite.core.model.DataService;
import com.suite.core.model.viewer.DataServiceVwr;
import com.suite.core.repository.filter.DataServiceFilter;
import com.suite.core.service.DataServiceService;

/**
 * EndPoint de acesso a recurso dataservice
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/dataservice")
public class DataServiceResource extends ResourceCore {

	@Autowired
	private DataServiceService dataServiceService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_VIEWER','CORE_DATA_SERVICE_LIST','CORE_DATA_TASK_VIEWER','CORE_DATA_TASK_LIST')")
	public List<DataServiceResume> listResume() {
		DataServiceFilter filter = new DataServiceFilter();
		filter.setStatus(true);
		return DataServiceResume.build(dataServiceService.listByFilter(filter));

	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filterResume")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_VIEWER','CORE_DATA_SERVICE_LIST','CORE_DATA_TASK_VIEWER','CORE_DATA_TASK_LIST')")
	public List<DataServiceResume> filterResume(DataServiceFilter filter) {
		return DataServiceResume.build(dataServiceService.listByFilter(filter));

	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filterDataService")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_VIEWER')")
	public List<DataServiceVwr> filterDataService(DataServiceFilter filter) {
		return DataServiceVwr.build(dataServiceService.listByFilter(filter));

	}
	
	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filterDataTask")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_TASK_VIEWER')")
	public List<DataServiceVwr> filterDataTask(DataServiceFilter filter) {
		return DataServiceVwr.build(dataServiceService.listByFilter(filter));

	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/filterDataServiceExternalObjeto")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_VIEWER','CORE_DATA_SERVICE_LIST','CORE_DATA_TASK_VIEWER','CORE_DATA_TASK_LIST')")
	public List<DataServiceResume> filterDataServiceExternalObjeto(DataServiceFilter filter) {
		return DataServiceResume.build(dataServiceService.filterDataServiceExternalObjeto(filter));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_VIEWER','CORE_DATA_SERVICE_LIST','CORE_DATA_TASK_VIEWER','CORE_DATA_TASK_LIST')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(DataServiceVwr.build(dataServiceService.findByCode(code)));

	}

	/**
	 * Clone objeto pelo codigo
	 * @param code
	 */
	@GetMapping("/cloneTemplate/{datServiceType}")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_CLONE','CORE_DATA_TASK_CLONE')")
	public ResponseEntity<?> cloneTemplate(@PathVariable String datServiceType) {
		return ResponseEntity.status(HttpStatus.OK).body(DataServiceVwr.build(dataServiceService.cloneTemplate(datServiceType)));

	}

	/**
	 * Filtra lista de objetos
	 * @param filter
	 * @return
	 */
	@GetMapping("/allForApi/{sectorCode}")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_API')")
	public List<DataServiceVwr> allForApi(@PathVariable Long sectorCode) {
		return DataServiceVwr.build(dataServiceService.listAll(sectorCode));

	}

	@PostMapping("/keyForApi")
	@PreAuthorize("hasAuthority('CORE_DATA_SERVICE_API')")
	public String keyForApi(@RequestBody String value) {
		return Base64.getEncoder().encodeToString(rsaService.getRsa().dencrypt(value).getBytes());

	}

	/* PUT POST DELETE */

	/**
	 * Deleta o objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 */
	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('DELETE')")
	public void deleteByCode(@PathVariable Long code) {
		dataServiceService.delete(code);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_a", formatTranslate("lbl_servico_de_dados"), code));

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param dataService
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_INSERT','CORE_DATA_TASK_INSERT','CORE_DATA_SERVICE_CLONE','CORE_DATA_TASK_CLONE')")
	public ResponseEntity<DataServiceVwr> save(@RequestBody DataService dataService, HttpServletResponse response) {
		DataServiceVwr serviceSaved = DataServiceVwr.build(dataServiceService.insert(dataService));

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, serviceSaved.getCode()));
		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_servico_de_dados"), serviceSaved.getName()));

		return ResponseEntity.status(HttpStatus.CREATED).body(serviceSaved);

	}

	/**
	 * Atualiza objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param dataService
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_UPDATE','CORE_DATA_TASK_UPDATE')")
	public ResponseEntity<DataServiceVwr> edit(@PathVariable Long code, @RequestBody DataService dataService) {
		dataService = dataServiceService.update(code, dataService);
		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_servico_de_dados"), dataService.getName()));

		return ResponseEntity.status(HttpStatus.OK).body(DataServiceVwr.build(dataService));

	}

	/**
	 * Clone objeto pelo codigo
	 * @param code
	 */
	@PutMapping("/clone/{code}")
	@PreAuthorize("hasAnyAuthority('CORE_DATA_SERVICE_CLONE','CORE_DATA_TASK_CLONE')")
	public ResponseEntity<?> cloneByCode(@PathVariable Long code) {
		// dataServiceService.cloneDataService(code, false);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c1", formatTranslate("lbl_servico_de_dados"), code));

		return ResponseEntity.status(HttpStatus.OK).body(DataServiceVwr.build(dataServiceService.cloneDataService(code, false)));

	}

	/**
	 * Insere objeto no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param dataService
	 * @param response
	 * @return
	 */
	@PostMapping("/testDataService/{code}")
	public ResponseEntity<BoolDTO> testDataService(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(dataServiceService.testDataService(code));

	}

}
