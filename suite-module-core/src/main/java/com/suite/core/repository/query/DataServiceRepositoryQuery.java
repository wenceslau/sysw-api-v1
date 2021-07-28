package com.suite.core.repository.query;

import java.util.List;

import com.suite.core.base.ModelCore;
import com.suite.core.model.DataService;
import com.suite.core.repository.filter.DataServiceFilter;

/**
 * Interface customizada para consultas no repositorio da entidade DataService
 * @author Wenceslau
 *
 */
public interface DataServiceRepositoryQuery {

	public List<DataService> filter(DataServiceFilter filter);

	public void detachEntity(ModelCore user);

}
