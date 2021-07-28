package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.GlossaryTerm;
import com.suite.core.repository.query.GlossaryTermRepositoryQuery;


public interface GlossaryTermRepository extends JpaRepository<GlossaryTerm, Long>, GlossaryTermRepositoryQuery {

}
