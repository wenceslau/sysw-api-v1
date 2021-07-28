
package com.suite.core.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.dto.BoolDTO;
import com.suite.app.exception.WarningException;
import com.suite.app.service.EncodeService;
import com.suite.app.util.Utils;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.dao.DataDao;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.DataService;
import com.suite.core.model.Property;
import com.suite.core.model.Sector;
import com.suite.core.repository.DataServiceRepository;
import com.suite.core.repository.filter.DataServiceFilter;
import com.suite.core.util.Mailer;
import com.suite.core.util.UtilsCore;

/**
 * Service responsavel por manipular os dados recurso DataService
 * @author Wenceslau
 *
 */
@Service
public class DataServiceService extends CoreRulesService {

	@Autowired
	private DataServiceRepository dataServiceRepository;

	@Autowired
	private Mailer mailer;

	@Autowired
	private DataDao dataDao;

	@Autowired
	private EncodeService rsaService;

	/**
	 * Encontra o recurso pelo codigo
	 * @param code
	 * @return
	 */
	public DataService findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Insere o recurso no repositorio
	 * @param dataService
	 * @return
	 */
	public DataService insert(DataService dataService) {
		info("Insert: " + dataService);
		
		if (dataService.getCodSectorDataTaskParent() == null)
			if (dataService.getName().startsWith("DDT"))
				throw new WarningException("A palavra DDT é reservada para Data Task internos.");

		// relaciona lista propriedade com o objeto DataService
		relationshipDataServiceProperty(dataService, dataService);

		// verifica duplicidade do recurso na base
		ruleOfDataService(dataService);

		return save(dataService);

	}

	/**
	 * Insere o recurso no repositorio
	 * @param dataService
	 * @return
	 */
	public BoolDTO testDataService(Long code) {
		DataService dataService = findById(code);

		info("Test Connection:" + dataService);

		String ret = "";

		if (dataService.getType().equals(DataServiceType.DATABASE)) {

			try {
				ret = formatTranslate("msg_conexao_realiza_com_s_d_h_d_b_d_d_[%s]", dataDao.testConnection(dataService));

			} catch (RuntimeException | ClassNotFoundException | SQLException e) {
				throw new RuntimeException("msg_erro_ao_realizar_t_d_c", e);

			}

		} else if (dataService.getType().equals(DataServiceType.EMAILSERVICE)) {
			String idfr = dataService.getIdentifier();
			String nameProperty = dataService.getType() + "_EMAILTEST";
			Property property = dataService.getProperties().stream().filter(x -> x.getName().equals(nameProperty)).findFirst().orElse(null);

			if (property == null)
				throw new WarningException("msg_a_propriedade_emailtest_n_f_e_c_o_d_p_t");

			String body = UtilsCore.templateEmail(getAppLogged(), formatTranslate("msg_teste_de_conexao_s_d_d_[%s]", dataService.getName()));
			BusinessUnit buLogged = getSector().getBusinessUnit();
			BoolDTO sent = mailer.sendMail(idfr, buLogged, Arrays.asList(property.getValue().split(";")), formatTranslate("msg_teste_de_conexao"), body);

			if (sent.isValue())
				ret = formatTranslate("msg_email_enviado_com_s_p_[%s]", property.getValue());
			else
				throw new WarningException(formatTranslate("msg_o_teste_de_c_c_o_e_f_[%s]", sent.getMessage()));

		} else {
			throw new WarningException("msg_o_teste_de_c_e_v_a_p_o_t_d_e_e");

		}

		return new BoolDTO(true, ret);

	}

