package com.suite.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.suite.core.dto.MenuItemDTO;
import com.suite.core.model.Application;
import com.suite.core.model.Permission;
import com.suite.core.repository.query.PermissionRepositoryQuery;

public interface PermissionRepository extends JpaRepository<Permission, Long>, PermissionRepositoryQuery {

	@Query(value = "SELECT "
			+ "new com.suite.core.dto.MenuItemDTO(p.sequenceRoot, p.root, p.rootToolbar) "
			+ "FROM 	Permission p "
			+ "WHERE 	p.root IS NOT NULL "
			+ "GROUP BY p.root, p.rootToolbar, p.sequenceRoot "
			+ "ORDER BY p.sequenceRoot ", nativeQuery = false)
	List<MenuItemDTO> listRoot();

	@Query(value = "SELECT "
			+ "new com.suite.core.dto.MenuItemDTO(p.label, p.router, p.icon) "
			+ "FROM 	Permission p "
			+ "WHERE 	p.root = :root "
			+ "AND 		p.key IN :keys "
			+ "AND 		p.status = true "
			+ "ORDER BY p.sequence ", nativeQuery = false)
	List<MenuItemDTO> listMenuItemByRoot(@Param("root") String root, @Param("keys") List<String> keys);

	@Query(value = "SELECT "
			+ "new com.suite.core.dto.MenuItemDTO(p.label, p.router) "
			+ "FROM 	Permission p "
			+ "WHERE 	p.toolbar = true "
			+ "AND 		p.key IN :keys "
			+ "AND 		p.status = true "
			+ "ORDER BY p.root, p.sequence ", nativeQuery = false)
	List<MenuItemDTO> listMenuItemToolbar(@Param("keys") List<String> keys);

	List<Permission> findAllByStatusOrderByModule(Boolean status);

	List<Permission> findAllByApplication(Application application);

	List<Permission> findAllByRootIsNotNullAndSequenceRootIsNull();
	
	Permission findByRouterIgnoreCase(String router);

	Permission findByKeyIgnoreCase(String key);

	List<Permission> findAllByCodeIn(Collection<Long> codes);
	
}
