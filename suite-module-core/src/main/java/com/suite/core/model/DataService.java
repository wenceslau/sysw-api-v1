package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.base.ModelCore;
import com.suite.core.model.DataService;

/**
 * Classe que representa a entidade Servico de Dados
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_data_service")
public class DataService extends ModelCore {

	/*
	 * Tipo do DataService. Ver Enum DataServiceType
	 */
	@NotNull
	@Column(name = "val_type")
	@Enumerated(EnumType.STRING)
	private DataServiceType type;

	/*
	 * Identificador do Servico de Dados .
	 * Usado para definir o quem irar usar o DataService.
	 * Ex: DataService tipo EMAILSERVICE para envio de notificacoes padrao do sistema
	 * O sistema para enviar notificacoes ira procurar por um DataService com o
	 * identificador CDPDB0000000001 (sysmonkey)
	 * Se o cliente cadastrar outro DataService com o mesmo tipo.
	 * precisa ser outro identificador
	 */
	@Column(name = "val_identifier")
	private String identifier;

	/*
	 */
	@Column(name = "val_scope")
	private String scope;

	/*
	 * Nome do Servico de Dados
	 */
	@NotNull
	@Column(name = "nam_data_service")
	private String name;

	/*
	 * Descricao do Servico de Dados
	 */
	@Column(name = "des_data_service")
	private String description;

	/*
	 * Unidade de Negocio a qual o Servico de Dados pertence
	 */
	@ManyToOne
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	/*
	 * Lista de propriedades do Servico de Dados
	 * 
	 * A notacao orphanRemoval, quando o objeto pai for deletado, deleta tb o objeto filho, diferente de delete_cascade
	 * OneToMany, migra a chave do pai para a tabela filho
	 */
	@OneToMany(mappedBy = "dataService", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("dataService") // Ignora dentro do objeto property o objeto service, evita overflow no json
	private List<Property> properties;

	/*
	 * codigos dos setores que pode visualizar esse registro
	 * usado apenas para registros datatask
	 */
	@Column(name = "cod_sectors_data_task")
	private String codSectorsDataTask;

	@Column(name = "cod_sector_data_task_parent")
	private Long codSectorDataTaskParent;

	public DataServiceType getType() {
		return type;

	}

	public void setType(DataServiceType type) {
		this.type = type;

	}

	public String getIdentifier() {
		return identifier;

	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

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

	public List<Property> getProperties() {
		return properties;

	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;

	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;

	}

	public String getScope() {
		return scope;

	}

	public void setScope(String scope) {
		this.scope = scope;

	}

	public String getCodSectorsDataTask() {
		return codSectorsDataTask;

	}

	public void setCodSectorsDataTask(String codSectorsDataTask) {
		this.codSectorsDataTask = codSectorsDataTask;

	}

	public Long getCodSectorDataTaskParent() {
		return codSectorDataTaskParent;
	}

	public void setCodSectorDataTaskParent(Long codSectorDataTaskParent) {
		this.codSectorDataTaskParent = codSectorDataTaskParent;
	}
	

	@JsonIgnore

	public String getValueProperty(String nameProperty, String... valueDefault) {
		
		nameProperty = nameProperty.replace(getType() + "_", "");
		String name = getType() + "_" + nameProperty.toUpperCase();

		if (getProperties() == null)
			throw new RuntimeException("A lista de propriedades esta vazia");

		Property property = getProperties().stream().filter(x -> x.getName().equals(name))
				.findFirst().orElse(null);

		if (property == null && valueDefault.length != 0)
			return valueDefault[0];

		if (property == null)
			throw new RuntimeException("A propriedade [" + nameProperty + " ] não foi encontrada");

		return property.getValue();

	}

	public static DataService build(DataService source) {
		if (source == null)
			return null;

		DataService target = new DataService();
		// copyProperties(source, target);
		copyProperties(source, target, "businessUnit");
		target.setBusinessUnit(BusinessUnit.build(source.getBusinessUnit()));

		return target;

	}

	public static List<DataService> build(List<DataService> sourceList) {
		List<DataService> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

}