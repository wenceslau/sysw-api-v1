package com.suite.core.repository.impl;

import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.repository.UserActionRepositoryQuery;
import com.suite.core.base.RepositoryCore;

/**
 * Classe de implementacao da Interface UserActionRepositoryQuery do Modulo App
 * A interface UserActionCoreRepository estende a interface JpaRepository(Spring) e a UserActionRepositoryQuery do Modulo App
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * 
 * Todas as classes UserAction{Modulo}Repository dos Modulos deve implementar a interface UserActionRepositoryQuery
 * do modulo App. Ela expoe os metodos que precisam ser implementados.
 * @author Wenceslau
 *
 */
@Component
public class UserActionCoreRepositoryImpl extends RepositoryCore implements UserActionRepositoryQuery {

	@Override
	protected void interceptOrderBy(Filter filter) {
		//For UserAction this method do nothing... but need to be implemnted
		//The entity UserAction does not have property Status

	}

}