	/**
	 * Atualiza recurso no repositorio
	 * 
	 * @param code
	 * @param dataService
	 * @return
	 */
	public DataService update(Long code, DataService dataService) {
		// verifica duplicidade do recurso na base
		ruleOfDataService(dataService);

		DataService dbDataService = findById(code);

		String codSectorsDtTsk = dataService.getCodSectorsDataTask();

		if (codSectorsDtTsk != null && !codSectorsDtTsk.isEmpty()) {

			String codSectorsDtTskOriginal = dbDataService.getCodSectorsDataTask();

			if (codSectorsDtTsk != null && !codSectorsDtTsk.isEmpty()) {

				List<String> lstCodSectorsDtTsk = Arrays.asList(codSectorsDtTsk.split(","));
				List<String> lstCodSectorsDtTskOriginal = Arrays.asList(codSectorsDtTskOriginal.split(","));

				// Se o registro original tiver mais code que o recebido, houve remocao.
				if (lstCodSectorsDtTskOriginal.size() > lstCodSectorsDtTsk.size()) {
					for (String codSector : lstCodSectorsDtTskOriginal)// Percorre a lista com mais registros e identifica os que foram removidos

						if (!codSector.trim().isEmpty() && !lstCodSectorsDtTsk.contains(codSector)) { // se a lista anterior nao tiver o registro
														
						}
				}

				if (dbDataService.getCodSectorDataTaskParent() != null) {
					boolean contain = false;

					for (String cod : lstCodSectorsDtTsk) {
						if (Long.parseLong(cod) == dbDataService.getCodSectorDataTaskParent()) {
							contain = true;
							break;
						}
					}

					if (!contain)
						throw new WarningException("msg_o_setor_proprietario_d_t_d_d_n_p_s_r");
				}

			}

		}

		// relaciona lista propriedade do objeto DataService ao objeto que sera salvo
		dbDataService.getProperties().clear();
		dbDataService.getProperties().addAll(dataService.getProperties());
		relationshipDataServiceProperty(dataService, dbDataService);

		// Merge os dados do objeto recebido ao objeto original. Ignora os campos do array na copia
		BeanUtils.copyProperties(dataService, dbDataService, new String[] { "code", "properties", "sector", "codSectorDataTaskParent" });

		if (dbDataService.getCodSectorDataTaskParent() == null)
			if (dbDataService.getName().startsWith("DDT"))
				throw new WarningException("A palavra DDT é reservada para Data Task internos.");
		
		info("Update: " + dbDataService);

		return save(dbDataService);

	}

	/**
	 * Deleta recurso no repositorio
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		dataServiceRepository.deleteById(code);

	}

	/**
	 * Recupera a lista filtrada de recursos
	 * @param filter
	 * @return
	 */
	public List<DataService> listByFilter(DataServiceFilter filter) {
		List<DataService> list = listAllByBusinessUnitLogged(filter);

		// REMOVI. estava causando confusao pq ele aparece na lita do cadastro
		// e nao na lita para usar no atributo. a regra é somente os ds que tem o setor
		// logado podem ser usados na criacao de atributos.
		// e antes se o user tinha ums dos setores que o DS tem, ele aparecia na lista de cadasto
		// mas isso gera dupla interpretacao.
		// List<Sector> lsScUsLg = getUser().getSectors();
		List<Sector> lsScUsLg = new ArrayList<>();

		if (isSa() || isUa())
			lsScUsLg = getUser().getSectors();
		else
			lsScUsLg.add(getSector());

		list = applyRuleSectorVersusDataService(list, lsScUsLg);

		list.sort((p1, p2) -> p2.getStatus().compareTo(p1.getStatus()));

		for (DataService ds : list)
			for (Property p : ds.getProperties())
				if (p.getDataType().equals("PASSWORD"))
					p.setValue("");

		return list;

	}

	/**
	 * Recupera a lista filtrada de recursos
	 * @param filter
	 * @return
	 */
	public List<DataService> filterDataServiceExternalObjeto(DataServiceFilter filter) {
		List<DataService> list = listAllByBusinessUnitLogged(filter);

		List<Sector> lsScUsLg = new ArrayList<>();
		lsScUsLg.add(getSector());

		list = applyRuleSectorVersusDataService(list, lsScUsLg);

		list.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

		for (DataService ds : list)
			for (Property p : ds.getProperties())
				if (p.getDataType().equals("PASSWORD"))
					p.setValue("");

		return list;

	}

