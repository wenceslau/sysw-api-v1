package com.suite.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.suite.core.model.Language;
import com.suite.core.repository.LanguageRepository;
import com.suite.core.repository.filter.LanguageFilter;

/**
 * Service responsavel por manipular os dados recurso Application
 * @author Wenceslau
 *
 */
@Service
public class LanguageService extends CoreRulesService {

	private ConcurrentHashMap<String, String> cMapTranslate = new ConcurrentHashMap<>();

	@Autowired
	private LanguageRepository languageRepository;

	/**
	 * Encontra o recurso pelo codigo
	 * @param id
	 * @return
	 */
	public Language findByCode(Long code) {
		Optional<Language> opt = languageRepository.findById(code);

		if (!opt.isPresent())
			// No resource found for code %s
			throw new EmptyResultDataAccessException(formatTranslate("msg_nenhum_recurso_econtrado_p_o_c_[%s]", code), 1);

		return opt.get();

	}

	/**
	 * Recupera a lista de recursos ativos
	 * @return
	 */
	public List<Language> list() {
		List<Language> lst = languageRepository.findAll();

		// Retorna apenas as key do tipo lbl_
		lst.removeIf(l -> l.getKey().startsWith("msg_"));

		return lst;

	}

	/**
	 * Insere o recurso no repositorio
	 * @param application
	 * @return
	 */
	public Language insert(Language language) {
		ruleOfLanguage(language);

		language.setDateRecord(LocalDateTime.now());
		return languageRepository.save(language);

	}

	/**
	 * Atualiza recurso no repositorio
	 * @param code
	 * @param application
	 * @return
	 */
	public Language update(Long code, Language language) {
		ruleOfLanguage(language);

		Language languageToSave = findByCode(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(language, languageToSave, new String[] { "code", "dateRecord", "dateUpdate" });

		languageToSave.setDateUpdate(LocalDateTime.now());
		return languageRepository.save(languageToSave);

	}

	/**
	 * Deleta recurso no repositorio
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("delete:" + code);
		Language language = findByCode(code);

		languageRepository.delete(language);

	}

	/**
	 * Recupera a lista filtrada de recurso
	 * @return
	 */
	public Page<Language> filter(LanguageFilter filter, Pageable pageable) {
		info("filter: " + filter);

		return languageRepository.filter(filter, pageable);

	}

	/**
	 * Recupera a lista filtrada de recurso
	 * @return
	 */
	public List<Language> filter(LanguageFilter filter) {
		info("filter: " + filter);

		List<Language> list = languageRepository.filter(filter);

		return list;

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

	public String findValueByLangAndKey(String lang, String key) {
		String value = cMapTranslate.get(lang + key);
		// System.out.println("Lang: " + lang + " Key: " + key + " ValueFromMap: " + value);

		if (value != null)
			return value;

		value = languageRepository.findValueByLangAndKey(lang, key);
		// System.out.println("ValueFromDB: " + value);

		if (value != null)
			cMapTranslate.put(lang + key, value);

		return value;

		// String value = "";
		//
		// switch (lang) {
		//
		// case "PT":
		// value = (languageRepository.findValuePTByKey(key));
		// break;
		// case "EN":
		// value = languageRepository.findValueENByKey(key);
		// break;
		//
		// case "ES":
		// value = languageRepository.findValueESByKey(key);
		// break;
		//
		// }
		//
		// if (value == null || value.isEmpty())
		// value = key;
		//
		// return value;

	}

	public Language findByKey(String key) {
		return languageRepository.findByKeyIgnoreCase(key);

	}

	public boolean existsByKey(String key) {
		return languageRepository.existsByKeyIgnoreCase(key);

	}

	public boolean existsByKeyAndCodeIsNotIn(String key, Long code) {
		return languageRepository.existsByKeyIgnoreCaseAndCodeIsNotIn(key, code);

	}

	public boolean existsByPortugues(String portugues) {
		return languageRepository.existsByPortuguesIgnoreCase(portugues);

	}

	public boolean existsByPortuguesAndCodeIsNotIn(String portugues, Long code) {
		return languageRepository.existsByPortuguesIgnoreCaseAndCodeIsNotIn(portugues, code);

	}

	public boolean existsByEnglish(String english) {
		return languageRepository.existsByEnglishIgnoreCase(english);

	}

	public boolean existsByEnglishAndCodeIsNotIn(String english, Long code) {
		return languageRepository.existsByEnglishIgnoreCaseAndCodeIsNotIn(english, code);

	}

	public boolean existsBySpanish(String spanish) {
		return languageRepository.existsBySpanishIgnoreCase(spanish);

	}

	public boolean existsBySpanishAndCodeIsNotIn(String spanish, Long code) {
		return languageRepository.existsBySpanishIgnoreCaseAndCodeIsNotIn(spanish, code);

	}

	public void deleteAll() {
		languageRepository.deleteAll();
		
	}

	public void deleteByKey(String trim) {
		languageRepository.deleteByKey(trim);
		
	}

	public Language saveBySystem(Language l) {
		return languageRepository.save(l);
		
	}

}
