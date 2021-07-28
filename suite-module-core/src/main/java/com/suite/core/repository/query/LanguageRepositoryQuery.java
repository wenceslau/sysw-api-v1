package com.suite.core.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Language;
import com.suite.core.repository.filter.LanguageFilter;

/**
 * Interface customizada para consultas no repositorio da entidade Language
 * @author Wenceslau
 *
 */
public interface LanguageRepositoryQuery {

	public Page<Language> filter(LanguageFilter filter, Pageable pageable);

	public List<Language> filter(LanguageFilter filter);
	
	public void detachEntity(ModelCore user);

}
