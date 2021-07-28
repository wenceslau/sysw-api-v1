package com.suite.core.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.UserRequest;

public interface UserRquestRepository extends JpaRepository<UserRequest, Long> {

	List<UserRequest> findAllByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(LocalDateTime startDateRequest, LocalDateTime endDateRequest);

	long countByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(LocalDateTime startDateRequest, LocalDateTime endDateRequest);

}
