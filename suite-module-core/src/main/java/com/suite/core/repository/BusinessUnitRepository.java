package com.suite.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.suite.core.model.BusinessUnit;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndCodeIsNotIn(String name, Long code);

	List<BusinessUnit> findAllByStatus(Boolean status);
	
	BusinessUnit findByNameIgnoreCase(String name);
	
	BusinessUnit findByUniqueIdIgnoreCase(String uniqueId);

	@Modifying
	@Transactional
	@Query("update BusinessUnitParameter o set o.value = :value WHERE o.businessUnit.code = :code AND o.key = :key")
	void setValueBuParameter(@Param("code") Long code, @Param("key") String key, @Param("value") String value);
	
}
