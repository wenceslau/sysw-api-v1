package com.suite.job.processor.interfaces;

import com.suite.job.base.EnumJob.State;
import com.suite.job.model.Task;

public interface IProcessor {

	
	void init(Task task);
	
	void execute(Task task);
	
	void end(Task task, String result, State state);
	
}
