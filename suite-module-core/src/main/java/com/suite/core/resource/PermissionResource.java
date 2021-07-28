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
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.suite.core.dto.MenuItemDTO;
import com.suite.core.model.resume.PermissionResume;
import com.suite.core.model.Permission;
import com.suite.core.model.viewer.PermissionVwr;
import com.suite.core.repository.filter.PermissionFilter;
import com.suite.core.service.PermissionService;

/**
 * EndPoint de acesso a recurso permission
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/permission")
public class PermissionResource extends ResourceCore {

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_PERMISSION_VIEWER')")
	public List<Permission> filter(PermissionFilter filter) {
		return (permissionService.listByFilter(filter));

	}

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listAllEnabled")
	@PreAuthorize("hasAuthority('CORE_PERMISSION_LIST')")
	public List<PermissionResume> listAllEnabled() {
		return PermissionResume.build(permissionService.listAllEnabled());

	}

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listAllEnabledToAssociate")
	@PreAuthorize("hasAuthority('CORE_PERMISSION_LIST')")
	public List<PermissionResume> listAllEnabledToAssociate() {
		return PermissionResume.build(permissionService.listAllEnabledToAssociate());

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PERMISSION_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.findByCode(code));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("icon/{router}")
	public ResponseEntity<?> icon(@PathVariable String router) {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.icon(router));

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
		permissionService.delete(code);

	}

	/**
	 * Deleta o objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 */
	@PatchMapping("/apllyRulesPermissionProfile")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('CORE_PERMISSION_VIEWER')")
	public void apllyRulesPermissionProfile() {
		permissionService.associatePermisionProfile();

	}

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param permission
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_PERMISSION_INSERT')")
	public ResponseEntity<Permission> save(@RequestBody Permission permission, HttpServletResponse response) {
		Permission permissionSaved = permissionService.insert(permission);

		// adiciona no header do response a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, permissionSaved.getCode()));

		notify(formatTranslate("msg_a_permissao_[%s]_foi_c", permission.getKey()));

		return ResponseEntity.status(HttpStatus.CREATED).body(permissionSaved);

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param permission
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PERMISSION_UPDATE')")
	public ResponseEntity<Permission> edit(@PathVariable Long code, @RequestBody Permission permission) {
		permission = permissionService.update(code, permission);

		notify(formatTranslate("msg_a_permissao_[%s]_foi_e", permission.getKey()));

		return ResponseEntity.status(HttpStatus.OK).body(permission);

	}

	/* CUSTOM METHODS RESOURCE */

	/**
	 * Retorna a permissao baseado na rota
	 * TODO: Avaliar se esta sendo usado, se eh necessario
	 * Nao requer permissao de acesso
	 * @param router
	 * @return
	 */
	@GetMapping("/findByRouter/{router}")
	public PermissionVwr findByRouter(@PathVariable String router) {
		return PermissionVwr.build(permissionService.findByRouter(router));

	}

	/**
	 * Retorna lista de Permissoes do tipo MENU
	 * no Objeto MenuItemDTO para o Menu horizontal da UI
	 * Nao requer permissao de acesso
	 * @return
	 */
	@GetMapping("/menuItemsHorizontal/{lang}")
	public List<MenuItemDTO> menuItemsHorizontal(@PathVariable String lang) {
		return permissionService.menuItemsHorizontal(Lang.valueOf(lang));

	}

	/**
	 * Retorna lista de Permissoes do tipo MENU
	 * no Objeto MenuItemDTO para o menu Veritcal da UI
	 * A rota eh usada para achar a raiz do menu e mostrar somente os itens do grupo
	 * Rota = home, traz todos os menus
	 * Nao requer permissao de acesso
	 * @param router
	 * @return
	 */
	@GetMapping("/menuItemsVertical/{router}/{lang}")
	public List<MenuItemDTO> menuItemsVertical(@PathVariable String router, @PathVariable String lang) {
		return permissionService.menuItemsVertical(router, Lang.valueOf(lang));

	}

}
