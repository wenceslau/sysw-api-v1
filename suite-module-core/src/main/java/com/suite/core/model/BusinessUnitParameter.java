package com.suite.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

/**
 * Classe que representa a entidade Parametros
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_business_unit_parameter")
public class BusinessUnitParameter extends ModelCore {

	/*
	 * Chave do parametro
	 */
	@NotNull
	@Column(name = "val_application")
	private String application;

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

	/*
	 * Servico de dados a qual a propriedade pertence
	 */
	@ManyToOne
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	public String getApplication() {

		return application;

	}

	public void setApplication(String application) {

		this.application = application;

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

	public BusinessUnit getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {

		this.businessUnit = businessUnit;

	}

}