package com.suite.job.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.app.base.Enum.Module;
import com.suite.app.dto.BoolDTO;
import com.suite.app.module.IJobGenericService;
import com.suite.app.repository.UserActionFilter;
import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ServiceJob;
import com.suite.job.model.Task;
import com.suite.job.model.UserActionJob;

@Service
public class JobGenericService extends ServiceJob implements IJobGenericService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserActionJobService userActionJobService;

	@Override
	public Module module() {
		return Module.JOB;
	}

	@Override
	public ActionApp countActionsApp(UserActionFilter userActionFilter) {
		UserActionFilter usrActFilter = new UserActionFilter();
		usrActFilter.setDateRecordStart(userActionFilter.getDateRecordStart());
		usrActFilter.setDateRecordEnd(userActionFilter.getDateRecordEnd());

		List<UserActionJob> listUsrActCore = userActionJobService.filter(usrActFilter, UserActionJob.class);

		ActionApp ab = new ActionApp();

		ab.countInsert = listUsrActCore.stream().filter(x -> x.getAction().equals("Insert")).count();
		ab.countUpdate = listUsrActCore.stream().filter(x -> x.getAction().equals("Update")).count();
		ab.countDelete = listUsrActCore.stream().filter(x -> x.getAction().equals("Delete")).count();

		return ab;
	}

	@Override
	public BoolDTO startTaskUpdateByObject(Long codObject) {
		Task task = taskService.findByBenRun("updateLabelByObject");

		if (task != null) {

			if (task.getState().equals(State.RUNNING) || task.getState().equals(State.WAIT)) {
				String code = task.getValueProperty("code_object", "-1");
				return new BoolDTO(false, "Ja existe uma atualização de label em curso para o objeto codigo " + code+". Aguarde sua finalização", code);
			}

			task.setValueProperty("code_object", codObject + "");
			task.setState(State.WAIT);
			taskService.save(task);
			
			return new BoolDTO(true, "A atualização de label foi iniciada e você será notificado quando o processamento terminar");
		}

		return new BoolDTO(false, "bean not found");
	}

	@Override
	public BoolDTO statusTaskByBean(String bean) {
		Task task = taskService.findByBenRun(bean);
		return new BoolDTO(true, "Sucessful", task.getState());
	}

}
