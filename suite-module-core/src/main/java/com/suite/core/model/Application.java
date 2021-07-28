package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Application;

/**
 * Classe que representa a entidade Aplicacao
 * Na arquitetura de codigo fonte, uma aplicacao representa um modulo
 * TODO: Criar lista de modulos dentro da Aplicacao
 * Ex: PARAMETER, SECTOR, PROFILE sao modulos da aplicacao CORE
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_application")
public class Application extends ModelCore {

	/*
	 * Nome da aplicacao
	 */
	@NotNull
	@Column(name = "nam_application")
	private String name;

	/*
	 * Nome resumido da aplicacao
	 */
	@NotNull
	@Column(name = "val_display_name")
	private String displayName;

	/*
	 * Cor principal da aplicacao
	 */
	@Column(name = "val_main_color")
	private String mainColor =  "";

	/*
	 * Logo a ser usada no header da pagina principal
	 */
	@Column(name = "val_image")
	private String image;

	/*
	 * Indica se corresponde a aplicacao principal
	 */
	@Column(name = "ind_main")
	private Boolean main = false;

	/*
	 * Data na inicializacao da aplicacao
	 * Eh atualizado quando o Initializer do modulo executa
	 */
	@Column(name = "dtt_initializer")
	private LocalDateTime dateInitializer;

	/*
	 * Nome do modulo no codigo fonte
	 */
	@Column(name = "nam_module_source")
	private String nameModuleSource;

	/*
	 *lince esta sendo usando como o idetificador (TODO RENOMEAR INCLUSIVE NO BANCO)
	 */
	@Column(name = "val_licence")
	private String licence;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getDisplayName() {

		return displayName;

	}

	public void setDisplayName(String displayName) {

		this.displayName = displayName;

	}

	public String getMainColor() {

		return mainColor;

	}

	public void setMainColor(String mainColor) {

		this.mainColor = mainColor;

	}

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public Boolean getMain() {

		return main;

	}

	public void setMain(Boolean main) {

		this.main = main;

	}

	public LocalDateTime getDateInitializer() {

		return dateInitializer;

	}

	public void setDateInitializer(LocalDateTime dateInitializer) {

		this.dateInitializer = dateInitializer;

	}

	public String getNameModuleSource() {

		return nameModuleSource;

	}

	public void setNameModuleSource(String nameModuleSource) {

		this.nameModuleSource = nameModuleSource;

	}

	public String getLicence() {

		return licence;

	}

	public void setLicence(String licence) {

		this.licence = licence;

	}

	public static Application build(Application source) {

		if (source == null)
			return null;

		Application target = new Application();
		copyProperties(source, target);

		return target;

	}

	public static List<Application> build(List<Application> sourceList) {

		List<Application> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}
}