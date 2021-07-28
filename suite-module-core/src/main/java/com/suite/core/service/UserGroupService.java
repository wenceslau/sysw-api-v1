
package com.suite.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.model.UserGroup;
import com.suite.core.repository.UserGroupRepository;
import com.suite.core.repository.filter.UserGroupFilter;

/**
 * Service responsavel por manipular os dados recurso Sector
 * @author Wenceslau
 *
 */
@Service
public class UserGroupService extends CoreRulesService {

	@Autowired
	private UserGroupRepository userGroupRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public UserGroup findByCode(Long code) {
		UserGroup userGroup = findById(code);

		removeUserFromGroup(userGroup, getUser());

		return userGroup;

	}

	/**
	 * Insere o recurso na base
	 * @param userGroup
	 * @return
	 */
	@Transactional("transactionManager")
	public UserGroup insert(final UserGroup userGroup) {
		info("Insert: " + userGroup);

		insertNoTransaction(userGroup);

		return userGroup;

	}

	public void insertNoTransaction(final UserGroup userGroup) {

		try {
			ruleOfGroup(userGroup);

			// User SA e UN sao obrigatorios em todos os grupos
			List<User> listUserSaUN = userService.listByProfileTypeIsLessThanAndStatus(3, true);

			for (User user : listUserSaUN) {

				if (!userGroup.getUsers().stream().filter(x -> x.getCode().equals(user.getCode())).findFirst().isPresent()) {
					userGroup.getUsers().add(user);
				}

			}

			// O proprio usuario deve participar do grupo
			User usrLogged = getUser();
			if (!userGroup.getUsers().stream().filter(x -> x.getCode().equals(usrLogged.getCode())).findFirst().isPresent())
				userGroup.getUsers().add(usrLogged);

			save(userGroup);

		} catch (Exception e) {
			throwException(e, formatTranslate("msg_falha_ao_inserir_[%s]", formatTranslate("lbl_grupo_de_usuarios")));

		}

	}

	/**
	 * Atualiza objeto na base9
	 * @param code
	 * @param userGroup
	 * @return
	 */
	@Transactional("transactionManager")
	public UserGroup update(Long code, UserGroup userGroup) {
		info("Insert: " + userGroup);

		try {
			ruleOfGroup(userGroup);

			UserGroup userGroupToSave = findById(code);

			User usrLogged = getUser();

			// Recupera os users removidos pelas regras de visualizacao do usuario
			List<User> usersRemoved = removeUserFromGroup(userGroupToSave, usrLogged);

			BeanUtils.copyProperties(userGroup, userGroupToSave, new String[] { "code", "businessUnit" });

			// Readiciona os usuarios removidos
			userGroupToSave.getUsers().addAll(usersRemoved);

			// User SA e UN sao obrigatorios em todos os grupos
			List<User> listUserSaUN = userService.listByProfileTypeIsLessThanAndStatus(3, true);
			listUserSaUN.stream().forEach(elem -> {
				if (!userGroupToSave.getUsers().stream().filter(x -> x.getCode().equals(elem.getCode())).findFirst().isPresent())
					userGroupToSave.getUsers().add(elem);

			});

			// O proprio usuario deve participar do grupo
			if (!userGroup.getUsers().stream().filter(x -> x.getCode().equals(usrLogged.getCode())).findFirst().isPresent())
				userGroup.getUsers().add(usrLogged);

			userGroup = save(userGroupToSave);

		} catch (Exception e) {
			throwException(e, formatTranslate("msg_falha_ao_atualizar_[%s]", formatTranslate("lbl_grupo")));

		}

		return userGroup;

	}

	/**
	 * Recupera a lista filtrada
	 * @return
	 */
	public List<UserGroup> listByFilter(UserGroupFilter filter) {
		// UserGroup ug = userGroupRepository.findById(3L).get();
		//
		// System.out.println();
		//
		// UserGroup ugTO = UserGroup.build(ug);
		//
		// ugTO.setName("1");
		// ugTO.getBusinessUnit().setName("Alterado");

		User usrLogged = getUser();
		if (filter.getUsername() == null)
			filter.setUsername(usrLogged.getUsername());

		// se nao for o setor default aplica filtro para a businessUnit do setor logado
		if (!isDefaultSector())
			applyFilterBusinessUnit(filter);

		List<UserGroup> list;
		list = filter(filter);

		for (UserGroup userGroup : list)
			removeUserFromGroup(userGroup, usrLogged);

		// Remove os grupos que estao com os user zerado.
		// Se ta zerado indica que o grupo so possui user que viola a regra de vizualização
		// list.removeIf(x -> x.getUsers().size() == 0);

		List<UserGroup> listAux = new ArrayList<>(list);

		if (filter.getUsername() != null) {
			listAux.clear();

			for (UserGroup userGroup : list) {
				List<User> usrs = userGroup.getUsers();
				if (usrs.stream().filter(x -> x.getUsername().equals(filter.getUsername())).findAny().isPresent())
					listAux.add(userGroup);

			}

		}

		return listAux;

		// return list;

	}

