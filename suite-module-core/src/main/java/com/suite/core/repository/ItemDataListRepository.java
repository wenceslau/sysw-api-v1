package com.suite.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suite.core.model.ItemDataList;

public interface ItemDataListRepository extends JpaRepository<ItemDataList, Long> {

	List<ItemDataList> findAllByDataList_Name(String name);

	List<ItemDataList> findAllByStatusAndDataList_Name(Boolean status, String name);

	List<ItemDataList> findAllByDataList_NameAndGroupAndStatus(String name, String group, Boolean status);

	ItemDataList findByValueItemAndDataList_Code(String name, Long code);

	ItemDataList findByLabelItemAndValueItemAndDataList_Code(String label, String value, Long code);

}
