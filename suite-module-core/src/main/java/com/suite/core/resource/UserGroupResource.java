package com.suite.core.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.listener.ResourceCreatedEvent;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.resume.UserGroupResume;
import com.suite.core.model.UserGroup;
import com.suite.core.model.viewer.UserGroupVwr;
import com.suite.core.repository.filter.UserGroupFilter;
import com.suite.core.service.UserGroupService;

/**
 * EndPoint de acesso a recurso sector
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/usergroup")
public class UserGroupResource extends ResourceCore {

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	@PreAuthorize("hasAuthority('CORE_USER_GROUP_LIST')")
	public List<UserGroupResume> listResume() {
		UserGroupFilter filter = new UserGroupFilter();
		filter.setStatus(true);
		return UserGroupResume.build(userGroupService.listByFilter(filter));

	}

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAnyAuthority('CORE_USER_GROUP_LIST','CORE_USER_GROUP_VIEWER')")
	public List<UserGroupVwr> filter(UserGroupFilter filter) {
		return UserGroupVwr.build(userGroupService.listByFilter(filter));

	}

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filterResume")
	@PreAuthorize("hasAnyAuthority('CORE_USER_GROUP_LIST','CORE_USER_GROUP_VIEWER')")
	public List<UserGroupResume> filterResume(UserGroupFilter filter) {
		return UserGroupResume.build(userGroupService.listByFilter(filter));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_USER_GROUP_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return new ResponseEntity<>(UserGroupVwr.build(userGroupService.findByCode(code)), HttpStatus.OK);

	}

	/* POST PUT DELETE */

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param parameter
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_USER_GROUP_INSERT')")
	public ResponseEntity<?> save(@RequestBody UserGroup userGroup, HttpServletResponse response) {
		userGroup = userGroupService.insert(userGroup);

		// adiciona no header a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, userGroup.getCode()));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_grupo_de_usuarios"), userGroup.getName()));

		return new ResponseEntity<>(UserGroupVwr.build(userGroup), HttpStatus.CREATED);

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param parameter
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_USER_GROUP_UPDATE')")
	public ResponseEntity<?> edit(@PathVariable Long code, @RequestBody UserGroup userGroup) {
		userGroup = userGroupService.update(code, userGroup);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_grupo_de_usuarios"), userGroup.getName()));

		return new ResponseEntity<>(UserGroupVwr.build(userGroup), HttpStatus.OK);

	}

}
