package com.suite.job.repository.impl;

import org.springframework.stereotype.Component;

import com.suite.app.base.Filter;
import com.suite.app.repository.UserActionRepositoryQuery;
import com.suite.job.base.RepositoryJob;

@Component
public class UserActionJobRepositoryImpl extends RepositoryJob implements UserActionRepositoryQuery {

	@Override
	protected void interceptOrderBy(Filter filter) {
		//For UserAction this method do nothing... but need to be implemnted
		//The entity UserAction does not have property Status

	}

}
