
package com.suite.core.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.util.StringUtils;
import com.suite.core.base.ModelCore;
import com.suite.core.base.RepositoryCore;
import com.suite.core.model.UserGroup;
import com.suite.core.repository.filter.UserGroupFilter;
import com.suite.core.repository.query.UserGroupRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class UserGroupRepositoryImpl extends RepositoryCore implements UserGroupRepositoryQuery {

	/**
	 * Metodo da interface {Entidade}RepositoryQuery.
	 * Executa uma consulta na base de dados usando Objeto Filter da entidade como filtro de dados.
	 * 
	 * O metodo da superclasse Repository prepareQuery() eh chamado
	 * passando o filtro e ClassType como parametro.
	 * 
	 * Ele prepera e realiza a consulta no banco.
	 * 
	 * O retorno Output<ClassType> disponibiliza um ResultList, contendo resultado da consulta
	 */
	@Override
	public List<UserGroup> filter(UserGroupFilter filter) {

		// Verificar se o predicates voltara preenchido
		Output<UserGroup> out = prepareQuery(filter, UserGroup.class);

		// Retona a lista
		return out.typedQuery.getResultList();

	}

	@Override
	public Page<UserGroup> filter(UserGroupFilter filter, Pageable pageable) {

		// Verificar se o predicates voltara preenchido
		Output<UserGroup> out = prepareQuery(filter, UserGroup.class);

		addRestrictionPaginator(out.typedQuery, pageable);

		return new PageImpl<UserGroup>(out.typedQuery.getResultList(), pageable, total(out.predicates, UserGroup.class));

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {

		List<Predicate> predicates = new ArrayList<>();

		UserGroupFilter groupFilter = (UserGroupFilter) filter;

		addBaseCriteria(groupFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(groupFilter.getName()))
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + groupFilter.getName().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(groupFilter.getDescription()))
			predicates.add(builder.like(builder.lower(root.get("description")), "%" + groupFilter.getDescription().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(groupFilter.getDescription()))
			predicates.add(builder.like(builder.lower(root.get("description")), "%" + groupFilter.getDescription().toLowerCase() + "%"));

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}

	@Override
	public void detachAll() {

		entityClear();

	}

}
