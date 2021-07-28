
package com.suite.core.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.base.Enum.DataType;
import com.suite.app.dto.BoolDTO;
import com.suite.app.exception.WarningException;
import com.suite.core.dao.DataDao;
import com.suite.core.dto.ColumnDTO;
import com.suite.core.dto.DatabaseDTO;
import com.suite.core.dto.TableDTO;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.DataService;
import com.suite.core.model.Property;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.repository.SectorRepository;
import com.suite.core.repository.filter.SectorFilter;

/**
 * Service responsavel por manipular os dados recurso Sector
 * @author Wenceslau
 *
 */
@Service
public class SectorService extends CoreRulesService {

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private DataDao dataDao;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public Sector findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Insere o recurso na base
	 * @param sector
	 * @return
	 */
	@Transactional("transactionManager")
	public Sector insert(Sector sector) {
		info("Insert: " + sector);
		// Nao pode ser injetada, precisa ter uma intancia para cada execucao
		// Para criar DB usa sempre autocomit true
		boolean createDb = false;

		try {
			sector.setUniqueId(RandomStringUtils.randomAlphabetic(15).toUpperCase());
			ruleOfSector(sector);
			sector = save(sector);

			// RULE A BU define se o setor tera base de dados assiciada
			createDb = sector.getRequiredDb();

			// List users SA e BA ativos
			List<User> listUsers = userService.listByProfileTypeIsLessThanAndStatus(3, true);

			// Atualiza todos os users SA e BA ativos com novos setores
			for (User user : listUsers) {

				if (isSa(user.getProfile()) || user.getBusinessUnit().equals(sector.getBusinessUnit())) {
					// Hash apenas para notificar que o objeto user sofreu update
					user.setSectorHash(new Random().nextInt(9999999));
					user.getSectors().add(sector);
					userService.updateWithoutCheckSector(user);

				}

			}

			if (sector.getDataService() != null) {
				String desc = valueParameterBusinessUnit("SYS_DECRICAO_TAREFA_DE_DADOS_SETOR", null).trim();
				String name = valueParameterBusinessUnit("SYS_NANE_TAREFA_DE_DADOS_SETOR", null).trim();

				DataService dataTask = dataServiceService.cloneDataService(sector.getDataService().getCode(), true);
				dataTask.setCodSectorsDataTask(sector.getCode() + "");
				dataTask.setDescription(desc);
				dataTask.setName(name + " " + sector.getName());
				dataTask.setScope("DATATASK");
				dataTask.setIdentifier("LOCKED000000000");
				dataTask.setCodSectorDataTaskParent(sector.getCode());

				for (Property prop : dataTask.getProperties()) {

					if (prop.getName().equals("DATABASE_NAME")) {
						prop.setValue(sector.getNameExternalDatabase());
						break;

					}

				}

				dataServiceService.insert(dataTask);

			}

			if (createDb)
				createDatabase(sector);

		} catch (RuntimeException | ClassNotFoundException | SQLException e) {
			throwException(e, "Falha ao inserir setor");

		}

		return sector;

	}

	/**
	 * Atualiza objeto na base9
	 * @param code
	 * @param sector
	 * @return
	 */
	@Transactional("transactionManager")
	public Sector update(Long code, Sector sector) {
		info("Insert: " + sector);
		// Nao pode ser injetada, precisa ter uma intancia para cada execucao
		boolean createDb = false;

		try {
			ruleOfSector(sector);
			Sector sectorToSave = findByCode(code);
			// RULE A BU define se o setor tera base de dados associada
			createDb = sector.getRequiredDb();

			if (createDb) {

				if (sectorToSave.getDateCreateDatabase() != null) {
					if (!sector.getDataService().equals(sectorToSave.getDataService()))
						throw new WarningException(
								"O serviço de dados não pode ser editado após a base de dados ter sido criada!");

				}

			}

			// RULE Setor nao pode ter sua BU editada apos o insert
			BeanUtils.copyProperties(sector, sectorToSave, new String[] { "code", "businessUnit" });
			sector = save(sectorToSave);

			if (createDb) {
				if (sector.getDateCreateDatabase() == null)
					createDatabase(sector);
				// Cria a tabela userAction se ela ainda nao foi criada
				createUserActionTable(sector);

			}

			//atualiza o DTT do setor caso ocorreu alguma alteracao no nome ou nas propriedades do dataservcie
			if (sector.getDataService() != null) {
				String name = valueParameterBusinessUnit("SYS_NANE_TAREFA_DE_DADOS_SETOR", null).trim();
				DataService dataTask = dataServiceService.findByCodSectorDataTaskParent(sector.getCode());
				dataTask.setName(name + " " + sector.getName());
				List<Property> list = new ArrayList<>();
				Property property;
				for (Property p : sector.getDataService().getProperties()) {
					property = new Property();
					BeanUtils.copyProperties(p, property, new String[] { "code", "dataService" });
					if (property.getName().equals("DATABASE_NAME"))
						property.setValue(sector.getNameExternalDatabase());					
					list.add(property);
				}

				list.forEach(c -> c.setCode(null));
				list.forEach(c -> c.setDataService(dataTask)); // atualiza o objeto dataservice de cada objeto property				
				dataTask.getProperties().clear();
				dataTask.getProperties().addAll(list);
				dataServiceService.save(dataTask);
			}

		} catch (RuntimeException | ClassNotFoundException | SQLException e) {
			throwException(e, "Falha ao atualizar setor");

		}

		return sector;

	}

