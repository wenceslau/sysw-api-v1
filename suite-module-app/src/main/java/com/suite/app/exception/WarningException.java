package com.suite.app.exception;

public class WarningException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;

	public WarningException(String arg0) {

		super(arg0);

	}

	public WarningException(String arg0, Throwable cause) {

		super(arg0, cause);

	}

	/**
	 * 
	 * @param code
	 * @param arg0
	 * @since 2.0.2
	 */
	public WarningException(int code, String arg0) {

		super(arg0);
		this.code = code;

	}

	/**
	 * 
	 * @param code
	 * @param arg0
	 * @param cause
	 * @since 2.0.2
	 */
	public WarningException(int code, String arg0, Throwable cause) {

		super(arg0, cause);
		this.code = code;

	}

	public int getCode() {

		return code;

	}

	// CODIGOS EXCEPTION

	/*
	 * 101 - msg_ja_existe_um_r_c_a_m_c_e_c_a_v_e_a_p_i_o_e_n_f_a_a
	 * (Já existe um registro com a mesma chave com a vigência em aberto, para iniciar outro é necessário fechar a anterior)
	 * 
	 */
}
