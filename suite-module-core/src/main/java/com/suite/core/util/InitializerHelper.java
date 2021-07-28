
package com.suite.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.suite.app.base.Base;
import com.suite.app.service.EncodeService;
import com.suite.app.util.Utils;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.BusinessUnitParameter;
import com.suite.core.model.DataList;
import com.suite.core.model.DataService;
import com.suite.core.model.ItemDataList;
import com.suite.core.model.Parameter;
import com.suite.core.model.Permission;
import com.suite.core.model.Profile;
import com.suite.core.model.Property;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.repository.BusinessUnitParameterRepository;
import com.suite.core.repository.InitializerRepository;
import com.suite.core.repository.ItemDataListRepository;
import com.suite.core.service.ApplicationService;
import com.suite.core.service.BusinessUnitService;
import com.suite.core.service.DataListService;
import com.suite.core.service.DataServiceService;
import com.suite.core.service.LanguageService;
import com.suite.core.service.LicenseService;
import com.suite.core.service.ParameterService;
import com.suite.core.service.PermissionService;
import com.suite.core.service.ProfileService;
import com.suite.core.service.SectorService;
import com.suite.core.service.UserService;
import com.suite.security.licence.AppLicence;
import com.suite.security.licence.Licence;

@Service
public class InitializerHelper extends Base {

	@Autowired
	protected Environment env;

	@Autowired
	protected Mailer mailer;

	@Autowired
	protected ServiceCore serviceCore;

	@Autowired
	protected LicenseService licenseService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected ParameterService parameterService;
	
	@Autowired
	protected DataServiceService dataServiceService;

	@Autowired
	protected InitializerRepository initializerRepository;

	@Autowired
	protected BusinessUnitService businessUnitRepository;

	@Autowired
	protected BusinessUnitParameterRepository businessUnitParameterRepository;

	@Autowired
	protected SectorService sectorService;

	@Autowired
	protected ProfileService profileService;

	@Autowired
	protected UserService userService;

	//@Autowired
	//protected PermissionRepository permissionRepository;

	@Autowired
	protected DataServiceService dataServicesService;

	@Autowired
	protected ApplicationService applicationService;

	@Autowired
	protected DataListService dataListService;

	@Autowired
	protected ItemDataListRepository itemDataListRepository;

	@Autowired
	protected LanguageService languageService;

	@Autowired
	protected EncodeService rsaService;

	/* -------- MTHODS FILL AND SAVE --------- */

	protected Application fillApplication(
			String name,
			String displayName,
			String nameModuleSource,
			String identifier,
			String image,
			boolean main) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		Application application = applicationService.findByName(name);

		if (application != null)
			return application;

		application = new Application();
		application.setName(name);
		application.setDisplayName(displayName);
		application.setNameModuleSource(nameModuleSource);
		application.setMainColor("");
		application.setImage(image);
		application.setLicence(identifier);
		application.setMain(main);
		application.setStatus(true);
		application.setDateInitializer(LocalDateTime.now());

