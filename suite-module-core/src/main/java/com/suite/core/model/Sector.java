
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

/**
 * Classe que representa a entidade Setor
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_sector")
public class Sector extends ModelCore {

	/*
	 * Nome do setor
	 */
	@NotNull
	@Column(name = "nam_sector")
	private String name;

	/*
	 * Descricao do setor
	 */
	@Column(name = "des_sector")
	private String description;

	/*
	 * Identificador do setor
	 */
	@NotNull
	@Column(name = "val_unique_id")
	private String uniqueId;

	/*
	 * Imagen do setor
	 */
	@Column(name = "val_image")
	private String image;

	/*
	 * Define se os setores da Unidade de Negocio ira gerar
	 * base de dados em sua criacao
	 */
	@Column(name = "ind_required_db")
	private Boolean requiredDb = false;

	/*
	 * Aplicação do Setor
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_application_fk")
	private Application application;

	/*
	 * Servico de dados do setor
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_data_service_fk")
	private DataService dataService;

	/*
	 * Nome da base de dados do setor
	 */
	@Column(name = "nam_external_database", length = 200)
	private String nameExternalDatabase;

	/*
	 * Data da criacao da base de dados
	 */
	@Column(name = "dtt_create_database")
	private LocalDateTime dateCreateDatabase;

	/*
	 * Unidade de negocio a qual o setor pertence
	 */
	@ManyToOne
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	/*
	 * Rotulo do setor. codigo + nome
	 * Notacao @Transient indica que propriedade nao existe na entidade
	 */
	@Transient
	private String label;

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

	public String getUniqueId() {
		return uniqueId;

	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;

	}

	public String getImage() {
		return image;

	}

	public void setImage(String image) {
		this.image = image;

	}

	public Boolean getRequiredDb() {
		return requiredDb;

	}

	public void setRequiredDb(Boolean requiredDb) {
		this.requiredDb = requiredDb;

	}

	public String getNameExternalDatabase() {
		return nameExternalDatabase;

	}

	public void setNameExternalDatabase(String nameExternalDatabase) {
		this.nameExternalDatabase = nameExternalDatabase;

	}

	public LocalDateTime getDateCreateDatabase() {
		return dateCreateDatabase;

	}

	public void setDateCreateDatabase(LocalDateTime dateCreateDatabase) {
		this.dateCreateDatabase = dateCreateDatabase;

	}

	public DataService getDataService() {
		return dataService;

	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;

	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;

	}

	public String getLabel() {
		return this.getCode() + " - " + this.name;

	}

	public void setLabel(String label) {
		this.label = label;

	}

	public Application getApplication() {
		return application;

	}

	public void setApplication(Application application) {
		this.application = application;

	}

	public static Sector build(Sector source) {
		if (source == null)
			return null;

		Sector target = new Sector();
		copyProperties(source, target, "application", "dataService", "businessUnit");
		target.setBusinessUnit(BusinessUnit.build(source.getBusinessUnit()));
		target.setApplication(Application.build(source.getApplication()));
		target.setDataService(DataService.build(source.getDataService()));

		return target;

	}

	public static List<Sector> build(List<Sector> sourceList) {
		List<Sector> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

}
