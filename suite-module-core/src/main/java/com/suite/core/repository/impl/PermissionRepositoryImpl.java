package com.suite.core.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.util.StringUtils;
import com.suite.core.base.ModelCore;
import com.suite.core.base.RepositoryCore;
import com.suite.core.model.Permission;
import com.suite.core.repository.filter.PermissionFilter;
import com.suite.core.repository.query.PermissionRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class PermissionRepositoryImpl extends RepositoryCore implements PermissionRepositoryQuery {

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
	public List<Permission> filter(PermissionFilter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<Permission> out = prepareQuery(filter, Permission.class);

		// Retona a lista
		return out.typedQuery.getResultList();

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {

		PermissionFilter permissionFilter = (PermissionFilter) filter;

		List<Predicate> predicates = new ArrayList<>();

		addBaseCriteria(permissionFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(permissionFilter.getStrApplication()))
			predicates.add(builder.like(builder.lower(root.get("strApplication")),
					"%" + permissionFilter.getStrApplication().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getRole()))
			predicates.add(builder.like(builder.lower(root.get("role")), "%" + permissionFilter.getRole().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getDescription()))
			predicates.add(builder.like(builder.lower(root.get("description")),
					"%" + permissionFilter.getDescription().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getModule()))
			predicates.add(builder.like(builder.lower(root.get("module")), "%" + permissionFilter.getModule().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getComponent()))
			predicates.add(
					builder.like(builder.lower(root.get("component")), "%" + permissionFilter.getComponent().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getRoot()))
			predicates.add(builder.like(builder.lower(root.get("root")), "%" + permissionFilter.getRoot().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(permissionFilter.getApplication())) {

			// filtra a permissao se ela estiver na llista de app do BU
			List<String> parentList = Arrays.asList(permissionFilter.getApplication().split(","));
			Expression<String> parentExpression = root.get("application");
			predicates.add(parentExpression.in(parentList));

		}

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void executeQuery(String query) {

		Query q = entityManager.createNativeQuery(query);
		q.executeUpdate();

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}
}
