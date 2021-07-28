package com.suite.core.repository.query;

import java.util.List;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Permission;
import com.suite.core.repository.filter.PermissionFilter;

/**
 * Interface customizada para consultas no repositorio da entidade Permission
 * @author Wenceslau
 *
 */
public interface PermissionRepositoryQuery {

	public List<Permission> filter(PermissionFilter filter);

	public void executeQuery(String query);
	
	public void detachEntity(ModelCore user);

}
