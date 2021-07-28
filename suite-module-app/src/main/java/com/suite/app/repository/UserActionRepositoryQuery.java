package com.suite.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Interface generica para select dos dados de UserAction Cada modulo possui sua
 * entidade userAction, para nao duplicar codigo foi implementado no modulo app,
 * que eh importado por todos os modulos e assim pode ser usado por todos
 * @author Wenceslau
 *
 */
@Component
public interface UserActionRepositoryQuery {
	
	public <T> Long count(Class<T> classType);

	public <T> List<T> filter(UserActionFilter filter, Class<T> classType);

	public <T> Page<T> filter(UserActionFilter filter, Pageable pageable, Class<T> classType);

}
