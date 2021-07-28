package com.suite.job.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suite.app.base.Base;
import com.suite.core.util.UtilsCore;
import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.EnumJob.State;
import com.suite.job.model.Task;
import com.suite.job.processor.interfaces.IProcessor;
import com.suite.job.repository.TaskRepository;

// @Scheduled(cron = "0 1 1 * * ?") = TODOS OS DIAS AS 1:01 DA MADRUGDA
// Below you can find the example patterns from the spring forum:
//
// * "0 0 * * * *" = the top of every hour of every day.
// * "*/10 * * * * *" = every ten seconds.
// * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
// * "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
// * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
// * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
// * "0 0 0 25 12 ?" = every Christmas Day at midnight
// Cron expression is represented by six fields:
//
// second, minute, hour, day of month, month, day(s) of week
// (*) means match any
//
// */X means "every X"
//
// ? ("no specific value") - useful when you need to specify something in one of the two fields in which the character is allowed,
// but not the other. For example,
// if I want my trigger to fire on a particular day of the month (say, the 10th), but I don't care what day of the week that happens
// to be,
// I would put "10" in the day-of-month field and "?" in the day-of-week field.
//

@Component
public class TaskJob extends Base {

	@Autowired
	private TaskRepository taskRepository;

	public synchronized void execute(String scheduler, Frequency frequency) {
		System.out.println(
				scheduler + ": " + frequency + ": " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss:SSSS")));

		List<Task> tasks = taskRepository.findAllByFrequencyAndSchedulerAndState(frequency, scheduler, State.WAIT);

		for (final Task task : tasks) {

			if (task.getStatus() == false)
				continue;

			info("JOB: " + scheduler + " TASK: " + task.getName() + " Started "
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss:SSSS")));
			Runnable run = new Runnable() {

				@Override
				public void run() {
					IProcessor processor;

					try {
						processor = (IProcessor) UtilsCore.getBean(task.getBenRun());
						if (processor == null)
							warn("The class " + task.getBenRun() + " has not been found in context to run");
						else
							processor.execute(task);
					} catch (Exception e) {
						error("Error Get Bean: ", e);
						task.setState(State.FAILURE);
						task.setLastRun(LocalDateTime.now());
						taskRepository.save(task);
					}

					info("JOB: " + scheduler + " TASK: " + task.getName() + " Finished "
							+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss:SSSS")));
				}

			};

			Thread th = new Thread(run);
			th.start();

		}

	}

}
