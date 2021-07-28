package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.model.DataService;
import com.suite.core.repository.query.DataServiceRepositoryQuery;

public interface DataServiceRepository extends JpaRepository<DataService, Long>, DataServiceRepositoryQuery {

	boolean existsByNameIgnoreCaseAndBusinessUnit_Code(String name, Long code);

	boolean existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(String name, Long buCode, Long code);

	boolean existsByIdentifierIgnoreCaseAndTypeAndBusinessUnit_Code(String identifier, DataServiceType type, Long buCode);

	boolean existsByIdentifierIgnoreCaseAndTypeAndBusinessUnit_CodeAndCodeIsNotIn(String identifier, DataServiceType type, Long buCode, Long code);
	
	DataService findByCodSectorDataTaskParent(Long codSectorDataTaskParent);
	
	DataService findTop1ByIdentifierIgnoreCaseAndType(String identifier, DataServiceType type);
	
	DataService findByIdentifierIgnoreCaseAndCodSectorsDataTask(String identifier, String codSectors);

	DataService findTop1ByType(DataServiceType type);
	
	DataService findByNameIgnoreCase(String name);
		
}


