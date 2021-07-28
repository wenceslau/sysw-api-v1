
package com.suite.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.suite.app.base.Model;
import com.suite.app.model.UserAction;
import com.suite.app.repository.UserActionFilter;
import com.suite.app.util.Utils;

/**
 * Classe abstrata a ser implementada pode todos os UserActionService de cada modulo
 * Realiza todas as operacoes requeridas pelo objetivo da entidade UserAction
 * @author wbane
 *
 */
@Service
public abstract class UserActionService extends com.suite.app.base.Service {

	/**
	 * Reliza um count da entidade informada no classType
	 * @param <T>
	 * @param classType
	 * @return
	 */
	public abstract <T> Long count(Class<T> classType);

	/**
	 * Retorna lista de objetos paginaves T baseado nos filtros O Filter deve ser
	 * tipado para saber qual ClassType usar
	 * @param filter
	 * @return
	 */
	public abstract <T> Page<T> filter(UserActionFilter filter, Pageable pageable, Class<T> classType);

	/**
	 * Retorna lista de objetos T baseado nos filtros O Filter deve ser tipado para
	 * saber qual ClassType usar
	 * @param filter
	 * @return
	 */
	public abstract <T> List<T> filter(UserActionFilter filter, Class<T> classType);

	/**
	 * Salva o objeto UserAction. A implemtacao desse metodo deve ser feita por cada modulo
	 * ira salvar o registro na base correspondente
	 * @param userAction
	 * @return
	 */
	public abstract UserAction save(UserAction userAction);

	/**
	 * Cria e salva o objeto a implementacao do UserAction
	 * Recebe um model que pode ser qualquer extensao dela
	 * @param action
	 * @param model
	 * @param type
	 */
	public void createAndSaveUserAction(UserAction action, Model model, String type) {
		
		//System.out.println("createAndSaveUserAction USR: "+model.getCodeUserRecord());

		// Objeto com os dados do model e seu tipo
		action.setAction(type);
		action.setNameObject(model.getClass().getSimpleName());
		action.setIdRecord(model.getCode());

		Long code = 0L;
		if (Utils.userJob)
			code = -2L;
		else if (Utils.userSystem)
			code = -1L;
		else 
			code = model.getCodeUserContext();
		
		// adiciona o usuario que executou a a acao ao objeto a ser salvo
		action.setUserRecordCode(code);
		
		if (Utils.userJob || Utils.userSystem)
			code = -1L;
		else
			code = model.getCodeSectorContext();
		
		// adiciona o setor logado no qual o usuario executou a a acao
		action.setCodeSector(code);
		action.setDateRecord(LocalDateTime.now());

		// Converte o objeto altedado em um JSON
		String jsonInString = Utils.objectToJson(model);
		action.setValueRecord(jsonInString);

		Utils.userJob = false;
		Utils.userSystem = false;
		save(action);

	}

	/**
	 * Realiza uma busca usando o metodo implementado filter
	 * @param <T>
	 * @param classType
	 * @param idRecord
	 * @param namObject
	 * @param action
	 * @return
	 */
	public <T> T findByIdRecordAndNameObjectAndAction(Class<T> classType, Long idRecord, String namObject, String action) {
		UserActionFilter filter = new UserActionFilter();
		filter.setIdRecord(idRecord);
		filter.setNameObject(namObject);
		filter.setAction(action);

		List<T> list = filter(filter, classType);
		if (list.isEmpty())
			return null;

		return list.get(0);

	}

	/**
	 * Realiza uma busca usando o metodo implementado filter
	 * @param <T>
	 * @param classType
	 * @param idRecord
	 * @param namObject
	 * @return
	 */
	public <T> T findTop1ByIdRecordAndNameObjectOrderByCodeDesc(Class<T> classType, Long idRecord, String namObject) {
		UserActionFilter filter = new UserActionFilter();
		filter.setIdRecord(idRecord);
		filter.setNameObject(namObject);
		filter.setOrderBy("code");
		filter.setDesc(true);

		List<T> list = filter(filter, classType);
		if (list.isEmpty())
			return null;

		return list.get(0);

	}

	/**
	 * Realiza uma busca usando o metodo implementado filter
	 * @param <T>
	 * @param classType
	 * @param namObject
	 * @param codeSector
	 * @return
	 */
	public <T> T findFirstByNameObjectAndCodeSectorOrderByCodeDesc(Class<T> classType, String namObject, Long codeSector) {
		UserActionFilter filter = new UserActionFilter();
		filter.setNameObject(namObject);
		filter.setCodeSectorUserLogged(codeSector);
		filter.setOrderBy("code");
		filter.setDesc(true);

		List<T> list = filter(filter, classType);
		if (list.isEmpty())
			return null;

		return list.get(0);

	}

	@Deprecated
	public void deleteUserActionForRooblack(Long idRecord, String namObject) {
		// deleteByIdRecordAndNameObject(idRecord, namObject);
	}

}