package com.suite.core.model;

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
@Table(name = "tb_core_property")
public class Property extends ModelCore {

	/*
	 * Tipo de dados da propriedade
	 * TEXTO e PASSWORD
	 * TODO: Avaliar o uso de enum
	 */
	@NotNull
	@Column(name = "val_data_type")
	private String dataType;

	/*
	 * Nome da propriedade
	 */
	@NotNull
	@Column(name = "nam_property")
	private String name;

	/*
	 * Valor da propriedade
	 */
	@Column(name = "val_property")
	private String value;

	/*
	 * Descricao da propriedade
	 */
	@Column(name = "des_property")
	private String description;

	/*
	 * Servico de dados a qual a propriedade pertence
	 */
	@ManyToOne
	@JoinColumn(name = "cod_data_service_fk")
	private DataService dataService;

	public String getDataType() {

		return dataType;

	}

	public void setDataType(String dataType) {

		this.dataType = dataType;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

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

	public DataService getDataService() {

		return dataService;

	}

	public void setDataService(DataService dataService) {

		this.dataService = dataService;

	}

}