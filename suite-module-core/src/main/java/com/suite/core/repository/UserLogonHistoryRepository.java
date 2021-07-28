package com.suite.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suite.core.dto.LogonDTO;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.query.UserLogonHistoryRepositoryQuery;

public interface UserLogonHistoryRepository extends JpaRepository<UserLogonHistory, Long>, UserLogonHistoryRepositoryQuery {

	@Deprecated
	@Query(value = "SELECT new com.suite.core.dto.LogonDTO(CONVERT(DATE, dtt_record), " +
			"val_status_logon, COUNT(*)) " +
			"FROM tb_core_user_logon_history " +
			"WHERE CONVERT(DATE, dtt_record) > DATEADD(DAY,-15, GETDATE()) " +
			"GROUP BY CONVERT(DATE, dtt_record), " +
			"val_status_logon ORDER BY " +
			"CONVERT(DATE, dtt_record) DESC", nativeQuery = true)
	List<LogonDTO> findAllLogon();

	List<UserLogonHistory> findAllTop2ByUserLogonAndStatusLogonOrderByDateRecordDesc(String userLogon, String statusLogon);
		
	List<UserLogonHistory> findAllTop2ByUserLogonAndStatusLogonOrderByCodeDesc(String userLogon, String statusLogon);
	
	List<UserLogonHistory> findAllByDateLogon(LocalDate dateLogon);

}
