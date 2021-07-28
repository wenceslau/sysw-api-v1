
package com.suite.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.suite.app.base.Enum.ItemLicense;
import com.suite.app.exception.WarningException;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;
import com.suite.core.base.FilterCore;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.DataService;
import com.suite.core.model.GlossaryTerm;
import com.suite.core.model.Language;
import com.suite.core.model.Permission;
import com.suite.core.model.Profile;
import com.suite.core.model.Property;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.model.UserGroup;

/**
 * Service que contem metodos com as regeas de insercao e edicao dos recursos do
 * modulo
 * 
 * @author Wenceslau
 *
 */
@Service

public class CoreRulesService extends ServiceCore {

	// ******Public rules *****//

	/**
	 * Bloqueia a insercao ou edicao se for o setor logado for Default Se for o
	 * default lanca excecao
	 */
	public void checkDefaultSector() {
		if (isDefaultSector())
			throw new WarningException("msg_voce_esta_logado_c_o_s_d_n_e_p_i_o_a_o_d_n_n_s");

	}

	/**
	 * Valida se existe algum perfil com as memas permissoes
	 * @param profile
	 */
	public void checkDuplicateProfile(Profile profile) {
		List<Profile> lst = profileService.findByBusinessUnit(profile.getBusinessUnit());

		profile.getPermissions().sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));

		String cod1 = "";
		for (Permission p : profile.getPermissions())
			cod1 += p.getCode() + "";

		System.out.println(profile.getName() + " - " + cod1);

		for (Profile otherProfile : lst) {

			if (profile.getCode() == null || !otherProfile.getCode().equals(profile.getCode())) {
				otherProfile.getPermissions().sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));

				String cod2 = "";
				for (Permission p : otherProfile.getPermissions())
					cod2 += p.getCode() + "";

				System.out.println(otherProfile.getName() + " - " + cod2);

				boolean collectionsAreEqual = cod2.equals(cod1);

				if (collectionsAreEqual)
					throw new WarningException("O perfil " + otherProfile.getName()
							+ " contêm as mesmas permissões. Não é permitido perfis iguais");

			}

		}

	}

	/**
	 * Aplica no objeto Filter a BU do usario logado quando nao for o setor Default
	 * O usuario somente pode ver dados da sua BU. Os objetos do CORE nao possui
	 * setor. Nao restricao de para nao visualizar dados de outro setor
	 * @param filter
	 */
	public void applyFilterBusinessUnit(FilterCore filter) {
		// Se nao for setor Default aplica filtro do BU do setor logado. O setor Default
		// ve todas as UN
		if (!isDefaultSector())
			filter.setCodBusinessUnit(getSector().getBusinessUnit().getCode());

	}

	/**
	 * Valida o tamanho do campo de acordo com os parametros Lanca excecao
	 * persanalizada se o valor ultrapassar o permitido
	 * 
	 * @param value
	 * @param nameField
	 * @param length
	 * @param checkEmpty
	 */
	public void ruleOfStringLength(String value, String nameField, int length, boolean checkEmpty) {
		if (checkEmpty)
			ruleOfEmptyString(value, nameField);

		if (value != null && value.length() > length)
			throw new WarningException(formatTranslate("msg_o_tamanho_do_c_[%s]_s_o_l_s_[%s]_c_p", nameField, length));

	}

	/**
	 * Valida se a string possui caracteres especiais Lanca excecao personalizada se
	 * possuir
	 * 
	 * @param value
	 * @param model
	 */
	public void ruleOfSpecialCharacter(String value, String model) {
		if (StringUtils.hasSpecialCharacter(value))
			throw new WarningException(formatTranslate("msg_o_campo_[%s]_n_p_c_c_e", model));

	}

	/**
	 * Valida se a string possui caracteres especiais, usados para criacao de
	 * atributos e tabelas em banco de dados Lanca excecao personalizada se possuir
	 * 
	 * @param value
	 * @param model
	 */
	public void ruleOfSpecialCharacterObjectDataBase(String value, String model) {
		if (StringUtils.hasSpecialCharacterObjetoDataBase(value))
			throw new WarningException(formatTranslate("msg_o_campo_[%s]_n_p_c_c_e", model));

	}

	// ******Rules for core model *****//

	/**
	 * Aplica as regras de insercao e edicao para BusinessUnit
	 * @param businessUnit
	 */
	protected void ruleOfLanguage(Language language) {
		checkDefaultSectorAndNotSA();

		checkNotDefaultSector();

		// ruleOfSpecialCharacter(language.getKey(), message("lbl_chave"));

		ruleOfStringLength(language.getKey(), formatTranslate("lbl_chave"), 100, true);
		ruleOfStringLength(language.getDescription(), formatTranslate("lbl_descricao"), 200, false);
		ruleOfStringLength(language.getPortugues(), formatTranslate("lbl_portugues"), 900, true);
		ruleOfStringLength(language.getEnglish(), formatTranslate("lbl_ingles"), 900, true);
		ruleOfStringLength(language.getSpanish(), formatTranslate("lbl_espanhol"), 900, true);

		boolean exist = false;

		if (language.getCode() != null)
			exist = languageService.existsByKeyAndCodeIsNotIn(language.getKey(), language.getCode());
		else
			exist = languageService.existsByKey(language.getKey());

		if (exist) // //"Já existe um registro com essa chave"
			throw new WarningException(formatTranslate("msg_ja_existe_um_r_c_e_c"));

		if (language.getCode() != null)
			exist = languageService.existsByPortuguesAndCodeIsNotIn(language.getPortugues(), language.getCode());
		else
			exist = languageService.existsByPortugues(language.getPortugues());

		if (exist) // "Já existe um registro com essa tradução para o Português"
			throw new WarningException(formatTranslate("msg_ja_existe_um_r_c_e_t_p_o_[%s]", formatTranslate("lbl_portugues")));

		if (language.getCode() != null)
			exist = languageService.existsByEnglishAndCodeIsNotIn(language.getEnglish(), language.getCode());
		else
			exist = languageService.existsByEnglish(language.getEnglish());

		if (exist) // "Já existe um registro com essa tradução para o Ingles"
			throw new WarningException(formatTranslate("msg_ja_existe_um_r_c_e_t_p_o_[%s]", formatTranslate("lbl_ingles")));

		if (language.getCode() != null)
			exist = languageService.existsBySpanishAndCodeIsNotIn(language.getSpanish(), language.getCode());
		else
			exist = languageService.existsBySpanish(language.getSpanish());

		if (exist) // "Já existe um registro com essa tradução para o Espanhol"
			throw new WarningException(formatTranslate("msg_ja_existe_um_r_c_e_t_p_o_[%s]", formatTranslate("lbl_espanhol")));

	}

	/**
	 * Aplica as regras de insercao e edicao para BusinessUnit
	 * @param businessUnit
	 */
	protected void ruleOfBusinessUnit(BusinessUnit businessUnit) {
		checkDefaultSectorAndNotSA();

		checkNotDefaultSector();

		if (businessUnit.getUniqueId() != null && businessUnit.getUniqueId().length() < 15)
			businessUnit.setUniqueId(RandomStringUtils.randomAlphabetic(15).toUpperCase());

		ruleOfSpecialCharacter(businessUnit.getName(), formatTranslate("lbl_nome"));
		// ruleOfSpecialCharacter(businessUnit.getImage(), "Imagem");

		ruleOfStringLength(businessUnit.getName(), formatTranslate("lbl_nome"), 100, true);
		ruleOfStringLength(businessUnit.getDescription(), formatTranslate("lbl_descricao"), 250, false);
		ruleOfStringLength(businessUnit.getUniqueId(), formatTranslate("lbl_identificador"), 100, true);
		ruleOfStringLength(businessUnit.getImage(), formatTranslate("lbl_imagem"), 50, true);
		ruleOfStringLength(businessUnit.getLicense(), formatTranslate("lbl_licenca"), 4000, true);

		businessUnit.setName(trim(businessUnit.getName()));
		businessUnit.setDescription(trim(businessUnit.getDescription()));
		businessUnit.setUniqueId(trim(businessUnit.getUniqueId()));
		businessUnit.setImage(trim(businessUnit.getImage()));
		businessUnit.setLicense(trim(businessUnit.getLicense()));

		boolean exist = false;

		if (businessUnit.getCode() != null)
			exist = businessUnitService.existsByNameAndCodeIsNotIn(businessUnit.getName(), businessUnit.getCode());
		else
			exist = businessUnitService.existsByName(businessUnit.getName());

		if (exist)
			throw new WarningException("Já existe uma Unidade de Negócio com esse nome");

	}

	/**
	 * Aplica as regras de insercao e edicao para Permission
	 * @param businessUnit
	 */
	protected void ruleOfPermssion(Permission permission) {
		checkDefaultSectorAndNotSA();

		checkNotDefaultSector();

		permission.setStrApplication(permission.getApplication().getName());

	}

	/**
	 * Aplica as regras de insercao e edicao para DataService
	 * @param dataService
	 */
	protected void ruleOfDataService(DataService dataService) {
		// Qdo for o setor default o SA informa a BU na tela, qdo eh outro setor pega a
		// BU do setor logado
		if (!isDefaultSector())
			dataService.setBusinessUnit(getSector().getBusinessUnit()); // Adiciona a BU do setor logado
		
		for (Property prop : dataService.getProperties()) {

			if (prop.getName().equals("DATABASE_INSTANCE"))
				continue;

			if (prop.getValue() == null || prop.getValue().isEmpty())
				throw new WarningException(prop.getName() + " é obrigatório(a)");

		}

		// RULE se nao for SA scopo sempre sera USER
		// desativado 32-05-2020
		// if (!isSa())
		// dataService.setScope("USER");

		ruleOfSpecialCharacter(dataService.getName(), formatTranslate("lbl_nome"));
		ruleOfStringLength(dataService.getScope(), formatTranslate("lbl_scopo"), 50, true);
		ruleOfStringLength(dataService.getName(), formatTranslate("lbl_nome"), 100, true);
		ruleOfStringLength(dataService.getDescription(), formatTranslate("lbl_descricao"), 250, true);

		dataService.setName(trim(dataService.getName()));
		dataService.setDescription(trim(dataService.getDescription()));

		if (dataService.getIdentifier() == null || dataService.getIdentifier().length() < 15)
			dataService.setIdentifier(RandomStringUtils.randomAlphabetic(15).toUpperCase());

		// String identifier = StringUtils.upperAndUnderscor(trim(dataService.getIdentifier()));
		// dataService.setIdentifier(identifier);

		boolean exist = false;

		if (dataService.getCode() != null)
			// if exist by name and BU and the object is not himself. For update
			exist = dataServiceService.existsByNameAndBusinessUnitAndCodeIsNotIn(dataService.getName(),
					dataService.getBusinessUnit().getCode(), dataService.getCode());
		else
			// If exist by name and BU. For insert
			exist = dataServiceService.existsByNameAndBusinessUnit(dataService.getName(),
					dataService.getBusinessUnit().getCode());

		boolean isDataTask = false;
		if (dataService.getCodSectorsDataTask() != null && !dataService.getCodSectorsDataTask().isEmpty())
			isDataTask = true;

		String source = isDataTask ? "uma Tarefa de Dados" : "um Serviço de Dados";

		if (exist)
			throw new WarningException("Já existe " + source + " com esse nome");

		// if (dataService.getCode() != null)
		// // if exist by name and BU and the object is not himself. For update
		// exist = dataServiceService.existsByIdentifierAndTypeAndBusinessUnitAndCodeIsNotIn(
		// dataService.getIdentifier(), dataService.getType(), dataService.getBusinessUnit(),
		// dataService.getCode());
		// else
		// // If exist by name and BU. For insert
		// exist = dataServiceService.existsByIdentifierAndTypeAndBusinessUnit(dataService.getIdentifier(),
		// dataService.getType(), dataService.getBusinessUnit());
		//
		// if (exist)
		// throw new WarningException("Já existe um Serviço de Dados com esse identificador");
				
	}

	/**
	 * Aplica as regras de insercao e edicao para Sector
	 * @param sector
	 */
	protected void ruleOfSector(Sector sector) {
		// Qdo for o setor default o SA informa a BU na tela, qdo eh outro setor pega a
		// BU do setor logado
		if (!isDefaultSector())
			sector.setBusinessUnit(getSector().getBusinessUnit()); // Adiciona a BU do setor logado

		if (sector.getUniqueId() != null && sector.getUniqueId().length() < 15)
			sector.setUniqueId(RandomStringUtils.randomAlphabetic(15).toUpperCase());

		ruleOfSpecialCharacter(sector.getName(), formatTranslate("lbl_nome"));

		ruleOfStringLength(sector.getName(), formatTranslate("lbl_nome"), 100, true);
		ruleOfStringLength(sector.getDescription(), formatTranslate("lbl_descricao"), 250, true);
		ruleOfStringLength(sector.getUniqueId(), formatTranslate("lbl_identificador"), 100, true);

		sector.setName(trim(sector.getName()));
		sector.setDescription(trim(sector.getDescription()));
		sector.setUniqueId(trim(sector.getUniqueId()));

		if (sector.getRequiredDb()) {
			ruleOfStringLength(sector.getNameExternalDatabase(), "Nome Base de Dados", 100, true);

			ruleOfSpecialCharacterObjectDataBase(sector.getNameExternalDatabase(), "Nome Base de Dados");

			String name = StringUtils.lowerAndUnderscor(StringUtils.deAccent(sector.getNameExternalDatabase()));
			name = "db_" + name.replace("db_", "");

			sector.setNameExternalDatabase(StringUtils.lowerAndUnderscor(name));

		}

		boolean exist = false;

		if (sector.getCode() != null)
			// if exist by name and BU and the object is not himself. For update
			exist = sectorService.existsByNameAndBusinessUnitAndCodeIsNotIn(sector.getName(), sector.getBusinessUnit().getCode(),
					sector.getCode());
		else
			// If exist by name and BU. For insert
			exist = sectorService.existsByNameAndBusinessUnit(sector.getName(), sector.getBusinessUnit().getCode());

		if (exist)
			throw new WarningException("Já existe um setor com esse nome");

		exist = false;

		if (sector.getCode() != null)
			// if exist by uniqueId and BU and the object is not himself. For update
			exist = sectorService.existsByUniqueIdAndBusinessUnitAndCodeIsNotIn(sector.getUniqueId(),
					sector.getBusinessUnit().getCode(), sector.getCode());
		else
			// If exist by uniqueId and BU. For insert
			exist = sectorService.existsByUniqueIdAndBusinessUnit(sector.getUniqueId(), sector.getBusinessUnit().getCode());

		if (exist)
			throw new WarningException("Já existe um setor com esse identificador");

	}

	/**
	 * Aplica as regras de insercao e edicao para Profile
	 * @param profile
	 */
	protected void ruleOfProfile(Profile profile) {
		ruleProfilePemissionExclusive(profile);

		// Se for default e SA lanca excecao
		checkDefaultSectorAndNotSA();

		if (profile.getStatus() != null && profile.getStatus() == false)
			if (userService.existsByProfile(profile))
				throw new WarningException("msg_esse_perfil_esta_e_u");

		if (isDefaultSector()) {
			// RULE Perfil inseridos pelo setor Default sem BusinessUnit são sempre nativos
			// tipo 4...
			// RULE com BusinessUnit sao customizado tipo 5
			if (profile.getBusinessUnit() == null)
				profile.setType(4); // Nativo
			else
				profile.setType(5); // Customizado

		} else {
			// Profile 5 = inserido pelo usuario, customizado
			profile.setType(5);
			profile.setBusinessUnit(getSector().getBusinessUnit()); // Adiciona a BU do setor logado

		}

		ruleOfSpecialCharacter(profile.getName(), formatTranslate("lbl_nome"));
		ruleOfStringLength(profile.getName(), formatTranslate("lbl_nome"), 100, true);

		profile.setName(trim(profile.getName()));

		boolean exist = false;

		// For update
		if (profile.getCode() != null) {

			// Busiess Unit is null, profile belongs for all BU
			if (profile.getBusinessUnit() == null) {
				// check if exist by name and the objet is not himself
				exist = profileService.existsByNameAndCodeIsNotIn(profile.getName(), profile.getCode());

			} else {
				// if exist business unit, the pofiel ony for your BU check if exist by name,
				// and is not himself and BU is equal or is null
				// This validates if there is not the same name in your BU, or for all BU except
				// himself
				int count = profileService.existsByNameAndCodeIsNotInAndBusinessUnitOrBusinessUnitIsNull(
						profile.getName(), profile.getCode(), profile.getBusinessUnit());

				if (count != 0)
					exist = true;

			}

		} else {
			// For Insert

			// Busiess Unit is null, profile belongs for all BU

			if (profile.getBusinessUnit() == null) {
				// check if exist by name
				exist = profileService.existsByName(profile.getName());

			} else {
				// if exist business unit, the pofiel ony for your BU check if exist by name and
				// BU is equal or is null
				// This validates if there is not the same name in your BU, or for all BU except
				// himself
				int count = profileService.existsByNameAndBusinessUnitOrBusinessUnitIsNull(profile.getName(),
						profile.getBusinessUnit());

				if (count != 0)
					exist = true;

			}

		}

		if (exist)
			throw new WarningException("Já existe um perfil com esse nome");

		List<Long> codes = new ArrayList<>();

		for (Permission perm : profile.getPermissions())
			codes.add(perm.getCode());

	}

	/**
	 * Aplica as regras de insercao e edicao para User
	 * @param user
	 */
	protected void ruleOfUser(User user) {
		checkDefaultSectorAndNotSA();

		// Qdo for o setor default o SA informa a BU na tela, qdo eh outro setor pega a
		// BU do setor logado
		if (!isDefaultSector())
			// Qdo for o setor default o SA informa a BU na tela, qdo eh outro setor pega a
			user.setBusinessUnit(getSector().getBusinessUnit()); // Adiciona a BU do setor logado

		ruleOfSpecialCharacter(user.getName(), formatTranslate("lbl_nome"));
		ruleOfSpecialCharacter(user.getUsername(), formatTranslate("lbl_usuario"));

		ruleOfStringLength(user.getName(), formatTranslate("lbl_nome"), 100, true);
		ruleOfStringLength(user.getEmail(), formatTranslate("lbl_email"), 100, true);
		ruleOfStringLength(user.getUsername(), formatTranslate("lbl_usuario"), 100, true);

		user.setName(trim(user.getName()));
		user.setEmail(trim(user.getEmail()));
		user.setUsername(trim(user.getUsername()));

		if (user.getUsername().contains(" "))
			throw new WarningException("O nome do usuário não pode ter espaços");

		user.setUsername(StringUtils.deAccent(user.getUsername()));

		boolean existUsername = false;
		boolean existEmail = false;

		if (user.getCode() != null) {
			existUsername = userService.existsByUsernameAndCodeIsNotIn(user.getUsername(), user.getCode());
			if (!existUsername)
				existEmail = userService.existsByEmailAndCodeIsNotIn(user.getEmail(), user.getCode());

		} else {
			existUsername = userService.existsByUsername(user.getUsername());
			if (!existUsername)
				existEmail = userService.existsByEmail(user.getEmail());

		}

		if (existUsername)
			throw new WarningException("Já existe um usuário com esse login");

		if (existEmail)
			throw new WarningException("Já existe um usuário com esse e-mail");

		// RULE Nao pode se criar outro user SA, apenas o da instalacao
		if (user.getCode() == null && user.getProfile().getType().equals(1))
			throw new WarningException("O Perfil " + user.getProfile().getName() + " não pode ser usado em outro usuário");

		// RULE Apenas user SA no setor default pode criar perfil do tipo 2 (UA)
		if (!isDefaultSector() && user.getProfile().getType().equals(2) && user.getCode() == null)
			throw new WarningException(
					"Usuarios com o perfil " + user.getProfile().getName() + " não pode ser criado por este usuario neste setor");
	}

	public void userLicenseRule(User userToSave, User userToMerge) throws RuntimeException {

		// Usuarios criados no setor default nao valida licenca
		if (!isDefaultSector()) {

			try {

				int numUsersBu = userService.countUserOnAppLogged(userToSave.getBusinessUnit().getCode());

				licenseService.checkCountItemLicense(ItemLicense.USER, numUsersBu);

			} catch (Exception e) {

				if (e instanceof WarningException) {

					// Se estiver atualizando e o registro do banco for true, nao emite alerta
					if (userToMerge != null && userToMerge.getStatus())
						return;

					// se estiver atualizando e o registro do banco estiver falso, nao pode deixar pq pode
					// estar ativando um registro

					userToSave.setStatus(false);
					userToSave.setMessage(formatTranslate("msg_o_numero_de_u_u_o_m_p_p_s_l_o_u_f_c_o_s_i"));

				} else
					throw e;

				// O numero de usuários ultrapassou o maximo permitido pela sua licença. O usuário foi criado com o status inativo
			}

		}

	}

	/**
	 * Aplica as regras de insercao e edicao para grupo
	 * @param group
	 */
	protected void ruleOfGroup(UserGroup group) {
		// Qdo for o setor default o SA informa a BU na tela, qdo eh outro setor pega a BU do setor logado
		if (!isDefaultSector())
			group.setBusinessUnit(getSector().getBusinessUnit()); // Adiciona a BU do setor logado

		ruleOfSpecialCharacter(group.getName(), formatTranslate("lbl_nome"));
		ruleOfStringLength(group.getName(), formatTranslate("lbl_nome"), 100, true);
		ruleOfStringLength(group.getDescription(), formatTranslate("lbl_descricao"), 250, true);

		group.setName(trim(group.getName()));
		group.setDescription(trim(group.getDescription()));

		boolean exist = false;

		// Verifica se ja existe algum registro com os mesmos dados
		if (group.getCode() != null)
			exist = groupService.existsByNameAndBusinessUnitAndCodeIsNotIn(group.getName(), group.getBusinessUnit().getCode(), group.getCode());
		else
			exist = groupService.existsByNameAndBusinessUnit(group.getName(), group.getBusinessUnit().getCode());

		if (exist)
			throw new WarningException("Já existe um grupo com esse nome");

	}

	/**
	 * TODO, remover e passar par ao DPG
	 * @param termosGlossario
	 */
	public void ruleOfTermosGlossario(GlossaryTerm termosGlossario) {
		// TODO Auto-generated method stub

	}

	// ******Rules and auxilary for permissions *****//

	/**
	 * Remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
	 * @param listPermissions
	 * @param businessUnit
	 */
	public void rulePermissionApplication(List<Permission> listPermissions, BusinessUnit businessUnit, Sector sector, String type) {
		// Se nao tiver bu aplica usando a do setor logado
		if (businessUnit == null)
			businessUnit = getSector().getBusinessUnit();

		if (type != null)
			loggerPermissions(listPermissions, type);

		if (businessUnit.getApplications() == null)
			businessUnit = businessUnitService.findByCode(businessUnit.getCode());

		List<Application> applications = businessUnit.getApplications();

		if (type != null)
			loggerApplications(applications);

		// for (Permission p : listPermissions)
		// if (p.getApplication().getName() == null)
		// System.out.println();
		//
		// for (Application application : applications)
		// if (application == null)
		// System.out.println();

		// Remove todas as permissoes que nao pertence as apps da bu logado
		boolean b = listPermissions.removeIf(p -> !applications.stream().anyMatch(app -> p.getApplication().getName().equals(app.getName())));

		if (type != null)
			info("Removidas permissoes que nao pertence a BU logada: " + b + " Size: " + listPermissions.size());

		if (!sector.getName().toUpperCase().equals("DEFAULT")) {
			String nameApp = sector.getApplication() != null ? sector.getApplication().getName() : "";
			// info("Aplicacao do setor logado: [" + nameApp + "] App CORE ja eh nativa do setor");

			// Remove todas as permissoes que nao pertence as app do setor logado, menos o CORE
			b = listPermissions.removeIf(p -> (!p.getApplication().getName().equals("CORE") && !p.getApplication().getName().equals(nameApp)));

		} else {
			// info("Setor Default logado, nao aplica regra de remocao de permissao");

		}

		Set<String> modules = Utils.contextMap.keySet();

		if (type != null)
			info("Modulos ativos: " + modules);

		// Remove as permissoes das aplicacoes que nao estao ativas no pom.xml
		b = listPermissions.removeIf(p -> !modules.stream().anyMatch(mdl -> p.getApplication().getNameModuleSource().equals(mdl)));

		if (type != null)
			info("Removidas permissoes de modulos nao ativos: " + b + " Size: " + listPermissions.size());

	}

	/**
	 * Remove permissoes da lista baseado no tipo de perfil do usuario logado
	 * 
	 * @param listPermissions
	 */
	protected void removePermissionByTypeProfile(List<Permission> listPermissions, boolean applySA) {

		// Se for o SA remove as permissoes do tipo 0-Exclusiva SA, elas nao podem ser
		// Atribuidas a outros perfis
		if (isSa()) {
			if (applySA)
				listPermissions.removeIf(p -> p.getType() < 1);
			return;

		}

		// Se for UA, remove as permissoes do tipo 0-Exclusiva SA, 1-Compartilhada SA, 2-Exclusiva UA
		if (isUa()) {
			listPermissions.removeIf(p -> p.getType() < 3);
			return;

		}

		// Se for UA, remove as permissoes do tipo 0-Exclusiva SA, 1-Compartilhada SA, 2-Exclusiva UA 3-Compartilhada UA, 4-Exclusiva ADM
		if (isAdm()) {
			listPermissions.removeIf(p -> p.getType() < 5);
			return;

		}

		// Se nao qq outro usuer,
		// Remove as permissoes do tipo 0-Exclusiva SA, 1-Compartilhada SA, 2-Exclusiva UA 3-Compartilhada UA, 4-Exclusiva ADM,
		// 4-Compartilhada ADM
		listPermissions.removeIf(p -> p.getType() < 6);

		// Criar estrutura de classificacao de permissao
		// 0 para excusiva, 1 para SA, 2 - BA, 3 - A, 4 - users
		// quando o user for um SA mostra todas as permissoes maior ou igual a 1
		// quando o user for um BA mostra todas as permissoes maior ou igual a 2
		// quando o user for um A mostra todas as permissoes maior ou igual a 3
		// quando o user for um U mostra todas as permissoes maior ou igual a 4
		// Com esse controle nao havera necessidade de remover permissao,
		// Basta fazer o select de acordo com perfil do usuario

	}

	private void loggerApplications(List<Application> applications) {
		String apps = " ";
		for (Application app : applications)
			apps += app.getName() + ",";

		apps = apps.substring(0, apps.length() - 1).trim();
		info("Aplicacoes da BU do usuario logado: [" + apps + "]");

	}

	private void loggerPermissions(List<Permission> listPermissions, String type) {
		String perms = " ";
		for (Permission perm : listPermissions)
			perms += perm.getKey() + ",";

		perms = perms.substring(0, perms.length() - 1).trim();
		info("Permissoes [" + type + "] [" + listPermissions.size() + ": " + perms + "]");

	}

	// ******Rules and auxilary for permissions *****//

	/**
	 * Valida se string esta vazia Lanca excecao persanalizada se estiver
	 * 
	 * @param value
	 * @param model
	 */
	private void ruleOfEmptyString(String value, String field) {
		if (value != null)
			value = value.trim();

		if (StringUtils.isEmpty(value))
			throw new WarningException(formatTranslate("msg_o_campo_[%s]_n_p_s_v", field));

	}

	/**
	 * Bloqueia a insercao ou edicao seja se for o setor logado for Default e o
	 * usuario logado nao for o SA Se for o default e nao for o SA lanca excecao
	 */
	private void checkDefaultSectorAndNotSA() {
		if (isDefaultSector() && !isSa())
			throw new WarningException("msg_voce_esta_logado_c_o_s_d_a_o_s_p_i_o_e_d_d_c_n_s");

	}

	/**
	 * Remove as permissoes exclusivas SA da lista de permissao do perfil Permissoes
	 * exclusivas sao as permissoes do tipo 0 Nao lanca execao, apenas remove e
	 * adiciona mensagem no objeto
	 * 
	 * @param profile
	 */
	private void ruleProfilePemissionExclusive(Profile profile) {
		// Remove as permissoes exclusivas
		// RULE Permissoes exclusiva SA nao pode ser atribuidas a nenhum outro perfil...
		// RULE Nem mesmo a perfil SA, porque no cadastro da permissao se ela for...
		// RULE definida como exclusisa SA ela ja eh atribuida ao perfil SA
		boolean remove = profile.getPermissions().removeIf(p -> (p.getType() != null && p.getType().intValue() == 0L));

		if (remove)
			profile.setMessage("msg_permissoes_exclusivas_foram_r_e_p_n_p_s_a");

	}

	/**
	 * Bloqueia a insercao ou edicao seja seo setor logado NAO for o Default Se nao
	 * for o default lanca excecao
	 */
	private void checkNotDefaultSector() {
		if (!isDefaultSector())
			throw new WarningException("msg_esse_objeto_somente_p_s_c_o_e_n_s_d");

	}

	/**
	 * Centraliza a chamada do metodo
	 * 
	 * @param value
	 * @return
	 */
	private String trim(String value) {
		return StringUtils.trim(value);

	}

}
