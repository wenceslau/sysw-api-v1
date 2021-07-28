package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.DataList;

public interface DataListRepository extends JpaRepository<DataList, Long> {

	DataList findByNameIgnoreCase(String name);

}
