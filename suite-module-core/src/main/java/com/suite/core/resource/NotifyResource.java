package com.suite.core.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.core.base.ResourceCore;
import com.suite.core.model.Notify;
import com.suite.core.service.NotifyService;

/**
 * Endpoint de acesso as notificacoes enviadas
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/notify")
public class NotifyResource extends ResourceCore {

	@Autowired
	private NotifyService notifyService;

	/**
	 * Lista ultimas notificacoes recebidas do usuario
	 * Apenas noticacoes enviadas para setores do usuario logado
	 * Notify nao tem permissao, a permissao eh definida no usuario
	 * @return
	 */
	@RequestMapping
	public List<Notify> listLatestNotify() {
		return notifyService.listLatestNotify();

	}

}
