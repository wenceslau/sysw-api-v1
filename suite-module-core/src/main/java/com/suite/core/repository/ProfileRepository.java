package com.suite.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.suite.core.model.BusinessUnit;
import com.suite.core.model.Profile;
import com.suite.core.repository.query.ProfileRepositoryQuery;

public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryQuery {

	//TODO: avaliar o lower case nas query com select HQL
	
	@Query(value = "SELECT count(*) "
			+ "FROM 	Profile p "
			+ "WHERE 	p.name = :name "
			+ "AND 		(p.businessUnit = :businessUnit "
			+ "OR 		p.businessUnit IS NULL ) "
			+ "", nativeQuery = false)
	int existsByNameAndBusinessUnitOrBusinessUnitIsNull(@Param("name") String name,
			@Param("businessUnit") BusinessUnit businessUnit);

	@Query(value = "SELECT count(*) "
			+ "FROM 	Profile p "
			+ "WHERE 	p.name = :name "
			+ "AND	 	p.code != :code "
			+ "AND 		(p.businessUnit = :businessUnit "
			+ "OR 		p.businessUnit IS NULL) "
			+ "", nativeQuery = false)
	int existsByNameAndCodeIsNotInAndBusinessUnitOrBusinessUnitIsNull(@Param("name") String name, @Param("code") Long code,
			@Param("businessUnit") BusinessUnit businessUnit);

	@Query(value = "SELECT p.name "
			+ "FROM 	Profile p "
			+ "WHERE 	p.permissions IN :permissions "
			+ "AND 		p.businessUnit = :businessUnit"
			+ "", nativeQuery = false)
	String existsByBusinessUnitAndPermissions(@Param("businessUnit") BusinessUnit businessUnit,
			@Param("permissions") List<Long> permissions);
	
	@Query("SELECT p FROM Profile p JOIN FETCH p.permissions WHERE p.code = (:code)")
	Profile findByCodeAndFetchPermissionsEagerly(@Param("code") Long code);
	
    @EntityGraph(value = "permissions", type = EntityGraphType.LOAD)
    Profile getByCode(Long code);
	
	boolean existsByNameIgnoreCaseAndCodeIsNotIn(String name, Long code);

	boolean existsByNameIgnoreCase(String name);

	List<Profile> findByBusinessUnitOrBusinessUnitIsNull(BusinessUnit businessUnit);
	
	List<Profile> findByBusinessUnit(BusinessUnit businessUnit);

	Profile findByType(Integer type);
	
	Profile findByNameIgnoreCase(String name);


}
