package com.suite.job.repository.query;

import java.util.List;

import com.suite.job.model.Task;
import com.suite.job.repository.filter.TaskFilter;

/**
 * @author Wenceslau
 *
 */
public interface TaskRepositoryQuery {

	public List<Task> filter(TaskFilter filter);

}