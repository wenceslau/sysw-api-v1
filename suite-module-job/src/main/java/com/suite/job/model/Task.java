package com.suite.job.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.job.base.EnumJob.Frequency;
import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ModelJob;

/**
 * Classe que representa a entidade Parametros
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_job_task")
public class Task extends ModelJob {

	@NotNull
	@Column(name = "nam_task")
	private String name;

	@Column(name = "des_task")
	private String description;

	@Column(name = "val_frequency")
	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	@Column(name = "val_scheduler")
	private String scheduler;

	@Column(name = "val_state")
	@Enumerated(EnumType.STRING)
	private State state;

	@Column(name = "val_ben_run")
	private String benRun;

	@Column(name = "dtt_last_run")
	private LocalDateTime lastRun;

	@Column(name = "cod_business_unit_fk")
	private Long codeBusinessUnit;

	@NotNull
	@Column(name = "ind_notify")
	private Boolean notify = false;

	/*
	 * A notacao orphanRemoval, quando o objeto pai for deletado, deleta tb o objeto filho, diferente de delete_cascade
	 * OneToMany, migra a chave do pai para a tabela filho
	 */
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("task") // Ignora dentro do objeto property o objeto service, evita overflow no json
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

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

	public List<TaskProperty> getTaskProperties() {
		if (taskProperties == null)
			taskProperties = new ArrayList<>();
		return taskProperties;
	}

	public void setTaskProperties(List<TaskProperty> taskProperties) {
		this.taskProperties = taskProperties;
	}

	public static Task build(Task source) {
		if (source == null)
			return null;

		Task target = new Task();
		copyProperties(source, target);

		return target;

	}

	public static List<Task> build(List<Task> sourceList) {
		List<Task> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

	public String toPrint() {
		String str = "Task \r\n \r\n "
				+ "name = " + name + "\r\n "
				+ "description = " + description + "\r\n "
				+ "frequency = " + frequency + "\r\n "
				+ "scheduler = " + scheduler + "\r\n "
				+ "state = " + state + "\r\n "
				+ "benRun = " + benRun + "\r\n "
				+ "lastRun = " + lastRun + "\r\n "
				+ "codeBusinessUnit = " + codeBusinessUnit + "\r\n "
				+ "notify = " + notify + "\r\n \r\n ";
		String prop = "";
		for (TaskProperty tp : taskProperties)
			prop += tp.getName() + " = " + tp.getValue() + "\r\n ";

		str += "Properties " + "\r\n \r\n"
				+ prop;

		return str;
	}

	@Transient
	public String getValueProperty(String nameProperty, String... valueDefault) {
		if (taskProperties == null)
			throw new RuntimeException("No properties found in task " + name);

		String name = "TASK_" + nameProperty.toUpperCase().replace("TASK_","");

		TaskProperty property = taskProperties.stream().filter(x -> x.getName().equals(name))
				.findFirst().orElse(null);

		if (property == null && valueDefault.length != 0)
			return valueDefault[0];

		if (property == null)
			throw new RuntimeException("The propertie [" + nameProperty + " ] has not been found");

		return property.getValue();

	}
	
	@Transient
	public void setValueProperty(String nameProperty, String value) {
		if (taskProperties == null)
			throw new RuntimeException("No properties found in task " + name);

		String name = "TASK_" + nameProperty.toUpperCase().replace("TASK_","");

		TaskProperty property = taskProperties.stream().filter(x -> x.getName().equals(name))
				.findFirst().orElse(null);

		if (property == null)
			throw new RuntimeException("The propertie [" + nameProperty + " ] has not been found");

		property.setValue(value);

	}
	
}