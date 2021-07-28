package com.suite.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.BusinessUnit;
import com.suite.core.model.Sector;
import com.suite.core.repository.query.SectorRepositoryQuery;

public interface SectorRepository extends JpaRepository<Sector, Long>, SectorRepositoryQuery {

	Sector findByNameIgnoreCase(String name);
	
	Sector findByNameIgnoreCaseAndBusinessUnit_Code(String name, Long code);
	
	Sector findByNameIgnoreCaseExternalDatabase(String nameExternalDatabase);
	
	boolean existsByNameIgnoreCase(String name);

	List<Sector> findAllByBusinessUnitOrBusinessUnitIsNull(BusinessUnit businessUnit);
	
	List<Sector> findAllByStatus(Boolean status);
	
	Long countByBusinessUnit_Code(Long code);

	boolean existsByNameIgnoreCaseAndBusinessUnit_Code(String name, Long code);

	boolean existsByUniqueIdIgnoreCaseAndBusinessUnit_Code(String uniqueId, Long code);

	boolean existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(String name, Long buCode, Long code);

	boolean existsByUniqueIdIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(String uniqueId, Long buCode, Long code);

	boolean existsByDataService_Code(Long code);

	
}
