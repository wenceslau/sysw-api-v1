package com.suite.core.repository.query;

import java.util.List;

import com.suite.core.base.ModelCore;
import com.suite.core.model.User;
import com.suite.core.repository.filter.UserFilter;

/**
 * Interface customizada para consultas no repositorio da entidade User
 * @author Wenceslau
 *
 */
public interface UserRepositoryQuery {

	public List<User> filter(UserFilter filter);
	
	public void detachEntity(ModelCore user);
	
}