	/**
	 * Deleta objeto na base
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		sectorRepository.deleteById(code);

	}

	/**
	 * Recupera a lista filtrada
	 * TODO: criar metodo list que traz apenas os ativos usando as mesmas regras
	 * @return
	 */
	public List<Sector> filterBy(SectorFilter filter) {
		List<Sector> list;

		// Se for SA logado, filtra os setores com os dados de filtr da tela
		// Se nao for SA a lista de setor eh os setores do proprio usuario
		if (isSa()) {
			// se nao for o setor default aplica filtro para a businessUnit do setor logado
			if (!isDefaultSector())
				applyFilterBusinessUnit(filter);
			list = filter(filter);

		} else {
			// O usuario somente pode listar as setores que estao amarrados a ele
			list = getUser().getSectors();
			java.util.function.Predicate<Sector> filterPredicate;

			// Remove os registros inativos
			if (filter.getStatus() != null) {
				filterPredicate = p -> !p.getStatus();
				list.removeIf(filterPredicate);

			}

			// Aplica o fitro por nome recebido do filter, remove os registros que nao
			// contem o valor do filtro
			if (filter.getName() != null) {
				filterPredicate = p -> !p.getName().toLowerCase().contains(filter.getName().toLowerCase());
				list.removeIf(filterPredicate);

			}

		}

		if (filter.isOnlySectorFromAppLogged()) {
			Sector sec = getSector();
			Long codAppSectorLogged = sec.getApplication() != null ? sec.getApplication().getCode() : 0L;
			list.removeIf(p -> p.getApplication() != null && !p.getApplication().getCode().equals(codAppSectorLogged));

		}

		if (filter.getOrderBy() == null)
			list.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));

		return list;

	}

	/**
	 * Retorna a lista de setor baseado na UN
	 * @param username
	 * @return
	 */
	public List<Sector> listByBusinessUnit(Long code) {
		BusinessUnit businessUnit;
		// Se nao verio codigo, pega a BU do setor logado
		if (code != null && code.longValue() == -1L)
			businessUnit = getSector().getBusinessUnit();
		else
			businessUnit = businessUnitService.findById(code);

		// Recupera a lista beasedo no registro da BU ouse se ela for nula
		List<Sector> lst = findAllByBusinessUnitOrBusinessUnitIsNull(businessUnit);
		lst.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));
		return lst;

	}

	/**
	 * Retorna a lista de setor baseado no nome do usuario
	 * @param username
	 * @return
	 */
	public List<Sector> listByUsername(String username) {
		// Encontra o usuario
		User user = userService.findByUsernameOrNull(username);

		// Verifica se existe e se nao esta inativo
		if (user != null) {
			if (!user.getStatus())
				throw new WarningException("Este usu\u00E1rio est\u00E1 inativo");

			// Verifica se o user tem BU e se ela nao esta inativa
			if (user.getBusinessUnit() != null && !user.getBusinessUnit().getStatus())
				throw new WarningException("Unidade de Neg\u00F3cio do usu\u00E1rio est\u00E1 inativa");

			// lista de setor do usuario
			List<Sector> list = user.getSectors();
			java.util.function.Predicate<Sector> filterPredicate;

			// Remove os setores inativos
			filterPredicate = p -> p.getStatus() == false;
			list.removeIf(filterPredicate);
			list.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));

			return list;

		}

		// nao tem user retorna vazio
		return Collections.emptyList();

	}

	/**
	 * Cria a base de dados do setor qdo permitido
	 * @param code
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Transactional("transactionManager")
	private BoolDTO createDatabase(Sector sector) throws ClassNotFoundException, SQLException {
		if (sector.getDataService() == null)
			throw new WarningException("O setor não tem serviço de dados definido para poder criar a base de dados");

		// Atualiza o objeto data service com um objto full
		sector.setDataService(dataServiceService.findById(sector.getDataService().getCode()));

		if (dataDao.existDatabase(sector.getDataService(), sector.getNameExternalDatabase()))
			throw new WarningException("O nome " + sector.getNameExternalDatabase() + " já está associado a outro setor.");

		dataDao.createDataBase(sector.getDataService(), sector.getNameExternalDatabase());
		sector.setDateCreateDatabase(LocalDateTime.now());

		save(sector);

		return new BoolDTO(true, "A base de dados [" + sector.getNameExternalDatabase() + "]  foi criada com sucesso");

	}

	/**
	 * Cria a tabela UserAction
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void createUserActionTable(Sector sector) {
		if (!sector.getRequiredDb())
			return;

		// Cria o objeto sem nome de base, sera usada do dataservice
		DatabaseDTO databaseTO = getDatabaseTO(sector);
		TableDTO table = new TableDTO(databaseTO, "tb_user_action");

		try {
			if (dataDao.existTable(table))
				return;

			info("Criar tabela UserAction do Banco [" + databaseTO.getName() + "]");
			ColumnDTO columnPk = new ColumnDTO("cod_record", DataType.LONG, false);
			columnPk.setPrimaryKey(true);
			table.addColumn(columnPk);

			ColumnDTO column = (new ColumnDTO("nam_action", DataType.STRING, false));
			column.setSizeText(10);
			table.addColumn(column);

			column = (new ColumnDTO("nam_object", DataType.STRING, false));
			column.setSizeText(50);
			table.addColumn(column);
			table.addColumn(new ColumnDTO("idt_record", DataType.LONG, false));

			column = new ColumnDTO("val_record", DataType.STRING, false);
			column.setSizeText(Integer.MAX_VALUE);
			table.addColumn(column);

			table.addColumn(new ColumnDTO("dtt_record", DataType.DATETIME, false));
			table.addColumn(new ColumnDTO("usr_record_fk", DataType.LONG, false));

			dataDao.createTable(table);

			// Cria index exclusivo na coluna nam_object
			table.getColumns().clear();
			table.addColumn(new ColumnDTO("nam_object"));

			dataDao.createNoUniqueIndex(table);

			info("Tabela tb_user_action criada com sucesso");

			dataDao.commit(databaseTO);

		} catch (ClassNotFoundException | SQLException e) {
			dataDao.roolback(databaseTO);
			throw new WarningException("Falha ao Criar a tabela User Action do Setor " + e.getMessage());

		}

	}

	/**
	 * Retorna um objeto TO a partir do base do setor
	 * @param sector
	 * @return
	 */
	public DatabaseDTO getDatabaseTO(Sector sector) {
		if (sector.getDataService().getProperties() == null)
			sector.setDataService(dataServiceService.findById(sector.getDataService().getCode()));

		return new DatabaseDTO(sector.getDataService(), sector.getNameExternalDatabase());

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

	public Sector save(Sector sector) {
		Sector sec = sectorRepository.save(sector);

		return findById(sec.getCode());

	}

	public Sector findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Sector", id), 1);

		Optional<Sector> optional = sectorRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Sector", id), 1);

		Sector entity = optional.get();
		// sectorRepository.detachEntity(entity);

		return entity; // Sector.build(optional.get());

	}

	public Sector findByIdOrNull(Long id) {
		if (id == null)
			return null;

		Optional<Sector> optional = sectorRepository.findById(id);
		if (!optional.isPresent())
			return null;

		Sector entity = optional.get();
		// sectorRepository.detachEntity(entity);

		return entity; // Sector.build(optional.get());

	}

	public Sector findByNameExternalDatabase(String nameExternalDatabase) {
		return sectorRepository.findByNameIgnoreCaseExternalDatabase(nameExternalDatabase);

	}

	public List<Sector> findAllByBusinessUnitOrBusinessUnitIsNull(BusinessUnit businessUnit) {
		return (sectorRepository.findAllByBusinessUnitOrBusinessUnitIsNull(businessUnit));

	}

	private List<Sector> filter(SectorFilter filter) {
		return (sectorRepository.filter(filter));

	}

	public boolean existsByNameAndBusinessUnit(String name, Long code) {
		return sectorRepository.existsByNameIgnoreCaseAndBusinessUnit_Code(name, code);

	}

	public boolean existsByNameAndBusinessUnitAndCodeIsNotIn(String name, Long buCode, Long code) {
		return sectorRepository.existsByNameIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(name, buCode, code);

	}

	public boolean existsByUniqueIdAndBusinessUnit(String uniqueId, Long code) {
		return sectorRepository.existsByUniqueIdIgnoreCaseAndBusinessUnit_Code(uniqueId, code);

	}

	public boolean existsByUniqueIdAndBusinessUnitAndCodeIsNotIn(String uniqueId, Long buCode, Long code) {
		return sectorRepository.existsByUniqueIdIgnoreCaseAndBusinessUnit_CodeAndCodeIsNotIn(uniqueId, buCode, code);

	}

	public boolean existsByDataService_Code(Long code) {
		return sectorRepository.existsByDataService_Code(code);

	}

	public void detachEntity(Sector sector) {
		sectorRepository.detachEntity(sector);

	}

	public List<Sector> findAllByStatus(Boolean status) {
		return sectorRepository.findAllByStatus(status);

	}

	public Sector findByName(String name) {
		return sectorRepository.findByNameIgnoreCase(name);
	}

	public Long countByBusinessUnit_Code(Long code) {
		return sectorRepository.countByBusinessUnit_Code(code);
	}

	public Sector findByNameAndBusinessUnit_Code(String name, Long code) {
		return sectorRepository.findByNameIgnoreCaseAndBusinessUnit_Code(name, code);
	}

	public Sector saveBySystem(Sector sector) {
		return save(sector);
	}

}
