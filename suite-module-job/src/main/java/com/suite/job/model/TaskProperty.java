package com.suite.job.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

/**
 * Classe que representa a entidade Propriedade de Servico de Dados
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_job_task_property")
public class TaskProperty extends ModelCore {

	@NotNull
	@Column(name = "nam_property")
	private String name;

	@Column(name = "des_property")
	private String description;

	@Column(name = "val_property")
	private String value;

	@ManyToOne
	@JoinColumn(name = "cod_task_fk")
	private Task task;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}