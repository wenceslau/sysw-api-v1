package com.suite.core.repository.query;

import java.util.List;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Profile;
import com.suite.core.repository.filter.ProfileFilter;

/**
 * Interface customizada para consultas no repositorio da entidade Profile
 * @author Wenceslau
 *
 */
public interface ProfileRepositoryQuery {

	public List<Profile> filter(ProfileFilter filter);

	public void detachEntity(ModelCore user);
	
	public ModelCore mergeEntity(ModelCore user);


}
