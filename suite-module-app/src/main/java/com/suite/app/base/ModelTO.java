package com.suite.app.base;

/**
 * Super Classe a ser estendida pode todos os Objeto TO
 * @author Wenceslau
 *
 */
public abstract class ModelTO {

	/**
	 * Usada para receber confirmação vinda da UI Quando question igual true, foi
	 * enviado uma msg par UI E o confirmAction passa a ser obrigatorio Sim ou nao
	 * para executar a acao Null indida o fluxo normal
	 */
	private Boolean confirmAction;

	/**
	 * Usada para enviar mensagem para a UI True indica que uma msg esta sendo
	 * enviada a UI e espera uma resposta sim ou nao null indica fluxo normal
	 */
	private Boolean question;

	/**
	 * Usada para enviar mensagem para a UI Message a enviar a UI junto com question
	 * igual a true
	 */
	private String message;

	/**
	 * Usada na classe Initializer para definir se esse objeto esta sendo usado
	 * na Inicializacao para gerar o Script inicial. (validar se nao esta depreciado)
	 */
	private boolean initializer;

	/**
	 * Codigo do usuario logado no contexo ou null se nao existir user logado
	 */
	private Long codUserRecord;

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

	public boolean isInitializer() {

		return initializer;

	}

	public void setInitializer(boolean initializer) {

		this.initializer = initializer;

	}

	public Long getCodUserRecord() {

		return codUserRecord;

	}

	public void setCodUserRecord(Long codUserRecord) {

		this.codUserRecord = codUserRecord;

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

}