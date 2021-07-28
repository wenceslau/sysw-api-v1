package com.suite.app.base;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Pageable;

/**
 * Super Classe a ser estendida por todas as classes repository da suite
 * A representacao T eh a Entidade que sera usada no processo Ex: T pode ser
 * entidade User, ou Profile ou qualquer entidade
 * @author Wenceslau
 *
 */
public abstract class Repository extends Base {

	/**
	 * Inner Class Usada para armazenar internamente o objeto
	 * TypedQuery e os Precidate para realizar a consulta
	 * @author Wenceslau
	 *
	 * @param <T>
	 */
	protected class Output<T> {

		public Output() {}

		public TypedQuery<T> typedQuery;
		public Predicate[] predicates;

		public List<T> getResultList() {
			if (typedQuery != null)
				return typedQuery.getResultList();

			throw new RuntimeException("msg_a_query_nao_f_e_n_p");

		}

	}

	/**
	 * Metodo que prepara a query usando a implementacao do metodo createCriteria
	 * para executar a consulta. Retorna um Output com os dados do tipo T
	 * @param <T>
	 * @param filter
	 * @param classType
	 * @return
	 */
	protected <T> Output<T> prepareQuery(Filter filter, Class<T> classType) {
		// OldObjects de contrucado dos criterios de busca
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();

		// objeto a ser devolvido no criterio
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classType);

		// OldObjects de onde sera buscado os dados
		Root<T> root = criteriaQuery.from(classType);

		// Predicados com os criterios criados a partir do filter
		Predicate[] predicates = createCriteria(filter, criteriaBuilder, root);

		// Aplica o wher com os criterios do predicado
		criteriaQuery.where(predicates);

		addOrderBy(filter, criteriaBuilder, criteriaQuery, root);

