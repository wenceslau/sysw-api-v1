package com.suite.job.processor;

import org.springframework.stereotype.Service;

import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ProcessorJob;
import com.suite.job.model.Task;

@Service
public class CoreMailSender extends ProcessorJob {

	public CoreMailSender() {
		super();
	}

	@Override
	public void execute(Task task) {
		init(task);
		State state = State.WAIT;
		String msgInfo = "";

		try {

			send(task, "Teste", "Aohhaaaaaa", "wbaneto@yahoo.com.br", null);
			
			msgInfo += "Email sucessful sent";

		} catch (Exception e) {
			msgInfo =  e.toString();
			state = State.FAILURE;
			error("Erro na JOB " + task.getName(), e);

		}

		end(task, msgInfo, state);

	}

	@Override
	protected String formatTranslate(String key, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

}
