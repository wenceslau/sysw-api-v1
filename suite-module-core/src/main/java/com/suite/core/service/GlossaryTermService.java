package com.suite.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.suite.core.model.GlossaryTerm;
import com.suite.core.repository.GlossaryTermRepository;
import com.suite.core.repository.filter.GlossaryTermFilter;

@Service
public class GlossaryTermService extends CoreRulesService {

	@Autowired
	private GlossaryTermRepository termosGlossarioRepository;

	/**
	 * Encontra o recurso pelo codigo
	 * 
	 * @param id
	 * @return
	 */
	public GlossaryTerm findByCode(Long code) {
		Optional<GlossaryTerm> opt = termosGlossarioRepository.findById(code);

		if (!opt.isPresent())
			// No resource found for code %s
			throw new EmptyResultDataAccessException(formatTranslate("msg_nenhum_recurso_econtrado_p_o_c_[%s]", code),
					1);

		return opt.get();

	}

	/**
	 * Recupera a lista de recursos ativos
	 * 
	 * @return
	 */
	public List<GlossaryTerm> list() {
		return termosGlossarioRepository.findAll();
	}

	/**
	 * Insere o recurso no repositorio
	 * 
	 * @param application
	 * @return
	 */
	public GlossaryTerm insert(GlossaryTerm termosGlossario) {
		info("Insert: " + termosGlossario);

		ruleOfTermosGlossario(termosGlossario);

		return termosGlossarioRepository.save(termosGlossario);

	}

	/**
	 * Atualiza recurso no repositorio
	 * 
	 * @param code
	 * @param application
	 * @return
	 */
	public GlossaryTerm update(Long code, GlossaryTerm termosGlossario) {
		info("Update: " + termosGlossario);

		ruleOfTermosGlossario(termosGlossario);

		GlossaryTerm termosGlossarioToSave = findByCode(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(termosGlossario, termosGlossarioToSave, new String[] { "code" });

		return termosGlossarioRepository.save(termosGlossarioToSave);

	}

	/**
	 * Deleta recurso no repositorio
	 * 
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);
		GlossaryTerm termosGlossario = findByCode(code);

		termosGlossarioRepository.delete(termosGlossario);

	}

	/**
	 * Recupera a lista filtrada de recurso
	 * 
	 * @return
	 */
	public Page<GlossaryTerm> filter(GlossaryTermFilter filter, Pageable pageable) {
		return termosGlossarioRepository.filter(filter, pageable);

	}

	/**
	 * Recupera a lista filtrada de recurso
	 * 
	 * @return
	 */
	public List<GlossaryTerm> filter(GlossaryTermFilter filter) {
		List<GlossaryTerm> list = termosGlossarioRepository.filter(filter);

		return list;

	}

}
