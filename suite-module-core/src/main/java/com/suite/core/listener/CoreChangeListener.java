
package com.suite.core.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.suite.core.base.ModelCore;
import com.suite.core.model.UserActionCore;
import com.suite.core.service.UserActionCoreService;
import com.suite.core.util.UtilsCore;

/**
 * Classe listener dos metodos JPA Insert Update Delete
 * Eh disparado a cada acao ocorrida por esses metodos
 * As entidades precisam estar anotadas referenciado esta classe
 * @author Wenceslau
 */
public class CoreChangeListener {

	/*
	 * Esse service nao esta anotado com @utowired porque essa classe
	 * eh gerenciada pelo hibernate e mesmo que essa classe seja anotada
	 * com @service ou @component, quando o hibernate disparar eventos
	 * para ela, o contexto do spring nao sera encontrado.
	 * O objeto eh instanciado usando o metodo estatico do UtilsCore
	 * que procura no contexto do spring porque possui uma variavel estatica que
	 * contem o contexto
	 */
	private UserActionCoreService userActionCoreService;

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh persistida no repositorio
	 * Qualquer execcecao disparada nessse metodo causa roolback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostPersist
	public void postPersist(ModelCore baseCoreModel) {

		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionCoreService().createAndSaveUserAction(new UserActionCore(), baseCoreModel, "Insert");

	}

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh removida do repositorio
	 * Qualquer excecao disparada nesse metodo causa rollback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostRemove
	public void postRemove(ModelCore baseCoreModel) {

		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionCoreService().createAndSaveUserAction(new UserActionCore(), baseCoreModel, "Delete");

	}

	/**
	 * Evento sincrono disparado pelo hibernate quando uma entidade eh atualizada no repositorio
	 * Qualquer excecao disparada nesse metodo causa rollback de houver transacao no insert
	 * @param baseCoreModel
	 */
	@PostUpdate
	public void postUpdate(ModelCore core) {

		// Recupera a instancia do objeto do contexto da aplicacao, equivale ao autowired
		getUserActionCoreService().createAndSaveUserAction(new UserActionCore(), core, "Update");
		
		//System.err.println("Update ???? " + core.getClass() + "-" + core.getCode());

	}

	/**
	 * Evento sincrono disparado antes da remocao no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param core
	 */
	@PreRemove
	public void preRemove(ModelCore core) {

		// getCoreRulesService().checkDefaultSector();
	}

	/**
	 * Evento sincrono disparado antes da persistencia no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param core
	 */
	@PrePersist
	public void prePersist(ModelCore core) {

		//System.err.println("Persist ???? " + core.getClass() + "-" + core.getCode());

		// O getUserUpdate seta o user do contexto se ele nao existir
		//core.setUserUpdate(core.getUserUpdate());

	}

	/**
	 * Evento sincrono disparado antes da atualizacao no repositorio ser efetivada
	 * Qualquer excecao disparada nesse metodo nao conclui a execucao da remocao
	 * @param core
	 */
	@PreUpdate
	public void preUpdate(ModelCore core) {

		// int i = 1/0;
		//System.err.println("Update ???? " + core.getClass() + "-" + core.getCode());

		// O getUserUpdate seta o user do contexto se ele nao existir
		//core.setUserUpdate(core.getUserUpdate());

	}

	/**
	 * Procura no contexto o bean userActionCoreService
	 * Atribuido ao variaval de instancia da classe
	 * O bean eh procurado usando o contexto do spring que esta disponivel
	 * na classe UtilsCore. o Contexto foi atribuido a uma classe estatica
	 * no momento da inicializacao da aplicacao
	 * @return
	 */
	private UserActionCoreService getUserActionCoreService() {

		if (userActionCoreService == null)
			userActionCoreService = (UserActionCoreService) UtilsCore.getBean("userActionCoreService");

		return userActionCoreService;

	}

}
