package com.suite.core.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suite.core.base.ModelCore;
import com.suite.core.model.UserGroup;
import com.suite.core.repository.filter.UserGroupFilter;

public interface UserGroupRepositoryQuery {

	public List<UserGroup> filter(UserGroupFilter filter);
	
	public Page<UserGroup> filter(UserGroupFilter filter, Pageable pageable);	

	public void detachEntity(ModelCore user);

	public void detachAll();
}
