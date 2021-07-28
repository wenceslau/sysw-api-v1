package com.suite.app.module;

import org.springframework.stereotype.Component;

import com.suite.app.base.Enum.Module;
import com.suite.app.dto.BoolDTO;

@Component
public interface ISimcGenericService extends IBaseGenericService {

	/**
	 * Nome do modulo
	 * @return
	 */
	public Module module();


	/**
	 * Verifica se o objeto pode ser acessado pelo usuario logado
	 * Processo de permissao especifica do modulo SIMC
	 * @param usrActFilter
	 * @param codeSector
	 * @return
	 */
	public BoolDTO checkAccessObjeto(Long codeObjeto, Long codeUserLogged);
	
}
