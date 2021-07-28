package com.suite.job.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ModelJob;
import com.suite.job.model.Task;
import com.suite.job.model.TaskProperty;

/**
 * Classe que representa a entidade Parametros
 * @author Wenceslau
 *
 */
public class TaskVwr extends ModelJob {

	private String name;

	private String description;

	private Frequency frequency;

	private String scheduler;

	private State state;

	private String benRun;

	private LocalDateTime lastRun;

	private Long codeBusinessUnit;

	private Boolean notify = false;

	@JsonIgnoreProperties("task")
	private List<TaskProperty> taskProperties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public String getScheduler() {
		return scheduler;
	}

	public void setScheduler(String scheduler) {
		this.scheduler = scheduler;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LocalDateTime getLastRun() {
		return lastRun;
	}

	public void setLastRun(LocalDateTime lastRun) {
		this.lastRun = lastRun;
	}

	public String getBenRun() {
		return benRun;
	}

	public void setBenRun(String classRun) {
		this.benRun = classRun;
	}

	public Long getCodeBusinessUnit() {
		return codeBusinessUnit;
	}

	public void setCodeBusinessUnit(Long codeBusinessUnit) {
		this.codeBusinessUnit = codeBusinessUnit;
	}

	public List<TaskProperty> getTaskProperties() {
		return taskProperties;
	}

	public void setTaskProperties(List<TaskProperty> taskProperties) {
		this.taskProperties = taskProperties;
	}

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

	public static TaskVwr build(Task source) {
		if (source == null)
			return null;

		TaskVwr target = new TaskVwr();
		copyProperties(source, target);

		return target;

	}

	public static List<TaskVwr> build(List<Task> sourceList) {
		List<TaskVwr> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

}