		return applicationService.saveBySystem(application);

	}

	protected BusinessUnit fillBusinessUnit(
			String name,
			String description,
			String uniqueId,
			String image,
			Application app) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		BusinessUnit businessUnit = businessUnitRepository.findByName(name);

		if (businessUnit != null)
			return businessUnit;

		List<Application> apps = new ArrayList<>();
		apps.add(app);

		businessUnit = new BusinessUnit();
		businessUnit.setName(name);
		businessUnit.setDescription(description);
		businessUnit.setUniqueId(uniqueId);
		businessUnit.setApplications(apps);
		businessUnit.setLicense("");
		businessUnit.setStatus(true);
		businessUnit.setImage(image);

		return businessUnitRepository.saveBySystem(businessUnit);

	}

	protected Permission fillPermissionOld(
			Integer type,
			Application app,
			String module,
			String role,
			String description,
			String predecessorsPermission,
			String component,
			String router,
			String label,
			String root,
			Boolean toolbar,
			Boolean excluise,
			Integer sequence,
			Boolean status,
			String icon,
			Boolean rootToolbar,
			Integer seqRoot) {

		String key = app.getName() + "_" + module + "_" + role;
		Permission permission = findByKey(key);
		if (permission != null)
			return permission;

		permission = new Permission();
		permission.setType(type);
		permission.setApplication(app);
		permission.setStrApplication(app.getName());
		permission.setModule(module);
		permission.setRole(role);
		permission.setKey(key);
		permission.setDescription(description);
		permission.setPredecessorPermission(predecessorsPermission);
		permission.setComponent(component);
		permission.setRouter(router);
		permission.setLabel(label);
		permission.setSequenceRoot(seqRoot);
		permission.setRoot(root);
		permission.setToolbar(toolbar);
		permission.setSequence(sequence);
		permission.setStatus(status);
		permission.setIcon(icon);
		permission.setRootToolbar(rootToolbar);

		return permissionService.saveBySystem(permission);

	}

	protected Permission fillPermission(
			Integer type,
			Application app,
			String module,
			String role,
			String description,
			String component,
			Object predecessorsPermission,
			String router,
			String label,
			String root,
			String icon,
			Integer rootSequence, // Sequencia no grupo de menu
			Integer optionSequence, // Sequencia do item dentro do grupo
			Boolean rootToolbar) {

		String key = app.getName().replace(" ", "").toUpperCase() + "_" + module.toUpperCase() + "_" + role.toUpperCase();
		Permission permission = findByKey(key);
		if (permission != null)
			return permission;

		permission = new Permission();
		permission.setKey(key);
		permission.setType(type);
		permission.setApplication(app);
		permission.setStrApplication(app.getName());
		permission.setModule(module);
		permission.setRole(role);
		permission.setDescription(description);
		permission.setComponent(component);
		permission.setPredecessorPermission(predecessorsPermission == null ? null : predecessorsPermission + "");
		permission.setRouter(router);
		permission.setLabel(label);
		permission.setRoot(root);
		permission.setIcon(icon);
		permission.setSequenceRoot(rootSequence);
		permission.setSequence(optionSequence);
		permission.setToolbar(null);
		permission.setRootToolbar(rootToolbar);
		permission.setStatus(true);

		return permissionService.saveBySystem(permission);

		// NOVOS TIPOS D PERMISSAO
		// 0 Exclusiva SA, 1 Compartilhada SA, 2 Exclusiva UA, 3 Compartilhada UA, 4 Exclusiva ADM, 5 Compartilh ADM, 6 Nativos

		/*
		 * DEPRECIADO
		 * Tipo da permissao
		 * 0 - Exclusiva SA = permissoes atribuidas automaticamente ao perfil SA e que nao podem ser atribuidas a outros perfis
		 * 1 - Super Admin = permissoes atribuidas automaticamente ao perfil SA e que podem ser atribuidas a outros perfis
		 * 2 - UN Admin = permissoes atribuidas automaticamente aos perfis SA e UN e que podem ser atribuidas a outros perfis
		 * 3 - Business Admin = permissoes atribuidas automaticamente aos perfis SA e UN e BA e que podem ser atribuidas a outros perfis
		 * 4 - Usuário = permissoes atribuidas automaticamente aos pefis SA e UN e BA e perfis customizados e que podem ser atribuidas a
		 * outros perfis
		 * TODO: Avaliar se pode ser usado enum
		 */

	}

	protected Permission fillPermission(
			Integer type,
			Application app,
			String module,
			String role,
			String description,
			String component,
			Object predecessorsPermission) {

		return fillPermission(type, app, module, role, description, component, predecessorsPermission,
				null, null, null, null, null, null, null);

	}

	private Permission findByKey(String key) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		Permission permission = permissionService.findByKey(key);

		return permission;

	}

	protected Parameter fillParameter(
			String group,
			String key,
			String value,
			String description) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		Parameter parameter = parameterService.findByGroupAndKey(group, key);

		if (parameter != null)
			return parameter;

		parameter = new Parameter();
		parameter.setGroup(group);
		parameter.setKey(key);
		parameter.setValue(value);
		parameter.setDescription(description);
		parameter.setStatus(true);

		return parameterService.saveBySystem(parameter);

	}

	protected DataService fillDataService(
			String identifier,
			String scope,
			DataServiceType dataServiceType,
			String name,
			String description,
			BusinessUnit bu) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		DataService dataService = dataServicesService.findByName(name);

		if (dataService != null)
			return dataService;

		dataService = new DataService();
		dataService.setIdentifier(identifier);
		dataService.setScope(scope);
		dataService.setType(dataServiceType);
		dataService.setName(name);
		dataService.setDescription(description);
		dataService.setBusinessUnit(bu);
		dataService.setStatus(true);

		return dataService;

	}

	protected Property fillProperty(String dataType, String name, String value, String description) {

		Property property = new Property();
		property.setDataType(dataType);
		property.setName(name);
		property.setValue(value);
		property.setDescription(description);
		property.setStatus(true);
		return property;

	}

	protected Sector fillSector(
			String name,
			String description,
			String uniqueId,
			boolean requiredDb,
			BusinessUnit bu,
			Application app) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		Sector sector = sectorService.findByName(name);

		if (sector != null)
			return sector;

		sector = new Sector();
		sector.setName(name);
		sector.setDescription(description);
		sector.setUniqueId(uniqueId);
		sector.setStatus(true);
		sector.setImage("default.png");
		sector.setRequiredDb(requiredDb);
		sector.setBusinessUnit(bu);
		sector.setApplication(app);

		return sectorService.saveBySystem(sector);

	}

	protected Profile fillProfile(
			String name,
			int type,
			BusinessUnit bu) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		Profile profile = profileService.findByName(name);

		if (profile != null)
			return profile;

		profile = new Profile();
		profile.setName(name);
		profile.setType(type);
		profile.setStatus(true);
		profile.setBusinessUnit(bu);

		return profileService.saveBySystem(profile);

	}

	protected User fillUser(
			String name,
			String username,
			String email,
			String password,
			Profile profile,
			List<Sector> sectors,
			BusinessUnit bu) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		User user = userService.findByUsername(username);

		if (user != null)
			return user;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		user = new User();
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(encoder.encode(password));
		user.setProfile(profile);
		user.setSectors(sectors);
		user.setBusinessUnit(bu);
		user.setFirstAccess(false);
		user.setStatus(true);
		user.setReceiveNotify(true);
		user.setViewNotify(true);

		try {
			user.setUserHash(user.generateUserHash());

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | NullPointerException e) {
			warn("Falha ao gerar userhash. " + e.getMessage());
			user.setUserHash("?");

		}

		return userService.saveBySystem(user);

	}

	protected DataList fillDataList(
			String name,
			String description) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		DataList dataList = dataListService.findByName(name);

		if (dataList != null)
			return dataList;

		dataList = new DataList();
		dataList.setName(name);
		dataList.setDescription(description);
		dataList.setStatus(true);

		return dataListService.saveBySystem(dataList);

	}

	protected ItemDataList fillDataItemList(
			String label,
			String value,
			String description,
			String group,
			DataList dataList,
			boolean status) {

		try {
			Thread.sleep(200);

		} catch (InterruptedException e) {}

		ItemDataList itemDataList = itemDataListRepository.findByValueItemAndDataList_Code(value, dataList.getCode());

		if (itemDataList != null)
			return itemDataList;

		itemDataList = new ItemDataList();
		itemDataList.setLabelItem(label);
		itemDataList.setValueItem(value);
		if (description == null)
			description = label + " > " + value;
		itemDataList.setDescription(description);
		itemDataList.setGroup(group);
		itemDataList.setStatus(status);
		itemDataList.setDataList(dataList);

		Utils.userSystem = true;
		return itemDataListRepository.save(itemDataList);

	}

	protected BusinessUnitParameter fillBusinessUnitParameter(
			String nameApplication,
			String key,
			String value,
			String description,
			BusinessUnit bu) {

		try {
			Thread.sleep(500);

		} catch (InterruptedException e) {}

		info("Fill Parameters BusinessUnit: " + bu.getName());

		BusinessUnitParameter buPar = null;

		if (bu.getBusinessUnitParameters() != null && !bu.getBusinessUnitParameters().isEmpty()) {
			info("Parameters Size: " + bu.getBusinessUnitParameters().size());
			info("Parameters: " + bu.getBusinessUnitParameters());

			for (BusinessUnitParameter par : bu.getBusinessUnitParameters()) {
				if (par != null && par.getKey() != null && par.getKey().equals(key))
					return par;

			}

		}

		buPar = new BusinessUnitParameter();
		buPar.setBusinessUnit(bu);
		buPar.setApplication(nameApplication);
		buPar.setKey(key);
		buPar.setValue(value);
		buPar.setDescription(description);
		buPar.setStatus(true);

		Utils.userSystem = true;
		return businessUnitParameterRepository.save(buPar);

	}

	protected List<String> languages() {

		File f;

		try {
			f = ResourceUtils.getFile("classpath:templates/languages");
			return Files.readAllLines(Paths.get(f.getAbsolutePath()));

		} catch (Exception e) {
			error("Error to load language file!", e);
			return Collections.emptyList();

		}

	}

	/**
	 * 
	 * @param lst
	 * @param newType
	 * @param key
	 */
	protected void changePermissionType(List<Permission> lst, int newType, String key) {

		Permission perm = lst.stream().filter(x -> x.getKey().equals(key)).findFirst().get();
		perm.setType(newType);
		permissionService.saveBySystem(perm);

	}

	/**
	 * 
	 * @param bu
	 * @param buPar
	 */
	protected void checkBuParInBu(BusinessUnit bu, BusinessUnitParameter buPar) {

		info("Add Parameters BusinessUnit: " + buPar.getKey());

		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar.getCode())).findFirst().isPresent()) {
			bu.getBusinessUnitParameters().add(buPar);
			businessUnitRepository.saveBySystem(bu);

		}

	}

	/**
	 * Aplica as regras e adiciona a permissao aos perfis do sistema
	 * @param permission
	 */
	@SuppressWarnings("unused")
	private void associatePermisionProfile() {

		permissionService.associatePermisionProfile();

	}

	@SuppressWarnings("unused")
	private void associateProfilePermission(Profile profile) {

		System.out.println(profile.getPermissions());

		try {
			Thread.sleep(500);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		List<Permission> permissons = permissionService.findAll();

		// Permissoes sem duplicidade;
		Set<Permission> setPermissionsProfile = new HashSet<>();

		if (profile.getPermissions() != null)
			setPermissionsProfile.addAll(profile.getPermissions());

		Permission perLog = permissons.stream().filter(p -> p.getModule().equals("LOG")).findFirst().get();
		Permission pChgPwd = permissons.stream().filter(p -> p.getKey().equals("CORE_PASSWORD_CHANGE")).findFirst().get();
		Permission pDshLgn = permissons.stream().filter(p -> p.getKey().equals("CORE_DASHBOARD_LOGON_VIEWER")).findFirst().get();

		Permission pUsrLst = permissons.stream().filter(p -> p.getKey().equals("CORE_USER_LIST")).findFirst().get();
		Permission pUsrVwr = permissons.stream().filter(p -> p.getKey().equals("CORE_USER_VIEWER")).findFirst().get();
		Permission pUsrIns = permissons.stream().filter(p -> p.getKey().equals("CORE_USER_INSERT")).findFirst().get();
		Permission pUsrUpd = permissons.stream().filter(p -> p.getKey().equals("CORE_USER_UPDATE")).findFirst().get();

		// Permissoes compartilhada do SA que precisa ser dadas ao Perfil UN Admin
		List<Permission> permissonSharedSA = permissons.stream().filter(p -> p.getType() == 1).collect(Collectors.toList());

		if (profile.getName().equals("SUPER ADMIN")) {
			// Para o SA adiciona todas as permissoes
			saveProfileAssociatedPermission(profile, permissons);
			return;

		}

		if (profile.getName().equals("UN ADMIN")) {
			// remove as permissoes menor que 2 (exclusiva SA)
			permissons.removeIf(p -> p.getType() < 2);

			// Adiciona as permissoes do perfil as possivel permissoes novas
			setPermissionsProfile.addAll(permissons);

			// Adiciona as permissoes do perfil as permissoes share do SA
			setPermissionsProfile.addAll(permissonSharedSA);

			saveProfileAssociatedPermission(profile, new ArrayList<Permission>(setPermissionsProfile));
			return;

		}

		if (profile.getName().equals("ADMIN")) {
			// As permissoes tipo < 3 serao removidas, assim o user list viwer update e insert nao sera adicionada ao perfil
			// adm, porem o perfil adm precisa ter essas permissao, e perfils do tipo 1, 2 ou 3 nao podem ser editados
			// nem pelo sa, nesse caso as permissoes abaixos sao addicionadas manuanmente

			// remove as permissoes menor que 3 (Exclusiva SA, Compartilhada SA e Exclusiva UA)
			permissons.removeIf(p -> p.getType() < 3);
			permissons.add(pUsrLst);
			permissons.add(pUsrVwr);
			permissons.add(pUsrIns);
			permissons.add(pUsrUpd);

			// Adiciona as permissoes do perfil as possivel permissoes novas
			setPermissionsProfile.addAll(permissons);

			saveProfileAssociatedPermission(profile, new ArrayList<Permission>(setPermissionsProfile));
			return;

		}

		if (profile.getName().equals("VIEWER")) {
			// Este perfil precisa das todas permissons list
			List<Permission> permissionsList = permissons.stream().filter(p -> p.getRole().equals("LIST")).collect(Collectors.toList());

			// remove as permissoes menor que 4 (Exclusiva SA, Compartilhada SA, Exclusiva UA, Compartilhada ADM)
			permissons.removeIf(p -> p.getType() < 4);

			// remove as permissoes diferentes de veiwer
			permissons.removeIf(p -> !p.getRole().equals("VIEWER"));

			permissons.add(pChgPwd); // Permissao de alterar a propria senha
			permissons.add(perLog); // add permission log
			permissons.addAll(permissionsList); // Add todas as permissoes list

			// Adiciona as permissoes do perfil as possivel permissoes novas
			setPermissionsProfile.addAll(permissons);

			saveProfileAssociatedPermission(profile, new ArrayList<Permission>(setPermissionsProfile));
			return;

		}

		if (profile.getName().equals("WRITER")) {
			// remove os permissoes menor que 4 (Exclusiva SA, Compartilhada SA, Exclusiva UA, Compartilhada ADM)
			permissons.removeIf(p -> p.getType() < 4);
			permissons.add(perLog); // add permission log

			// Adiciona as permissoes do perfil as possivel permissoes novas
			setPermissionsProfile.addAll(permissons);

			saveProfileAssociatedPermission(profile, new ArrayList<Permission>(setPermissionsProfile));
			return;

		}

		if (profile.getName().equals("INSPECTOR")) {
			// remove os permissoes menor que 4 (Exclusiva SA, Compartilhada SA, Exclusiva UA, Compartilhada ADM)
			permissons.removeIf(p -> p.getType() < 4);

			// remove as permissoes diferentes de LOG
			permissons.removeIf(p -> !p.getModule().equals("LOG"));

			permissons.add(pDshLgn); // ver dash de logon
			permissons.add(pChgPwd); // Permissao de alterar a propria senha
			permissons.add(perLog); // add permission log

			// Adiciona as permissoes do perfil as possivel permissoes novas
			setPermissionsProfile.addAll(permissons);

			saveProfileAssociatedPermission(profile, new ArrayList<Permission>(setPermissionsProfile));
			return;

		}

	}

	private void saveProfileAssociatedPermission(Profile profile, List<Permission> listPermission) {

		profile.setPermissions(listPermission);
		profile.setHash(new Random().nextInt(9999999));
		profileService.saveBySystem(profile);

	}

	/* -------- -----END ----------- --------- */

	protected String valueParameterBusinessUnitOrNull(String key, BusinessUnit bu) {

		BusinessUnitParameter buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst()
				.orElse(null);

		if (buPar == null) {
			bu = businessUnitRepository.findByName("DEFAULT");
			buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);

		}

		if (buPar == null)
			return null;

		return buPar.getValue();

	}

	protected BusinessUnit addApplicationToBusinessUnit(Application app) {

		BusinessUnit businessUnit = businessUnitRepository.findByName("DEFAULT");

		if (!businessUnit.getApplications().contains(app)) {
			businessUnit.getApplications().add(app);
			businessUnitRepository.saveBySystem(businessUnit);

		}

		return businessUnit;

	}

	protected String templateEmailStart() {

		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		String modules = Utils.contextMap.keySet().toString();
		String host = "Unknow";
		String ip = "Unknow";
		String pid = new ApplicationPid().toString();
		String os = Utils.getOsName();
		String disks = "";
		String arch = System.getProperty("os.arch");

		String memory = getAvailableMem();

		/* Get a list of all filesystem roots on this system */
		File[] roots = File.listRoots();

		/* For each filesystem root, print some info */
		for (File root : roots) {
			disks += root.getAbsolutePath() + "= Space: " + (root.getTotalSpace() / 1024 / 1024 / 1024) + "GB Free: "
					+ (root.getFreeSpace() / 1024 / 1024 / 1024) + "GB | ";

		}

		try {
			host = InetAddress.getLocalHost().getHostName();
			ip = InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e) {
			e.printStackTrace();

		}

		List<BusinessUnit> list = businessUnitRepository.findAll();
		String trs = "";
		String table = "<table style=\"width: 100%;\" border=\"1\">\r\n" +
				"<tbody>\r\n" +
				"<tr style=\"height: 20.8px; background: #444; color: white; font-weight: bold\">\r\n" +
				"<td style=\"height: 20.8px; width: 25%;\">&nbsp;Business Unit</td>\r\n" +
				"<td style=\"height: 20.8px; width: 25%;\">Applications</td>\r\n" +
				"<td style=\"height: 20.8px; width: 25%;\">Sector(s)</td>\r\n" +
				"<td style=\"height: 20.8px; width: 25%;\">Licence</td>\r\n" +
				"</tr>" +
				"{{trs}}" +
				"</tbody>\r\n" +
				"</table>";

		for (BusinessUnit bu : list) {
			String apps = "";

			for (Application app : bu.getApplications())
				apps += app.getName() + " ";

			if (!apps.isEmpty())
				apps = apps.substring(0, apps.length() - 1);

			Long count = sectorService.countByBusinessUnit_Code(bu.getCode());

			String strLicense = "";

			try {

				if (bu.getLicense() != null && !bu.getLicense().isEmpty()) {
					Licence licence = licenseService.getLicense(bu.getLicense());

					int size = licence.getAppLicences().size();

					for (int i = 0; i < size; i++) {
						AppLicence appLic = licence.getAppLicences().get(i);
						strLicense += appLic.getApplicationName() + " | Size: " + appLic.getLicenceNumber() + " | "
								+ appLic.getDetailLicenseNumber() + " | " + appLic.getExpiration();
						if (i < size)
							strLicense += "<br>";

					}

				}

			} catch (Exception e) {
				strLicense = "Invalid License";

			}

			String tr = "</tr>\r\n" +
					"<tr style=\"height: 20px;\">\r\n" +
					"<td style=\"height: 20px; width: 25%;\">" + bu.getName() + "</td>\r\n" +
					"<td style=\"height: 20px; width: 25%;\">" + apps + "</td>\r\n" +
					"<td style=\"height: 20px; width: 25%;\">" + count + "</td>\r\n" +
					"<td style=\"height: 20px; width: 25%;\">" + strLicense + "</td>\r\n" +
					"</tr>";
			trs += tr;

		}

		table = table.replace("{{trs}}", trs);

		String body = "A Suite foi iniciada no servidor";

		return "<div>\r\n" + UtilsCore.headerTemplateEmail("CORE") + "  <br>\r\n" + "  <hr>\r\n" + "  <br>\r\n"
				+ "  <div style=\"font-size: 13px; text-align: center;\">\r\n"
				+ "    <span style=\"font-weight: bold;\">" + body + "</span>\r\n" + "  </div>\r\n" + "  <br>\r\n"
				+ "  <div style=\"font-size: 13px; text-align: center;\">\r\n"
				+ "    <span style=\"font-weight: bold;\">Data Hora:</span> " + dateTime + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Modulos Ativos:</span> " + modules + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Sistema Operacional:</span> " + os + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Arquitetura:</span> " + arch + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Endereço IP:</span> " + ip + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Hostname:</span> " + host + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">ID Processo:</span> " + pid + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Memory:</span> " + memory + " <br>\r\n"
				+ "    <span style=\"font-weight: bold;\">Discos:</span> " + disks + " <br>\r\n" + "  </div>\r\n"
				+ "    <br>\r\n"
				+ "   " + table + "\r\n"
				+ "  <br>\r\n" + "  <hr>\r\n" + "  <br>\r\n" + UtilsCore.footerTemplateEmail("CORE") + "</div>";

	}

	@SuppressWarnings("restriction")
	public String getAvailableMem() {

		try {

			if (Utils.isUnix()) {

				try {
					@SuppressWarnings("resource")
					BufferedReader memInfo = new BufferedReader(new FileReader("/proc/meminfo"));
					String line;

					String info = "";

					// info("Mem Info......");

					while ((line = memInfo.readLine()) != null) {
						info("...:" + line);

						if (line.startsWith("MemTotal: ")) {
							// Output is in KB which is close enough.
							info += "Total: " + java.lang.Long.parseLong(line.split("[^0-9]+")[1]) / 1024 + "MB ";

						}

						if (line.startsWith("MemAvailable: ")) {
							// Output is in KB which is close enough.
							info += "Available: " + java.lang.Long.parseLong(line.split("[^0-9]+")[1]) / 1024 + "MB";

						}

					}

					return info;

				} catch (Exception e) {}

				// We can also add checks for freebsd and sunos which have different ways of getting available memory
			} else {
				OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
				// @SuppressWarnings("restriction")
				com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
				// @SuppressWarnings("restriction")
				Long total = (sunOsBean.getTotalPhysicalMemorySize() / 1024 / 1024);
				// @SuppressWarnings("restriction")
				Long free = (sunOsBean.getFreePhysicalMemorySize() / 1024 / 1024);

				return "Total: " + total + "MB Available: " + free + "MB";

			}

		} catch (Exception e) {
			warn("Erro get memory " + e);

		}

		return "unknow";

	}

	protected boolean findInitializer(String name) {

		// Localiza a atualizacao
		com.suite.core.model.Initializer init = initializerRepository.findByNameIgnoreCase(name);

		return init != null;

	}

	protected void createInitializer(String name, String description) {

		com.suite.core.model.Initializer init;
		// Cria o a atualizacao
		init = new com.suite.core.model.Initializer();
		init.setDateInitializer(LocalDateTime.now());
		init.setName(name);
		init.setDescription(description);
		Utils.userSystem = true;
		initializerRepository.save(init);

	}

}
