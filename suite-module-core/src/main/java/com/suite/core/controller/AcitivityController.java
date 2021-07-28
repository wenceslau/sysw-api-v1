//package com.suite.core.controller;
//
//import java.util.Collection;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.suite.app.base.Controller;
//import com.suite.app.dto.LoggedDTO;
//import com.suite.core.model.StackError;
//import com.suite.core.repository.filter.StackErrorFillter;
//import com.suite.core.service.ActivityService;
//
///**
// * Classe que expoe o endpoint Rest para acesso a dados de atividades da suite
// * @author Wenceslau
// *
// */
//@RestController
//@RequestMapping("/activity")
//public class AcitivityController extends Controller {
//
//	@Autowired
//	private ActivityService activityService;
//
//
//	/**
//	 * Usuarios online. Conectados no websocket endopoint /logged/{username}-{usercode}
//	 * @param message
//	 * @return
//	 */
//	@GetMapping("/logged")
//	public Collection<LoggedDTO> logged(String message) {
//
//		return activityService.logged();
//
//	}
//
//	/**
//	 * Lista paginada de execoes lanacadas pelo sistema que estao armazenada na entidade StackError
//	 * @param filter
//	 * @param pageable
//	 * @return
//	 */
//	@GetMapping("/stackError")
//	public Page<StackError> stackError(StackErrorFillter filter, Pageable pageable) {
//
//		return activityService.stackError(filter, pageable);
//
//	}
//
//	@Override
//	protected String formatTranslate(String key, Object... args) {
//
//		return activityService.formatTranslate(key, args);
//
//	}
//
//}
