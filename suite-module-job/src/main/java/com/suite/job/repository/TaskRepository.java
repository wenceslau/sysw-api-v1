package com.suite.job.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.EnumJob.State;
import com.suite.job.model.Task;
import com.suite.job.repository.query.TaskRepositoryQuery;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryQuery {

	List<Task> findAllByCodeBusinessUnit(Long codeBusinessUnit);

	List<Task> findAllByFrequencyAndSchedulerAndState(Frequency frequency, String scheduler , State state);
	
	Task findByBenRunIgnoreCase(String benRun);

}