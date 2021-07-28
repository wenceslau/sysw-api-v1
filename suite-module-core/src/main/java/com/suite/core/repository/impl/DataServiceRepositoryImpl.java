
package com.suite.core.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.util.StringUtils;
import com.suite.core.base.ModelCore;
import com.suite.core.base.RepositoryCore;
import com.suite.core.model.DataService;
import com.suite.core.repository.filter.DataServiceFilter;
import com.suite.core.repository.query.DataServiceRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class DataServiceRepositoryImpl extends RepositoryCore implements DataServiceRepositoryQuery {

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
	public List<DataService> filter(DataServiceFilter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<DataService> out = prepareQuery(filter, DataService.class);

		// Retona a lista
		return out.getResultList();

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filters, CriteriaBuilder builder, Root<T> root) {

		// Cast do filter base para o filter da entidade
		DataServiceFilter datasServiceFilter = (DataServiceFilter) filters;

		List<Predicate> predicates = new ArrayList<>();

		// Adiciona criterios bases comum a todas entidades do modulo
		addBaseCriteria(datasServiceFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(datasServiceFilter.getName()))
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + datasServiceFilter.getName().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(datasServiceFilter.getScope()))
			predicates.add(builder.like(builder.lower(root.get("scope")), "%" + datasServiceFilter.getScope().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(datasServiceFilter.getType()))
			predicates.add(builder.equal(root.get("type"), datasServiceFilter.getType()));

		if (datasServiceFilter.getCodBusinessUnit() != null)
			predicates.add(builder.equal(root.get("businessUnit").get("code"), datasServiceFilter.getCodBusinessUnit()));
		
		predicates.add(builder.equal(root.get("businessUnit").get("status"), true));


		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}

}
