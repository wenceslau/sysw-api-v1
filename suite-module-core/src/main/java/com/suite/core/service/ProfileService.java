
package com.suite.core.service;

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
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.exception.WarningException;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.Permission;
import com.suite.core.model.Profile;
import com.suite.core.model.Sector;
import com.suite.core.repository.ProfileRepository;
import com.suite.core.repository.filter.ProfileFilter;

/**
 * Service responsavel por manipular os dados recurso Profile
 * @author Wenceslau
 *
 */
@Service
public class ProfileService extends CoreRulesService {

	@Autowired
	private ProfileRepository profileRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public Profile findByCode(Long code) {
		Profile profile = findById(code);

		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
		applyRulePermissionInProfile(profile, getSector(), profile.getBusinessUnit());

		// REMOVIDO, AGORA O FILTRO ACONTECE USANDO SEMPRE O SETOR
		// if (isSa() && Utils.setorOnContextIsDefault())
		// return pr;

		// PROCESSO CENTRALIZADO NO coreRuleService
		// //Recupera as apps do setor logado, se o setor for default pega do user
		// String apps;
		// if (Utils.setorOnContextIsDefault())
		// apps = getUser().getBusinessUnit().getApplications();
		// else
		// apps = getSector().getBusinessUnit().getApplications();
		//
		// //Remove todas as permissoes que nao esta contida nas app da unidade de
		// negocio do user
		// pr.getPermissions().removeIf(p-> !apps.contains(p.getApplication()));

		return profile;

	}

	/**
	 * Retorna a lista de profile baseado na BU
	 * @param username
	 * @return
	 */
	public List<Profile> listByBusinessUnit(Long code) {
		BusinessUnit businessUnit = businessUnitService.findById(code);

		List<Profile> listProfile = findByBusinessUnitOrBusinessUnitIsNull(businessUnit);

		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
		applyRulePermissionInListProfile(listProfile);

		return listProfile;

	}

	/**
	 * Insere o recurso na base
	 * @param profile
	 * @return
	 */
	@Transactional("transactionManager")
	public Profile insert(Profile profile) {
		// Verifica duplicidade, permissoes e exclusiva, e predecessoras de permissao apos o save
		ruleOfProfile(profile);

		// Aplica as permissoes predecessoras
		applyPredecessorPemission(profile);

		checkDuplicateProfile(profile);

		info("ProfileToSave. Insert: " + profile);

		profile = save(profile);

		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU do perfil
		// Perfil com BU null sao de todas entao nao pode aplicar regras
		if (profile.getBusinessUnit() != null)
			applyRulePermissionInProfile(profile, getSector(), profile.getBusinessUnit());

		// Sleep sistemico
		sleep(150);

		Profile profileToBack = (new Profile());
		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(profile, profileToBack, new String[] { "permissions" });

		return profileToBack;

	}

	/**
	 * Atualiza objeto na base
	 * @param code
	 * @param profile
	 * @return
	 */
	@Transactional("transactionManager")
	public Profile update(Long code, Profile profile) {
		ruleOfProfile(profile);

		Profile profileToUpdate = findById(code);

		// Essa regra tem q ser aplicada no objeto antes da alteracao, por isso nao ta no ruleOfProfile
		// RULE Perfil 4- Nativo somente pode ser editados pelo SA no setor default porque ele pode criar perfil nativo selecionando
		// a BU (Todas)
		if (!isDefaultSector())
			if (profileToUpdate.getType() <= 4)
				throw new WarningException("Perfis nativos não podem ser editados.");

		// RULE Perfil 1-SADM, 2-UNADM, 3-ADM não podem ser editados mesmo pelo SA...
		// RULE Ao ao criar ums permissao, eh informado o tipo, de acordo com o tipo...
		// RULE ela eh atribuida ao perfil correspondente, de forma hierarquica...
		// RULE Se a permissao for criada e ela precisa pertencer a um perfil que nao...
		// RULE eh cutomizada, sera necessrio mudar o type dessa permissao.

		// Recupera as apps do setor logado, se o setor for default pega do user
		List<Application> apps;
		if (isDefaultSector())
			apps = getUser().getBusinessUnit().getApplications();
		else
			apps = getSector().getBusinessUnit().getApplications();

		// Percorre as permissoes do usuario e readiciona as permissoes que nao pertence a app do setor logado
		// Elas nao sao exibidas para o usuario que recupera o perfil. Se ela ja existe nao readiciona
		for (Permission permission : profileToUpdate.getPermissions()) {
			// TODO ESSE TRECHO PRECISA SER DETALHADO MAIS, ESTA CONFUSO
			// A primeira condicao apps nao contem a app da permissao
			// A segunda condicao, a permissao pode ter sido mesma removida na tela entao
			// Verifica se No perfil vindo da tela nao contem a permissao, assim ele nao eh readicionado
			if (!apps.contains(permission.getApplication()) && !profile.getPermissions().contains(permission))
				profile.getPermissions().add(permission);
			// else {
			// profile.getPermissions().stream().filter(x->x.getCode().equals(permission.getCode())).findAny().get().setPredecessorPermission(permission.getPredecessorPermission());
			// }

		}

		applyPredecessorPemission(profile);

		checkDuplicateProfile(profile);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(profile, profileToUpdate, new String[] { "code", "sector" });

		info("ProfileToSave. Update: " + profileToUpdate);

		save(profileToUpdate);

		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
		// Perfil com BU null sao de todas entao nao pode aplicar regras
		if (profile.getBusinessUnit() != null)
			applyRulePermissionInProfile(profile, getSector(), profileToUpdate.getBusinessUnit());

		// User us = getUser();
		// us.setName("AAAA");
		// us.getBusinessUnit().setName("AAAAA");

		return profileToUpdate;

	}

