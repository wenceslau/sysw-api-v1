
package com.suite.app.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Super classe para a entidade UserAction
 * Cada modulo deve implementar seu UserAction
 * e extender essa classe implementados seus metodos abstrato
 * @author Wenceslau
 *
 */
@MappedSuperclass
public abstract class UserAction {

	@Transient
	private String application;

	/*
	 * Nome da acao do usuario. UPDATE INSERT DELETE
	 */
	@NotNull
	@Column(name = "nam_action")
	private String action;

	/*
	 * Nome do objeto que esta sofrendo a acao
	 */
	@NotNull
	@Column(name = "nam_object")
	private String nameObject;

	/*
	 * ID do objeto, PK do registro na tabela
	 */
	@Column(name = "idt_record")
	private Long idRecord;

	/*
	 * Objeto em formato JSON. O registro que esta sofrendo a acao
	 */
	@NotNull
	@Column(name = "val_record")
	private String valueRecord;

	/*
	 * Codigo do usuario que esta executando a acao
	 */
	@Column(name = "usr_record_fk")
	private Long userRecordCode;

	/*
	 * Codigo do setor logado no momento da acao
	 */
	@Column(name = "cod_sector_fk")
	private Long codeSector;

	/*
	 * Data do registro
	 */
	@NotNull
	@Column(name = "dtt_record")
	private LocalDateTime dateRecord;

	public String getApplication() {

		return application;

	}

	public void setApplication(String application) {

		this.application = application;

	}

	/**
	 * Metodo abstrato para o codigo do UseAction
	 * @return
	 */
	public abstract Long getCode();

	public String getAction() {

		return action;

	}

	public void setAction(String action) {

		this.action = action;

	}

	public String getNameObject() {

		return nameObject;

	}

	public void setNameObject(String nameObject) {

		this.nameObject = nameObject;

	}

	public Long getIdRecord() {

		return idRecord;

	}

	public void setIdRecord(Long idRecord) {

		this.idRecord = idRecord;

	}

	public String getValueRecord() {

		return valueRecord;

	}

	public void setValueRecord(String valueRecord) {

		this.valueRecord = valueRecord;

	}

	public Long getUserRecordCode() {

		return userRecordCode;

	}

	public void setUserRecordCode(Long userRecordCode) {

		this.userRecordCode = userRecordCode;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}

	public LocalDateTime getDateRecord() {

		return dateRecord;

	}

	public void setDateRecord(LocalDateTime dateRecord) {

		this.dateRecord = dateRecord;

	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
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
		UserAction other = (UserAction) obj;

		if (getCode() == null) {
			if (other.idRecord != null)
				return false;

		} else if (!getCode().equals(other.getCode()))
			return false;

		return true;

	}

}