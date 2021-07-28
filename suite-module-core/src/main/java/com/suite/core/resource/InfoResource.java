
package com.suite.core.resource;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.dto.LoggedDTO;
import com.suite.core.base.ResourceCore;
import com.suite.core.service.BusinessUnitService;
import com.suite.core.service.InfoService;
import com.suite.core.service.UserService;

/**
 * Endpoint de acesso a Informacoes
 * TODO: Avaliar migrar par Controller
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/info")
public class InfoResource extends ResourceCore {

	@Autowired
	private InfoService infoService;

	@Autowired
	private UserService userService;

	@Autowired
	private BusinessUnitService businessUnitService;

	/**
	 * Usuarios online. Conectados no websocket endopoint /logged/{username}-{usercode}
	 * @param message
	 * @return
	 */
	@GetMapping("/logged")
	public Collection<LoggedDTO> logged(String message) {
		return infoService.logged();

	}

	/**
	 * Retorna informacoes sobre o usuario
	 * @param code do usuario
	 * @return
	 */
	@GetMapping("/user/{code}")
	public ResponseEntity<?> userInfo(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoUser(code));

	}

	/**
	 * Retorna informacoes sobre o ambiente
	 * @param code
	 * @return
	 */
	@GetMapping("/enviroment/{code}")
	public ResponseEntity<?> infoEnviorment(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoEnviorment(code));

	}

	/**
	 * Retorna informacoes sobre o sistema
	 * @param code
	 * @return
	 */
	@GetMapping("/system/{code}")
	public ResponseEntity<?> infoSystem(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoSystem());

	}

	/**
	 * Retorna informacoes sobre a entidade
	 * @param entity
	 * @param router
	 * @return
	 */
	@GetMapping("/model/{entity}/{router}")
	public ResponseEntity<?> infoModel(@PathVariable String entity, @PathVariable String router) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoModel(entity, router));

	}

	/**
	 * Retorna informacoes sobre a entidade
	 * @param entity
	 * @param router
	 * @return
	 */
	@GetMapping("/connection")
	public ResponseEntity<?> infoConnection() {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoConnection());

	}

	@PostMapping("/connection/close/{key}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void connectionClose(@PathVariable String key) {
		infoService.forceCloseConnection(key);

	}

	@PostMapping("/connection/roolback/{key}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void connectionRoolback(@PathVariable String key) {
		infoService.forceRoolback(key);

	}

	/**
	 * Retorna informacoes da licenca dessa BU e dessa App
	 * @param code
	 * @return
	 */
	@GetMapping("/license/{appUniqueId}/{license}")
	public ResponseEntity<?> infoLicense(@PathVariable String appUniqueId, @PathVariable String license) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoLicense(license, appUniqueId, ""));

	}

	/**
	 * 
	 * @param appUniqueId
	 * @param license
	 * @param machineInfo
	 * @return
	 */
	@GetMapping("/license/{appUniqueId}/{machineInfo}/{license}")
	public ResponseEntity<?> infoLicense(@PathVariable String appUniqueId, @PathVariable String license, @PathVariable String machineInfo) {
		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoLicense(license, appUniqueId, machineInfo));

	}

	@PostMapping("/infoStart1")
	public ResponseEntity<?> infoStart(@RequestBody String value) {
		value = rsaService.getRsa().dencrypt(value);

		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoStart(value));

	}

	@PostMapping("/infoStart2")
	public ResponseEntity<?> infoSector(@RequestBody String value) {
		value = rsaService.getRsa().dencrypt(value);

		// value = rsaTransporter.encrypt(infoService.infoSector(value).toString());

		return ResponseEntity.status(HttpStatus.OK).body(infoService.infoSector(value));

	}

	@PostMapping("/infoStart3")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetByEmail(@RequestBody String email) {
		email = rsaService.getRsa().dencrypt(email);

		userService.resetByEmail(email);

	}

	@PostMapping("/infoStart4")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sysMonkey(@RequestBody String value) {
		businessUnitService.setSysMonkey(value);

	}

}
