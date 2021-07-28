package com.suite.core.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.util.StringUtils;
import com.suite.core.base.RepositoryCore;
import com.suite.core.dto.LogonDTO;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.filter.UserLoginHistoryFilter;
import com.suite.core.repository.query.UserLogonHistoryRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class UserLogonHistoryRepositoryImpl extends RepositoryCore implements UserLogonHistoryRepositoryQuery {

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
	public Page<UserLogonHistory> filter(UserLoginHistoryFilter filter, Pageable pageable) {

		// Verificar se o predicates voltara preenchido
		Output<UserLogonHistory> out = prepareQuery(filter, UserLogonHistory.class);

		addRestrictionPaginator(out.typedQuery, pageable);

		return new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, UserLogonHistory.class));

	}

	/**
	 * Metodo da interface {Entidade}RepositoryQuery.
	 * Possui o mesmo conceito do metodo filter, recebendo um novo parametro, objeto Pageable
	 * 
	 * Devolve uma objeto Page que contem uma lista paginada
	 */
	@Override
	public List<UserLogonHistory> filter(UserLoginHistoryFilter filter) {

		// Verificar se o predicates voltara preenchido
		Output<UserLogonHistory> out = prepareQuery(filter, UserLogonHistory.class);

		return out.typedQuery.getResultList();

	}

	/**
	 * Metodo impelementado da interface
	 */
	@Override
	public List<LogonDTO> listLogonByDay(Long codeBusinessUnit, Integer days, String device) {

		CriteriaBuilder builder;
		CriteriaQuery<LogonDTO> query;
		Root<UserLogonHistory> root;

		// Objeto de contrucado dos criterios de busca
		builder = getEntityManager().getCriteriaBuilder();

		// objeto a ser devolvido no criterio
		query = builder.createQuery(LogonDTO.class);

		// Objeto de onde sera buscado os dados
		root = query.from(UserLogonHistory.class);

		// Constroi o objeto a ser devolvido e passa os parametros desse objeto a partir
		// dos dados do objeto de busca na base
		query.select(builder.construct(LogonDTO.class, root.get("dateLogon"), root.get("statusLogon"),
				builder.count(root.get("statusLogon"))));

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.greaterThanOrEqualTo(root.get("dateLogon"), LocalDate.now().plusDays(days * -1)));

		if (codeBusinessUnit != null)
			predicates.add(builder.equal(root.get("userRecord").get("businessUnit").get("code"), codeBusinessUnit));

//		if (device != null)
//			predicates.add(builder.like(builder.lower(root.get("device")), "%" + device.toLowerCase() + "%"));

		query.where(predicates.toArray(new Predicate[predicates.size()]));

		// Agrupamento da consulta
		query.groupBy(root.get("dateLogon"), root.get("statusLogon"));

		TypedQuery<LogonDTO> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getResultList();

	}

	/**
	 * Metodo que crias os creterios de filtro.
	 * Executado pelo metodo prepareQuery na superclass
	 * Cada classe deve implementar esse metodo usando seu fitro.
	 */
	@Override
	protected <T> Predicate[] createCriteria(Filter filters, CriteriaBuilder builder, Root<T> root) {

		UserLoginHistoryFilter filter = (UserLoginHistoryFilter) filters;
		List<Predicate> predicates = new ArrayList<>();

		if (filter.getTypeProfile() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get("userRecord").get("profile").get("type"), filter.getTypeProfile()));

		if (filter.getCodBusinessUnit() != null)
			predicates.add(builder.equal(root.get("userRecord").get("businessUnit").get("code"), filter.getCodBusinessUnit()));

		if (filter.getCode() != null)
			predicates.add(builder.equal(root.get("code"), filter.getCode()));

		if (!StringUtils.isEmpty(filter.getIpAddress()))
			predicates.add(builder.like(builder.lower(root.get("ipAddress")), "%" + filter.getIpAddress().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(filter.getStatusLogon()))
			predicates.add(builder.like(builder.lower(root.get("statusLogon")), "%" + filter.getStatusLogon().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(filter.getUserLogon()))
			predicates.add(builder.like(builder.lower(root.get("userLogon")), "%" + filter.getUserLogon().toLowerCase() + "%"));

		if (!StringUtils.isEmpty(filter.getDevice()))
			predicates.add(builder.like(builder.lower(root.get("device")), "%" + filter.getDevice().toLowerCase() + "%"));

		if (filter.getCodUserRecord() != null)
			predicates.add(builder.equal(root.get("userRecord").get("code"), filter.getCodUserRecord()));

		if (filter.getDateRecordStart() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordStart()));

		if (filter.getDateRecordEnd() != null)
			predicates.add(builder.lessThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordEnd()));

		if (!StringUtils.isEmpty(filter.getDeviceIn())) {

			// filtra a permissao se ela estiver na llista de app do BU
			List<String> parentList = Arrays.asList(filter.getDeviceIn().split(","));
			Expression<String> parentExpression = root.get("device");
			predicates.add(parentExpression.in(parentList));

		}

		// Lista de preditados
		return predicates.toArray(new Predicate[predicates.size()]);

	}
	
}