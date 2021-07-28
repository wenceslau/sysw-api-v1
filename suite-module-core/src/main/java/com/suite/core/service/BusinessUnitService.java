
package com.suite.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.exception.WarningException;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.BusinessUnitParameter;
import com.suite.core.model.Sector;
import com.suite.core.repository.BusinessUnitRepository;
import com.suite.security.licence.Licence;

/**
 * Service responsavel por manipular os dados recurso BusinessUnit
 * @author Wenceslau
 *
 */
@Service
public class BusinessUnitService extends CoreRulesService {

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	/**
	 * Encontra o recurso pelo codigo
	 * @param code
	 * @return
	 */
	public BusinessUnit findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Recupera a lista de recursos ativos
	 * @return
	 */
	public List<BusinessUnit> findAllEnabled() {
		List<BusinessUnit> lst = findAllByStatus(true);

		if (!isSa())
			lst.removeIf(x -> x.getName().equals("DEFAULT"));

		return lst;

	}

	/**
	 * Recupera a lista filtrada de recursos
	 * necessidade de filtro
	 * @return
	 */
	public List<BusinessUnit> listByFilter() {
		List<BusinessUnit> lst = findAll();

		if (!isSa())
			lst.removeIf(x -> x.getName().equals("DEFAULT"));

		return lst;

	}

	/**
	 * Insere o recurso na base
	 * @param businessUnit
	 * @return
	 */
	public BusinessUnit insert(BusinessUnit businessUnit) {
		info("Insert: " + businessUnit);

		businessUnit.setUniqueId(RandomStringUtils.randomAlphabetic(15).toUpperCase());

		ruleOfBusinessUnit(businessUnit);

		cloneBuParamenterDefault(businessUnit);

		return save(businessUnit);

	}

	/**
	 * Atualiza recurso no repositorio
	 * @param code
	 * @param businessUnitEdited
	 * @return
	 */
	public BusinessUnit update(Long code, BusinessUnit businessUnitEdited) {
		info("Update: " + businessUnitEdited);

		ruleOfBusinessUnit(businessUnitEdited);

		BusinessUnit businessUnitToSave = findById(code);

		// Valida se houve alteracao em paramtro que nao pode ser alterado
		CheckRuleBusinessUnitParameter(businessUnitToSave.getBusinessUnitParameters(), businessUnitEdited.getBusinessUnitParameters(),
				businessUnitEdited);

		// Remove os parametros de acordo com as app da BU e readiciona da default as que corresponde as app da BU
		cloneBuParamenterDefault(businessUnitEdited);

		businessUnitToSave.getBusinessUnitParameters().clear();
		businessUnitToSave.getBusinessUnitParameters().addAll(businessUnitEdited.getBusinessUnitParameters());

		// Copia os dados do objeto editado na UI para o objeto a ser salvo
		BeanUtils.copyProperties(businessUnitEdited, businessUnitToSave, new String[] { "code", "businessUnitParameters" });

		// Ignora os campos do array na copia do valor das atributos
		return save(businessUnitToSave);

	}

	/**
	 * Deleta recurso no repositorio
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		businessUnitRepository.deleteById(code);

	}

	/**
	 * Atualiza recurso na base
	 * @param code
	 * @param businessUnit
	 * @return
	 */
	public void setSysMonkey(String value) {
		String[] arr = value.split(";");

		try {
			StringUtils.dec(arr[2].trim());

		} catch (Exception e) {
			error("Erro a validar licenca", e);
			throw new WarningException("Licença invalida");

		}

		Sector sector = sectorService.findById(Long.parseLong(arr[0]));

		if (sector.getName().toUpperCase().equals("DEFAULT"))
			throw new WarningException("Licença não pode ser aplicada ao setor DEFAULT");

		BusinessUnit businessUnitToSave = findById(sector.getBusinessUnit().getCode());
		businessUnitToSave.setLicense(arr[2].trim());

		saveBySystem(businessUnitToSave);

	}

	/**
	 * Retorna o objeto License para um aplicacao que usa API
	 * @param sectorCode
	 * @param value
	 * @return
	 */
	public Licence sysMonkeyForApi(Long sectorCode, String value) {
		Sector sector = sectorService.findById(sectorCode);

		licenseService.checkLicense(sector, value);

		Licence sysmonkey = licenseService.getLicense(value);

		return sysmonkey;

	}

	/**
	 * Retorna o objeto License para um aplicacao que usa API
	 * @param sectorCode
	 * @param value
	 * @return
	 */
	public Licence sysmonkey(Long sectorCode) {
		Sector sector = sectorService.findByCode(sectorCode);

		String value = sector.getBusinessUnit().getLicense();

		licenseService.checkLicense(sector, value);

		Licence sysmonkey = licenseService.getLicense(value);

		return sysmonkey;

	}

