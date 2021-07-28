package com.suite.core.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suite.core.dto.LogonDTO;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.filter.UserLoginHistoryFilter;

/**
 * Interface customizada para consultas no repositorio da entidade UserLogonHistory
 * @author Wenceslau
 *
 */
public interface UserLogonHistoryRepositoryQuery {

	public Page<UserLogonHistory> filter(UserLoginHistoryFilter filter, Pageable pageable);

	public List<UserLogonHistory> filter(UserLoginHistoryFilter filter);

	public List<LogonDTO> listLogonByDay(Long codeBusinessUnit, Integer days, String device);

}
