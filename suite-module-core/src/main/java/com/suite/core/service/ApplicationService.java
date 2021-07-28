
package com.suite.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.util.Utils;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.User;
import com.suite.core.repository.ApplicationRepository;

/**
 * Service responsavel por manipular os dados recurso Application
 * @author Wenceslau
 *
 */
@Service
public class ApplicationService extends ServiceCore {

	@Autowired
	private ApplicationRepository applicationRepository;

	/**
	 * Encontra o recurso pelo codigo
	 * @param id
	 * @return
	 */
	public Application findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Recupera a lista de recursos ativos
	 * @return
	 */
	public List<Application> list() {
		return findAll();

	}

	/**
	 * Retorna a lista dos nomes das aplicacoes da BU logada
	 * @return
	 */
	public List<Application> listApplicationBusinessUnitLogged() {
		// Recupera o user logado
		User user = getUser();

		// Recupera as aplicacoes da BU do usuario lpgado
		List<Application> apps = user.getBusinessUnit().getApplications();

		// Retorna em formato de lista as aplicacoes
		return apps;

	}

	/**
	 * Retorna a lista dos nomes das aplicacoes da BU logada
	 * @return
	 */
	public List<Application> listByBusinessUnitOrSectorLogged(Long codeBu) {
		BusinessUnit bu;
		if (codeBu == 0)
			bu = getSector().getBusinessUnit();
		else
			bu = businessUnitService.findById(codeBu);

		// TODO: avaliar se eh o melhor jeito, aqui esta removendo a app core para que ela naos eja
		// usada na criacao de setor
		if (!isDefaultSector() && !bu.getName().equals("DEFAULT") && bu.getApplications().size() > 1)
			bu.getApplications().removeIf(x -> x.getName().equals("CORE"));

		return bu.getApplications();

	}

	/**
	 * Recupera a aplicacao Main da unidade de negocio
	 * @return
	 */
	public Application mainApplicatin(BusinessUnit businessUnit) {
		List<Application> listApplications = list();

		// List<String> strApplications = Arrays.asList(businessUnit.getStrApplications().split(","));
		List<Application> strApplications = businessUnit.getApplications();

		listApplications.removeIf(a -> !strApplications.stream().anyMatch(s -> a.getName().contains(s.getName())));

		Set<String> nameModules = Utils.contextMap.keySet();

		// Remove as permissoes das aplicacoes que nao estao ativas no pom.xml
		listApplications.removeIf(a -> !nameModules.stream().anyMatch(module -> a.getNameModuleSource().equals(module)));

		if (listApplications.isEmpty())
			return null;

		return listApplications.stream().filter(a -> a.getMain()).findFirst().orElse(listApplications.get(0));

	}

	/**
	 * Recupera a lista de nomes das aplicaoes
	 * @return
	 */
	public List<String> listNameApplications() {
		List<Application> listApplications = list();
		List<String> listStrApplication = new ArrayList<>();

		for (Application application : listApplications) {
			listStrApplication.add(application.getName());

		}

		return listStrApplication;

	}

	/**
	 * Insere o recurso no repositorio
	 * @param application
	 * @return
	 */
	public Application insert(Application application) {
		info("Insert: " + application);

		application.setLicence(RandomStringUtils.randomAlphabetic(15).toUpperCase());
		application.setMainColor("");

		return save(application);

	}

	/**
	 * Atualiza recurso no repositorio
	 * @param code
	 * @param application
	 * @return
	 */
	public Application update(Long code, Application application) {
		info("Update: " + application);

		Application applicationToSave = findById(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(application, applicationToSave, new String[] { "code", "dateInitializer" });

		if (applicationToSave.getLicence() == null || applicationToSave.getLicence().length() < 15)
			applicationToSave.setLicence(RandomStringUtils.randomAlphabetic(15).toUpperCase());

		return save(applicationToSave);

	}

	/**
	 * Deleta recurso no repositorio
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);

		applicationRepository.deleteById(code);

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

	public Application save(Application entity) {
		Application sec = applicationRepository.saveAndFlush(entity);

		return findById(sec.getCode());

	}

	public Application findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Application", id), 1);

		Optional<Application> optional = applicationRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Application", id), 1);

		return (optional.get());

	}

	public Application findByName(String name) {
		return (applicationRepository.findByNameIgnoreCase(name));

	}

	public List<Application> findAll() {
		return (applicationRepository.findAll());

	}

	public Application saveBySystem(Application app) {
		return save(app);
		
	}

}
