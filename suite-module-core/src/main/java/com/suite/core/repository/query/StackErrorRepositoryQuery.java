package com.suite.core.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suite.core.base.ModelCore;
import com.suite.core.model.StackError;
import com.suite.core.repository.filter.StackErrorFillter;

/**
 * Interface customizada para consultas no repositorio da entidade StackError
 * @author Wenceslau
 *
 */
public interface StackErrorRepositoryQuery {

	public List<StackError> filter(StackErrorFillter filter);

	public Page<StackError> filter(StackErrorFillter filter, Pageable pageable);

	public void detachEntity(ModelCore user);

}
