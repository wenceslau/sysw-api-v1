package com.suite.core.exception;

import java.time.LocalDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.suite.app.exception.AppExceptionHandler;
import com.suite.core.model.StackError;
import com.suite.core.service.StackErrorService;

/**
 * Estende a classe AppExceptionHandler para implementar o metodo saveStackError
 * Toda excecao definida na classe lancada pela aplicacao eh interceptada e
 * salva no repositorio
 * 
 * @author Wenceslau
 *
 */
@ControllerAdvice
public class CustomExceptionHandler extends AppExceptionHandler {

	@Autowired
	private StackErrorService stackErrorService;

	/**
	 * Salva a excecao no repositorio
	 */
	@Override
	public void saveStackError(Exception ex, String causes, String stack, String fullStack) {

		try {

			StackError stackError = new StackError();
			stackError.setDateTimeError(LocalDateTime.now());

			String msg = ExceptionUtils.getRootCauseMessage(ex);
			String typeEx = "";

			//Identifica a parte da string que precisa ser traduzida se necessario
			if (msg != null & msg.length() > 2) {

				int idx = msg.indexOf(":");

				if (idx > 0) {

					typeEx = msg.substring(0, idx).trim() + ": ";
					msg = msg.substring(idx, msg.length()).replace(":", "").trim();

				}

			}

			stackError.setMessage(typeEx + translate(msg));

			try {

				stackError.setCodeUser(stackErrorService.getCodeUserContext());
				stackError.setCodeSector(stackErrorService.getCodeSectorContext());

			} catch (Exception e) {}

			stackError.setCauses(causes);
			stackError.setStack(stack);
			stackError.setFullStack(fullStack);

			stackErrorService.insert(stackError);

		} catch (Exception e) {

			logger.warn("Falha ao gravar StackError: " + e.getMessage());

		}

	}

	/**
	 * Realiza a bussca do valor traduzido se necessario
	 */
	@Override
	public String translate(String arg) {

		if (stackErrorService != null)
			return stackErrorService.formatTranslate(arg);

		return arg;

	}

}
