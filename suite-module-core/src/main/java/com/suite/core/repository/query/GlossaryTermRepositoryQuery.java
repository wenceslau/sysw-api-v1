package com.suite.core.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suite.core.base.ModelCore;
import com.suite.core.model.GlossaryTerm;
import com.suite.core.repository.filter.GlossaryTermFilter;

public interface GlossaryTermRepositoryQuery {
	public Page<GlossaryTerm> filter(GlossaryTermFilter filter, Pageable pageable);

	public List<GlossaryTerm> filter(GlossaryTermFilter filter);

	public void detachEntity(ModelCore user);

}
