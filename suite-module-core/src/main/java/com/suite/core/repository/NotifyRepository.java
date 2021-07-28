package com.suite.core.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.Notify;

public interface NotifyRepository extends JpaRepository<Notify, Long> {

	List<Notify> findAllBySectorCodeAndDateTimeNotifyGreaterThanOrderByDateTimeNotifyDesc(Long sectorCode,
			LocalDateTime dateTimeNotify);

	List<Notify> findAllByDateTimeNotifyGreaterThanAndSectorCodeInOrderByDateTimeNotifyDesc(LocalDateTime dateTimeNotify,
			Collection<Long> sectorCodes);

	List<Notify> findAllByDateTimeNotifyGreaterThanOrderByDateTimeNotifyDesc(LocalDateTime dateRecord);

}
