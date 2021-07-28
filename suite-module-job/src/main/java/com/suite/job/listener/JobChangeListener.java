
package com.suite.job.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.suite.core.util.UtilsCore;
import com.suite.job.base.ModelJob;
import com.suite.job.model.UserActionJob;
import com.suite.job.service.UserActionJobService;

/**
 * Classe listener dos metodos JPA Insert Update Delete
 * Eh disparado a cada acao ocorrida por esses metodos
 * As entidades precisam estar anotadas referenciado esta classe
 * @author Wenceslau
 */
public class JobChangeListener {

	/*
	 * Esse service nao esta anotado com @utowired porque essa classe
	 * eh gerenciada pelo hibernate e mesmo que essa classe seja anotada
	 * com @service ou @component, quando o hibernate disparar eventos
	 * para ela, o contexto do spring nao sera encontrado.
	 * O objeto eh instanciado usando o metodo estatico do UtilsCore
	 * que procura no contexto do spring porque possui uma variavel estatica que
	 * contem o contexto
	 */
	private UserActionJobService userActionService;

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh persistida no repositorio
	 * Qualquer execcecao disparada nessse metodo causa roolback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostPersist
	public void postPersist(ModelJob model) {
		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionService().createAndSaveUserAction(new UserActionJob(), model, "Insert");

	}

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh removida do repositorio
	 * Qualquer excecao disparada nesse metodo causa rollback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostRemove
	public void postRemove(ModelJob model) {
		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionService().createAndSaveUserAction(new UserActionJob(), model, "Delete");

	}

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh atualizada no repositorio
	 * Qualquer excecao disparada nesse metodo causa rollback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostUpdate
	public void postUpdate(ModelJob model) {
		//System.out.println("postUpdate USR: "+model.getCodeUserRecord());
	
		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionService().createAndSaveUserAction(new UserActionJob(), model, "Update");

		// System.err.println("Update ???? " + job.getClass() + "-" + job.getCode());

	}

	/**
	 * Evento sincrono disparado antes da remocao no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param userJob
	 */
	@PreRemove
	public void preRemove(ModelJob model) {
		// getCoreRulesService().checkDefaultSector();
	}

	/**
	 * Evento sincrono disparado antes da persistencia no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param userJob
	 */
	@PrePersist
	public void prePersist(ModelJob model) {
		//System.out.println("prePersist USR: "+model.getCodeUserRecord());
		// System.err.println("Persist ???? " + job.getClass() + "-" + job.getCode());

		// O getUserUpdate seta o user do contexto se ele nao existir
		//model.setUserUpdate(model.getUserUpdate());

	}

	/**
	 * Evento sincrono disparado antes da atualizacao no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param userJob
	 */
	@PreUpdate
	public void preUpdate(ModelJob model) {
		//System.out.println("preUpdate USR: "+model.getCodeUserRecord());
		// int i = 1/0;
		// System.err.println("Update ???? " + job.getClass() + "-" + job.getCode());

		// O getUserUpdate seta o user do contexto se ele nao existir
		//model.setUserUpdate(model.getUserUpdate());

	}

	/**
	 * Procura no contexto o bean userActionCoreService
	 * Atribuido ao variaval de instancia da classe
	 * O bean eh procurado usando o contexto do spring que esta disponivel
	 * na classe UtilsCore. o Contexto foi atribuido a uma classe estatica
	 * no momento da inicializacao da aplicacao
	 * @return
	 */
	private UserActionJobService getUserActionService() {
		if (userActionService == null)
			userActionService = (UserActionJobService) UtilsCore.getBean("userActionJobService");

		return userActionService;

	}

}
