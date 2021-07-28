package com.suite.app.module;

import com.suite.app.repository.UserActionFilter;

public interface IBaseGenericService {

	/**
	 * Count a quantidade de acoes da aplicacao
	 * @param start
	 * @param end
	 * @return
	 */
	public ActionApp countActionsApp(UserActionFilter userActionFilter);

	class ActionApp {

		public Long countInsert;
		public Long countUpdate;
		public Long countDelete;

	}

	
}
