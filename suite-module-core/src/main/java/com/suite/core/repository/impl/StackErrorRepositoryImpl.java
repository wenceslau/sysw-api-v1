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
import com.suite.core.model.StackError;
import com.suite.core.repository.filter.StackErrorFillter;
import com.suite.core.repository.query.StackErrorRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class StackErrorRepositoryImpl extends RepositoryCore implements StackErrorRepositoryQuery {

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
	public List<StackError> filter(StackErrorFillter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<StackError> out = prepareQuery(filter, StackError.class);

		// Retona a lista
		return out.typedQuery.getResultList();

	}

	/**
	 * Metodo da interface {Entidade}RepositoryQuery.
	 * Possui o mesmo conceito do metodo filter, recebendo um novo parametro, objeto Pageable
	 * 
	 * Devolve uma objeto Page que contem uma lista paginada
	 */
	@Override
	public Page<StackError> filter(StackErrorFillter filter, Pageable pageable) {

		// Verificar se o predicates voltara preenchido
		Output<StackError> out = prepareQuery(filter, StackError.class);

		// Metodoo da super classe que adiciona parametros da paginacao
		addRestrictionPaginator(out.typedQuery, pageable);

		// Retorna o objeto page, Metodo total da super classe realiza o count da consulta com os mesmo criterios
		return new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, StackError.class));

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {

		// Cast do filter base para o filter da entidade
		StackErrorFillter stackErrorFillter = (StackErrorFillter) filter;

		List<Predicate> predicates = new ArrayList<>();

		// Adiciona criterios bases comum a todas entidades do modulo
		addBaseCriteria(stackErrorFillter, predicates, builder, root);

		if (stackErrorFillter.getCodeSector() != null)
			predicates.add(builder.equal(root.get("codeSector"), stackErrorFillter.getCodeSector()));

		if (!StringUtils.isEmpty(stackErrorFillter.getMessage()))
			predicates.add(
					builder.like(builder.lower(root.get("message")), "%" + stackErrorFillter.getMessage().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(stackErrorFillter.getCauses()))
			predicates
					.add(builder.like(builder.lower(root.get("causes")), "%" + stackErrorFillter.getCauses().toLowerCase() + "%"));

		if (stackErrorFillter.getCodUser() != null)
			predicates.add(builder.equal(root.get("userCode"), stackErrorFillter.getCodUser()));

		if (stackErrorFillter.getStartDateTimeError() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get("dateTimeError"), stackErrorFillter.getStartDateTimeError()));

		if (stackErrorFillter.getEndDateTimeError() != null)
			predicates.add(builder.lessThanOrEqualTo(root.get("dateTimeError"), stackErrorFillter.getEndDateTimeError()));

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	protected void interceptOrderBy(Filter filter) {
		//For StackError this method do nothing... but need to be implemnted
		//The entity StackError does not have property Status
	}
	
	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}
}
