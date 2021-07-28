
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.core.base.ModelCore;
import com.suite.core.model.BusinessUnit;
import com.suite.security.cryptography.RSACryptography;

/**
 * Classe que representa a entidade Unidade Negocio
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_business_unit")
public class BusinessUnit extends ModelCore {

	/*
	 * Nome da Unidade de Negocio (BU)
	 */
	@NotNull
	@Column(name = "nam_business_unit")
	private String name;

	/*
	 * Descricao da Unidade de Negocio
	 */
	@Column(name = "des_business_unit")
	private String description;

	/*
	 * Identificador unico da Unidade de Negocio
	 */
	@NotNull
	@Column(name = "val_unique_id")
	private String uniqueId;

	/*
	 * Licenca da Unidade de Negocio para rodas as aplicacoes
	 */
	@Column(name = "val_license")
	private String license;

	@Transient
	private String valueLicense;

	@Transient
	private boolean hasDomainControl;

	@OneToMany(mappedBy = "businessUnit", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("businessUnit") // Ignora dentro do objeto property o objeto service, evita overflow no json
	private List<BusinessUnitParameter> businessUnitParameters;

	/*
	 * String concatenadas com os nomes da aplicacao ativas na Unidade de Negocio
	 */
	// @Column(name = "val_applications")
	// private String strApplications;

	/*
	 * Lista de aplicacoes ativas na Unidade de Negocio
	 */
	@ManyToMany(fetch = FetchType.EAGER) // ManyToMany, cria uma tabela de relacionamento entre os dois objetos
	@Fetch(value = FetchMode.SUBSELECT) // Soluciona o erro cannot simultaneously fetch multiple bags:
	@JoinTable(name = "tb_core_business_unit_application", joinColumns = @JoinColumn(name = "cod_business_unit"), inverseJoinColumns = @JoinColumn(name = "cod_application"))
	private List<Application> applications;

	/*
	 * Imagem da Unidade de Negocio
	 */
	@Column(name = "val_image")
	private String image;

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

	// public String getStrApplications() {
	//
	// return strApplications;
	//
	// }
	//
	// public void setStrApplications(String applications) {
	//
	// this.strApplications = applications;
	//
	// }

	public List<Application> getApplications() {

		if (applications == null)
			applications = new ArrayList<>();
		return applications;

	}

	public void setApplications(List<Application> applications) {

		this.applications = applications;

	}

	public String getLicense() {

		return license;

	}

	public void setLicense(String license) {

		this.license = license;

	}

	public boolean isHasDomainControl() {

		hasDomainControl = false;

		BusinessUnitParameter buPar;
		buPar = getBusinessUnitParameters().stream().filter(x -> x.getKey().equals("LDAP_PROVIDER_URL")).findFirst().orElse(null);

		if (buPar != null && !buPar.getValue().isEmpty()) {
			buPar = getBusinessUnitParameters().stream().filter(x -> x.getKey().equals("LDAP_DOMAIN")).findFirst().orElse(null);

			if (buPar != null && !buPar.getValue().isEmpty()) {
				buPar = getBusinessUnitParameters().stream().filter(x -> x.getKey().equals("LDAP_BASEDN")).findFirst().orElse(null);

				if (buPar != null && !buPar.getValue().isEmpty()) {
					buPar = getBusinessUnitParameters().stream().filter(x -> x.getKey().equals("LDAP_SECURITY_PRINCIPAL")).findFirst()
							.orElse(null);
					if (buPar != null && !buPar.getValue().isEmpty())
						hasDomainControl = true;

				}

			}

		}

		return hasDomainControl;

	}

	public void setHasDomainControl(boolean hasDomainControl) {

		this.hasDomainControl = hasDomainControl;

	}

	public List<BusinessUnitParameter> getBusinessUnitParameters() {

		if (businessUnitParameters == null)
			businessUnitParameters = new ArrayList<>();
		return businessUnitParameters;

	}

	public void setBusinessUnitParameters(List<BusinessUnitParameter> businessUnitParameters) {

		this.businessUnitParameters = businessUnitParameters;

	}

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public String getValueLicense() {

		if (license != null && license.length() > 5) {

			try {
				valueLicense = new RSACryptography().dencrypt(license, "");

				LocalDate ld = LocalDate.parse(valueLicense, DateTimeFormatter.ofPattern("yyyyMMdd"));

				valueLicense = ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			} catch (Exception e) {}

		}

		return valueLicense;

	}

	public void setValueLicense(String valueLicense) {

		this.valueLicense = valueLicense;

	}

	public static BusinessUnit build(BusinessUnit source) {

		if (source == null)
			return null;

		BusinessUnit target = new BusinessUnit();
		//copyProperties(source, target);
		copyProperties(source, target, "applications");
		source.getApplications().stream().forEach((object -> {
			target.getApplications().add(Application.build(object));

		}));

		return target;

	}

	public static List<BusinessUnit> build(List<BusinessUnit> sourceList) {

		List<BusinessUnit> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}