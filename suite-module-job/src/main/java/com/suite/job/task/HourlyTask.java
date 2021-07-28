package com.suite.job.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.suite.app.base.Base;
import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.TaskJob;

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
public class HourlyTask extends Base {

	@Autowired
	private TaskJob taskJob;

	private void execute(String scheduler) {
		taskJob.execute(scheduler, Frequency.INTERVAL);
	}

	// @Scheduled(cron = "0 0/1 * * * *")
	@Scheduled(fixedDelay = 60000) // delay 1 minutes
	public void minute1() {
		execute("1minute");

	}

	// @Scheduled(cron = "0 0/5 * * * *")
	@Scheduled(fixedDelay = 300000) // delay 5 minutes
	public void minute5() {
		execute("5minutes");

	}

	@Scheduled(fixedDelay = 600000) // delay 10 minutes
	public void minute10() {
		execute("10minutes");

	}

	@Scheduled(fixedDelay = 1800000) // delay 30 minutes
	public void minute30() {
		execute("30minutes");

	}

	@Scheduled(fixedDelay = 3600000) // delay 60 minutes
	public void minute60() {
		execute("60minutes");

	}

}
