package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.Initializer;

public interface InitializerRepository extends JpaRepository<Initializer, Long> {

	Initializer findByNameIgnoreCase(String name);

}
