
package com.suite.core.base;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import com.suite.app.repository.impl.SuiteRepository;

/**
 * Classe base Repository do Modulo Core
 * Estende a classe base Repository
 * @author Wenceslau
 *
 */
@Service
public abstract class RepositoryCore extends SuiteRepository {

	/*
	 * EntityManager do modulo core
	 * Configurado na classe CoreConfiguration
	 */
	@PersistenceContext(unitName = "coreEntityManagerFactory")
	protected EntityManager entityManager;

	/**
	 * Adiciona criterios padrao das entidade do tipo ModelCore
	 * @param filter
	 * @param predicates
	 * @param builder
	 * @param root
	 */
	protected <T> void addBaseCriteria(FilterCore filter, List<Predicate> predicates, CriteriaBuilder builder, Root<T> root) {

		if (filter.getCode() != null)
			predicates.add(builder.equal(root.get("code"), filter.getCode()));

		if (filter.getStatus() != null)
			predicates.add(builder.equal(root.get("status"), filter.getStatus()));

		if (filter.getCodBusinessUnit() != null)
			predicates.add(builder.or(builder.equal(root.get("businessUnit").get("code"), filter.getCodBusinessUnit()),
					builder.isNull(root.get("businessUnit"))));

	}

	@Override
	protected EntityManager getEntityManager() {

		return entityManager;

	}

	protected void detach(ModelCore entity) {

		entityManager.detach(entity);

	}

	protected void entityClear() {

		entityManager.clear();

	}

	protected ModelCore merge(ModelCore entity) {

		entityManager.persist(entity);
		return null;

	}

	protected void refresh(ModelCore entity) {

		// TODO, avaliar
		// entityManager.detach(entity);

	}

}
