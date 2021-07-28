package com.suite.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {

	public Optional<Parameter> findByKeyIgnoreCase(String key);

	public Optional<Parameter> findByGroupIgnoreCaseAndKeyIgnoreCase(String group, String key);

	public List<Parameter> findByGroupIgnoreCase(String group);

	List<Parameter> findAllByStatus(Boolean status);

}
