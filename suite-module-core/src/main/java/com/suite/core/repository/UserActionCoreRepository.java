package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.app.repository.UserActionRepositoryQuery;
import com.suite.core.model.UserActionCore;

public interface UserActionCoreRepository extends JpaRepository<UserActionCore, Long>, UserActionRepositoryQuery {

}