		// Executa a query na base e tras os dados
		TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);

		applyMaxResults(filter, query);

		Output<T> out = new Output<T>();
		out.predicates = predicates;
		out.typedQuery = query;

		return out;

	}

	/**
	 * Realiza um Count na tabela correspondente ao Objeto tipo T
	 * @param <T>
	 * @param classType
	 * @return
	 */
	protected <T> Long count(Class<T> classType) {
		CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(classType)));

		return getEntityManager().createQuery(cq).getSingleResult();

	}

	/**
	 * Adiciona restricao de paginacao Define valores da pagincao para a query que
	 * sera exectuada
	 * @param query
	 * @param pageable
	 */
	protected <T> void addRestrictionPaginator(TypedQuery<T> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistroPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistroPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistroPorPagina);

	}

	/**
	 * Cria uma query com uma criteria para contar os registros da tabela Usa os
	 * preditados ja definidos previamente no filter TODO Avaliar se nao pode usar o
	 * count da prorpria lista AVALIAR!!!
	 * @param predicates
	 * @param Type
	 * @return
	 */
	protected <T> Long total(Predicate[] predicates, Class<T> Type) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<T> root = criteria.from(Type);

		criteria.where(predicates);
		criteria.select(builder.count(root));

		return getEntityManager().createQuery(criteria).getSingleResult();

	}

	/**
	 * Ordena a consulta qdo existir necessidade
	 * 
	 * @param filter
	 * @param builder
	 * @param criteriaQuery
	 * @param root
	 */
	protected <T> void addOrderBy(Filter filter, CriteriaBuilder builder, CriteriaQuery<T> criteriaQuery, Root<T> root) {
		interceptOrderBy(filter);

		if (filter.getOrderBy() != null) {

			List<Order> orderList = new ArrayList<>();

			// Verifica se tem mais de um campo
			String[] arr = filter.getOrderBy().split(",");

			//Percorre lista de colunas a ordenar
			for (String value : arr) {

				//Array q diz se tem desc or asc por coluna
				String[] vrOrd = value.split("\\|");

				//Se tem 2, tem definicao de desc ou asc
				if (vrOrd.length == 2) {
					
					//Pos 0 eh o nome da coluna e pos 1 eh asc ou desc

					// objeto.propriedade, verifica e tem propriedade objeto.propriedade
					String[] ar = vrOrd[0].split("\\.");

					if (ar.length == 2) {
						if (vrOrd[1].equals("desc"))
							orderList.add(builder.desc(root.get(ar[0]).get(ar[1])));
						else
							orderList.add(builder.asc(root.get(ar[0]).get(ar[1])));

					} else {
						if (vrOrd[1].equals("desc"))
							orderList.add(builder.desc(root.get(ar[0])));
						else
							orderList.add(builder.asc(root.get(ar[0])));
					}

				} else {

					// objeto.propriedade, verifica e tem propriedade
					String[] ar = value.split("\\.");

					// Verifica se a orde eh decrecente ou ascendente
					if (filter.getDesc() != null && filter.getDesc()) {

						// Tem duas posicao, tem objeto e propriedade, nao tem so propriedade
						if (ar.length == 2)
							orderList.add(builder.desc(root.get(ar[0]).get(ar[1])));
						else {
							orderList.add(builder.desc(root.get(ar[0])));
						}

					} else {

						// Duas posicao, objeto e propriedade, nao, so propriedade
						if (ar.length == 2)
							orderList.add(builder.asc(root.get(ar[0]).get(ar[1])));
						else
							orderList.add(builder.asc(root.get(ar[0])));

					}

				}

			}

			// adiciona no criteria a lista de ordenacao
			criteriaQuery.orderBy(orderList);

		}

	}

	/**
	 * @param filter
	 * @param query
	 */
	protected <T> void applyMaxResults(Filter filter, TypedQuery<T> query) {
		if (filter.getMaxResults() != null)
			query.setMaxResults(filter.getMaxResults());

	}

	/**
	 * Metodo abastrato que deve ser implementado Retorna o EntityManager da conexao
	 * @return
	 */
	protected abstract EntityManager getEntityManager();

	/**
	 * Metodo abstrato que deve ser implementado ele eh executado no prepareQuery
	 * desta classe e executara sempre o metodo implementado na classe filha
	 * @param filter
	 * @param builder
	 * @param root
	 * @return
	 */
	protected abstract <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root);

	/**
	 * Metodo abstrato que deve ser implementado para intercepar e adicionar
	 * no order by o code e o status se ele estiver vazio
	 * @param filter
	 * @param builder
	 * @param root
	 * @return
	 */
	protected abstract void interceptOrderBy(Filter filter);

	//
	//
	// //@Override
	// public <T> Page<T> filter(UserActionFilter filter, Pageable pageable, Class<T> classType) {
	// // Verificar se o predicates voltara preenchido
	// Output<T> out = prepareQuery(filter, classType);
	//
	// addRestrictionPaginator(out.typedQuery, pageable);
	//
	// Page<T> page = new PageImpl<>(out.typedQuery.getResultList(), pageable, total(out.predicates, classType));
	//
	// return page;
	// }
	//
	// //@Override
	// public <T> List<T> filter(UserActionFilter filter, Class<T> classType) {
	// // Verificar se o predicates voltara preenchido
	// Output<T> out = prepareQuery(filter, classType);
	//
	// List<T> list = out.typedQuery.getResultList();
	//
	// // Retona a lista
	// return list;
	// }
	//
	// //@Override
	// protected synchronized <T> Predicate[] createCriteria(Filter filters, CriteriaBuilder builder, Root<T> root) {
	//
	// UserActionFilter filter = (UserActionFilter) filters;
	//
	// List<Predicate> predicates = new ArrayList<>();
	//
	// // Parametro para o mes informado
	// if (filter.getCode() != null)
	// predicates.add(builder.equal(root.get("code"), filter.getCode()));
	//
	// if (!StringUtils.isEmpty(filter.getAction()))
	// predicates.add(builder.like(builder.lower(root.get("action")), "%" + filter.getAction().toLowerCase() + "%"));
	//
	// if (filter.getIdRecord() != null)
	// predicates.add(builder.equal(root.get("idRecord"), filter.getIdRecord()));
	//
	// if (!StringUtils.isEmpty(filter.getNameObject()))
	// predicates.add(builder.equal(builder.lower(root.get("nameObject")), filter.getNameObject().toLowerCase()));
	//
	// if (filter.getCodUserRecord() != null)
	// predicates.add(builder.equal(root.get("userRecordCode"), filter.getCodUserRecord()));
	//
	// if (filter.getCodeSectorUserLogged() != null)
	// predicates.add(builder.equal(root.get("codeSector"), filter.getCodeSectorUserLogged()));
	//
	// if (filter.getDateRecordStart() != null)
	// predicates.add(builder.greaterThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordStart()));
	//
	// if (filter.getDateRecordEnd() != null)
	// predicates.add(builder.lessThanOrEqualTo(root.get("dateRecord"), filter.getDateRecordEnd()));
	//
	// // Lista de predicados
	// return predicates.toArray(new Predicate[predicates.size()]);
	//
	// }

}
