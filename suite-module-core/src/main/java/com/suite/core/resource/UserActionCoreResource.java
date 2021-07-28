
package com.suite.core.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.repository.UserActionFilter;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.UserActionCore;
import com.suite.core.model.viewer.UserActionVwr;
import com.suite.core.service.UserActionCoreService;

/**
 * EndPoint de acesso a recurso userAction
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/userAction")
public class UserActionCoreResource extends ResourceCore {

	@Autowired
	private UserActionCoreService userActionService;

	/**
	 * Retorna uma lista paginavel de historico de acao do usuario
	 * baseado no filtro
	 * MARK: Nao requer permissao
	 * @param filter
	 * @param pageable
	 * @return
	 */
	@GetMapping("/filter/pageable")
	public Page<?> filter(UserActionFilter filter, Pageable pageable) {
		Page<UserActionCore> listPg = userActionService.filter(filter, pageable, UserActionCore.class);

		if (filter.isResume())
			return new PageImpl<>(UserActionVwr.build(listPg.getContent()), listPg.getPageable(), listPg.getTotalElements());
		else
			return listPg;

	}

	/**
	 * Retorna uma lista de historico de acao do usuario
	 * baseado no filtro
	 * MARK: Nao requer permissao
	 * @param filter
	 * @param pageable
	 * @return
	 */
	@GetMapping("/filter")
	public List<?> filter(UserActionFilter filter) {
		List<UserActionCore> lst = userActionService.filter(filter, UserActionCore.class);

		if (filter.isResume())
			return UserActionVwr.build(lst);
		else
			return lst;

	}

}
