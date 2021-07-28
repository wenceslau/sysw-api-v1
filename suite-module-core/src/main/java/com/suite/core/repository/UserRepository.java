package com.suite.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.Profile;
import com.suite.core.model.User;
import com.suite.core.repository.query.UserRepositoryQuery;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUsernameIgnoreCase(String username);

	List<User> findByBusinessUnit_Code(Long code);
	
	List<User> findByProfileTypeAndStatus(Integer type, Boolean status);

	List<User> findByProfileTypeIsLessThanAndStatus(Integer type, Boolean status);

	boolean existsByUsernameIgnoreCase(String username);

	boolean existsByUsernameIgnoreCaseAndCodeIsNotIn(String username, Long code);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndCodeIsNotIn(String email, Long code);
	
	boolean existsByProfile(Profile profile);
	
	int countByBusinessUnit_Code(Long code);
	
	String findNameByCode(Long code);

}
