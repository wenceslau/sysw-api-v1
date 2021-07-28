package com.suite.app.base;

import com.suite.app.exception.WarningException;

/**
 * Super Classe a ser estendida por todas as classe Service da Suite
 * @author Wenceslau
 *
 */
public abstract class Service extends Base {

	/**
	 * Se a exception for warning, faz o cast e relanca Se nao, relanca coom Runtime
	 * adicionado a msg e a propria exception
	 * @param e
	 */
	protected void throwException(Throwable e, String msg) {

		if (e instanceof WarningException)
			throw (WarningException) e;

		throw new RuntimeException(msg, e);

	}
	
	protected abstract String formatTranslate(String key, Object... args);


}
