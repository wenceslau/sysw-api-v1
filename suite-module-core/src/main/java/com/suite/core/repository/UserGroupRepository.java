package com.suite.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.UserGroup;
import com.suite.core.repository.query.UserGroupRepositoryQuery;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, UserGroupRepositoryQuery {

	boolean existsByNameIgnoreCaseAndBusinessUnit_Code(String name, Long code);

	boolean existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(String name, Long buCode, Long code);

	List<UserGroup> findAllByBusinessUnit_Code(Long code);

}
