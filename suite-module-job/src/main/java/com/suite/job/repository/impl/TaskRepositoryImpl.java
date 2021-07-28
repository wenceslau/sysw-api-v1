package com.suite.job.repository.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.suite.app.base.Filter;
import com.suite.job.base.RepositoryJob;
import com.suite.job.model.Task;
import com.suite.job.repository.filter.TaskFilter;
import com.suite.job.repository.query.TaskRepositoryQuery;

public class TaskRepositoryImpl extends RepositoryJob implements TaskRepositoryQuery {

	@Override
	public List<Task> filter(TaskFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T> Predicate[] createCriteria(Filter filter, CriteriaBuilder builder, Root<T> root) {
		// TODO Auto-generated method stub
		return null;
	}

}
