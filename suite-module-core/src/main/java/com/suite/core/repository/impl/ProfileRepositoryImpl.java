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
import com.suite.core.model.Profile;
import com.suite.core.repository.filter.ProfileFilter;
import com.suite.core.repository.query.ProfileRepositoryQuery;

/**
 * Classe de implementacao da Interface {Entidade}RepositoryQuery
 * A interface {Entidade}Repository estende a interface JpaRepository(Spring) e a {Entidade}RepositoryQuery(Modulo)
 * entao quando se usa a interface {Entidade}Repository se tem acesso aos metodos implementados aqui
 * @author Wenceslau
 *
 */
@Component
public class ProfileRepositoryImpl extends RepositoryCore implements ProfileRepositoryQuery {

//	@Autowired
//	private PermissionService permissionService;

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
	public List<Profile> filter(ProfileFilter filter) {

		// Prepara e executa a query. Internamente o metodo executa o metodo abtratrado implementado createCriteria
		Output<Profile> out = prepareQuery(filter, Profile.class);

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

		ProfileFilter profileFilter = (ProfileFilter) filter;

		List<Predicate> predicates = new ArrayList<>();

		addBaseCriteria(profileFilter, predicates, builder, root);

		if (!StringUtils.isEmpty(profileFilter.getName()))
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + profileFilter.getName().toLowerCase() + "%"));

		if (profileFilter.getType() != null)
			predicates.add(builder.equal(root.get("type"), profileFilter.getType()));

//O FILTRO ESTA SENDO FEITO NO SERVICE , MANUALMENTE NA LISTA E NAO NO BANCO
//		if (!StringUtils.isEmpty(profileFilter.getCodPermissions())) {
//
//			//Recupera a permissao pelo codigo
//			//Optional<Permission> optionalPermission = permissionService.findById(profileFilter.getCodPermissions());
//			// coloca no filtro a permissao e verifica se ela contem na lista de permmissao do perfil
//			//predicates.add(builder.isMember(optionalPermission.orElse(null), root.get("permissions")));
//						
//			// Cria uma lista de codigos com os valores recebidos no campo codPermissions
//			List<String> parentList = Arrays.asList(profileFilter.getCodPermissions().split(","));			
//			
//			Collection<Long> parentListLong = new ArrayList<>();
//			
//			for (String str : parentList) {
//				parentListLong.add(Long.parseLong(str));		
//			}
//			
//			//Recupera as permissoes da lista de codigos
//			Collection<?> permissions = permissionService.findAllByCodeIn(parentListLong);
//			
//			//Cria uma expressao para fazer o criterio de in para a query
//			Expression<Permission> parentExpression = root.get("permissions");
//			predicates.add(builder.isMember(permissions, root.get("permissions")));
//			//predicates.add(builder.isMember(optionalPermission.orElse(null), root.get("permissions")));
//
//		}

		if (profileFilter.getCodBusinessUnitProfile() != null) {

			if (profileFilter.getCodBusinessUnitProfile() != -1) {

				predicates.add(builder.equal(root.get("businessUnit").get("code"), profileFilter.getCodBusinessUnitProfile()));

			} else {

				predicates.add(builder.isNull(root.get("businessUnit")));

			}

		}
		
		//predicates.add(builder.equal(root.get("businessUnit").get("status"), true));

		// Lista de predicados
		return predicates.toArray(new Predicate[predicates.size()]);

	}

	@Override
	public void detachEntity(ModelCore user) {

		detach(user);

	}
	
	@Override
	public ModelCore mergeEntity(ModelCore user) {

		return merge(user);

	}
}