	/**
	 * Realiza o clone do recurso
	 * @param code
	 */
	public DataService cloneDataService(Long code, boolean keepPwd) {
		DataService dataService = findById(code);
		DataService dataServiceClone = (new DataService());

		List<Property> list = new ArrayList<>();
		Property property;

		for (Property p : dataService.getProperties()) {
			property = new Property();
			BeanUtils.copyProperties(p, property, new String[] { "code" });

			if (property.getDataType().equals("PASSWORD")) {

				if (keepPwd) {
					if (p.getValue().trim().length() < 50)
						property.setValue(rsaService.getRsa().encrypt(p.getValue().trim()));
					else
						property.setValue(p.getValue().trim());

				} else
					property.setValue("");

			}

			list.add(property);

		}

		list.forEach(c -> c.setCode(null));
		list.forEach(c -> c.setDataService(dataServiceClone)); // atualiza o objeto dataservice de cada objeto property
		dataServiceClone.setProperties(list);

		BeanUtils.copyProperties(dataService, dataServiceClone, new String[] { "code", "properties" });
		dataServiceClone.setCode(null);
		// String randonName = new Random().nextInt(9999) + "";
		dataServiceClone.setName("");
		dataServiceClone.setDescription("");
		dataServiceClone.setIdentifier("");
		dataServiceClone.setCodSectorsDataTask("");

		return (dataServiceClone);

	}

	/**
	 * Realiza o clone do recurso
	 * @param code
	 */
	public DataService cloneTemplate(String type) {
		DataService dataService = findTop1ByType(DataServiceType.valueOf(type));

		dataService = cloneDataService(dataService.getCode(), false);
		dataService.setScope("");
		dataService.getProperties().forEach(c -> c.setValue("")); // limpa o valor

		return dataService;

	}

	/**
	 * Faz o relacionamento entre as propriedades e o dataservice
	 * @param dataService
	 * @param dataServiceToSave
	 */
	private void relationshipDataServiceProperty(DataService dataService, DataService dataServiceToSave) {

		// Verifica se tem propriedades
		if (dataServiceToSave.getProperties() != null) {
			// atualiza o objeto DataService de cada Property
			dataServiceToSave.getProperties().forEach(p -> {
				p.setDataService(dataServiceToSave);
				p.setStatus(true);
				// O nome da propriedade eh composto do nome do tipo do dataservice
				// Se nao tiver nesse padrao, aplica a regra
				if (!p.getName().toUpperCase().contains(dataService.getType().toString()))
					p.setName(dataService.getType() + "_" + p.getName().trim().toUpperCase());

				// Campos do tipo pass, valor deve ser convertifo para hexa
				if (p.getDataType().equals("PASSWORD")) {
					if (p.getValue().trim() != null && !p.getValue().trim().isEmpty() && p.getValue().trim().length() < 50)
						p.setValue(rsaService.getRsa().encrypt(p.getValue().trim()));

				} else
					p.setValue(p.getValue().trim());

				// // valida se a string ja eh hexa
				// if (!StringUtils.isHexString(p.getValue().trim()))
				// p.setValue(StringUtils.toHexString(p.getValue().trim()));

			});

		}

	}

	/**
	 * Aplica regras para remover os DS que nao pode ser visto de acordo com o setor logado
	 * As regras sao so aplicadas quando possuir dados no campo CodSectorsDataTask
	 * Ou seja so eh aplicado no DS do escopo DATATASK
	 * @param list
	 * @return
	 */
	public List<DataService> applyRuleSectorVersusDataService(List<DataService> list, List<Sector> lsScUsLg) {
		// Set nao permite duplicata
		Set<DataService> setRet = new HashSet<>();

		// Percorre a lita de usuarios filtrados
		for (DataService ds : list) {

			// Se nao tiver setor cadastrado nao precisa aplicar a regra
			if (ds.getCodSectorsDataTask() == null || ds.getCodSectorsDataTask().trim().length() == 0) {
				setRet.add(ds);
				continue;

			}

			String[] sectors = ds.getCodSectorsDataTask().split(",");

			// Percorre a lista de setor do user
			for (String codSec : sectors) {
				if (codSec.trim().isEmpty())
					continue;
				Long code = Long.parseLong(codSec.trim());
				// Verifica se a lista de setor do usuario logado, tem o setor na qual o dataservice pode ser usado
				// Se tiver adiciona ele na lista a retornar
				if (lsScUsLg.stream().filter(s -> s.getCode().equals(code)).findFirst().isPresent())
					setRet.add(ds);

			}

		}

		list = new ArrayList<DataService>(setRet);
		return list;

	}

