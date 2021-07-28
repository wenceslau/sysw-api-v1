package com.suite.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.model.UserAction;
import com.suite.app.repository.UserActionFilter;
import com.suite.app.service.UserActionService;
import com.suite.core.model.UserActionCore;
import com.suite.core.repository.UserActionCoreRepository;

/**
 * Service responsavel por manipular os dados recurso UserAction
 * @author Wenceslau
 *
 */
@Service
public class UserActionCoreService extends UserActionService {

	@Autowired
	private UserActionCoreRepository userActionRepository;

	/**
	 * Conta o numero de registros
	 */
	@Override
	public <T> Long count(Class<T> classType) {
		return userActionRepository.count(classType);

	}

	/**
	 * Filtra os objetos UserAction paginavel
	 * O Class<T> informa qual useraction e de qual Modulo ele deve procurar
	 */
	@Override
	public <T> Page<T> filter(UserActionFilter filter, Pageable pageable, Class<T> classType) {
		return userActionRepository.filter(filter, pageable, classType);

	}

	/**
	 * Filtra os objetos UserAction
	 * O Class<T> informa qual useraction e de qual Modulo ele deve procurar
	 */
	@Override
	public <T> List<T> filter(UserActionFilter filter, Class<T> classType) {
		return userActionRepository.filter(filter, classType);

	}

	/**
	 * Salva o recurso no repositorio
	 */
	@Override
	@Transactional("transactionManager")
	public UserAction save(UserAction userAction) {
		return userActionRepository.save((UserActionCore) userAction);

	}

	/**
	 * Metodo depreciado
	 */
	@Deprecated
	public void deleteUserActionForRooblack(Long idRecord, String namObject) {
		// deleteByIdRecordAndNameObject(idRecord, namObject);
	}

	@Override
	protected String formatTranslate(String key, Object... args) {
		return null;

	}

}