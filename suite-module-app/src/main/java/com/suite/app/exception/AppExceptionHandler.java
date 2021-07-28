
package com.suite.app.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.suite.app.util.StringUtils;

/**
 * Classe que intercepta as excecoes do sistema e devolve respostas http de acordo com o Erro
 * @author Wenceslau
 *
 */
@ControllerAdvice
public abstract class AppExceptionHandler extends ResponseEntityExceptionHandler {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Execeção para mensagens formato JSON invalidas
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleHttpMessageNotReadable()");

		String msgs = "Formato JSON invalido. " + exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		Erro err = new Erro(msgs, createStacks(ex, msgs, httpStatus));

		return handleExceptionInternal(ex, err, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);

	}

	// Execeção para propriedade de objtos nao aceitos
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleMethodArgumentNotValid()");

		// String msgs = exceptionMessage(ex);
		List<Erro> erros = createErrorsList(ex.getBindingResult());

		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);

	}

	// Execeção para Null Pointer
	@ExceptionHandler({ NullPointerException.class })
	public ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleNullPointerException()");

		String msgs = "Internal failure. " + exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.BAD_GATEWAY;
		Erro err = new Erro(msgs, createStacks(ex, msgs, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), HttpStatus.BAD_GATEWAY, request);

	}

	// Execeção para recursos nao encontrado
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleEmptyResultDataAccessException()");

		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		String msgs = exceptionMessage(ex); // translate(removeNameException(ExceptionUtils.getRootCauseMessage(ex)));
		Erro err = new Erro(msgs, createStacks(ex, msgs, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	// Execeção para erros de PK no banco
	@ExceptionHandler({ DataIntegrityViolationException.class, JpaObjectRetrievalFailureException.class })
	public ResponseEntity<Object> handleDataIntegrityViolationException(Exception ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleDataIntegrityViolationException()");

		String msgs = "Integridade de dados na base violada. " + exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		Erro err = new Erro(msgs, createStacks(ex, msgs, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	// Execeção para recursos nao encontrado
	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleRuntimeException()");

		String msgs = "";

		if (ex.getCause() instanceof NullPointerException)
			msgs = "Internal failure. ";

		msgs += exceptionMessage(ex);

		// message += " -> " + translate(removeNameException(ExceptionUtils.getRootCauseMessage(ex)));
		// message = exceptionMessage(ex);

		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		Erro err = new Erro(msgs, createStacks(ex, msgs, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	@ExceptionHandler({ FriendlyException.class })
	public ResponseEntity<Object> handleFriendlyException(FriendlyException ex, WebRequest request) {

		String msg = exceptionMessage(ex); // translate(removeNameException(ExceptionUtils.getRootCauseMessage(ex)));
		HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
		Erro err = new Erro(msg, createStacks(ex, msg, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	@ExceptionHandler({ WarningException.class })
	public ResponseEntity<Object> handleWarningException(WarningException ex, WebRequest request) {

		// String message1 = translate(ex.getMessage() + "");
		// String message2 = translate(removeNameException(ExceptionUtils.getRootCauseMessage(ex)));
		// if (!message1.equals(message2) && !message1.contains(message2) )
		// message1 = message1 + " -> " + message2;

		String msg = exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;
		Erro err = new Erro(msg, createStacks(ex, msg, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	// Execeção para recursos nao encontrado
	@ExceptionHandler({ QuestionException.class })
	public ResponseEntity<Object> handleConfirmException(QuestionException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleConfirmException()");

		String message = translate(removeNameException(ExceptionUtils.getRootCauseMessage(ex)));
		System.out.println(message);
		HttpStatus httpStatus = HttpStatus.CONFLICT;
		Erro err = new Erro(message, createStacks(ex, message, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	// Execeção para recursos nao encontrado
	@ExceptionHandler({ InvalidGrantException.class })
	public ResponseEntity<Object> handleInvalidGrantException(InvalidGrantException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleInvalidGrantException()");

		// String message = removeNameException(ExceptionUtils.getRootCauseMessage(ex));
		String msg = exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.FORBIDDEN;
		Erro err = new Erro(msg, createStacks(ex, msg, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	// Execeção para recursos nao encontrado
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

		// System.out.println("CustomExceptionHandler.handleAccessDeniedException()");

		// String message = removeNameException(ExceptionUtils.getRootCauseMessage(ex));
		String msg = exceptionMessage(ex);
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
		Erro err = new Erro(msg, createStacks(ex, msg, httpStatus));

		return handleExceptionInternal(ex, err, new HttpHeaders(), httpStatus, request);

	}

	/**
	 * Monta lista de erros usadas internamente
	 * @param bindingResult
	 * @return
	 */
	private List<Erro> createErrorsList(BindingResult bindingResult) {

		// System.out.println("CustomExceptionHandler.createErrorsList()");

		// Cria lista de erros recebidos no objeto BindingResult originado no objeto
		// exception

		List<Erro> errosList = new ArrayList<>();

		for (FieldError fiedlError : bindingResult.getFieldErrors()) {
			// fiedlError é o field que esta com erro, cujo poderia busca-lo no arquivo
			// properites uma descricao mais amigavel
			String message = fiedlError + "";
			String cause = fiedlError.toString();

			errosList.add(new Erro(message, cause));

		}

		return errosList;

	}

	/**
	 * Cria uma lista de string com o stack da excecao
	 * @param ex
	 * @param httpStatus
	 * @return
	 */
	private List<String> createStacks(Exception ex, String msgs, HttpStatus httpStatus) {

		logger.error(loggerMessage(msgs, ex), ex);

		List<String> list = new ArrayList<>();
		list.add("message: " + msgs);
		list.add("timestamp: " + LocalDateTime.now());
		list.add("status: " + httpStatus.value());
		list.add("error: " + httpStatus.getReasonPhrase());
		list.add("exception: " + ClassUtils.getShortClassName(ex, null));

		// String causes = "";
		// Throwable[] frames = ExceptionUtils.getThrowables(ex);
		// for (Throwable throwable : frames) {
		// String value = translate(throwable.getMessage());
		// list.add(value);
		// causes += value + "\n";
		// }
		// list.add("stacktrace:");

		String stack = "";
		String fullStack = "";
		String[] stackFrames = ExceptionUtils.getStackFrames(ex);

		if (stackFrames.length > 0)
			stack = "Caused by: " + stackFrames[0]  + "\n";

		for (String ste : stackFrames) {
			fullStack += ste.trim() + "\n";

			if (ste.trim().startsWith("..."))
				continue;

			else if (ste.trim().startsWith("Caused"))
				stack += "\n" + ste.trim() + "\n";
			else if (ste.contains("com.suite") && !ste.contains("$$"))
				stack += "\t at " + ste.trim().replaceFirst("at ", "") + "\n";

		}

		saveStackError(ex, msgs, stack, fullStack);

		return list;

	}

	private String exceptionMessage(Exception ex) {

		List<String> list = new ArrayList<>();
		Throwable[] frames = ExceptionUtils.getThrowables(ex);

		for (Throwable throwable : frames) {
			String value = translate(removeNameException(throwable.getMessage()));
			System.out.println(value);
			if (!list.contains(value))
				list.add(value);

		}

		return String.join(". ", list);

	}

	/**
	 * Metodo abstrato a ser implementado para salvar as excecoes na base de dados
	 * @param ex
	 * @param causes
	 * @param stack
	 * @param fullStack
	 */
	public abstract void saveStackError(Exception ex, String causes, String stack, String fullStack);

	/**
	 * Metodo abstradi a ser implemntado para traduzir o msg
	 * @param arg
	 * @return
	 */
	public abstract String translate(String arg);

	/**
	 * Identifica o nome de uma execao na string
	 * A string deve ser a execao completa
	 * @param value
	 * @return
	 */
	private static String removeNameException(final String value) {

		if (value == null)
			return "";
		
		String result = "";
		int pos = value.indexOf("n:") + 1;
		if (pos > 1)
			result = value.substring(pos).trim();

		if (result.isEmpty())
			return value;

		return result;

	}

	/**
	 * Monta uma msg para o logger
	 * @param value
	 * @param thr
	 * @return
	 */
	private String loggerMessage(String value, Throwable thr) {

		StringBuilder clsMthLn = new StringBuilder();

		StringUtils.prepareMsgLogger(clsMthLn, thr.getStackTrace()[0]);

		clsMthLn.append(value);

		return clsMthLn.toString();

	}

	public static class Erro {

		private String message;

		private String field;

		private List<String> stacks;

		public Erro(String message, List<String> stacks) {

			super();
			this.message = message;
			this.stacks = stacks;
			this.field = "";

		}

		public Erro(String message, String field) {

			super();
			this.message = message;
			this.field = field;

		}

		public String getMessage() {

			return message;

		}

		public void setMessage(String message) {

			this.message = message;

		}

		public List<String> getStacks() {

			return stacks;

		}

		public void setStacks(List<String> stacks) {

			this.stacks = stacks;

		}

		public String getField() {

			return field;

		}

		public void setField(String field) {

			this.field = field;

		}

	}

}