	public List<DataService> listAllByBusinessUnitLogged(DataServiceFilter filter) {
		// Aplica filtro para a businessUnit do setor logado
		applyFilterBusinessUnit(filter);

		return filter(filter);

	}

	public List<DataService> listAll(Long sectorCode) {
		DataServiceFilter filter = new DataServiceFilter();

		Sector sector = sectorService.findById(sectorCode);

		filter.setCodBusinessUnit(sector.getBusinessUnit().getCode());

		List<DataService> list = filter(filter);

		list.removeIf(x -> "DATATASK".equals(x.getScope()));

		return list;

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

	public DataService save(DataService dataService) {
		DataService bu = dataServiceRepository.save(dataService);

		return findById(bu.getCode());

	}

	public DataService findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "DataService", id), 1);

		Optional<DataService> optional = dataServiceRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "DataService", id), 1);

		DataService entity = optional.get();
		// dataServiceRepository.detachEntity(entity);

		return entity; // DataService.build(optional.get());

	}

	public DataService findTop1ByIdentifierAndType(String identifier, DataServiceType type) {
		DataService entity = dataServiceRepository.findTop1ByIdentifierIgnoreCaseAndType(identifier, type);
		// dataServiceRepository.detachEntity(entity);

		return entity; // DataService.build(dataServiceRepository.findTop1ByIdentifierAndType(identifier, type));

	}
	
	DataService findByCodSectorDataTaskParent(Long codSectorDataTaskParent) {
		return dataServiceRepository.findByCodSectorDataTaskParent(codSectorDataTaskParent);
	}


	public DataService findByIdentifierAndCodSectorsDataTask(String identifier, String codSectors) {
		DataService entity = dataServiceRepository.findByIdentifierIgnoreCaseAndCodSectorsDataTask(identifier, codSectors);
		// dataServiceRepository.detachEntity(entity);

		return entity; // DataService.build(dataServiceRepository.findByIdentifierAndCodSectorsDataTask(identifier, codSectors));

	}

	public DataService findTop1ByType(DataServiceType type) {
		DataService entity = dataServiceRepository.findTop1ByType(type);
		// dataServiceRepository.detachEntity(entity);

		return entity; // DataService.build(dataServiceRepository.findTop1ByType(type));

	}

	private List<DataService> filter(DataServiceFilter filter) {
		return (dataServiceRepository.filter(filter));

	}

	public boolean existsByNameAndBusinessUnitAndCodeIsNotIn(String name, Long buCode, Long code) {
		return dataServiceRepository.existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(name, buCode, code);

	}

	public boolean existsByNameAndBusinessUnit(String name, Long code) {
		return dataServiceRepository.existsByNameIgnoreCaseAndBusinessUnit_Code(name, code);

	}

	public boolean existsByIdentifierAndTypeAndBusinessUnit(String identifier, DataServiceType type, Long code) {
		return dataServiceRepository.existsByIdentifierIgnoreCaseAndTypeAndBusinessUnit_Code(identifier, type, code);

	}

	public boolean existsByIdentifierAndTypeAndBusinessUnitAndCodeIsNotIn(String identifier, DataServiceType type,
			Long buCode, Long code) {
		return dataServiceRepository.existsByIdentifierIgnoreCaseAndTypeAndBusinessUnit_CodeAndCodeIsNotIn(identifier, type,
				buCode, code);

	}

	public DataService findByName(String name) {
		return dataServiceRepository.findByNameIgnoreCase(name);
	}

	public List<DataService> findAll() {
		// TODO Auto-generated method stub
		return dataServiceRepository.findAll();
	}

	public DataService saveBySystem(DataService dataService) {
		Utils.userSystem = true;
		return save(dataService);
	}

}
