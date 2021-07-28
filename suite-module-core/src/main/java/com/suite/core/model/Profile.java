
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.suite.core.base.ModelCore;
import com.suite.core.model.Profile;

/**
 * Classe que representa a entidade perfil
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_profile")
public class Profile extends ModelCore {

	/*
	 * Nome do perfil
	 */
	@NotNull
	@Column(name = "nam_profile")
	private String name;

	/*
	 * Tipo do perfil
	 * 1- SADM, 2- UNADM, 3- ADM, 4- Nativo, 5- User
	 * TODO: Avaliar se pode ser usado enum
	 */
	@Column(name = "typ_profile")
	private Integer type;

	/*
	 * Usado para definir que o registro foi alterado quando
	 * existe adicao ou remova de permissoes. Essa alteracao e necessaria
	 * para que o hibernate entenda que a entidade foi alterado e
	 * dispare o envento de postUpdate.
	 * Quando apenas premissoes sao adicionadas ou alteradas
	 * a entidade alterada eh a tb_core_profile_permisions que nao possui classe
	 * que a representa
	 */
	@Column(name = "val_hash")
	private Integer hash;

	/*
	 * Lista de permissoes do pefil
	 */
	@ManyToMany(fetch = FetchType.LAZY) // ManyToMany, cria uma tabela de relacionamento entre os dois objetos
	@Fetch(value = FetchMode.SUBSELECT) // Soluciona o erro cannot simultaneously fetch multiple bags:
	@JoinTable(name = "tb_core_profile_permisions", joinColumns = @JoinColumn(name = "cod_profile"), inverseJoinColumns = @JoinColumn(name = "cod_permission"))
	private List<Permission> permissions;

	/*
	 * Unidade de negocio a qual o perfil pertence
	 * A BU do perfil pode ser nulo, sendo assim o perfil pode ser usado em todas as BU
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public List<Permission> getPermissions() {

		if (permissions == null)
			permissions = new ArrayList<>();
		return permissions;

	}

	public void setPermissions(List<Permission> permissions) {

		this.permissions = permissions;

	}

	public Integer getType() {

		return type;

	}

	public void setType(Integer type) {

		this.type = type;

	}

	public Integer getHash() {

		return hash;

	}

	public void setHash(Integer hash) {

		this.hash = hash;

	}

	public BusinessUnit getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {

		this.businessUnit = businessUnit;

	}

	public static Profile build(Profile source) {

		if (source == null)
			return null;

		Profile target = new Profile();
		//copyProperties(source, target);
		copyProperties(source, target, "businessUnit", "permissions");
		target.setBusinessUnit(BusinessUnit.build(source.getBusinessUnit()));
		source.getPermissions().stream().forEach((object -> {
			target.getPermissions().add(Permission.build(object));
		}));

		return target;

	}

	public static List<Profile> build(List<Profile> sourceList) {

		List<Profile> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}

}
