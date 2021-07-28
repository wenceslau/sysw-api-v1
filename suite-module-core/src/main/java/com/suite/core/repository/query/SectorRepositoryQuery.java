package com.suite.core.repository.query;

import java.util.List;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Sector;
import com.suite.core.repository.filter.SectorFilter;

/**
 * Interface customizada para consultas no repositorio da entidade Sector
 * @author Wenceslau
 *
 */
public interface SectorRepositoryQuery {

	public List<Sector> filter(SectorFilter filter);

	public void detachEntity(ModelCore user);

}
