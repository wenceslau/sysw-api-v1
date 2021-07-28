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
import com.suite.core.model.resume.ProfileResume;
import com.suite.core.model.Profile;
import com.suite.core.model.viewer.ProfileVwr;
import com.suite.core.repository.filter.ProfileFilter;
import com.suite.core.service.ProfileService;

/**
 * EndPoint de acesso a recurso profile
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/profile")
public class ProfileResource extends ResourceCore {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ApplicationEventPublisher publisher;

	/* GET */

	/**
	 * Retorna uma lista de objetos ativos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @return
	 */
	@GetMapping("/listResume")
	@PreAuthorize("hasAuthority('CORE_PROFILE_LIST')")
	public List<ProfileResume> list() {
		ProfileFilter filter = new ProfileFilter();
		filter.setStatus(true);
		return ProfileResume.build(profileService.listByFilter(filter));

	}

	/**
	 * Retorna lista de perfil pela unidade de negocio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/listResumebyBusinessUnit/{code}")
	@PreAuthorize("hasAuthority('CORE_PROFILE_LIST')")
	public List<ProfileResume> listByBusinessUnit(@PathVariable Long code) {
		return ProfileResume.build(profileService.listByBusinessUnit(code));

	}

	/**
	 * Filtra lista de objetos
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param filter
	 * @return
	 */
	@GetMapping("/filter")
	@PreAuthorize("hasAuthority('CORE_PROFILE_VIEWER')")
	public List<ProfileVwr> filter(ProfileFilter filter) {
		return ProfileVwr.build(profileService.listByFilter(filter));

	}

	/**
	 * Retorna Objeto pelo codigo
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @return
	 */
	@GetMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PROFILE_VIEWER')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		// Retorna o recurso e retorna status OK
		return ResponseEntity.status(HttpStatus.OK).body(ProfileVwr.build(profileService.findByCode(code)));

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
		profileService.delete(code);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_a", formatTranslate("lbl_perfil"), code));

	}

	/**
	 * Insere recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param profile
	 * @param response
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('CORE_PROFILE_INSERT')")
	public ResponseEntity<ProfileVwr> save(@RequestBody Profile profile, HttpServletResponse response) {
		Profile profileSaved = profileService.insert(profile);

		// adiciona no header a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, profileSaved.getCode()));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_perfil"), profileSaved.getName()));

		return ResponseEntity.status(HttpStatus.CREATED).body(ProfileVwr.build(profileSaved));

	}

	/**
	 * Atualiza recurso no repositorio
	 * Notacao @PreAuthorize - Apenas usuarios com tal permissao tem acesso ao metodo
	 * @param code
	 * @param profile
	 * @return
	 */
	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('CORE_PROFILE_UPDATE')")
	public ResponseEntity<ProfileVwr> edit(@PathVariable Long code, @RequestBody Profile profile) {
		profile = profileService.update(code, profile);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_perfil"), profile.getName()));

		return ResponseEntity.status(HttpStatus.OK).body(ProfileVwr.build(profile));

	}

}
