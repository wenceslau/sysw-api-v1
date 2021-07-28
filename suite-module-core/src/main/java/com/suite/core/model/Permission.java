
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Permission;

/**
 * Classe que representa a entidade Permissao
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_permission")
public class Permission extends ModelCore {

	/*
	 * Nome da aplicacao a quala permmissao pertence
	 * TODO: Remover apos trocar pelo atributo application
	 * 
	 */
	@NotNull
	@Column(name = "nam_application")
	private String strApplication = "";

	/*
	 * Aplicacao a qual q permissao pertence
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "cod_application_fk")
	private Application application;

	/*
	 * Papel da permissao Ex: UPDATE, DELETE, VIWER, LIST
	 * TODO: Usar os dados que vier de um DataList
	 */
	@NotNull
	@Column(name = "val_role")
	private String role;

	/*
	 * Nome do modulo da pemissao. Nao confundir com o modulo da arquitetura do codigo fonte
	 * Modulo eh uma area de maniuplacao dos dados da entidade.
	 * Ex: PARAMETER, SECTOR, PROFILE sao modulos da aplicacao CORE.
	 * O modulo devera vir de uma lista de modulos da aplicacao
	 */
	@NotNull
	@Column(name = "nam_module")
	private String module;

	/*
	 * Key eh um identificador da permissao
	 * Eh a concatencacao do application + module + role
	 * Ex: CORE_SECTOR_INSERT
	 */
	@Column(name = "val_key")
	private String key;

	/*
	 * Descricao da permissao
	 */
	@NotNull
	@Column(name = "des_permission")
	private String description;

	/*
	 * Tipo de componete que essa permissao pode ser associada na UI
	 * Baseado no tipo do componente, outras propriedades serao obrigatorias
	 * Ex: MENU, BUTTON
	 */
	@Column(name = "val_component")
	private String component;

	/*
	 * Ordem de exibicao de permissoes do tipo MENU
	 */
	@Column(name = "val_sequence_root")
	private Integer sequenceRoot;

	/*
	 * Para permissoes do Tipo MENU, root é o menu raiz da permisssao
	 */
	@Column(name = "val_root")
	private String root;

	/*
	 * Rotulo a ser exibido para permissoes do tipo MENU
	 */
	@Column(name = "val_label")
	private String label;

	/*
	 * Icone a ser exibido no menu para permissoes do tipo MENU
	 */
	@Column(name = "val_icon")
	private String icon;

	/*
	 * Proprieade era usada para exibir o menu como icone no toolbar
	 * foi depreciada. Nao tem mais funcao toolbar na tela, o tollbar
	 * virou um menu vertical
	 * TODO: Avaliar e remover de todo o sistema
	 * Talvez possa ser usada para mostrar ou nao no meu vertical
	 */
	@Deprecated
	@Column(name = "ind_toolbar")
	private Boolean toolbar;

	/*
	 * Propriedade possivelmente ser depreciada, era usado como a propriedade
	 * toolbar
	 * TODO: Avaliar se nao precisar der depreciada
	 */
	@Column(name = "ind_root_toolbar")
	private Boolean rootToolbar;

	/*
	 * Ordem de exibicao de permissoes do tipo MENU
	 */
	@Column(name = "val_sequence")
	private Integer sequence;

	/*
	 * Nome da rota na UI Angular, usada em permissoes do tipo MENU
	 */
	@Column(name = "val_router")
	private String router;

	/*
	 * Permissoes predecessoras da permissao em questao
	 * Ao ser adicionada a um perfil, todas as suas predecessoras
	 * sao adicionadas junto
	 */
	@Column(name = "val_predecessor_permission")
	private String predecessorPermission;

	/*
	 * String de exibicao da permissao na UI
	 * Formado por: codigo + nomeAplicacao + modulo + descricao + (Inativo)
	 * se estiver inativa.
	 * A notacao @Transiente indica que a propriedade nao existe na entidade do banco
	 */
	@Transient
	private String display;

	/*
	 * Tipo da permissao
	 * 0 - Exclusiva SA = permissoes atribuidas automaticamente ao perfil SA e que nao podem ser atribuidas a outros perfis
	 * 1 - Super Admin = permissoes atribuidas automaticamente ao perfil SA e que podem ser atribuidas a outros perfis
	 * 2 - UN Admin = permissoes atribuidas automaticamente aos perfis SA e UN e que podem ser atribuidas a outros perfis
	 * 3 - Business Admin = permissoes atribuidas automaticamente aos perfis SA e UN e BA e que podem ser atribuidas a outros perfis
	 * 4 - Usuário = permissoes atribuidas automaticamente aos pefis SA e UN e BA e perfis customizados e que podem ser atribuidas a
	 * outros perfis
	 * TODO: Avaliar se pode ser usado enum
	 */
	@Column(name = "val_type")
	private Integer type;

	public String getStrApplication() {

		return strApplication;

	}

	public void setStrApplication(String strApplication) {

		this.strApplication = strApplication;

	}

	public Application getApplication() {

		return application;

	}

	public void setApplication(Application application) {

		this.application = application;

	}

	public String getRole() {

		return role;

	}

	public void setRole(String role) {

		this.role = role;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public String getModule() {

		return module;

	}

	public void setModule(String module) {

		this.module = module;

	}

	public String getComponent() {

		return component;

	}

	public void setComponent(String component) {

		this.component = component;

	}

	public String getLabel() {

		return label;

	}

	public void setLabel(String label) {

		this.label = label;

	}

	public String getIcon() {

		return icon;

	}

	public void setIcon(String icon) {

		this.icon = icon;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getPredecessorPermission() {

		return predecessorPermission;

	}

	public void setPredecessorPermission(String predecessorPermission) {

		this.predecessorPermission = predecessorPermission;

	}

	public String getDisplay() {

		String strApplication = application != null ? application.getName() : "null";
//		this.display = "[ " + getCode() + " " + strApplication + " " + module + "] " + description
//				+ (getStatus() ? "" : " (Inativo)");
		this.display = "[" + getCode() + " " + strApplication + "] " + description
				+ (getStatus() ? "" : " (Inativo)");
		return display;

	}

	public void setDisplay(String diplay) {

		this.display = diplay;

	}

	public Boolean getToolbar() {

		return toolbar;

	}

	public void setToolbar(Boolean toolbar) {

		this.toolbar = toolbar;

	}

	public String getRouter() {

		return router;

	}

	public void setRouter(String router) {

		this.router = router;

	}

	public Integer getSequence() {

		return sequence;

	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;

	}

	public String getRoot() {

		return root;

	}

	public void setRoot(String root) {

		this.root = root;

	}

	public Integer getType() {

		return type;

	}

	public void setType(Integer typePerfil) {

		this.type = typePerfil;

	}

	public Boolean getRootToolbar() {

		return rootToolbar;

	}

	public void setRootToolbar(Boolean rootToolbar) {

		this.rootToolbar = rootToolbar;

	}

	public Integer getSequenceRoot() {

		return sequenceRoot;

	}

	public void setSequenceRoot(Integer sequenceRoot) {

		this.sequenceRoot = sequenceRoot;

	}

	public static Permission build(Permission source) {

		if (source == null)
			return null;

		Permission target = new Permission();
		//copyProperties(source, target);
		copyProperties(source, target, "application");
		target.setApplication(Application.build(source.getApplication()));

		return target;

	}

	public static List<Permission> build(List<Permission> sourceList) {

		List<Permission> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

}