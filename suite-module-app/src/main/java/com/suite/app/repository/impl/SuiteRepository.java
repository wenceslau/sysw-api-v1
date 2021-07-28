package com.suite.app.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.suite.app.base.Filter;
import com.suite.app.base.Repository;
import com.suite.app.repository.UserActionFilter;
import com.suite.app.repository.UserActionRepositoryQuery;
import com.suite.app.util.StringUtils;

public abstract class SuiteRepository extends Repository implements UserActionRepositoryQuery {

	/**
	 * The methods below is implemeented from interface UserActionRepositoryQuery
	 * It's use for all user action from suite that extends Class UserAction
	 */

	@Override
	public <T> Long count(Class<T> classType) {
		return super.count(classType);

	}

	@Override
	public <T> List<T> filter(UserActionFilter filter, Class<T> classType) {
		// Verificar se o predicates voltara preenchido
		Output<T> out = prepareQuery(filter, classType);

		List<T> list = out.typedQuery.getResultList();

		// Retona a lista
		return list;
	}

	@Override
	public <T> Page<T> filter(UserActionFilter filter, Pageable pageable, Class<T> classType) {
		// Verificar se o predicates voltara preenchido
		Output<T> out = prepareQuery(filter, classType);

		addRestrictionPaginator(out.typedQuery, pageable);

		Page<T> page = new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, classType));

		return page;
	}
		
	@Override
	protected synchronized <T> Predicate[] createCriteria(Filter filters, CriteriaBuilder builder, Root<T> root) {
		UserActionFilter filter = (UserActionFilter) filters;

		List<Predicate> predicates = new ArrayList<>();

		// Parametro para o mes informado
		if (filter.getCode() != null)
			predicates.add(builder.equal(root.get("code"), filter.getCode()));

		if (!StringUtils.isEmpty(filter.getAction()))
			predicates.add(builder.like(builder.lower(root.get("action")), "%" + filter.getAction().toLowerCase() + "%"));

		if (filter.getIdRecord() != null)
			predicates.add(builder.equal(root.get("idRecord"), filter.getIdRecord()));

		if (!StringUtils.isEmpty(filter.getNameObject()))
			predicates.add(builder.equal(builder.lower(root.get("nameObject")), filter.getNameObject().toLowerCase()));

		if (filter.getCodUserRecord() != null)
			predicates.add(builder.equal(root.get("userRecordCode"), filter.getCodUserRecord()));

		if (filter.getCodeSectorUserLogged() != null)
			predicates.add(builder.equal(root.get("codeSector"), filter.getCodeSectorUserLogged()));

		if (filter.getDateRecordStart() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordStart()));

		if (filter.getDateRecordEnd() != null)
			predicates.add(builder.lessThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordEnd()));

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	protected void interceptOrderBy(Filter filter) {
		if (filter.getOrderBy() == null || filter.getOrderBy().isEmpty()) {
			filter.setOrderBy("status|desc,code|asc");
			filter.setDesc(true);
		}

	}
}
