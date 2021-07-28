package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

/**
 * Classe que representa a entidade Parametros
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_global_parameter")
public class Parameter extends ModelCore {

	/*
	 * Nome do grupo do parametro.
	 * Usado para separar os parametros em grupos
	 */
	@NotNull
	@Column(name = "nam_group")
	private String group;

	/*
	 * Chave do parametro
	 */
	@NotNull
	@Column(name = "val_key")
	private String key;

	/*
	 * Valor do parametro
	 */
	@NotNull
	@Column(name = "val_parameter")
	private String value;

	/*
	 * Descricao do parametro
	 */
	@Column(name = "des_parameter")
	private String description;

	public String getGroup() {

		return group;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getValue() {

		return value;

	}

	public void setValue(String value) {

		this.value = value;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public void setGroup(String group) {

		this.group = group;

	}

	public static Parameter build(Parameter source) {

		if (source == null)
			return null;

		Parameter target = new Parameter();
		copyProperties(source, target);

		return target;

	}

	public static List<Parameter> build(List<Parameter> sourceList) {

		List<Parameter> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}
}