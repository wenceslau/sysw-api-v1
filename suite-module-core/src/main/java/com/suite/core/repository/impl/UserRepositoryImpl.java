
package com.suite.core.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.util.StringUtils;
import com.suite.core.base.ModelCore;
import com.suite.core.base.RepositoryCore;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.repository.filter.UserFilter;
import com.suite.core.repository.query.UserRepositoryQuery;
import com.suite.core.service.SectorService;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class UserRepositoryImpl extends RepositoryCore implements UserRepositoryQuery {

	@Autowired
	private SectorService sectorService;

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
	public List<User> filter(UserFilter filter) {

		// Verificar se o predicates voltara preenchido
		Output<User> out = prepareQuery(filter, User.class);

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

		List<Predicate> predicates = new ArrayList<>();

		UserFilter userFilter = (UserFilter) filter;

		addBaseCriteria(userFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(userFilter.getName()))
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + userFilter.getName().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(userFilter.getUsername()))
			predicates.add(builder.like(builder.lower(root.get("username")), "%" + userFilter.getUsername().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(userFilter.getEmail()))
			predicates.add(builder.like(builder.lower(root.get("email")), "%" + userFilter.getEmail().toLowerCase() + "%"));

		// MARK O filtro por tipo de perfil usa o conceito de maior ou igual...
		// MARK quanto menor o tipo maior o grau de acesso
		if (userFilter.getTypeProfile() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get("profile").get("type"), userFilter.getTypeProfile()));

		// Parametro para que é um objeto, encontra pelo id informado
		if (userFilter.getCodProfile() != null)
			predicates.add(builder.equal(root.get("profile").get("code"), userFilter.getCodProfile()));

		if (!StringUtils.isEmpty(userFilter.getCodSector())) {
			Sector sector = sectorService.findByIdOrNull(userFilter.getCodSector());
			// Passa a lista de permissao encontrada para o predicado
			predicates.add(builder.isMember(sector, root.get("sectors")));

		}
		
		predicates.add(builder.equal(root.get("businessUnit").get("status"), true));


		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}

}
