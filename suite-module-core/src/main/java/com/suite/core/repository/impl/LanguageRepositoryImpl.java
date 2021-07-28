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
import com.suite.core.model.Language;
import com.suite.core.repository.filter.LanguageFilter;
import com.suite.core.repository.query.LanguageRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class LanguageRepositoryImpl extends RepositoryCore implements LanguageRepositoryQuery {

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
	public List<Language> filter(LanguageFilter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<Language> out = prepareQuery(filter, Language.class);

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
	public Page<Language> filter(LanguageFilter filter, Pageable pageable) {

		// Verificar se o predicates voltara preenchido
		Output<Language> out = prepareQuery(filter, Language.class);

		addRestrictionPaginator(out.typedQuery, pageable);

		return new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, Language.class));

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {

		LanguageFilter languageFilter = (LanguageFilter) filter;

		List<Predicate> predicates = new ArrayList<>();

		addBaseCriteria(languageFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(languageFilter.getKey()))
			predicates.add(builder.like(builder.lower(root.get("key")),
					"%" + languageFilter.getKey().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(languageFilter.getPortugues()))
			predicates.add(builder.like(builder.lower(root.get("portugues")), 
					"%" + languageFilter.getPortugues().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(languageFilter.getEnglish()))
			predicates.add(builder.like(builder.lower(root.get("english")),
					"%" + languageFilter.getEnglish().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(languageFilter.getSpanish()))
			predicates.add(builder.like(builder.lower(root.get("spanish")), 
					"%" + languageFilter.getSpanish().toLowerCase() + "%"));

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}
}
