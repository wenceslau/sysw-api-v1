
package com.suite.job.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.repository.UserActionFilter;
import com.suite.core.base.ResourceCore;
import com.suite.job.model.UserActionJob;
import com.suite.job.service.UserActionJobService;

/**
 * EndPoint de acesso a recurso userAction
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/userActionJob")
public class UserActionJobResource extends ResourceCore {

	@Autowired
	private UserActionJobService userActionService;

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
		Page<UserActionJob> listPg = userActionService.filter(filter, pageable, UserActionJob.class);

		// if (filter.isResume())
		// return new PageImpl<>(UserActionVwr.build(listPg.getContent()), listPg.getPageable(), listPg.getTotalElements());
		// else
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
		List<UserActionJob> lst = userActionService.filter(filter, UserActionJob.class);

		// if (filter.isResume())
		// return UserActionVwr.build(lst);
		// else
		return lst;

	}

}
