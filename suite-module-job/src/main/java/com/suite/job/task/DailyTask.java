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
public class DailyTask extends Base {

	@Autowired
	private TaskJob taskJob;

	private void execute(String scheduler) {
		taskJob.execute(scheduler, Frequency.EVERYDAY);
	}

	@Scheduled(cron = "00 00 1 * * *")
	public void hour1() {
		execute("hour1");

	}

	@Scheduled(cron = "00 00 2 * * *")
	public void hour2() {
		execute("hour2");

	}

	@Scheduled(cron = "00 00 3 * * *")
	public void hour3() {
		execute("hour3");

	}

	@Scheduled(cron = "00 00 4 * * *")
	public void hour4() {
		execute("hour4");

	}

	@Scheduled(cron = "00 00 5 * * *")
	public void hour5() {
		execute("hour5");

	}

	@Scheduled(cron = "00 00 6 * * *")
	public void hour6() {
		execute("hour6");

	}

	@Scheduled(cron = "00 00 7 * * *")
	public void hour7() {
		execute("hour7");

	}

	@Scheduled(cron = "00 00 8 * * *")
	public void hour8() {
		execute("hour8");

	}

	@Scheduled(cron = "00 00 9 * * *")
	public void hour9() {
		execute("hour9");

	}

	@Scheduled(cron = "00 00 10 * * *")
	public void hour10() {
		execute("hour10");

	}

	@Scheduled(cron = "00 00 11 * * *")
	public void hour11() {
		execute("hour11");

	}

	@Scheduled(cron = "00 00 12 * * *")
	public void hour12() {
		execute("hour12");

	}

	@Scheduled(cron = "00 00 13 * * *")
	public void hour13() {
		execute("hour13");

	}

	@Scheduled(cron = "00 00 14 * * *")
	public void hour14() {
		execute("hour14");

	}

	@Scheduled(cron = "00 00 15 * * *")
	public void hour15() {
		execute("hour15");

	}

	@Scheduled(cron = "00 00 16 * * *")
	public void hour16() {
		execute("hour16");

	}

	@Scheduled(cron = "00 00 17 * * *")
	public void hour17() {
		execute("hour17");

	}

	@Scheduled(cron = "00 00 18 * * *")
	public void hour18() {
		execute("hour18");

	}

	@Scheduled(cron = "00 00 19 * * *")
	public void hour19() {
		execute("hour19");

	}

	@Scheduled(cron = "00 00 20 * * *")
	public void hour20() {
		execute("hour20");

	}

	@Scheduled(cron = "00 00 21 * * *")
	public void hour21() {
		execute("hour21");

	}

	@Scheduled(cron = "00 00 22 * * *")
	public void hour22() {
		execute("hour22");

	}

	@Scheduled(cron = "00 00 23 * * *")
	public void hour23() {
		execute("hour23");

	}

	@Scheduled(cron = "00 00 0 * * *")
	public void hour0() {
		execute("hour0");

	}

}