	private void cloneBuParamenterDefault(BusinessUnit businessUnit) {
		BusinessUnit buDefault = businessUnitRepository.findByNameIgnoreCase("DEFAULT");

		List<BusinessUnitParameter> listAuxDefault = new ArrayList<>(buDefault.getBusinessUnitParameters());

		for (BusinessUnitParameter bupDefault : listAuxDefault) {

			// copiar da default apensa se tiver a app do parametros
			if (businessUnit.getApplications().stream().filter(x -> x.getName().equals(bupDefault.getApplication())).findFirst().isPresent()) {

				// Se ja tiver na BU nao precisa readicionar, se nao tiver adiona
				if (!businessUnit.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(bupDefault.getKey())).findFirst()
						.isPresent()) {
					BusinessUnitParameter buParameter = new BusinessUnitParameter();
					BeanUtils.copyProperties(bupDefault, buParameter, new String[] { "code", "businessUnit" });
					buParameter.setCode(null);
					businessUnit.getBusinessUnitParameters().add(buParameter);

				}

			} else {
				System.out.println(businessUnit.getBusinessUnitParameters().size());
				businessUnit.getBusinessUnitParameters().removeIf(x -> x.getKey().equals(bupDefault.getKey()));
				System.out.println(businessUnit.getBusinessUnitParameters().size());

			}

		}

		// atualiza o objeto DataService de cada Property
		businessUnit.getBusinessUnitParameters().forEach(p -> {
			p.setBusinessUnit(businessUnit);
			p.setStatus(true);

		});

	}

	/**
	 * @param dataService
	 * @param dataServiceToSave
	 */
	private void CheckRuleBusinessUnitParameter(List<BusinessUnitParameter> listOriginal, List<BusinessUnitParameter> listEdit,
			BusinessUnit bu) {
		String numObjetos = valueParameterBusinessUnitOrNull("SYS_NUMERO_OBJETOS", bu);

		for (BusinessUnitParameter bupEdited : listEdit) {
			bupEdited.setKey(bupEdited.getKey().trim().toUpperCase());
			bupEdited.setValue(bupEdited.getValue().trim());

			// Recupera o bupar da lista original
			BusinessUnitParameter buForTest = listOriginal.stream().filter(x -> x.getKey().equals(bupEdited.getKey())).findFirst().orElse(null);

			if (buForTest == null)
				continue;

			// se o bu edit for de systema verifica se pode ser alterado
			if (bupEdited.getKey().startsWith("SYS_")) {
				// se ja tiver objeto criado e o valor tiver sendo alterado lanca excecao
				if (Long.parseLong(numObjetos) > 0 && !buForTest.getValue().equals(bupEdited.getValue()))
					throw new WarningException("A UN ja possui objeto criado. O parametro [" + bupEdited.getKey() + "] não pode ser alterdo");

			}

		}

	}

	/*
	 * < Metodos expostos pelo repository >
	 * 
	 * Apenas o service da entidade pode acessar
	 * o repositorio da entidade.
	 * Os metodos sao expostos para serem usados
	 * por outras classes service que precisar acessar
	 * os dados da entidade
	 */

	public BusinessUnit save(BusinessUnit businessUnit) {
		BusinessUnit bu = businessUnitRepository.save(businessUnit);

		return findById(bu.getCode());

	}
	
	public BusinessUnit saveBySystem(BusinessUnit businessUnit) {
		
		//Define que o save exectuado esta sendo feito pelo sitema, assim o usuario da acao é definido como sistema
		Utils.userSystem = true;	
		BusinessUnit bu = businessUnitRepository.save(businessUnit);

		return findById(bu.getCode());

	}

	/**
	 * Encontra o recurso pelo codigo
	 * @param code
	 * @return
	 */
	public BusinessUnit findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "BusinessUnit", id), 1);

		Optional<BusinessUnit> optional = businessUnitRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "BusinessUnit", id), 1);

		return (optional.get());

	}

	public BusinessUnit findByName(String name) {
		return (businessUnitRepository.findByNameIgnoreCase(name));

	}

	public BusinessUnit findByUniqueId(String uniqueId) {
		return (businessUnitRepository.findByUniqueIdIgnoreCase(uniqueId));

	}

	public List<BusinessUnit> findAllByStatus(Boolean status) {
		return (businessUnitRepository.findAllByStatus(status));

	}

	public List<BusinessUnit> findAll() {
		return (businessUnitRepository.findAll());

	}

	public boolean existsByName(String name) {
		return businessUnitRepository.existsByNameIgnoreCase(name);

	}

	public boolean existsByNameAndCodeIsNotIn(String name, Long code) {
		return businessUnitRepository.existsByNameIgnoreCaseAndCodeIsNotIn(name, code);

	}

	public void setValueBuParameter(Long buCode, String key, String value) {
		businessUnitRepository.setValueBuParameter(buCode, key, value);

	}

}
