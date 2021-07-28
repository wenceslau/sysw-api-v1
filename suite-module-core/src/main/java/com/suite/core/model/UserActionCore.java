
package com.suite.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.suite.app.model.UserAction;
import com.suite.core.service.UserService;
import com.suite.core.util.UtilsCore;

/**
 * Classe que representa a entidade UserActionCore do modulo
 * Cada modulo deve ter sua propria classe UserAction
 * Ele estende a classe UserAction definida no modulo App
 * cujo possui atributos comuns a todos os UserAction
 * e metodos abstratos que precisa ser implementados
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_user_action")
public class UserActionCore extends UserAction {

	/*
	 * PK do registro
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	protected Long code;

	/*
	 * Objeto user responsavel pela acao
	 * 
	 * @Transient - dado nao eh armazenado no banco
	 * 
	 * @JsonIgnoreProperties - ignoras as propriedades do usuario no JSON desse objeto
	 */
	@Transient
	@JsonIgnoreProperties({ "profile", "sectors", "businessUnit" })
	private User userRecord;

	@Override
	public String getApplication() {
		this.setApplication("CORE");
		return super.getApplication();

	}

	@Override
	public Long getCode() {
		return code;

	}

	public void setCode(Long code) {
		this.code = code;

	}

	public User getUserRecord() {

		if (userRecord == null) {
			UserService userService = (UserService) UtilsCore.getBean("userService");

			if (getUserRecordCode() == null || getUserRecordCode() == -1) {
				userRecord = new User();
				userRecord.setCode(-1L);
				userRecord.setName("System");
			} else if (getUserRecordCode() == -2) {
				userRecord = new User();
				userRecord.setCode(-2L);
				userRecord.setName("Job");
			} else {
				userRecord = (userService.findByCode(getUserRecordCode()));
//				userRecord = new User();
//				userRecord.setCode(getUserRecordCode());
//				userRecord.setName(userService.findNameByCode(userRecord.getCode()));
			}		 

		}

		return userRecord;

	}

	public void setUserRecord(User userRecord) {
		this.userRecord = userRecord;

	}

}