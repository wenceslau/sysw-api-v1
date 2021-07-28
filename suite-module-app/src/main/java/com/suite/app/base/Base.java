package com.suite.app.base;

import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;

/**
 * Super classe para ser estendida por qualquer classe da suite
 * Expoe metodos comuns uteis a todas as classes
 * @author Wenceslau
 *
 */
public abstract class Base {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Objeto usuario que manipulou esse recurso
	 * Ele eh transiente, nao eh armazenado na entidade
	 * Eh definido com o usuario do contexto
	 */
	@Transient
	@JsonIgnore
	private Long codeUserContext;

	/**
	 * Objeto usuario que manipulou esse recurso
	 * Ele eh transiente, nao eh armazenado na entidade
	 * Eh definido com o usuario do contexto
	 */
	@Transient
	@JsonIgnore
	private Long codeSectorContext;

	@Transient
	@JsonIgnore
	private String nameSectorContext;

	/**
	 * Retorna o usuario logado no contexto
	 * @return the userLogger
	 */
	@JsonIgnore
	public String getUserRequest() {
		// if (userRequest == null)
		// if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
		// userRequest = SecurityContextHolder.getContext().getAuthentication().getName();

		return Utils.getUserRequest();

	}

	public Long getCodeUserContext() {
		//if (codeUserContext == null)
			codeUserContext = Utils.getContextUserCode();
		return codeUserContext;
	}

	public Long getCodeSectorContext() {
		//if (codeSectorContext == null)
			codeSectorContext = Utils.getContextSectorCode();
		return codeSectorContext;
	}

	public String getNameSectorContext() {
		//if (nameSectorContext == null)
			nameSectorContext = Utils.getContextSectorName();
		return nameSectorContext;
	}

	/**
	 * Verifica se o setor logado eh o Default
	 * @return
	 */
	@JsonIgnore
	public boolean isDefaultSector() {
		String value = getNameSectorContext();
		return value != null && value.toUpperCase().equals("DEFAULT");

	}

	/**
	 * Trace info no log
	 * @param value
	 */
	public void log(String value) {
		if (Utils.info)
			logger.info(loggerMessage(value));

	}

	/**
	 * Trace info no log
	 * @param value
	 */
	protected void info(String value) {
		if (Utils.info)
			logger.info(loggerMessage(value));

	}

	/**
	 * Trace info no log
	 * @param value
	 */
	protected void warn(String value) {
		if (Utils.warn)
			logger.warn(loggerMessage(value));

	}

	/**
	 * Trace info no log
	 * @param value
	 */
	protected void debug(String value) {
		if (Utils.debug)
			logger.info(loggerMessage(value));

	}

	/**
	 * Trace info no log
	 * @param value
	 */
	protected void error(String value, Throwable throwable) {
		logger.error(loggerMessage(value), throwable);

	}

	protected void error(String value) {
		logger.error(loggerMessage(value));

	}

	/**
	 * Aplica um sleep
	 * @param value milliseconds
	 */
	protected void sleep(int value) {

		try {

			Thread.sleep(value);

		} catch (InterruptedException e) {}

	}

	/**
	 * Monta uma msg para o log
	 * @param value
	 * @return
	 */
	private String loggerMessage(String value) {
		StringBuilder clsMthLn = new StringBuilder();
		// System.out.println("this.getClass().getName(): "+ this.getClass().getName());

		for (StackTraceElement ste : new Throwable().getStackTrace()) {

			// System.out.println(".......ste.getClassName(): "+ ste.getClassName());

			if (ste.getClassName().equals(this.getClass().getName())) {

				StringUtils.prepareMsgLogger(clsMthLn, ste);
				break;

			}

		}

		if (clsMthLn.length() == 0)
			StringUtils.prepareMsgLogger(clsMthLn, new Throwable().getStackTrace()[2]);

		clsMthLn.append(value);
		return clsMthLn.toString();

	}

}
