package com.suite.job.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.app.repository.UserActionFilter;
import com.suite.app.util.Utils;
import com.suite.core.model.StackError;
import com.suite.core.model.UserActionCore;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.filter.StackErrorFillter;
import com.suite.core.service.StackErrorService;
import com.suite.core.service.UserActionCoreService;
import com.suite.core.service.UserLogonHistoryService;
import com.suite.core.service.UserRequestService;
import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ProcessorJob;
import com.suite.job.model.Task;

@Service
public class CoreDailyReport extends ProcessorJob {

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	@Autowired
	private StackErrorService stackErrorService;

	@Autowired
	private UserRequestService userRequestService;

	@Autowired
	private UserActionCoreService userActionCoreService;


	@Override
	public void execute(Task task) {
		State state = State.WAIT;
		String msgInfo = "";

		try {
			init(task);
		} catch (Exception e) {
			error("Erro na iniciar TASK " + task.getName(), e);
			return;
		}

		try {

			LocalDateTime start = LocalDate.now().plusDays(-1).atStartOfDay();

			String report = prepareReport(task, start);
			String subject = task.getValueProperty("subject", "");
			String recipient = task.getValueProperty("RECIPIENT", "wbaneto@yahoo.com.br");
			subject += " - " + start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			;

			send(task, subject, report, recipient, null);

			msgInfo += "Email sucessful ";

		} catch (Exception e) {
			msgInfo = e.toString();
			state = State.FAILURE;
			error("Erro na TASK " + task.getName(), e);

		}

		try {
			end(task, msgInfo, state);
		} catch (Exception e) {
			error("Erro na terminar TASK " + task.getName(), e);
		}

	}

	@Override
	protected String formatTranslate(String key, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	private String prepareReport(Task task, LocalDateTime start) {
		LocalDateTime end = LocalDate.now().atStartOfDay();

		String template = templateEmailRepot();

		// Total de logins no dia
		List<UserLogonHistory> list = userLogonHistoryService.findAllByDateLogon(start.toLocalDate());
		Integer totalTries = list.size();
		Long totalSuccesss = list.stream().filter(x -> x.getStatusLogon().equals("SUCCESS")).count();

		// Count de login por Usuarios
		String users = "";
		Set<String> set = new HashSet<>();
		for (UserLogonHistory ulh : list)
			set.add(ulh.getUserLogon());

		Map<String, Long> map = new HashMap<>();
		for (String user : set)
			map.put(user, list.stream().filter(x -> x.getUserLogon().equals(user)).count());

		for (String string : map.keySet())
			users += "<div style=\"font-size: 13px;\"> " + string + ": " + map.get(string) + " </div>\r\n";

		// Setor mais usado
		String sectors = "";
		sectors += "<div style=\"font-size: 13px;\"> Disponivel somente na V4 </div>\r\n";

		// total de exceptions/warnings no dia
		StackErrorFillter filter = new StackErrorFillter();

		filter.setStartDateTimeError(start);
		filter.setEndDateTimeError(end);
		List<StackError> listErro = stackErrorService.filter(filter);
		Long totalWarn = listErro.stream().filter(x -> x.getMessage().startsWith("Warning")).count();
		Long totalErros = listErro.size() - totalWarn;

		// Erros por aplicação CORE, 
		String errosModule = "";

		for (String module : Utils.contextMap.keySet()) {
			String name = "com.suite." + module.replace("suite-module-", "");
			Long totalApp = listErro.stream().filter(x -> x.getStack().startsWith(name) && !x.getMessage().startsWith("Warning")).count();
			errosModule += "<div style=\"font-size: 13px;\"> " + name + ": " + totalApp + " </div>\r\n";
		}

		// total de requisiçoes no dia
		long totalReq = userRequestService.countByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(start, end);

		// Quantidade de registros inseridos nas APLICAÇOES
		UserActionFilter usrActFilter = new UserActionFilter();
		usrActFilter.setDateRecordStart(start);
		usrActFilter.setDateRecordEnd(end);

		String appsInsert = "<div style=\"font-size: 14px;font-weight: bold; font-style: italic;\">CORE</div>\r\n";
		List<UserActionCore> listUsrActCore = userActionCoreService.filter(usrActFilter, UserActionCore.class);
		Long totalInsertCore = listUsrActCore.stream().filter(x -> x.getAction().equals("Insert")).count();
		appsInsert += "<div style=\"font-size: 13px;\"> INSERT: " + totalInsertCore + " </div>";
		Long totalUpdateCore = listUsrActCore.stream().filter(x -> x.getAction().equals("Update")).count();
		appsInsert += "<div style=\"font-size: 13px;\"> UPDATE: " + totalUpdateCore + " </div>";
		Long totalDeleteCore = listUsrActCore.stream().filter(x -> x.getAction().equals("Delete")).count();
		appsInsert += "<div style=\"font-size: 13px;\"> DELETE: " + totalDeleteCore + " </div>";

		String sectorInsert = "";

		template = template.replace("totalTries", totalTries + "");
		template = template.replace("totalSuccesss", totalSuccesss + "");
		template = template.replace("users", users + "");
		template = template.replace("sectors", sectors + "");
		template = template.replace("totalWarn", totalWarn + "");
		template = template.replace("totalErros", totalErros + "");
		template = template.replace("errosModule", errosModule + "");
		template = template.replace("totalReq", totalReq + "");
		template = template.replace("appsInsert", appsInsert + "");
		template = template.replace("sectorInsert", sectorInsert + "");

		return template;

	}

	private String templateEmailRepot() {
		return "		"
				+ "	<div style=\"text-align: left;\">\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Numero de Login  </div>\r\n"
				+ "		<div style=\"font-size: 13px;\"> Tentativas: totalTries </div>\r\n"
				+ "		<div style=\"font-size: 13px;\"> Sucesso: totalSuccesss </div>\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Total Login por usuario: </div>\r\n"
				+ "		users\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Total Login por setor: </div>\r\n"
				+ "		sectors\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Numero de Erros/Warns  </div>\r\n"
				+ "		<div style=\"font-size: 13px;\"> Warning: totalWarn </div>\r\n"
				+ "		<div style=\"font-size: 13px;\"> Error:	totalErros </div>\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Total erros por aplicação: </div>\r\n"
				+ "		errosModule\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\"> Total de requisições no sistema </div>\r\n"
				+ "		<div style=\"font-size: 13px;\"> Requisições: totalReq </div>\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\">Quantidade de registros manipulados nas aplicações da Suite</div>\r\n"
				+ "		appsInsert\r\n"
				+ "		<br>\r\n"
				+ "		<div style=\"font-size: 15px;font-weight: bold;\">Quantidade de registros manipulados nas setores da aplicação</div>\r\n"
				+ "		sectorInsert"
				+ "</div>";
	}

}
