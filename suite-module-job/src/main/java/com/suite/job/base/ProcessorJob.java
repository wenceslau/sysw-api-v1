package com.suite.job.base;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.dto.BoolDTO;
import com.suite.core.util.Mailer;
import com.suite.core.util.UtilsCore;
import com.suite.job.base.EnumJob.State;
import com.suite.job.model.Task;
import com.suite.job.processor.interfaces.IProcessor;
import com.suite.job.repository.TaskRepository;

public abstract class ProcessorJob extends com.suite.app.base.Service implements IProcessor {

	@Autowired
	private Mailer mailer;

	@Autowired
	private TaskRepository taskRepository;

	@Override
	@Transactional("jobTransactionManager")
	public void init(Task task) {
		task.setState(State.RUNNING);
		taskRepository.save(task);
		notifyStartEnd(task, "started", null);
	}

	@Override
	@Transactional("jobTransactionManager")
	public void end(Task task, String result, State state) {
		task.setState(state);
		task.setLastRun(LocalDateTime.now());
		taskRepository.save(task);
		notifyStartEnd(task, "ended", result);
	}

	protected void send(Task task, String subject, String body, String recipients, File file) {

		try {

			info("Enviando...");
			String id = "CDPEMAIL0000001";
			body = UtilsCore.templateEmail("CORE", body);

			BoolDTO sent = mailer.sendMail(id, null, Arrays.asList(recipients.split(";")), subject.trim(), body);

			info("Status Envio email " + sent.isValue() + " - " + sent.getMessage());

			if (sent.isValue() == false)
				throw new RuntimeException(sent.getMessage());

		} catch (Exception e) {
			error("Ocorreu um erro ao enviar email. ", e);
			throw e;
		}

	}

	
	private void notifyStartEnd(Task task, String action, String resultTask) {
		if (task.getNotify() != null && task.getNotify()) {		
			String subject = "Task '" + task.getName() + "' "+action+ " at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSS"));
			String body = task.toPrint().replace("\r\n", "<br>");
			if (resultTask != null) {
				body += "<br><br>RESULT TASK<br>";
				body += resultTask.replace("\r\n", "<br>");			
			}
			String recipients = task.getValueProperty("NOTIFY_TO", "wbaneto@ymail.com");
			send(task, subject, body, recipients, null);
		}
	}
}