	/**
	 * Deleta objeto na base
	 * 
	 * @param code
	 * @param profile
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		profileRepository.deleteById(code);

	}

	/**
	 * Recupera a lista filtrada de recursos
	 * @return
	 */
	public List<Profile> listByFilter(ProfileFilter filter) {
		// Ordena pelo codigo
		//filter.setOrderBy("code");

		// Aplica filtro para a businessUnit do setor logado
		applyFilterBusinessUnit(filter);

		List<Profile> listProfile = filter(filter);

		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
		applyRulePermissionInListProfile(listProfile);

		// Aplica o filtro de permissoes na lista
		if (!StringUtils.isEmpty(filter.getCodPermissions())) {
			// Cria uma lista de codigos a partir da string
			List<String> listStrCode = Arrays.asList(filter.getCodPermissions().split(","));

			// Colecoes para armazenar os codigos, das permissoes do filtro e das permissoes do perfil
			List<Long> listLongCodeFilter = new ArrayList<>();
			List<Long> listLongCodePermi = new ArrayList<>();

			// Popula a lista de codigo do filtro
			for (String cod : listStrCode)
				listLongCodeFilter.add(Long.parseLong(cod));

			Set<Profile> setProfile = new HashSet<>();

			// Percorre os perfis
			for (Profile p : listProfile) {
				// Adiciona os codigos das permissoes do perfil na lista
				for (Permission pm : p.getPermissions())
					listLongCodePermi.add(pm.getCode());

				boolean add = false;

				// Percorre a lista de codigos dos filtro
				for (Long code : listLongCodeFilter) {
					add = false;
					// verifica se o codigo do filtr esta contido nos codigos da lista de permissao do perrfil
					// Se nao estiver interrompe o laço e nao exibe esse perfil
					if (listLongCodePermi.contains(code))
						add = true;
					else
						break;

				}

				if (add)
					setProfile.add(p);

				// Limpa os codigos das permissoes do perfil para o proximo ciclo
				listLongCodePermi.clear();

			}

			listProfile.clear();
			listProfile.addAll(setProfile);

		}

		return listProfile;

	}

	/**
	 * Recupera a lista perfil nativos
	 * @return
	 */
	public List<Profile> listNativeProfile() {
		ProfileFilter filter = new ProfileFilter();
		filter.setCodBusinessUnitProfile(-1L);
		filter.setType(4L);

		return filter(filter);

	}

	/**
	 * Remove dos perfis da lista as permissoes que nao pertence as apliacoes da BU
	 * @param profile
	 */
	public void applyRulePermissionInListProfile(List<Profile> listProfile) {
		Sector sectorLogged = getSector();
		BusinessUnit buProfile = null;

		for (Profile profile : listProfile) {
			if (profile.getBusinessUnit() != null)
				buProfile = businessUnitService.findByCode(profile.getBusinessUnit().getCode());
			applyRulePermissionInProfile(profile, sectorLogged, buProfile);

		}

	}

	/**
	 * Remove da lista de permissao do perfil as permissoes que nao pertence as apliacoes da BU
	 * @param profile
	 */
	public void applyRulePermissionInProfile(Profile profile, Sector sectorLogged, BusinessUnit buProfile) {
		rulePermissionApplication(profile.getPermissions(), buProfile, sectorLogged, null);

	}

