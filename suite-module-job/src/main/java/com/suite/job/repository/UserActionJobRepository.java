package com.suite.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.app.repository.UserActionRepositoryQuery;
import com.suite.job.model.UserActionJob;

public interface UserActionJobRepository extends JpaRepository<UserActionJob, Long>, UserActionRepositoryQuery {

}
