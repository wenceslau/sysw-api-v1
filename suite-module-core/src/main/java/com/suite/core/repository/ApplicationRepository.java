package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	Application findByNameIgnoreCase(String name);

}