	/**
	 * Aplica e insere as permissoes predecedores do perfil
	 * @param profile
	 */
	public void applyPredecessorPemission(Profile profile) {
		// RULE Nao eh permitido perfil sem permissao
		if (profile.getPermissions().isEmpty())
			throw new RuntimeException("Não é permitido perfil sem permissão");

		String[] codePredPerm;
		List<Permission> listPerPred = new ArrayList<>();

		// Percorre as permissoes do perfil
		for (Permission permission : profile.getPermissions()) {

			// Permissao full do banco
			Permission perAux = permissionService.findById(permission.getCode());

			// A permissao possue predecessora?
			if (perAux.getPredecessorPermission() != null && !perAux.getPredecessorPermission().isEmpty()) {
				// Array de codigos das predecessoras da permissao
				codePredPerm = perAux.getPredecessorPermission().split(",");

				// Percorre os codigos das predecessoras
				for (String string : codePredPerm) {
					final Long lCode = Long.parseLong(string);
					// Recupera a permissao pelo codigo predecessora nas permissoes ja existente no
					// perfil
					Optional<Permission> opt = profile.getPermissions().stream().filter(p -> p.getCode().equals(lCode)).findFirst();

					// Retora a permissao da lista para identificar se a permissao ja nao existe na
					// lista
					Optional<Permission> opt1 = listPerPred.stream().filter(p -> p.getCode().equals(lCode)).findFirst();

					// A permissao predecessora nao existe no perfil e tb ainda nao foi adicionada
					if (!opt.isPresent() && !opt1.isPresent()) {
						// Recupera da base e adiciona na lista
						Permission p = permissionService.findById(Long.parseLong(string));
						// Apensa permissoes predecessoras ativas
						if (p.getStatus())
							listPerPred.add(p);

					}

				}

			}

		}

		String messageSendUi = "";

		// Percorre a lista de predecessora e adiciona ela na lista do perfil
		// Monta msg para enviar a UI
		for (Permission permission : listPerPred) {
			profile.getPermissions().add(permission);
			messageSendUi += "'" + permission.getDescription() + "' ";

		}

		// Se existe msg a enviar, define ela no objeto
		if (!messageSendUi.isEmpty()) {
			messageSendUi = "Algumas permissões adicionadas possuem permissões predecessoras: " + messageSendUi
					+ ". Esta(s) permissões foram associada(s) automaticamente. ";
			profile.setMessage(messageSendUi);

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

	public Profile save(Profile profile) {
		// Profile pr = Profile.build(profile);

		profileRepository.saveAndFlush(profile);

		return findById(profile.getCode());

	}

	public Profile findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Profile", id), 1);

		Optional<Profile> optional = profileRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Profile", id), 1);

		Profile entity = optional.get();
		profileRepository.detachEntity(entity);

		return entity;
		// return Profile.build(entity);

	}

	public Profile findByIdOrNull(Long id) {
		if (id == null)
			return null;

		Optional<Profile> optional = profileRepository.findById(id);
		if (!optional.isPresent())
			return null;

		Profile entity = optional.get();
		profileRepository.detachEntity(entity);

		return entity; // Profile.build(optional.get());

	}

	public Profile findByType(Integer type) {
		Profile entity = profileRepository.findByType(type);
		profileRepository.detachEntity(entity);

		return entity; // Profile.build(profileRepository.findByType(type));

	}

	public List<Profile> findByBusinessUnitOrBusinessUnitIsNull(BusinessUnit businessUnit) {
		return (profileRepository.findByBusinessUnitOrBusinessUnitIsNull(businessUnit));

	}

	public List<Profile> findByBusinessUnit(BusinessUnit businessUnit) {
		return (profileRepository.findByBusinessUnit(businessUnit));

	}

	private List<Profile> filter(ProfileFilter filter) {
		return (profileRepository.filter(filter));

	}

	public int existsByNameAndBusinessUnitOrBusinessUnitIsNull(String name, BusinessUnit businessUnit) {
		return profileRepository.existsByNameAndBusinessUnitOrBusinessUnitIsNull(name, (businessUnit));

	}

	public int existsByNameAndCodeIsNotInAndBusinessUnitOrBusinessUnitIsNull(String name, Long code, BusinessUnit businessUnit) {
		return profileRepository.existsByNameAndCodeIsNotInAndBusinessUnitOrBusinessUnitIsNull(name, code, (businessUnit));

	}

	public boolean existsByNameAndCodeIsNotIn(String name, Long code) {
		return profileRepository.existsByNameIgnoreCaseAndCodeIsNotIn(name, code);

	}

	public boolean existsByName(String name) {
		return profileRepository.existsByNameIgnoreCase(name);

	}

	public String existsByBusinessUnitAndPermissions(BusinessUnit businessUnit, List<Long> permissions) {
		return profileRepository.existsByBusinessUnitAndPermissions(businessUnit, permissions);

	}

	public Profile findByName(String name) {
		return profileRepository.findByNameIgnoreCase(name);
	}

	public Profile saveBySystem(Profile profile) {
		Utils.userSystem = true;
		return save(profile);

	}

}
