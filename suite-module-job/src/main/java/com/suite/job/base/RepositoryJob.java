package com.suite.job.base;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.suite.app.repository.impl.SuiteRepository;

/**
 * 
 * @author Wenceslau
 *
 */
public abstract class RepositoryJob extends SuiteRepository {

	@PersistenceContext(unitName = "jobEntityManagerFactory")
	private EntityManager entityManager;

	/**
	 * Adiciona criterios padrao das entidade do tipo
	 * @param filter
	 * @param predicates
	 * @param builder
	 * @param root
	 */
	protected <T> void addBaseCriteria(FilterJob filter, List<Predicate> predicates, CriteriaBuilder builder, Root<T> root) {
		if (filter.getCode() != null)
			predicates.add(builder.equal(root.get("code"), filter.getCode()));

		if (filter.getStatus() != null)
			predicates.add(builder.equal(root.get("status"), filter.getStatus()));

		if (filter.getCodeBusinessUnit() != null)
			predicates.add(builder.equal(root.get("codeBusinessUnit"), filter.getCodeBusinessUnit()));

	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;

	}

}
