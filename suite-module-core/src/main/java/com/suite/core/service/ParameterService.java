package com.suite.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.util.Utils;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.Parameter;
import com.suite.core.repository.ParameterRepository;
import com.suite.core.repository.filter.ParameterFilter;
import com.suite.core.util.UtilsCore;

/**
 * Service responsavel por manipular os dados recurso Parameter
 * @author 4931pc_neto
 *
 */
@Service
public class ParameterService extends ServiceCore {

	@Autowired
	private ParameterRepository parameterRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public Parameter findByCode(Long code) {
		Optional<Parameter> parameter = parameterRepository.findById(code);

		if (!parameter.isPresent())
			throw new EmptyResultDataAccessException("No resource found for code " + code, 1);

		return parameter.get();

	}

	/**
	 * Insere o recurso na base
	 * @param parameter
	 * @return
	 */
	public Parameter insert(Parameter parameter) {
		info("Insert: " + parameter);

		return parameterRepository.save(parameter);

	}

	/**
	 * Atualiza recurso na base
	 * @param code
	 * @param parameter
	 * @return
	 */
	public Parameter update(Long code, Parameter parameter) {
		info("Update: " + parameter);

		Parameter parameterToSave = findByCode(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(parameter, parameterToSave, new String[] { "code" });

		return parameterRepository.save(parameterToSave);

	}

	/**
	 * Deleta recurso na base
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		Parameter parameterToDelete = findByCode(code);

		parameterRepository.delete(parameterToDelete);

	}

	/**
	 * Retorna uma lista dos recursos ativos
	 * @return
	 */
	public List<Parameter> list() {
		return parameterRepository.findAllByStatus(true);

	}

	/**
	 * Recupera a lista filtrada de recursos
	 * @return
	 */
	public List<Parameter> filter(ParameterFilter filter) {
		
		updateStaticParameter();
		
		if (filter != null && filter.getGroup() != null)
			return parameterRepository.findByGroupIgnoreCase(filter.getGroup());

		return parameterRepository.findAll();

	}

	/**
	 * Encontra um recurso pela chave
	 * Lanca execao se nao econtrar
	 * @param id
	 * @return
	 */
	public Parameter findByKey(String key) {
		Optional<Parameter> parameter = parameterRepository.findByKeyIgnoreCase(key);

		if (!parameter.isPresent())
			throw new EmptyResultDataAccessException("No value found for key " + key, 1);

		return parameter.get();

	}

	/**
	 * Encontra um recurso pela chave ou retorna null se nao achar
	 * @param id
	 * @return
	 */
	public Parameter findByKeyOrNull(String key) {
		Optional<Parameter> parameter = parameterRepository.findByKeyIgnoreCase(key);

		if (!parameter.isPresent())
			return null;

		return parameter.get();

	}

	/**
	 * Encontra um recurso pela chave ou retorna null se nao achar
	 * @param id
	 * @return
	 */
	public String findByKeyOrEmpty(String key) {
		Optional<Parameter> parameter = parameterRepository.findByKeyIgnoreCase(key);

		if (!parameter.isPresent())
			return "";

		return parameter.get().getValue();

	}

	/**
	 * Retorna um parametro pela chave e pelo grupo
	 * Null se nao achar
	 * @param group
	 * @param key
	 * @return
	 */
	public Parameter findByGroupAndKey(String group, String key) {
		Optional<Parameter> opt = parameterRepository.findByGroupIgnoreCaseAndKeyIgnoreCase(group, key);

		if (opt.isPresent())
			return opt.get();

		return null;

	}

	/**
	 * Encontra um recurso pela chave ou retorna null se nao achar
	 * @param id
	 * @return
	 */
	public String findByKeyOrDefault(String key, String _default) {

		Optional<Parameter> parameter = parameterRepository.findByKeyIgnoreCase(key);

		if (!parameter.isPresent())
			return _default;

		return parameter.get().getValue();

	}
	
	private void updateStaticParameter() {
		UtilsCore.debug = Boolean.parseBoolean(findByKeyOrDefault("TRACE_DEBUG", "true"));
		UtilsCore.info = Boolean.parseBoolean(findByKeyOrDefault("TRACE_INFO", "true"));
		UtilsCore.warn = Boolean.parseBoolean(findByKeyOrDefault("TRACE_WARN", "true"));
		UtilsCore.error = Boolean.parseBoolean(findByKeyOrDefault("TRACE_ERROR", "true"));
	}

	public Parameter saveBySystem(Parameter parameter) {		
		Utils.userSystem = true;
		return parameterRepository.save(parameter);
	}
	
}
