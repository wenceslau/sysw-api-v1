package com.suite.job.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.model.UserAction;
import com.suite.app.repository.UserActionFilter;
import com.suite.app.service.UserActionService;
import com.suite.job.model.UserActionJob;
import com.suite.job.repository.UserActionJobRepository;

/**
 * Service responsavel por manipular os dados recurso UserAction
 * @author Wenceslau
 *
 */
@Service
public class UserActionJobService extends UserActionService {

	@Autowired
	private UserActionJobRepository userActionRepository;

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
		return userActionRepository.save((UserActionJob) userAction);

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