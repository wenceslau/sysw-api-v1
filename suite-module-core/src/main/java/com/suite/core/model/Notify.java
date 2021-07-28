package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Classe que representa a entidade Notificacao
 * Classe nao estende a ModelCore porque a entidade
 * nao possui os atributos da superclasse
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_notify")
public class Notify {

	/*
	 * PK do entidade
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	/*
	 * Data hora do envio da notificacao
	 */
	@NotNull
	@Column(name = "dtt_notify")
	private LocalDateTime dateTimeNotify;

	/*
	 * Mensagem da notificacao
	 */
	@NotNull
	@Column(name = "val_message")
	private String message;

	/*
	 * Codigo do usuario que enviou a notificacao
	 */
	@NotNull
	@Column(name = "cod_user")
	private Long userCode;

	/*
	 * Codigo do Setor que o usuario estava logado no momento do envio da notificacao
	 */
	@Column(name = "cod_sector")
	private Long sectorCode;

	/*
	 * Nome do setor
	 */
	@Column(name = "val_sector_name")
	private String sectorName;

	/*
	 * Username do usuario que enviou a notificacao
	 */
	@NotNull
	@Column(name = "val_user_name")
	private String userName;

	/*
	 * Tipo da notificacao
	 * TODO: trocar para enum
	 */
	@NotNull
	@Column(name = "val_type")
	private String type;

	public LocalDateTime getDateTimeNotify() {

		return dateTimeNotify;

	}

	public void setDateTimeNotify(LocalDateTime dateRecord) {

		this.dateTimeNotify = dateRecord;

	}

	public String getMessage() {

		return message;

	}

	public void setMessage(String message) {

		this.message = message;

	}

	public Long getUserCode() {

		return userCode;

	}

	public void setUserCode(Long userCode) {

		this.userCode = userCode;

	}

	public String getUserName() {

		return userName;

	}

	public void setUserName(String userName) {

		this.userName = userName;

	}

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public Long getSectorCode() {

		return sectorCode;

	}

	public void setSectorCode(Long sectorCode) {

		this.sectorCode = sectorCode;

	}

	public String getSectorName() {

		return sectorName;

	}

	public void setSectorName(String sectorName) {

		this.sectorName = sectorName;

	}

	public String getType() {

		return type;

	}

	public void setType(String type) {

		this.type = type;

	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notify other = (Notify) obj;

		if (code == null) {

			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

	public static Notify build(Notify source) {

		if (source == null)
			return null;

		Notify target = new Notify();
		copyProperties(source, target);

		return target;

	}

	public static List<Notify> build(List<Notify> sourceList) {

		List<Notify> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}
}
