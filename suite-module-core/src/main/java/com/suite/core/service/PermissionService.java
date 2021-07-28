
package com.suite.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.dto.BoolDTO;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;
import com.suite.core.base.EnumCore.Lang;
import com.suite.core.dto.MenuItemDTO;
import com.suite.core.model.Application;
import com.suite.core.model.Permission;
import com.suite.core.model.Profile;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.repository.PermissionRepository;
//import com.suite.core.repository.ProfilePermissionsRepository;
import com.suite.core.repository.filter.PermissionFilter;

/**
 * Service responsável por manipular os dados recurso Permission
 * @author Wenceslau
 *
 */
@Service
public class PermissionService extends CoreRulesService {

	@Autowired
	private PermissionRepository permissionRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */

	public Permission findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Insere o recurso no repositorio
	 * @param permission
	 * @return
	 */
	public Permission insert(Permission permission) {
		info("Insert: " + permission);

		ruleOfPermssion(permission);

		// Define a key da permissao
		defineKey(permission);

		permission = save(permission);

		// Associa a permissao ao perfil de acordo com regras definidas

		associatePermisionProfile();

		return permission;

	}

	/**
	 * Atualiza o recurso no repositorio
	 * @param code
	 * @param permission
	 * @return
	 */
	public Permission update(Long code, Permission permission) {
		info("Update: " + permission);

		ruleOfPermssion(permission);

		// Define a key da permissao
		defineKey(permission);

		Permission permissionToSave = findById(code);

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(permission, permissionToSave, new String[] { "code" });

		permissionToSave = save(permissionToSave);

		// Associa a permissao ao perfil de acordo com regras definidas
		associatePermisionProfile();

		return permissionToSave;

	}

	/**
	 * Deleta recurso na base
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);
		permissionRepository.deleteById(code);

	}

	/**
	 * Retorna lista de permissoes ativas baseado na BU que o usuario esta logado
	 * e baseado nas aplicacao da BU do usuario
	 * @return
	 */
	public List<Permission> listAllEnabledToAssociate() {
		List<Permission> listPermissions = findAllByStatusOrderByModule(true);

		// Remove permissoes da lista baseado no tipo de perfil do usuario logado
		removePermissionByTypeProfile(listPermissions, true);

		Sector sectorLogged = getSector();
		// RULE Remove as permissoes de aplicacao que nao estao nas aplicacoes da BU do setor logado
		rulePermissionApplication(listPermissions, sectorLogged.getBusinessUnit(), sectorLogged, "ATIVA PARA ASSOCIAR");

		listPermissions.sort((p1, p2) -> {

			if (p1.getCode().compareTo(p2.getCode()) == 0) {
				return p1.getCode().compareTo(p2.getCode());

			} else {
				return p1.getCode().compareTo(p2.getCode());

			}

		});

		return listPermissions;

	}

	/**
	 * Retorna lista de permissoes ativas baseado na BU que o usuario esta logado
	 * e baseado nas aplicacao da BU do usuario
	 * @return
	 */
	public List<Permission> listAllEnabled() {
		List<Permission> listPermissions = findAllByStatusOrderByModule(true);

		// Remove permissoes da lista baseado no tipo de perfil do usuario logado
		removePermissionByTypeProfile(listPermissions, false);

		Sector sectorLogged = getSector();
		// RULE Remove as permissoes de aplicacao que nao estao nas aplicacoes da BU do setor logado
		rulePermissionApplication(listPermissions, sectorLogged.getBusinessUnit(), sectorLogged, null);

		listPermissions.sort((p1, p2) -> {

			if (p1.getCode().compareTo(p2.getCode()) == 0) {
				return p1.getCode().compareTo(p2.getCode());

			} else {
				return p1.getCode().compareTo(p2.getCode());

			}

		});

		return listPermissions;

	}

