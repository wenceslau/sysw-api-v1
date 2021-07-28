package com.suite.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.suite.core.model.Language;
import com.suite.core.repository.query.LanguageRepositoryQuery;

public interface LanguageRepository extends JpaRepository<Language, Long>, LanguageRepositoryQuery {

	boolean existsByKeyIgnoreCase(String key);

	boolean existsByKeyIgnoreCaseAndCodeIsNotIn(String key, Long code);
	
	boolean existsByPortuguesIgnoreCase(String portugues);

	boolean existsByPortuguesIgnoreCaseAndCodeIsNotIn(String portugues, Long code);
	
	boolean existsByEnglishIgnoreCase(String english);

	boolean existsByEnglishIgnoreCaseAndCodeIsNotIn(String english, Long code);
	
	boolean existsBySpanishIgnoreCase(String spanish);

	boolean existsBySpanishIgnoreCaseAndCodeIsNotIn(String spanish, Long code);
	
	void deleteByKey(String key);
	
	Language findByKeyIgnoreCase(String key);
		
	@Query(value = "SELECT " + 
			"CASE " + 
			"    WHEN 'PT' =:lang THEN l.portugues " + 
			"    WHEN 'EN' =:lang THEN l.english " + 
			"    WHEN 'ES' =:lang THEN l.spanish " + 
			"    ELSE l.portugues " + 
			"END AS value " + 
			"FROM Language l "+
			"WHERE l.key =:key ", nativeQuery = false)
	String findValueByLangAndKey(@Param("lang") String lang, @Param("key") String key);
	
	@Query(value = "SELECT l.portugues FROM Language l WHERE l.key =:key ", nativeQuery = false)
	String findValuePTByKey(@Param("key") String key);
	
	@Query(value = "SELECT l.english FROM Language l WHERE l.key =:key ", nativeQuery = false)
	String findValueENByKey(@Param("key") String key);
	
	@Query(value = "SELECT l.spanish FROM Language l WHERE l.key =:key ", nativeQuery = false)
	String findValueESByKey(@Param("key") String key);
	
	
//	@Query(value = "SELECT " + 
//			"CASE " + 
//			"    WHEN 'PT' =:lang THEN l.val_portugues " + 
//			"    WHEN 'EN' =:lang THEN l.val_english " + 
//			"    WHEN 'ES' =:lang THEN l.val_spanish " + 
//			"    ELSE 'Language not suported' " + 
//			"END AS value " + 
//			"FROM tb_core_language l "+
//			"WHERE l.key =:key ", nativeQuery = true)
//	String findValueByLangAndKey(@Param("lang") String lang, @Param("key") String key);
	
}
