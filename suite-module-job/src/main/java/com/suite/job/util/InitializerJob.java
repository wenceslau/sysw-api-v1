
package com.suite.job.util;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.util.Utils;
import com.suite.core.model.Application;
import com.suite.core.model.DataList;
import com.suite.core.util.InitializerHelper;
import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.EnumJob.State;
import com.suite.job.model.Task;
import com.suite.job.model.TaskProperty;
import com.suite.job.service.TaskService;

@Service
@Transactional("jobTransactionManager")
public class InitializerJob extends InitializerHelper {

	@Autowired
	private TaskService taskService;

	public void start() {
		// Verifica se o modulo esta habilitado
		boolean enabled = Boolean.parseBoolean(env.getProperty("suite.job.initilizer.enabled", "false"));
		info("JOB Initializer enable: " + enabled);

		if (!enabled)
			return;

		info("InitializerJob.start() - INICIO");

		info("Modulos ativos no sistema: [" + Utils.contextMap + "]");

		if (!Utils.contextMap.containsKey("suite-module-job"))
			throw new IllegalArgumentException("O modulo job nao foi encontrado no pom.xml. Verifique se o pom.xml o modulo");

		install();

		info("InitializerJob.start() - TERMINO");

	}

	private void install() {
		String nameInitializer = "INITIALIZER_JOB";

		if (findInitializer(nameInitializer)) {
			info("Initializer ModelJob already was executed in " + nameInitializer);
			return;
		}

		Application app = applicationService.findByName("JOB");

		if (app == null) {
			String identifier = RandomStringUtils.randomAlphabetic(15).toUpperCase();
			app = fillApplication("JOB", "JOB", "suite-module-job", identifier, "home_job.png", false);
		}

		addApplicationToBusinessUnit(app);

		createPermission(app);

		createDataList();

		createTaskTest();

		app.setDateInitializer(LocalDateTime.now());

		applicationService.saveBySystem(app);

		createInitializer("INITIALIZER_JOB", "Incialização da aplicação");

	}

	private void createPermission(Application app) {
		fillPermission(6, app, "TASK", "LIST", "Listar Tarefas", null, null).getCode();
		Long pVO = fillPermission(6, app, "TASK", "VIEWER", "Visualizar Tarefas", "MENU", null, "/task",
				"Tarefas", "Job", "fad fa-microchip", 3, 1, true).getCode();
		fillPermission(6, app, "TASK", "INSERT", "Inserir Tarefas", "BUTTON", pVO).getCode();
		fillPermission(6, app, "TASK", "UPDATE", "Editar Tarefas", "BUTTON", pVO).getCode();

	}

	private void createDataList() {
		DataList dataList = dataListService.findByName("PERMISSION_MODULE");
		fillDataItemList("TASK", "TASK", "Módulo Job", "JOB", dataList, true);
	}

	private void createTaskTest() {
		Task tsk;

		tsk = taskService.findByBenRun("coreDailyReport");

		if (tsk == null) {
			tsk = new Task();
			tsk.setBenRun("coreDailyReport");
			tsk.setCodeBusinessUnit(1L);
			tsk.setName("Core Daily Report");
			tsk.setDescription("Daily report of Suite");
			tsk.setFrequency(Frequency.EVERYDAY);
			tsk.setScheduler("hour1");
			tsk.setState(State.SLEEP);
			tsk.setStatus(false);
			tsk.setNotify(true);

			TaskProperty tp = new TaskProperty();
			tp.setName("NOTIFY_TO");
			tp.setDescription("Mail address to send notify at start and end of task");
			tp.setValue("wenceslau@sysmonkey.com.br");
			tsk.getTaskProperties().add(tp);

			tp = new TaskProperty();
			tp.setName("RECIPIENT");
			tp.setDescription("Recipient of report");
			tp.setValue("wenceslau@sysmonkey.com.br");
			tsk.getTaskProperties().add(tp);

			tp = new TaskProperty();
			tp.setDescription("Subject of report mail");
			tp.setName("SUBJECT");
			tp.setValue("Daily Report Suite");
			tsk.getTaskProperties().add(tp);

			taskService.relationshipTaskProperty(tsk, tsk);
			taskService.saveBySystem(tsk);
		}

		tsk = taskService.findByBenRun("coreCloseConnection");

		if (tsk == null) {

			tsk = new Task();
			tsk.setBenRun("coreCloseConnection");
			tsk.setCodeBusinessUnit(1L);
			tsk.setName("Task to close connectinos database");
			tsk.setDescription("Close all open connections of all Data Services and Data Tasks");
			tsk.setFrequency(Frequency.EVERYWEEK);
			tsk.setScheduler("sunday");
			tsk.setState(State.SLEEP);
			tsk.setStatus(false);
			tsk.setNotify(true);

			TaskProperty tp = new TaskProperty();
			tp.setName("NOTIFY_TO");
			tp.setDescription("Mail address to send notify at start and end of task");
			tp.setValue("wenceslau@sysmonkey.com.br");
			tsk.getTaskProperties().add(tp);

			tp = new TaskProperty();
			tp.setDescription("Unit Business to apply label update.");
			tp.setName("CODE_BUSINESS_UNIT");
			tp.setValue("0");
			tsk.getTaskProperties().add(tp);

			taskService.relationshipTaskProperty(tsk, tsk);
			taskService.saveBySystem(tsk);
		}

	}

}