	/**
	 * Recupera a lista filtrada de recurso
	 * @return
	 */
	public List<Permission> listByFilter(PermissionFilter filter) {
		List<Permission> listPermissions = filter(filter);

		listPermissions.sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));

		return listPermissions;

	}

	/**
	 * Retorna uma lista de permissoes para montar o menu horizontal da UI
	 * Traz as permissoes baseados no perfil do usuario
	 * @return
	 */
	public List<MenuItemDTO> menuItemsHorizontal(Lang lang) {
		User userLogged = getUser();

		// Recupera a lista de permissao do usuario logado com o tipo componente MENU
		List<Permission> listPermission = userLogged.getProfile().getPermissions().stream()
				.filter(p -> p.getComponent() != null && p.getComponent().equals("MENU")).collect(Collectors.toList());

		Sector sectorLogged = getSector();

		// Regra de permissao basado na aplicacao do BU do setor logado
		rulePermissionApplication(listPermission, sectorLogged.getBusinessUnit(), sectorLogged, null);

		// Ordena a lista pelo sequence
		listPermission.sort((p1, p2) -> p1.getSequence().compareTo(p2.getSequence()));

		// Lista de string com as key de cada menu
		List<String> keys = new ArrayList<>();
		for (Permission permission : listPermission)
			keys.add(permission.getKey());

		// Recupera os menus roots do sistema
		// Existe um distinct entre root e root_tollbar
		// Se existir mais de um menu com o mesmo root e
		// eles tiverem root_toolbar diferente, ira trazer 2 registros
		List<MenuItemDTO> roots = permissionRepository.listRoot();

		List<MenuItemDTO> rootForToolbar = new ArrayList<>();

		for (MenuItemDTO menuRoot : roots) {

			// Cada menu root tem um conjunto de menu
			if (!keys.isEmpty()) {
				// Recupera o conjunto de menu do root que tenhas as keys dos menus do tipo
				// componente MENU
				List<MenuItemDTO> items = permissionRepository.listMenuItemByRoot(menuRoot.getLabel(), keys);
				// Adiciona esse conjnto ao menu root correspodente
				menuRoot.setItems(items);

				// TODO: Avaliar
				// Talves seja desnecessario. O modelo de menu ja nao funciona dessa forma
				// a primeira versao usava. Antes criava um icone com o menu para ser exibido no toolbar
				// Cria o menu rooot com a rota do primeiro menu do grupo
				// Sera usado no toolbar para carregar o primeiro item do grupo
				if (menuRoot.getRootToolbar() != null && menuRoot.getRootToolbar())
					if (!items.isEmpty())
						rootForToolbar.add(new MenuItemDTO(menuRoot.getLabel(), items.get(0).getRouterLink()));

			}

		}

		// Remove o menu root se ele estiver vazio
		java.util.function.Predicate<MenuItemDTO> filterPredicate;
		filterPredicate = p -> (p.getItems() == null || p.getItems().isEmpty());
		roots.removeIf(filterPredicate);

		// Cria um menu main com o icone de barrinhas e adicina a lista de roots a ele
		MenuItemDTO menuMain = new MenuItemDTO();
		menuMain.setIcon("fa fa-align-left");
		menuMain.setItems(roots);

		// Cria a lista de Objetos Menus a retornar e adiciona o menu main a ele
		// Verifica-se que o menu main ja contem os menus roots e cada root tem seu
		// conjunto de menu
		List<MenuItemDTO> itens = new ArrayList<>();
		itens.add(menuMain);

		// // TODO: Nao existe mais tollbar, hoje usa o menu vertical
		// // Avaliar se pode ser removido
		// // Adiciona os menus root aos itens,
		// // Eles serao exibidos no toolbar
		// itens.addAll(rootForToolbar);
		//
		// // TODO: Nao existe mais tollbar, hoje usa o menu vertical
		// // Avaliar se pode ser removido
		// // Cria uma lista de mmenus que se comportara na tela como toolbar
		// List<MenuItemDTO> toolbar = Collections.emptyList();
		//
		// // TODO: Nao existe mais tollbar, hoje usa o menu vertical
		// // Avaliar se pode ser removido
		// // Procura na base os menus definidos como toolbar no cadastro que tenha as keys
		// // do tipo MENU
		// if (!keys.isEmpty())
		// toolbar = permissionRepository.listMenuItemToolbar(keys);

		// Adiciona a lista para o toolbar a lista que vai retornar
		// itens.addAll(toolbar);

		applyLanguage(lang, itens);

		return itens;

	}

	/**
	 * Retorna uma lista de permissoes para montar o menu vertical da UI
	 * Traz as permissoes baseados no perfil do usuario
	 * @param router Apenas permissoes do mesmo grupo do router da permsissao
	 * @return
	 */
	public List<MenuItemDTO> menuItemsVertical(String router, Lang lang) {
		User userLogged = getUser();

		// Lista de permissao do user do tipo menu
		List<Permission> listPermission = userLogged.getProfile().getPermissions().stream()
				.filter(p -> p.getComponent() != null && p.getComponent().equals("MENU")).collect(Collectors.toList());

		Sector sectorLogged = getSector();

		// Regra de permissao basado na aplicacao do BU do setor logado
		rulePermissionApplication(listPermission, sectorLogged.getBusinessUnit(), sectorLogged, "MENU do usuario");

		// Lista de permissao
		List<Permission> listReturn = new ArrayList<>();

		// Se a pagina em navgacao for a home todos os menus devem ser exibidos no
		// acesso rapido
		if (router.equals("home") || router.equals("dashboard")) {
			// Retrieve menus roots
			List<MenuItemDTO> roots = permissionRepository.listRoot();
			List<Permission> list = new ArrayList<>();

			// Percorre os roots
			for (MenuItemDTO menuItem : roots) {
				// Recupera o conjunto de menu do root lista de permissao
				list = listPermission.stream().filter(p -> p.getRoot().equals(menuItem.getLabel())).collect(Collectors.toList());

				// remove os inativos
				list.removeIf(p -> p.getStatus() == false);

				// Ordena pelo sequence
				list.sort((p2, p1) -> p2.getSequence().compareTo(p1.getSequence()));

				// Adiciona um barra vertical para separar os grupos de menus de cada root
				Permission p = (new Permission());
				p.setIcon("-");
				p.setRouter("/no");
				if (!list.isEmpty())
					list.add(p);

				// adiciona o conjunto do root a lista a retornar
				listReturn.addAll(list);

			}

		} else {
			// Recupera a permissao baseado na pagina em nevegacao
			Permission perm = listPermission.stream().filter(p -> (p.getRouter() != null && p.getRouter().equals("/" + router)))
					.findAny()
					.orElse(null);

			// se nao tiver retorba lista vazia ao chanador
			if (perm == null)
				return Collections.emptyList();

			// Recupera o conjunto do root da pagina em nevegacao
			listPermission = listPermission.stream().filter(p -> p.getRoot().equals(perm.getRoot())).collect(Collectors.toList());
			// remove os inativos
			listPermission.removeIf(p -> p.getStatus() == false);

			listPermission.sort((p2, p1) -> p2.getSequence().compareTo(p1.getSequence()));

			// adiciona o conjunto do root a lista a retornar
			listReturn.addAll(listPermission);

		}

		// Cria a lista de objetes TO a partir da lista de permissao montada
		List<MenuItemDTO> itens = new ArrayList<>();
		MenuItemDTO menu;

		for (Permission permission : listReturn) {
			// Apenas permissoes do tipo MENU que tenhha icone pode estar no menu de acesso
			// rapido da UI
			if (StringUtils.isEmpty(permission.getIcon()))
				continue;

			// Cria o objeto e add a lista
			menu = new MenuItemDTO();
			menu.setIcon(permission.getIcon());
			menu.setTitle(permission.getLabel());
			menu.setRouterLink(permission.getRouter());
			menu.setLabel(permission.getRoot());
			itens.add(menu);

		}

		applyLanguage(lang, itens);

		return itens;

	}

	/**
	 * Define a key da permissao
	 * @param permission
	 */
	private void defineKey(Permission permission) {
		// Define a chave da permissao, padrao APP_MODULE_ROLE
		permission.setKey(permission.getApplication().getName() + "_" + permission.getModule() + "_" + permission.getRole());

	}

	/**
	 * Aplica as regras e adiciona a permissao aos perfis do sistema
	 * @param permission
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private void associatePermisionProfile(Permission permission) {
		// Todas as novas permissoes criadas sao associdas ao perfil tipo 1 SA
		Profile profile = profileService.findByType(1);

		if (profile == null)
			throw new RuntimeException(
					"O perfil SA não foi encontrado, não é possivel criar a permissão! O perfil SA é obrigatório no sistema");

		// Adiciona a permissao ao perfil SA
		savePermissionOnProfile(permission, profile);

		// 0 Exclusiva SA, 1 Compartilh SA, 2 Copmatilh UA, 3 Compartilh ADM, 4 Nativos

		// CRIAR ESSAS NOVAS TIPOS:
		// 0 Exclusiva SA, 1 Compartilhada SA, 2 Exclusiva UA, 3 Compartilhada UA, 4 Exclusiva ADM, 5 Compartilh ADM, 6 Nativos

		// Nao precisa verificar se a permissao eh de uma aplicacao que pertenca a BU do perfil
		// As permissoes sao assiciadas independende ao perfil independente de APP x BU
		// Todo processo de validacao se a permissao pertence APP x BU eh feito nos filtros

		// Se a permissao for do tipo 2 UA ou superior
		if (permission.getType() >= 2) {
			// Recupera perfil 2 - UA
			profile = profileService.findByType(2);
			if (profile == null)
				throw new RuntimeException(
						"O perfil UA não foi encontrado, não é possivel criar a permissão! O perfil UA é obrigatório no sistema");

			savePermissionOnProfile(permission, profile);

		}

		// se a permissao for do tipo 2 UA
		if (permission.getType() >= 3) {
			// Recupera perfil 3 - ADM
			profile = profileService.findByType(3);
			if (profile == null)
				throw new RuntimeException(
						"O perfil ADM não foi encontrado, não é possivel criar a permissão! O perfil ADM é obrigatório no sistema");

			savePermissionOnProfile(permission, profile);

		}

		// Perfis do tipo 1 2 e 3 tem apenas 1, outros perfis 4 e 5 podem ter mais de um
		if (permission.getType() != null && permission.getType().intValue() == 4) {
			// Recupera os perfis nativos que sao de todas as Unidade
			List<Profile> list = profileService.listNativeProfile();

			if (permission.getRole().equals("VIEWER")
					|| permission.getRole().equals("INSERT")
					|| permission.getRole().equals("UPDATE")
					|| permission.getRole().equals("INSERT_UPDATE")
					|| permission.getRole().equals("LIST")
					|| permission.getRole().equals("DELETE")
					|| permission.getRole().equals("IMPORT")
					|| permission.getRole().equals("EXPORT")
					|| permission.getRole().equals("DELETE")) {
				// Perfil codigo 4 eh WRITER
				profile = list.stream().filter(prf -> prf.getName().equals("WRITER")).findFirst().orElseGet(null);
				if (profile != null)
					savePermissionOnProfile(permission, profile);

			}

			if (permission.getRole().equals("VIEWER") || permission.getRole().equals("LIST")) {
				// Perfil codigo 5 eh VIEWER
				profile = list.stream().filter(prf -> prf.getName().equals("VIEWER")).findFirst().orElseGet(null);
				if (profile != null)
					savePermissionOnProfile(permission, profile);

			}

			if (permission.getRole().equals("LOG")) {
				// Perfil codigo 6 eh INSPECTOR
				profile = list.stream().filter(prf -> prf.getName().equals("INSPECTOR")).findFirst().orElseGet(null);
				if (profile != null)
					savePermissionOnProfile(permission, profile);

			}

		}

	}

	/**
	 * Aplica as regras e adiciona a permissao aos perfis do sistema
	 * @param permission
	 */
	public void associatePermisionProfile() {
		List<Permission> listPermission = findAll();

		// CRIAR ESSAS NOVAS TIPOS:
		// 0 Exclusiva SA, 1 Compartilhada SA, 2 Exclusiva UA, 3 Compartilhada UA, 4 Exclusiva ADM, 5 Compartilh ADM, 6 Nativos

		// Recupera os perfis do administrativos SA, UA, ADM e limpa as permissoes. Elas serao readicionadas
		Profile profileSA = profileService.findByType(1);
		profileSA.getPermissions().clear();

		Profile profileUA = profileService.findByType(2);
		profileUA.getPermissions().clear();

		Profile profileADM = profileService.findByType(3);
		profileADM.getPermissions().clear();

		// Perfis nativos podem ser modificados, assim as permissoes nao podem ser limpas
		List<Profile> prfilesNative = profileService.listNativeProfile();

		for (Permission permission : listPermission) {
			System.out.println(permission.getKey());

			// Permissao do tipo 0 Exclusiva SA, adiciona ela ao perfil SA
			if (permission.getType() >= 0)
				addPermissionOnProfile(permission, profileSA);

			// Permissao do tipo 2 Exclusiva UA, adiciona ela ao perfil UA
			if (permission.getType() >= 2)
				addPermissionOnProfile(permission, profileUA);

			// Permissao do tipo 4 Exclusiva ADM adiciona ela ao perfil ADM
			if (permission.getType() >= 4) {
				addPermissionOnProfile(permission, profileADM);

			}

			// Permissao do tipo 6 Nativos adiciona ela aos perfis Nativos, de acordo com a ROLE da permissao
			if (permission.getType() >= 6) {
				String permissionsRoleWriter = "VIEWER,LIST,INSERT,UPDATE,INSERT_UPDATE,DELETE,IMPORT,EXPORT,DELETE,LOG,CHANGE";
				String permissionExcepions = "";

				if (permissionsRoleWriter.contains(permission.getRole())) {

					// Apenas as permissoes que nao sao execcao
					if (!permissionExcepions.contains(permission.getKey())) {
						Optional<Profile> profileWriter = prfilesNative.stream().filter(prf -> prf.getName().equals("WRITER")).findFirst();
						if (profileWriter.isPresent())
							addPermissionOnProfile(permission, profileWriter.get());

					}

				}

				String permissionsRoleViewer = "VIEWER,LIST,LOG,CHANGE,EXPORT";

				if (permissionsRoleViewer.contains(permission.getRole())) {
					Optional<Profile> profileViewer = prfilesNative.stream().filter(prf -> prf.getName().equals("VIEWER")).findFirst();
					if (profileViewer.isPresent())
						addPermissionOnProfile(permission, profileViewer.get());

				}

				// String permissionsRoleInspector = "LOG,CHANGE";
				//
				// if (permissionsRoleInspector.contains(permission.getRole())) {
				//
				// Profile profileViewer = prfilesNative.stream().filter(prf -> prf.getName().equals("INSPECTOR")).findFirst().orElseGet(null);
				// if (profileViewer != null)
				// addPermissionOnProfile(permission, profileViewer);
				//
				// }

				String permissionsRoleObjectHandler = "VIEWER,LIST,INSERT,UPDATE,INSERT_UPDATE,DELETE,IMPORT,EXPORT,LOG,CHANGE,TRUNCATE,CLONE";

				// if (permission.getRole().equals("CLONE")) {
				// }

				if (permissionsRoleObjectHandler.contains(permission.getRole())) {
					Optional<Profile> profileObjHandler = prfilesNative.stream().filter(prf -> prf.getName().equals("OBJECT HANDLER"))
							.findAny();
					if (profileObjHandler.isPresent())
						addPermissionOnProfile(permission, profileObjHandler.get());

				}

			}

		}

		profileService.save(profileSA);
		profileService.save(profileUA);
		profileService.save(profileADM);

		for (Profile profileNative : prfilesNative)
			profileService.save(profileNative);

	}

	/**
	 * Salva a permissao adicionada oo perfil no repositorio
	 * @param permission
	 * @param profile
	 */
	private void savePermissionOnProfile(Permission permission, Profile profile) {
		// Se a permissao exisitr no perfil nao precisa ser adicionada
		if (profile.getPermissions().stream().filter(p -> p.equals(permission)).findAny().isPresent())
			return;

		profile.getPermissions().add(permission);
		profile.setHash(new Random().nextInt(9999999));

		profileService.applyPredecessorPemission(profile);

		profileService.save(profile);

	}

	/**
	 * Salva a permissao adicionada oo perfil no repositorio
	 * @param permission
	 * @param profile
	 */
	private void addPermissionOnProfile(Permission permission, Profile profile) {
		// Se a permissao exisitr no perfil nao precisa ser adicionada
		if (profile.getPermissions().stream().filter(p -> p.equals(permission)).findAny().isPresent())
			return;

		profile.getPermissions().add(permission);
		profile.setHash(new Random().nextInt(9999999));
		profileService.applyPredecessorPemission(profile);

	}

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public BoolDTO icon(String router) {
		Permission permission = findByRouter(router);

		if (permission != null)
			return new BoolDTO(true, permission.getIcon());

		return new BoolDTO(true, "unknow");

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

	public Permission save(Permission entity) {
		Permission sec = permissionRepository.saveAndFlush(entity);
		return findById(sec.getCode());

	}

	public Permission saveBySystem(Permission entity) {
		Utils.userSystem = true;
		return save(entity);
	}

	public Permission findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Permission", id), 1);

		Optional<Permission> optional = permissionRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Permission", id), 1);

		Permission entity = optional.get();
		permissionRepository.detachEntity(entity);

		return entity; // Permission.build(optional.get());

	}

	public Permission findByRouter(String router) {
		Permission entity = permissionRepository.findByRouterIgnoreCase("/" + router);
		permissionRepository.detachEntity(entity);

		return entity; // Permission.build(permissionRepository.findByRouter("/" + router));

	}

	public Permission findByKey(String key) {
		return permissionRepository.findByKeyIgnoreCase(key);
	}

	public List<Permission> findAllByStatusOrderByModule(Boolean status) {
		return (permissionRepository.findAllByStatusOrderByModule(status));

	}

	public List<Permission> findAllByCodeIn(Collection<Long> codes) {
		return (permissionRepository.findAllByCodeIn(codes));

	}

	private List<Permission> filter(PermissionFilter filter) {
		return (permissionRepository.filter(filter));

	}

	public List<Permission> findAll() {
		return (permissionRepository.findAll());

	}

	public List<Permission> findAllByApplication(Application app) {
		// TODO Auto-generated method stub
		return permissionRepository.findAllByApplication(app);
	}

}
