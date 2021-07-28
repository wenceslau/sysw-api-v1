package com.suite.app.dto;

/**
 * Objeto usado para transportar retornos booleanos com detalhamento
 * @author Wenceslau
 *
 */
public class BoolDTO {

	/*
	 * Valor boleano true or false
	 */
	private boolean value;

	/*
	 * Lista de mensagem para acompanhar o valor boleano
	 */
	// private List<String> messages;

	private String message;

	/*
	 * Qualquer objeto a ser retornado
	 */
	private Object object;

	public BoolDTO() {}

	public BoolDTO(boolean value) {
		this.value = value;

	}

	public BoolDTO(boolean value, String message) {
		this.value = value;
		this.message = message;
	}

	public BoolDTO(boolean value, String message, Object object) {
		this.value = value;
		this.message = message;
		this.object = object;

	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
