package com.suite.app.base;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Super classe Model para ser estendida por todas as classe Model da Suite
 * @author Wenceslau
 *
 */
public abstract class Model extends Base {

	/**
	 * Usada para receber confirmação vinda da UI Quando question igual true, foi
	 * enviado uma msg par UI E o confirmAction passa a ser obrigatorio Sim ou nao
	 * para executar a acao Null indida o fluxo normal
	 */
	@Transient
	private Boolean confirmAction;

	/**
	 * Usada para enviar mensagem para a UI True indica que uma msg esta sendo
	 * enviada a UI e espera uma resposta sim ou nao null indica fluxo normal
	 */
	@Transient
	private Boolean question;

	/**
	 * Usada para enviar mensagem para a UI Message a enviar a UI junto com question
	 * igual a true
	 */
	@Transient
	private String message;

//	/**
//	 * Objeto usuario que manipulou esse recurso
//	 * Ele eh transiente, nao eh armazenado na entidade
//	 * Eh definido com o usuario do contexto
//	 */
//	@Transient
//	private Long codeUserRecord;
//
//	/**
//	 * Objeto usuario que manipulou esse recurso
//	 * Ele eh transiente, nao eh armazenado na entidade
//	 * Eh definido com o usuario do contexto
//	 */
//	@Transient
//	private Long codeSectorRecord;

	/* Methods Get Set */

	public Boolean getConfirmAction() {
		return confirmAction;

	}

	public void setConfirmAction(Boolean confirmAction) {
		this.confirmAction = confirmAction;

	}

	public Boolean getQuestion() {
		return question;

	}

	public void setQuestion(Boolean question) {
		this.question = question;

	}

	public String getMessage() {
		return message;

	}

	public void setMessage(String message) {
		this.message = message;

	}

	/* Metodos abtratos que todo model deve implementar */

//	public Long getCodeUserRecord() {
//		if (codeUserRecord == null)
//			codeUserRecord = getCodeUserContext();
//		return codeUserRecord;
//	}
//
//	public void setCodeUserRecord(Long codeUserRecord) {
//		this.codeUserRecord = codeUserRecord;
//	}
//
//	public Long getCodeSectorRecord() {
//		if (codeSectorRecord == null)
//			codeSectorRecord = getCodeSectorContext();
//		return codeSectorRecord;
//	}
//
//	public void setCodeSectorRecord(Long codeSectorRecord) {
//		this.codeSectorRecord = codeSectorRecord;
//	}

	@JsonIgnore
	public String getCodeStr() {
		return getCode() + "";
	}

	/**
	 * Seta a PK da entidade
	 * @param code
	 */
	public abstract void setCode(Long code);

	/**
	 * Identifica a PK da entidade
	 * @return
	 */
	public abstract Long getCode();

	/**
	 * Identifica se o registro esta ativo ou inativo
	 * @return
	 */
	public abstract Boolean getStatus();

	/**
	 * Seta o stsatus do registro
	 * @param status
	 */
	public abstract void setStatus(Boolean status);

	// /**
	// * Usada na inicializacao dos dados
	// */
	// @Transient
	// @JsonIgnore
	// private boolean initializer;

	// /**
	// * Updates realizados via JOB nao requer usuario logado
	// */
	// @Transient
	// @JsonIgnore
	// private boolean job;

	/// **
	// * Codigo do usuario logado no contexo ou null se nao existir user logado
	// */
	// @Transient
	// @JsonIgnore
	// private Long codUserRecord;
	// public boolean isInitializer() {
	//
	// return initializer;
	//
	// }
	//
	// public void setInitializer(boolean initializer) {
	//
	// this.initializer = initializer;
	//
	// }

	// public boolean isJob() {
	// return job;
	// }
	//
	// public void setJob(boolean job) {
	// this.job = job;
	// }

	// public Long getCodUserRecord() {
	// if (codUserRecord == null)
	// this.codUserRecord = getContextUserCode();
	// return codUserRecord;
	//
	// }
	//
	// public void setCodUserRecord(Long codUserRecord) {
	// this.codUserRecord = codUserRecord;
	//
	// }
}