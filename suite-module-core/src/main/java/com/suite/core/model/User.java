
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suite.core.base.ModelCore;
import com.suite.core.model.User;
import com.suite.security.cryptography.SHACryptography;

/**
 * Classe que representa a entidade Usuario
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_user")
public class User extends ModelCore {

	/*
	 * Nome do usuario
	 */
	@NotNull
	@Column(name = "val_name")
	private String name;

	/*
	 * Login do usuario
	 */
	@NotNull
	@Column(name = "val_username")
	private String username;

	/*
	 * Email do usuario
	 */
	@NotNull
	@Column(name = "val_email")
	private String email;

	/*
	 * Senha criptografada do usuario
	 * TODO: Aplicar criptografia irreversivel
	 * A notacao @JsonIgnore indica que a propriedade nao aparece no JSON
	 */
	@JsonIgnore
	@Column(name = "val_password")
	private String password;

	/*
	 * Perfil do usuario
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_profile_fk")
	private Profile profile;

	/*
	 * Hash para inclusao de setores feitas no usuario
	 * Quando um setor eh criado ele é adicionado ao user SA
	 * e aos user UNADIM. essa inclusao altera a entidade tb_core_user_sector
	 * o hash eh adicionado para indicar que houve alteracao no usuario.
	 * e assim sera identificado pelo historico
	 */
	@Column(name = "val_hash")
	private Integer sectorHash;

	@Column(name = "user_hash")
	private String userHash;

	/*
	 * Hash para alteracao de senha do usuario
	 * A Senha nao eh exibida no JSON. O historico de alteracoes usa o JSON
	 * armazenado para exibir os dados na UI. Se a senha nao percebe que a alteracao
	 * ocorrida no usuario foi na senha. O hashPassword eh alterado qudo ha aleracao de
	 * senha e assim visualmente da para entender que a alteracao do usuario foi feito na senha
	 * 
	 * @Transient essa informacao nao eh armazenda no banco
	 */
	@Transient
	private Integer hashPassword;

	/*
	 * Indica se o usuario pode receber notificacoes de outros usuarios do mesmo setor
	 */
	@Column(name = "ind_receive_notify")
	private Boolean receiveNotify = false;

	/*
	 * Indica se o usuario pode enviar notificacoes de suas acoes realizadas
	 */
	@Column(name = "ind_send_notify")
	private Boolean sendNotify = false;

	/*
	 * Indica se o usuario pode visualizar notificacoes
	 */
	@Column(name = "ind_view_notify")
	private Boolean viewNotify = false;

	/*
	 * Lista de setores do usuario
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_core_user_sector", joinColumns = @JoinColumn(name = "cod_user"), inverseJoinColumns = @JoinColumn(name = "cod_sector"))
	private List<Sector> sectors;

	/*
	 * indica se o usuario ja acessou o sistema ou se eh o primeiro acesso
	 */
	@Column(name = "ind_firstAccess")
	private Boolean firstAccess;

	/*
	 * indica se o usuario ja acessou o sistema ou se eh o primeiro acesso
	 */
	@Column(name = "ind_locked")
	private Boolean locked;

	/*
	 * indica se o usuario ja acessou o sistema ou se eh o primeiro acesso
	 */
	@Column(name = "val_count_locked")
	private Integer countLocked;

	/*
	 * Unidade de negocio a qual o usuario pertence
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_business_unit_fk")
	private BusinessUnit businessUnit;

	/*
	 * Indica se o usuario pode receber notificacoes de outros usuarios do mesmo setor
	 */
	@Column(name = "ind_windows_autentication")
	private Boolean windowsAutentication = false;

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public String getEmail() {

		return email;

	}

	public void setEmail(String email) {

		this.email = email;

	}

	public String getPassword() {

		return password;

	}

	public void setPassword(String password) {

		this.password = password;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public Profile getProfile() {

		return profile;

	}

	public void setProfile(Profile profile) {

		this.profile = profile;

	}

	public List<Sector> getSectors() {

		if (sectors == null)
			sectors = new ArrayList<>();
		return sectors;

	}

	public void setSectors(List<Sector> sectors) {

		this.sectors = sectors;

	}

	public Boolean getFirstAccess() {

		return firstAccess;

	}

	public void setFirstAccess(Boolean firstAccess) {

		this.firstAccess = firstAccess;

	}

	public Integer getSectorHash() {

		return sectorHash;

	}

	public void setSectorHash(Integer sectorHash) {

		this.sectorHash = sectorHash;

	}

	public Integer getHashPassword() {

		return hashPassword;

	}

	public void setHashPassword(Integer hashPassword) {

		this.hashPassword = hashPassword;

	}

	public Boolean getReceiveNotify() {

		return receiveNotify;

	}

	public void setReceiveNotify(Boolean receiveNotify) {

		this.receiveNotify = receiveNotify;

	}

	public Boolean getViewNotify() {

		return viewNotify;

	}

	public void setViewNotify(Boolean viewNotify) {

		this.viewNotify = viewNotify;

	}

	public Boolean getSendNotify() {

		return sendNotify;

	}

	public void setSendNotify(Boolean sendNotify) {

		this.sendNotify = sendNotify;

	}

	public BusinessUnit getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnit businessUnit) {

		this.businessUnit = businessUnit;

	}

	public String getUserHash() {

		return userHash;

	}

	public void setUserHash(String userHash) {

		this.userHash = userHash;

	}

	public Boolean getLocked() {

		return locked;

	}

	public void setLocked(Boolean locked) {

		this.locked = locked;

	}

	public Integer getCountLocked() {

		return countLocked;

	}

	public void setCountLocked(Integer countLocked) {

		this.countLocked = countLocked;

	}

	public Boolean getWindowsAutentication() {

		return windowsAutentication;

	}

	public void setWindowsAutentication(Boolean windowsAutentication) {

		this.windowsAutentication = windowsAutentication;

	}

	@JsonIgnore
	public String generateUserHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {

		String hash = getName() +
				getUsername() +
				getEmail() +
				getProfile().getCode() +
				getSectorHash() +
				getPassword() +
				getReceiveNotify() +
				getSendNotify() +
				getStatus() +
				getLocked();

		return SHACryptography.encrypt(hash);

	}

	@JsonIgnore
	public boolean checkUserHash() {

		try {
			return (getUserHash().equals(generateUserHash()));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return false;

		}

	}

	public static User build(User source) {

		if (source == null)
			return null;

		User target = new User();
		//copyProperties(source, target);
		copyProperties(source, target, "profile", "businessUnit", "sectors");
		target.setBusinessUnit(BusinessUnit.build(source.getBusinessUnit()));
		target.setProfile(Profile.build(source.getProfile()));
		source.getSectors().stream().forEach((object -> {
			target.getSectors().add(Sector.build(object));

		}));

		return target;

	}

	public static List<User> build(List<User> sourceList) {

		List<User> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}
