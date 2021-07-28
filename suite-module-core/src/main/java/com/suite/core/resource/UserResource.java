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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.dto.BoolDTO;
import com.suite.app.listener.ResourceCreatedEvent;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.resume.UserResume;
import com.suite.core.model.User;
import com.suite.core.model.viewer.UserVwr;
import com.suite.core.repository.filter.UserFilter;
import com.suite.core.service.UserService;

/**
 * EndPoint de acesso a recurso user
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/user")
public class UserResource extends ResourceCore {

	@Autowired
	private UserService userService;

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
	public List<UserResume> listResume() {
		UserFilter filter = new UserFilter();
		filter.setStatus(true);
		List<UserResume> lst = UserResume.build(userService.listByFilter(filter));
		lst.sort((p1, p2) -> p1.getUsername().compareTo(p2.getUsername()));
		return lst;

	}

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_USER_VIEWER')")
	public List<UserVwr> filter(UserFilter filter) {
		return UserVwr.build(userService.listByFilter(filter));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_USER_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(UserVwr.build(userService.findByCode(code)));

	}

	/* POST PUT DELETE */

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param user
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_USER_INSERT')")
	public ResponseEntity<UserVwr> save(@RequestBody User user, HttpServletResponse response) {
		user = userService.insert(user);

		// adiciona no header a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, user.getCode()));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_usuario"), user.getName()));

		return ResponseEntity.status(HttpStatus.CREATED).body(UserVwr.build(user));

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param user
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_USER_UPDATE')")
	public ResponseEntity<UserVwr> edit(@PathVariable Long code, @RequestBody User user) {
		UserVwr userVwr = UserVwr.build(userService.update(code, user));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_usuario"), user.getName()));

		return ResponseEntity.status(HttpStatus.OK).body(userVwr);

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
		userService.delete(code);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_a", formatTranslate("lbl_usuario"), code));

	}

	/**
	 * Reseta a senha do Usuario
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 */
	@PutMapping("/resetByCode/{code}/{type}")
	@PreAuthorize("hasAuthority('CORE_PASSWORD_RESET')")
	public ResponseEntity<BoolDTO> resetByCode(@PathVariable Long code, @PathVariable Integer type) {
		notify(formatTranslate("msg_a_senha_do_u_i_[%s]_f_r", code));

		return ResponseEntity.status(HttpStatus.OK).body(userService.resetByCode(code, type));

	}

	/**
	 * Ativa ou desavita o recebimento de notificacoes
	 * TODO: Avaliar se eh necessario. Pode esta depreciado
	 * @param code
	 */
	@PutMapping("/changeReceiveNotify")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changeReceiveNotify(@RequestParam Long code) {
		userService.changeReceiveNotify(code);

	}

	/**
	 * Altera a senha do usuario
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * TODO: Avaliar receber o valor criptografado
	 * o valor eh recebido no corpo e nao na url
	 * @param value
	 */
	@PostMapping("/changeByValue")
	@PreAuthorize("hasAuthority('CORE_PASSWORD_CHANGE')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changeByValue(@RequestBody String value) {
		notify(formatTranslate("msg_o_usuario_[%s]_a_s_s", getCodeUserContext()));

		userService.changeByValue(value);

	}

}
