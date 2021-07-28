
package com.suite.core.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.core.base.ResourceCore;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.filter.UserLoginHistoryFilter;
import com.suite.core.service.UserLogonHistoryService;

/**
 * EndPoint de acesso a recurso userLogonHistory
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/userLogonHistory")
public class UserLogonHistoryResource extends ResourceCore {

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	/**
	 * Retorna lista paginada de logon de usuario
	 * baseado no filtro
	 * MARK: Nao requer permissao
	 * @param filter
	 * @param pageable
	 * @return
	 */

	@GetMapping("/filter/pageable")
	public Page<UserLogonHistory> filter(UserLoginHistoryFilter filter, Pageable pageable) {
		return userLogonHistoryService.listByFilter(filter, pageable);

	}

	@GetMapping("/filter/")
	public List<UserLogonHistory> filter(UserLoginHistoryFilter filter) {
		return userLogonHistoryService.listByFilter(filter);

	}

	/**
	 * Retorna Lista UserLogonHistory em formato
	 * objeto ChartLogonDTO com os dados de logon de usuarios
	 * MARK: Nao requer permissao
	 * @return
	 */
	@GetMapping("/chartLogon")
	public ResponseEntity<?> chartLogonByDay() {
		return ResponseEntity.status(HttpStatus.OK).body(userLogonHistoryService.chartLogonByDay("MOBILE,BROWSER"));

	}

}
