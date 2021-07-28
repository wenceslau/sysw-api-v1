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
import com.suite.core.model.GlossaryTerm;
import com.suite.core.repository.filter.GlossaryTermFilter;
import com.suite.core.repository.query.GlossaryTermRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 *
 */
@Component
public class GlossaryTermRepositoryImpl extends RepositoryCore implements GlossaryTermRepositoryQuery {

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
	public List<GlossaryTerm> filter(GlossaryTermFilter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<GlossaryTerm> out = prepareQuery(filter, GlossaryTerm.class);

		// Retona a lista
		return out.typedQuery.getResultList();

	}
	
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
	public Page<GlossaryTerm> filter(GlossaryTermFilter filter, Pageable pageable) {

		// Verificar se o predicates voltara preenchido
		Output<GlossaryTerm> out = prepareQuery(filter, GlossaryTerm.class);

		addRestrictionPaginator(out.typedQuery, pageable);

		return new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, GlossaryTerm.class));

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {

		GlossaryTermFilter termosGlossarioFilter = (GlossaryTermFilter) filter;

		List<Predicate> predicates = new ArrayList<>();

		addBaseCriteria(termosGlossarioFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(termosGlossarioFilter.getName()))
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + termosGlossarioFilter.getName().toLowerCase() + "%"));


		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}
}
