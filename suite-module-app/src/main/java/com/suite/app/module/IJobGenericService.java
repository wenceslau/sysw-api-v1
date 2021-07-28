package com.suite.app.module;

import org.springframework.stereotype.Component;

import com.suite.app.base.Enum.Module;
import com.suite.app.dto.BoolDTO;

@Component
public interface IJobGenericService extends IBaseGenericService {

	/**
	 * Nome do modulo
	 * @return
	 */
	public Module module();

	/**
	 * Contage de objetos
	 * @return
	 */
	public BoolDTO startTaskUpdateByObject(Long codObject);

	/**
	 * 
	 * @param bean
	 * @return
	 */
	public BoolDTO statusTaskByBean(String bean);

}
