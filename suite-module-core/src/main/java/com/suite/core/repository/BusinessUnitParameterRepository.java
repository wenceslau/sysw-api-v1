package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.BusinessUnitParameter;

public interface BusinessUnitParameterRepository extends JpaRepository<BusinessUnitParameter, Long> {

	
	BusinessUnitParameter findByKeyIgnoreCaseAndBusinessUnit_Code(String name, Long code);
	
}
