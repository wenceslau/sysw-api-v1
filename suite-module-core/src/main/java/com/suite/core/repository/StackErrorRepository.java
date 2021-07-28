package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.StackError;
import com.suite.core.repository.query.StackErrorRepositoryQuery;

public interface StackErrorRepository extends JpaRepository<StackError, Long>, StackErrorRepositoryQuery {

}
