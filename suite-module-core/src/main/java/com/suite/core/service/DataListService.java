package com.suite.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.util.Utils;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.DataList;
import com.suite.core.model.ItemDataList;
import com.suite.core.repository.DataListRepository;
import com.suite.core.repository.ItemDataListRepository;

/**
 * Service responsavel por manipular os dados recurso Parameter
 * @author 4931pc_neto
 *
 */
@Service
public class DataListService extends ServiceCore {

	@Autowired
	private DataListRepository dataListRepository;

	@Autowired
	private ItemDataListRepository itemDataListRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public ItemDataList findByCode(Long code) {
		if (code == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "ItemDataList", code), 1);

		Optional<ItemDataList> itemDataList = itemDataListRepository.findById(code);

		if (!itemDataList.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "ItemDataList", code), 1);

		return itemDataList.get();

	}

	/**
	 * Insere o recurso na base
	 * @param parameter
	 * @return
	 */
	public ItemDataList insert(ItemDataList itemDataList) {
		info("Insert: " + itemDataList);

		return itemDataListRepository.save(itemDataList);

	}

	/**
	 * Atualiza recurso na base
	 * @param code
	 * @param itemDataList
	 * @return
	 */
	public ItemDataList update(Long code, ItemDataList itemDataList) {
		info("Update: " + itemDataList);

		ItemDataList itemDataListToSave = findByCode(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(itemDataList, itemDataListToSave, new String[] { "code" });

		return itemDataListRepository.save(itemDataListToSave);

	}

	/**
	 * Deleta recurso na base
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		ItemDataList itemDataListToDelete = findByCode(code);

		itemDataListRepository.delete(itemDataListToDelete);

	}

	/**
	 * Retorna list dataList
	 * @return
	 */
	public List<DataList> listDataList() {
		return dataListRepository.findAll();

	}

	/**
	 * Retorna colecao de itens pelo nome da lista
	 * @return
	 */
	public List<ItemDataList> allItemByDataListName(String name) {
		return itemDataListRepository.findAllByDataList_Name(name);

	}

	/**
	 * Retorna colecao de itens ativo pelo nome da lista
	 * @return
	 */
	public List<ItemDataList> allItemEnableByDataListName(String name) {
		return itemDataListRepository.findAllByStatusAndDataList_Name(true, name);

	}

	/**
	 * Retorna colecao de itens ativo pelo grupo e nome da lista
	 * 
	 * @return
	 */
	public List<ItemDataList> allItemEnableByDataListNameAndGroup(String name, String group) {
		return itemDataListRepository.findAllByDataList_NameAndGroupAndStatus(name, group, true);

	}

	public DataList findByName(String name) {
		return dataListRepository.findByNameIgnoreCase(name);
	}

	public DataList saveBySystem(DataList dataList) {
		Utils.userSystem = true;
		return dataListRepository.save(dataList);
	}

}