	/**
	 * Remove usuers baseados nas regras de visuazacao
	 * 1 - Users que tem o perfil igual ou maior ao user logado
	 * 2 - Users ue tem ao menos um setor em comum com o setor do user logado
	 * Apenas user que condizen com as 2 regras acima ficam na lista
	 * @param userGroup
	 * @return
	 */
	private List<User> removeUserFromGroup(UserGroup userGroup, User usrLogged) {
		List<User> removed = userGroup.getUsers()
				.stream()
				.filter(x -> x.getProfile().getType() < usrLogged.getProfile().getType())
				.collect(Collectors.toList());

		// Remove os user ue tem perfil menor que o do usuario
		userGroup.getUsers().removeIf(x -> x.getProfile().getType() < usrLogged.getProfile().getType());

		// Todos os setores do usuario logado
		List<Sector> lsScUsLg = usrLogged.getSectors();

		// Set nao permite duplicata
		Set<User> setRet = new HashSet<>();

		// Percorre a lita de usuarios do grupo
		for (User user : userGroup.getUsers()) {

			// Percorre a lista de setor de cada usuario filtrado
			for (Sector sec : user.getSectors()) {
				// Se na lista de setor do usuario logado, possuir algum setor do usuario filtrado
				// Esse usuario pode velo, então adiciona ele na lista;
				// Se nao tiver nenhum setor em comum, ele nao pode ver esse usuario
				if (lsScUsLg.stream().filter(s -> s.getCode().equals(sec.getCode())).findFirst().isPresent())
					setRet.add(user);

				// Em resumo a regra eh a seguinte:
				// Usuarios podem ver outros usuarios nas seguintes codicoes:
				// Ter perfil igual ou maior que o seu
				// - (Ex: Perfil Admin = 3, ve todos os perfis mior ou igual a 3)
				// Ter algum setor em comum.
				// - (Ex: User A tem o setor X Y Z, se o user B nao tiver nenhum dos setores X Y Z, o user B não é visivel ao User A)

			}

		}

		removed.addAll(userGroup.getUsers()
				.stream()
				.filter(x -> !setRet.contains(x))
				.collect(Collectors.toList()));

		userGroup.getUsers().clear();
		userGroup.setUsers(new ArrayList<User>(setRet));
		userGroup.getUsers().sort((p1, p2) -> p1.getUsername().compareTo(p2.getUsername()));

		return removed;

	}

	/*
	 * < Metodos expostos pelo repository >
	 * 
	 * Apenas o service da entidade pode acessar
	 * o repositorio da entidade.
	 * Os metodos sao expostos para serem usados
	 * por outras classes service que precisar acessar
	 * os dados da entidade
	 */

	public UserGroup save(UserGroup entity) {
		UserGroup sec = userGroupRepository.save(entity);

		return findById(sec.getCode());

	}

	public UserGroup findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "UserGroup", id), 1);

		Optional<UserGroup> optional = userGroupRepository.findById(id);

		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "UserGroup", id), 1);

		UserGroup entity = optional.get();

		return entity;

	}

	private List<UserGroup> filter(UserGroupFilter filter) {
		List<UserGroup> lst = userGroupRepository.filter(filter);
		userGroupRepository.detachAll();

		return lst;

	}

	public boolean existsByNameAndBusinessUnit(String name, Long code) {
		return userGroupRepository.existsByNameIgnoreCaseAndBusinessUnit_Code(name, code);

	}

	public boolean existsByNameAndBusinessUnitAndCodeIsNotIn(String name, Long buCode, Long code) {
		return userGroupRepository.existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(name, buCode, code);

	}

	public void deleteById(Long code) {
		userGroupRepository.deleteById(code);

	}

	
	public List<UserGroup> findAllByBusinessUnit(Long code) {
		return userGroupRepository.findAllByBusinessUnit_Code(code);
	}

}
