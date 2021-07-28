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
import com.suite.core.model.DataList;
import com.suite.core.model.ItemDataList;
import com.suite.core.service.DataListService;

/**
 * EndPoint de acesso a recurso parameter
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/datalist")
public class DataListResource extends ResourceCore {

	@Autowired
	private DataListService dataListService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* DEFAULT METHODS RESOURCE */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@RequestMapping
	@PreAuthorize("hasAuthority('CORE_DATA_LIST_VIEWER')")
	public List<DataList> listDataList() {
		return dataListService.listDataList();

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_DATA_LIST_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(dataListService.findByCode(code));

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
		dataListService.delete(code);

	}

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param itemDataList
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_DATA_LIST_INSERT')")
	public ResponseEntity<ItemDataList> save(@RequestBody ItemDataList itemDataList, HttpServletResponse response) {
		ItemDataList itemDataListSaved = dataListService.insert(itemDataList);

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, itemDataListSaved.getCode()));

		notify(formatTranslate("msg_o_item_lista_d_d_[%s]_f_c", itemDataListSaved.getLabelItem()));

		return ResponseEntity.status(HttpStatus.CREATED).body(itemDataListSaved);

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param parameter
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_DATA_LIST_UPDATE')")
	public ResponseEntity<ItemDataList> edit(@PathVariable Long code, @RequestBody ItemDataList itemDataList) {
		itemDataList = dataListService.update(code, itemDataList);

		notify(formatTranslate("msg_o_item_lista_d_d_[%s]_f_c", itemDataList.getLabelItem()));

		return ResponseEntity.status(HttpStatus.OK).body(itemDataList);

	}

	/* CUSTOM METHODS RESOURCE */

	/**
	 * Colecao de itens pelo nome da lista
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/allItemByDataListName/{name}")
	@PreAuthorize("hasAuthority('CORE_DATA_LIST_VIEWER')")
	public List<ItemDataList> allItemByDataListName(@PathVariable String name) {
		return dataListService.allItemByDataListName(name);

	}

	/**
	 * Colecao de itens ativo pelo nome da lista
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/allItemEnableByDataListName/{name}")
	public List<ItemDataList> allItemEnableByDataListName(@PathVariable String name) {
		return dataListService.allItemEnableByDataListName(name);

	}

	/**
	 * Colecao de itens ativo pelo grupo e nome da lista
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/allItemEnableByDataListNameAndGroup/{name}/{group}")
	public List<ItemDataList> allItemEnableByDataListNameAndGroup(@PathVariable String name, @PathVariable String group) {
		return dataListService.allItemEnableByDataListNameAndGroup(name, group);

	}

}
