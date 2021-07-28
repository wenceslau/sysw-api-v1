package com.suite.core.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suite.app.util.Utils;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.BusinessUnitParameter;
import com.suite.core.model.DataList;
import com.suite.core.model.DataService;
import com.suite.core.model.Language;
import com.suite.core.model.Parameter;
import com.suite.core.model.Profile;
import com.suite.core.model.Property;
import com.suite.core.model.Sector;

/**
 * Classe de inicializacao de dados na instalacao do modulo Executada apenas uma
 * vez no primeiro start da modulo
 * 
 * @author Wenceslau
 *
 */
@Service
@Transactional("transactionManager")
public class Initializer extends InitializerHelper {

	/**
	 * Metodo que executa a inicializacao Aqui eh verificado se eh a primeira
	 * inicializacao
	 */
	public void start() {
		// Verifica se o modulo esta habilitado
		boolean enabled = Boolean.parseBoolean(env.getProperty("suite.core.initilizer.enabled", "false"));
		info("CORE Initializer enable: " + enabled);

		if (!enabled)
			return;

		info("Initializer.start() - INICIO");
		info("Modulos ativos no sistema: [" + Utils.contextMap + "]");

		if (Utils.contextMap.isEmpty())
			throw new IllegalArgumentException("Nao foi econtrado nenhum modulo ativo. Verifique se o pom.xml contem os modulos da suite");

		if (enabled) {

			install();

			update_21102020();

			update_06012021();

			update_29032021();

			update_12042021();

			update_16042021();

			updateLanguages();

		}

		info("Initializer.start() - TERMINO");

	}

	/**
	 * Inicializa variaveis staticas do sistema
	 */
	public void initStaticValues() {
		UtilsCore.debug = Boolean.parseBoolean(parameterService.findByKeyOrDefault("TRACE_DEBUG", "true"));
		UtilsCore.info = Boolean.parseBoolean(parameterService.findByKeyOrDefault("TRACE_INFO", "true"));
		UtilsCore.warn = Boolean.parseBoolean(parameterService.findByKeyOrDefault("TRACE_WARN", "true"));
		UtilsCore.error = Boolean.parseBoolean(parameterService.findByKeyOrDefault("TRACE_ERROR", "true"));
	}

	/**
	 * Nottifica o start da aplication
	 */
	public void notifyStart() {

		try {

			Utils.warname = env.getProperty("suite.name.version", "suite-api");

			Parameter sendMailStart = parameterService.findByKeyOrNull("SEND_EMAIL_START_APPLICATION");

			if (sendMailStart == null) {

				info("Initializer.notifyStart() - O parametro SEND_EMAIL_START_APPLICATION não existe");
				return;

			}

			if ("false".equalsIgnoreCase(sendMailStart.getValue())) {

				info("Initializer.notifyStart() - O envio de emails na inicializacao esta desabilitada");
				return;

			}

			Parameter recipent = parameterService.findByKeyOrNull("RECIPIENTS_START");

			if (recipent == null) {

				info("Initializer.notifyStart() - No recipent start found");
				return;

			}

			mailer.sendMail("CDPEMAIL0000001", null, Arrays.asList(recipent.getValue().split(";")), "Aplicação Iniciada (" + Utils.warname + ")",
					templateEmailStart());

		} catch (Exception e) {

			warn("Erro ao enviar email na inicializacao! " + e);

		}

	}

	/**
	 * Script de instalacao CORE
	 */
	private void install() {
		Application app = applicationService.findByName("CORE");

		if (app != null) {

			info("Initializer already was executed in " + app.getDateInitializer());
			return;

		}

		info("Initializer.install() - INICIO");

		BusinessUnit bu;
		Sector sector;

		app = createApplication();

		bu = createBusinessUnit(app);

		createBusinessUnitParameter(bu);

		createPermission(app);

		createParameter();

		createDataService(bu);

		sector = createSector(bu, app);

		createProfileAndUser(sector, bu);

		createDataList();

		createLanguage();

		app.setDateInitializer(LocalDateTime.now());

		applicationService.saveBySystem(app);

		createInitializer("INITIALIZER_CORE", "Incialização do sistema");

		info("Initializer.install() - FIM");

	}

	/**
	 * Cria a apliacao core
	 * @return
	 */
	private Application createApplication() {
		Application app;
		String identifier = RandomStringUtils.randomAlphabetic(15).toUpperCase();
		app = fillApplication("CORE", "CORE", "suite-module-core", identifier, "home_sysMonkey.png", true);
		return app;

	}

	/**
	 * Cria a BU Default
	 * @param app
	 * @return
	 */
	private BusinessUnit createBusinessUnit(Application app) {
		String identifier = RandomStringUtils.randomAlphabetic(15).toUpperCase();
		BusinessUnit bu = fillBusinessUnit("DEFAULT", "Unidade Negócio Default", identifier, "home_sysMonkey.png", app);
		return bu;

	}

	/**
	 * Cria os parametros da BU
	 * @param bu
	 */
	private void createBusinessUnitParameter(BusinessUnit bu) {
		BusinessUnitParameter buPar0 = fillBusinessUnitParameter("CORE", "SIGLA_ASSUNTO_EMAIL", "CAS",
				"Sigla que compoe o asssunto de emails enviados pela suite ex: [CAS] - Reset de senha", bu);

		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar0.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar0);

		BusinessUnitParameter buPar1 = fillBusinessUnitParameter("CORE", "SENHA_PADRAO_USUARIO", "@123",
				"Senha padrão para cadastro de usuarios. O reset de senha não usa esse valor", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar1.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar1);

		BusinessUnitParameter buPar2 = fillBusinessUnitParameter("CORE", "LDAP_ENABLE", "false",
				"Ativar o uso de autenticação LDAP", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar2.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar2);

		BusinessUnitParameter buPar3 = fillBusinessUnitParameter("CORE", "LDAP_PROVIDER_URL", "",
				"URL Active Directory LDAP (Ex: ldap://adc.sysmonkey.com.br:389)", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar3.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar3);

		BusinessUnitParameter buPar4 = fillBusinessUnitParameter("CORE", "LDAP_DOMAIN", "",
				"Dominio Active Directory LDAP (Ex: sysmonkey.com.br) ", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar4.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar4);

		BusinessUnitParameter buPar5 = fillBusinessUnitParameter("CORE", "LDAP_BASEDN", "",
				"DN Active Diretory LDAP (Ex: dc=adc,dc=sysmonkey,dc=com,dc=br)", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar5.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar5);

		BusinessUnitParameter buPar6 = fillBusinessUnitParameter("CORE", "LDAP_SECURITY_PRINCIPAL", "",
				"Formato da propriedade de autenticação (Ex: uid={user},{dn} ou {user}@{domain}", bu);
		if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar6.getCode())).findFirst().isPresent())
			bu.getBusinessUnitParameters().add(buPar6);

		businessUnitRepository.saveBySystem(bu);

	}

	/**
	 * Cria as permissoes do mdoulo core
	 */
	private void createPermission(Application app) {
		// 0 Exclusiva SA, 1 Compartilhada SA, 2 Exclusiva UA, 3 Compartilhada UA, 4 Exclusiva ADM, 5 Compartilh ADM, 6 Nativos

		// ++++++++++++++++++(Configuração)+++++++++++++++++++++++++//

		// -----------------------language--------------------------//

		fillPermission(0, app, "LANGUAGE", "VIEWER", "Visualizar Linguagem", "MENU", null, "/language",
				"Linguagem", "Configuração", "fad fa-language", 1, 0, true).getCode();
		fillPermission(0, app, "LANGUAGE", "INSERT", "Inserir Linguagem", "BUTTON", null);
		fillPermission(0, app, "LANGUAGE", "UPDATE", "Editar Linguagem", "BUTTON", null);
		fillPermission(0, app, "LANGUAGE", "DELETE", "Deletar Linguagem", "BUTTON", null);

		// -----------------------data list--------------------------//

		fillPermission(6, app, "DATA_LIST", "LIST", "Listar Lista de Dados", null, null).getCode();
		fillPermission(0, app, "DATA_LIST", "VIEWER", "Visualizar Lista de Dados", "MENU", null, "/datalist",
				"Lista de Dados", "Configuração", "fad fa-dice-six", 1, 1, true).getCode();
		fillPermission(0, app, "DATA_LIST", "INSERT", "Inserir Lista de Dados", "BUTTON", null).getCode();
		fillPermission(0, app, "DATA_LIST", "UPDATE", "Editar Lista de Dados", "BUTTON", null).getCode();

		// -----------------------application--------------------------//

		fillPermission(6, app, "APPLICATION", "LIST", "Listar Aplicacões", null, null).getCode();
		fillPermission(0, app, "APPLICATION", "VIEWER", "Visualizar Aplicacões", "MENU", null, "/application",
				"Aplicações", "Configuração", "fad fa-browser", 1, 2, true).getCode();
		fillPermission(0, app, "APPLICATION", "INSERT", "Inserir Aplicacões", "BUTTON", null).getCode();
		fillPermission(0, app, "APPLICATION", "UPDATE", "Editar Aplicacões", "BUTTON", null).getCode();

		// -----------------------permission--------------------------//

		Long pLP = fillPermission(6, app, "PERMISSION", "LIST", "Listar Permissão", null, null).getCode();
		Long pVP = fillPermission(0, app, "PERMISSION", "VIEWER", "Visualizar Permissão", "MENU", null, "/permission",
				"Modulos/Permissões", "Configuração", "fad fa-cog", 1, 3, true).getCode();
		fillPermission(0, app, "PERMISSION", "INSERT", "Inserir Permissão", "BUTTON", pVP).getCode();
		fillPermission(0, app, "PERMISSION", "UPDATE", "Editar Permissão", "BUTTON", pVP).getCode();

		// -----------------------busines unit--------------------------//

		Long pLUN = fillPermission(6, app, "BUSINESS_UNIT", "LIST", "Listar Unidade de Negócio", null, null).getCode();
		Long pVUN = fillPermission(0, app, "BUSINESS_UNIT", "VIEWER", "Visualizar Unidade de Negócio", "MENU", null, "/businessUnit",
				"Unidades de Negócio", "Configuração", "fad fa-building", 1, 4, true).getCode();
		fillPermission(0, app, "BUSINESS_UNIT", "INSERT", "Inserir Unidade de Negócio", "BUTTON", pVUN).getCode();
		fillPermission(0, app, "BUSINESS_UNIT", "UPDATE", "Editar Unidade de Negócio", "BUTTON", pVUN).getCode();
		fillPermission(2, app, "BUSINESS_UNIT", "API", "Acesso ao modulo Business Unit para uso em API", null, null).getCode();

		// -----------------------parameter--------------------------//

		fillPermission(6, app, "PARAMETER", "LIST", "Listar Parametros", null, null).getCode();
		Long pVPar = fillPermission(0, app, "PARAMETER", "VIEWER", "Visualizar Parametros", "MENU", null, "/parameter",
				"Parâmetros", "Configuração", "fad fa-th-list", 1, 5, true).getCode();
		fillPermission(0, app, "PARAMETER", "INSERT", "Inserir Parametros", "BUTTON", pVPar).getCode();
		fillPermission(0, app, "PARAMETER", "UPDATE", "Editar Parametros", "BUTTON", pVPar).getCode();

		// ++++++++++++++++++(Administrativo)+++++++++++++++++++++++++//

		// -----------------------data service--------------------------//

		fillPermission(6, app, "DATA_SERVICE", "LIST", "Listar Serviços de Dados", null, null).getCode();
		Long pVDS = fillPermission(0, app, "DATA_SERVICE", "VIEWER", "Visualizar Serviços de Dados", "MENU", pLUN, "/dataservice",
				"Serviço de Dados", "Administrativo", "fad fa-database", 2, 1, true).getCode();
		fillPermission(2, app, "DATA_SERVICE", "INSERT", "Inserir Serviços de Dados", "BUTTON", pLUN + "," + pVDS).getCode();
		fillPermission(2, app, "DATA_SERVICE", "UPDATE", "Editar Serviços de Dados", "BUTTON", pLUN + "," + pVDS).getCode();
		fillPermission(2, app, "DATA_SERVICE", "CLONE", "Clonar Serviço de Dados", "BUTTON", pLUN + "," + pVDS);
		fillPermission(2, app, "DATA_SERVICE", "API", "Acesso ao modulo Serviço de dados para uso em API", null, null).getCode();

		// -----------------------tarefa de dados--------------------------//

		fillPermission(6, app, "DATA_TASK", "LIST", "Listar Tarefa de Dados", null, null);
		String pVDT = fillPermission(3, app, "DATA_TASK", "VIEWER", "Visualizar Tarefa de Dados", "MENU", pLUN, "/datatask",
				"Tarefa de Dados", "Administrativo", "fad fa-coins", 2, 2, true).getCode() + "";
		fillPermission(4, app, "DATA_TASK", "INSERT", "Inserir Tarefa de Dados", "BUTTON", pLUN + "," + pVDT);
		fillPermission(4, app, "DATA_TASK", "UPDATE", "Editar Tarefa de Dados", "BUTTON", pLUN + "," + pVDT);
		fillPermission(4, app, "DATA_TASK", "CLONE", "Clonar Tarefa de Dados", "BUTTON", pLUN + "," + pVDT);

		// -----------------------sector--------------------------//

		Long pLS = fillPermission(6, app, "SECTOR", "LIST", "Listar Setor", null, null).getCode();
		Long pVS = fillPermission(0, app, "SECTOR", "VIEWER", "Visualizar Setor", "MENU", pLUN + "," + pVDS, "/sector",
				"Setores", "Administrativo", "fad fa-th-large", 2, 3, true).getCode();
		fillPermission(2, app, "SECTOR", "INSERT", "Inserir Setor", "BUTTON", pLUN + "," + pVDS + "," + pVS);
		fillPermission(2, app, "SECTOR", "UPDATE", "Editar Setor", "BUTTON", pLUN + "," + pVDS + "," + pVS);
		fillPermission(2, app, "SECTOR", "API", "Acesso ao modulo Setor para uso em API", null, null);

		// -----------------------profile--------------------------//

		Long pLPerf = fillPermission(6, app, "PROFILE", "LIST", "Listar Perfil", null, null).getCode();
		Long pVPerf = fillPermission(0, app, "PROFILE", "VIEWER", "Visualizar Perfil", "MENU", pLUN + "," + pLP, "/profile",
				"Perfis", "Administrativo", "fad fa-id-badge", 2, 3, true).getCode();
		fillPermission(2, app, "PROFILE", "INSERT", "Inserir Perfil", "BUTTON", pLUN + "," + pLP + "," + pVPerf).getCode();
		fillPermission(2, app, "PROFILE", "UPDATE", "Editar Perfil", "BUTTON", pLUN + "," + pLP + "," + pVPerf).getCode();

		// -----------------------user--------------------------//

		fillPermission(6, app, "USER", "LIST", "Listar Usuário", null, null).getCode();
		Long pVU = fillPermission(0, app, "USER", "VIEWER", "Visualizar Usuários", "MENU", pLUN + "," + pLS + "," + pLPerf, "/user",
				"Usuários", "Administrativo", "fad fa-user", 2, 4, true).getCode();
		fillPermission(4, app, "USER", "INSERT", "Inserir Usuário", "BUTTON", pLUN + "," + pLS + "," + pLPerf + "," + pVU).getCode();
		fillPermission(4, app, "USER", "UPDATE", "Editar Usuário", "BUTTON", pLUN + "," + pLS + "," + pLPerf + "," + pVU).getCode();

		// ++++++++++++++++++(Outros)+++++++++++++++++++++++++//

		// -----------------------password--------------------------//

		fillPermission(4, app, "PASSWORD", "RESET", "Reset Senhas de Usuários", "BUTTON", pLUN + "," + pLS + "," + pLPerf + "," + pVU).getCode();
		fillPermission(6, app, "PASSWORD", "CHANGE", "Alterar Própria Senha", "BUTTON", null).getCode();

		// -----------------------dashboard--------------------------//

		fillPermission(6, app, "DASHBOARD_LOGON", "VIEWER", "Visualizar Dashboard Logon", null, null).getCode();
		fillPermission(2, app, "DASHBOARD_LOG_SUITE", "VIEWER", "Visualizar aba de arquivos de logs da suite no dashboard", "TAB", null);

	}

	/**
	 * Cria o parametro padrao do sistema
	 */
	private void createParameter() {
		fillParameter("SISTEMA", "SYSTEM_NAME", "Application Suite", "Nome do sistema");
		fillParameter("SISTEMA", "SYSTEM_URL", "http://sysmonkey.brazilsouth.cloudapp.azure.com:8080/suite", "Nome do sistema");
		fillParameter("SISTEMA", "SYSTEM_COPY_RIGTH", "SysMonkey", "Nome do copy rigth a ser exibido");
		fillParameter("SISTEMA", "RECIPIENTS_START", "sistemas@sysmonkey.com.br",
				"Emails separado por ; para receber notificação de inicio da aplicação no servidor");
		fillParameter("SISTEMA", "SEND_EMAIL_START_APPLICATION", "false",
				"Habilita o envio de emails na inicialização da aplicação no container");
		fillParameter("SISTEMA", "SYSTEM_ACRONYM", "CAS", "Sigla da Suite");

	}

	/**
	 * Cria o servico de dados padrao e suas propriedades Associa ele a BU criada
	 */
	private void createDataService(BusinessUnit bu) {
		DataService dsBanco = fillDataService("CDPDB0000000001", "DATASERVICE", DataServiceType.DATABASE, "Data Service DEFAULT",
				"Banco de dados Default", bu);
		if (dsBanco.getProperties() != null)
			dsBanco.getProperties().clear();

		List<Property> lstPropBanco = new ArrayList<>();
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_PROVIDER", "MYSQL", "Provedor da base de dados MYSQL, MSSQL, PGSQL ou ORACLE"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_HOST", "127.0.0.1", "Endereço do base de dados"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_INSTANCE", "", "Nome da instância do banco de dados"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_PORT", "3306", "Porta de acesso ao banco de dados"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_NAME", "sys", "Nome do banco de dados"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_USER", "usr_sys", "Usuário de conexão do banco de dados"));
		lstPropBanco.add(fillProperty("TEXT", "DATABASE_TIMEOUT", "10", "Timeout de conexao com o banco"));
		lstPropBanco.add(fillProperty("PASSWORD", "DATABASE_PASSWORD", "", "Senha de autenticação no banco de dados"));

		if (dsBanco.getProperties() != null)
			dsBanco.getProperties().addAll(lstPropBanco);
		else
			dsBanco.setProperties(lstPropBanco);

		// Atualiza o objeto dataservice de cada objeto property
		dsBanco.getProperties().forEach(p -> {

			p.setDataService(dsBanco);
			if (!p.getName().toUpperCase().contains(dsBanco.getType().toString()))
				p.setName(dsBanco.getType() + "_" + p.getName().toUpperCase());

		});
		// dsBanco.setProperties(lstPropBanco);
		dataServicesService.saveBySystem(dsBanco);

		String pass = "";
		DataServiceType dst = DataServiceType.EMAILSERVICE;

		DataService dsEmail = fillDataService("CDPEMAIL0000001", "DATASERVICE", dst, "Email Service DEFAULT",
				"Serviço de envio de e-mail Default", bu);

		if (dsEmail.getProperties() != null)
			dsEmail.getProperties().clear();

		List<Property> lspPropEmail = new ArrayList<>();

		lspPropEmail.add(fillProperty("TEXT", "EMAILSERVICE_HOST", "smtp.gmail.com", "Host do serviço de email"));
		lspPropEmail.add(fillProperty("TEXT", "EMAILSERVICE_PORT", "587", "Porta do serviço de email"));
		lspPropEmail.add(fillProperty("TEXT", "EMAILSERVICE_USER", "notify.sysmonkey@gmail.com", "Usuário de autenticação do serviço email"));
		lspPropEmail.add(fillProperty("PASSWORD", "EMAILSERVICE_PASSWORD", pass, "Senha de autenticação do serviço de email (Gerar Chave)"));
		lspPropEmail.add(fillProperty("TEXT", "EMAILSERVICE_SENDER", "sistemas@sysmonkey.com.br", "Email Remetente para envio"));
		lspPropEmail.add(fillProperty("TEXT", "EMAILSERVICE_EMAILTEST", "sistemas@sysmonkey.com.br", "Email destinatário para teste conexão"));

		// Suite4Strats - dhaavrbaiovhavzl
		// SuiteDevNeto - wnxibowjtbxjclpk

		// dsEmail.setProperties(lspPropEmail);

		if (dsEmail.getProperties() != null)
			dsEmail.getProperties().addAll(lspPropEmail);
		else
			dsEmail.setProperties(lspPropEmail);

		// Atualiza o objeto dataservice de cada objeto property
		dsEmail.getProperties().forEach(p -> {

			p.setDataService(dsEmail);
			if (!p.getName().toUpperCase().contains(dsEmail.getType().toString()))
				p.setName(dsEmail.getType() + "_" + p.getName().toUpperCase());

		});
		// dsEmail.setProperties(lspPropEmail);
		dataServicesService.saveBySystem(dsEmail);

	}

	/**
	 * Cria o setor default
	 * @param bu
	 * @param app
	 * @return
	 */
	private Sector createSector(BusinessUnit bu, Application app) {
		Sector sct = fillSector("DEFAULT", "Setor Default", RandomStringUtils.randomAlphabetic(15).toUpperCase(), false, bu, app);
		return sct;

	}

	/**
	 * Cria perfis administrativos Associa eles a BU e ao Setor
	 * @param sct
	 */
	private Profile createProfileAndUser(Sector sct, BusinessUnit bu) {
		List<Sector> sectors = new ArrayList<>();
		sectors.add(sct);

		Profile profileSA = fillProfile("SUPER ADMIN", 1, null);
		fillUser("Super Administrador", "sadm", "sadm@sysmonkey.com.br", "@123", profileSA, sectors, bu);

		Profile profileApi = fillProfile("API", 4, null);
		fillUser("API", "sapi", "sapi@sysmonkey.com.br", "@123", profileApi, sectors, bu);

		fillProfile("UN ADMIN", 2, null);

		fillProfile("ADMIN", 3, null);

		fillProfile("WRITER", 4, null);

		fillProfile("VIEWER", 4, null);

		// Relaciona a permissao viwer para poder aplicar as regras permission x profile na UI
		if (!profileSA.getPermissions().stream().filter(x -> x.getKey().equals("CORE_PERMISSION_VIEWER")).findAny().isPresent()) {

			profileSA.getPermissions().add(permissionService.findByKey("CORE_PERMISSION_VIEWER"));
			profileService.saveBySystem(profileSA);

		}

		return profileSA;

	}

	/**
	 * Crias as listas de dados
	 */
	private void createDataList() {
		// ------------------PERMISSION_MODULE--------------------------

		DataList dataList = fillDataList("PERMISSION_MODULE", "List de dados para o drop-down de modulos no cadastro de permissão");
		fillDataItemList("PARAMETER", "PARAMETER", "Módulo de Parâmetros", "CORE", dataList, true);
		fillDataItemList("DATA_LIST", "DATA_LIST", "Módulo de Lista de Dados", "CORE", dataList, true);
		fillDataItemList("APPLICATION", "APPLICATION", "Módulo de Aplicações", "CORE", dataList, true);
		fillDataItemList("BUSINESS_UNIT", "BUSINESS_UNIT", "Módulo de Unidades de Negócio", "CORE", dataList, true);
		fillDataItemList("PERMISSION", "PERMISSION", "Módulo de Permissões", "CORE", dataList, true);
		fillDataItemList("DATA_SERVICE", "DATA_SERVICE", "Módulo de Serviço de Dados", "CORE", dataList, true);
		fillDataItemList("DATA_TASK", "DATA_TASK", "Módulo de Tarefa de Dados", "CORE", dataList, true);
		fillDataItemList("SECTOR", "SECTOR", "Módulo de Setores", "CORE", dataList, true);
		fillDataItemList("PROFILE", "PROFILE", "Módulo de Perfis", "CORE", dataList, true);
		fillDataItemList("USER", "USER", "Módulo de Usuários", "CORE", dataList, true);
		fillDataItemList("PASSWORD", "PASSWORD", "Módulo de Senha", "CORE", dataList, true);
		fillDataItemList("LOG", "LOG", "Módulo de Log", "CORE", dataList, true);
		fillDataItemList("DASHBOARD_LOGON", "DASHBOARD_LOGON", "Módulo de Dashboard Login", "CORE", dataList, true);
		fillDataItemList("LANGUAGE", "LANGUAGE", "Módulo de Linguagen", "CORE", dataList, true);
		fillDataItemList("DASHBOARD_LOG_SUITE", "DASHBOARD_LOG_SUITE", "Módulo Dashboard de arquivos logs suite", "CORE", dataList, true);
		fillDataItemList("LOG", "LOG", "Módulo de Log", "CORE", dataList, true);
		fillDataItemList("API", "API", "Modulo para uso em API", "CORE", dataList, true);
		fillDataItemList("USER_GROUP", "USER_GROUP", "Modulo grupo de usuários", "CORE", dataList, true);

		// ---------------------PERMISSION_ROLE----------------------

		dataList = fillDataList("PERMISSION_ROLE", "List de dados para o drop-down de papeis de permissão no cadastro de permissão");
		fillDataItemList("LIST", "LIST", "Permissão de listar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("VIEWER", "VIEWER", "Permissão de visualizar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("INSERT", "INSERT", "Permissão de inserir objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("UPDATE", "UPDATE", "Permissão de editar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("CLONE", "CLONE", "Permissão de clonar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("INSERT_UPDATE", "INSERT_UPDATE", "Permissão de conjunta de inserir e editar objetos dos modulos", "CORE", dataList,
				true);
		fillDataItemList("DELETE", "DELETE", "Permissão de deletar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("TRUNCATE", "TRUNCATE", "Permissão de truncar objetos dos modulos", "CORE", dataList, true);
		fillDataItemList("RESET", "RESET", "Permissão para reset de senhas", "CORE", dataList, true);
		fillDataItemList("CHANGE", "CHANGE", "Permissão para alterar senhas de usuários", "CORE", dataList, true);
		fillDataItemList("CLICK", "CLICK", "Permissão de click em botões na interface", "CORE", dataList, true);
		fillDataItemList("ACCESS", "ACCESS", "Permissão para acesso a objetos privados", "CORE", dataList, true);

		// ---------------------PERMISSION_TYPE----------------------

		dataList = fillDataList("PERMISSION_TYPE", "List de dados para o drop-down de tipos de permissão no cadastro de permissão");

		fillDataItemList("Exclusiva SA", "0",
				"Permissões atribuídas automaticamente ao perfil SA e que NÂO podem ser atribuídas a outros perfis", "CORE", dataList, true);
		fillDataItemList("Compartilhada SA", "1",
				"Permissões atribuídas automaticamente ao perfil SA e que podem ser atribuídas a outros perfis", "CORE", dataList, true);
		fillDataItemList("Exclusiva UA", "2",
				"Permissões atribuídas automaticamente ao perfis SA e UA e que NÂO podem ser atribuídas a outros perfis", "CORE", dataList,
				true);
		fillDataItemList("Compartilhada UA", "3",
				"Permissões atribuídas automaticamente ao perfil UA e que podem ser atribuídas a outros perfis", "CORE", dataList, true);
		fillDataItemList("Exclusiva ADM", "4",
				"Permissões atribuídas automaticamente aos perfis SA e UN e ADM e que NÂO podem ser atribuídas a outros perfis", "CORE",
				dataList, true);
		fillDataItemList("Compartilhada ADM", "5",
				"Permissões atribuídas automaticamente aos perfis SA e UN e ADM e que podem ser atribuídas a outros perfis", "CORE", dataList,
				true);
		fillDataItemList("Compartilhada Nativos", "6",
				"Permissões atribuídas automaticamente aos pefis SA e UN e ADM e perfis Nativos Writer, Viewer e Inspect e Object Handler",
				"CORE", dataList, true);

		// ---------------------PERMISSION_COMPONENT----------------------

		dataList = fillDataList("PERMISSION_COMPONENT", "List de dados para o drop-down de componentes no cadastro de permissão");

		fillDataItemList("MENU", "MENU", "Compmente a qual a permissão se aplica", "CORE", dataList, true);
		fillDataItemList("BUTTON", "BUTTON", "Compmente a qual a permissão se aplica", "CORE", dataList, true);
		fillDataItemList("CHECKBOX", "CHECKBOX", "Compmente a qual a permissão se aplica", "CORE", dataList, true);
		fillDataItemList("ABA", "ABA", "Compmente a qual a permissão se aplica", "CORE", dataList, true);

		// ---------------------DATASERVICE_TYPE----------------------

		dataList = fillDataList("DATASERVICE_TYPE", "List de dados para o drop-down de tipos de Serviço de Dados");

		fillDataItemList("DATABASE", "DATABASE", "Serviço de dados Database", "CORE", dataList, true);
		fillDataItemList("EMAIL SERVICE", "EMAILSERVICE", "Serviço de dados Emailservice", "CORE", dataList, true);
		fillDataItemList("WEB SERVICE", "WEBSERVICE", "Serviço de dados Webservice", "CORE", dataList, true);
		fillDataItemList("DATA LIBRARY", "LIBRARY", "Serviço de dados Biblioteca", "CORE", dataList, true);

		// ---------------------DATASERVICE_SCOPE----------------------
		dataList = fillDataList("DATASERVICE_SCOPE",
				"List de dados para o drop-down de escopo de Serviço de Dados. Apenas user SA e UN podem editar escopo de Sistema");
		fillDataItemList("SYSTEM", "SYSTEM", "Serviço de dados escopo de sistema. Apenas user SA e UN podem editar escopo de Sistema", "CORE",
				dataList, true);
		fillDataItemList("USER", "USER", "Serviço de dados escopo de usuário", "CORE", dataList, true);

		// ---------------------DATASERVICE_PROVIDER----------------------

		dataList = fillDataList("DATASERVICE_PROVIDER", "Providers para o serviço de dados do tipo DATABASE");

		fillDataItemList("Microsoft SQL Server", "MSSQL", "Provider Microsoft", "CORE", dataList, true);
		fillDataItemList("MySQL Database Service", "MYSQL", "Provider MySQL", "CORE", dataList, true);
		fillDataItemList("Postgre SQL", "PGSQL", "Provider Postgre", "CORE", dataList, true);
		fillDataItemList("Oracle Database", "ORACLE", "Provider Oracle", "CORE", dataList, true);

	}

	/**
	 * Cria as linguagens a partir arquivo languages
	 */
	private void createLanguage() {
		List<String> value = languages();

		info(value.size() + " linguages");

		languageService.deleteAll();

		for (String string : value) {

			String[] lang = string.split("###");

			if (lang.length == 4) {
				languageService.deleteByKey(lang[0].trim());
				Language l = new Language();
				l.setKey(lang[0].trim());
				l.setPortugues(lang[1].trim());
				l.setEnglish(lang[2].trim());
				l.setSpanish(lang[3].trim());
				l.setStatus(true);
				l.setDateRecord(LocalDateTime.now());

				try {
					languageService.saveBySystem(l);
				} catch (Exception e) {
					warn("Language duplicated: " + lang[0]);
				}

			}

		}

	}

	/**
	 * Criar a permissao userGroup e seu modulo
	 * @param app
	 */
	private void update_21102020() {
		String nameInitializer = "UPDATE_CORE_21102020";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		Application app = applicationService.findByName("CORE");

		Long pLUN = permissionService.findByKey("CORE_BUSINESS_UNIT_LIST").getCode();

		fillPermission(6, app, "USER_GROUP", "LIST", "Listar Grupo de Usuários", null, null).getCode();
		Long pVUG = fillPermission(0, app, "USER_GROUP", "VIEWER", "Visualizar Grupo de Usuários", "MENU", pLUN, "/usergroup",
				"Grupo de Usuários", "Administrativo", "fad fa-users", 2, 5, true).getCode();
		fillPermission(4, app, "USER_GROUP", "INSERT", "Inserir Grupo de Usuário", "BUTTON", pLUN + "," + pVUG).getCode();
		fillPermission(4, app, "USER_GROUP", "UPDATE", "Editar Grupo de Usuário", "BUTTON", pLUN + "," + pVUG).getCode();

		DataList dataList = dataListService.findByName("PERMISSION_MODULE");
		if (dataList != null)
			fillDataItemList("USER_GROUP", "USER_GROUP", "Modulo grupo de usuários", "CORE", dataList, true);

		createInitializer(nameInitializer, "Criar a permissao userGroup e seu modulo");

		info("Initializer " + nameInitializer + " - FIM");

	}

	/**
	 * Criar O parametro BU SIGLA_ASSUNTO_EMAIL
	 * @param app
	 */
	private void update_06012021() {
		String nameInitializer = "UPDATE_CORE_06012021";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		List<BusinessUnit> lst = businessUnitRepository.findAll();

		for (BusinessUnit bu : lst) {

			BusinessUnitParameter buPar0 = fillBusinessUnitParameter("CORE", "SIGLA_ASSUNTO_EMAIL", "CAS",
					"Sigla que compoe o asssunto de emails enviados pela suite ex: [CAS] - Reset de senha", bu);

			if (!bu.getBusinessUnitParameters().stream().filter(x -> x.getCode().equals(buPar0.getCode())).findFirst().isPresent()) {
				bu.getBusinessUnitParameters().add(buPar0);
				businessUnitRepository.saveBySystem(bu);
				sleep(500);
			}

		}

		createInitializer(nameInitializer, "Criar O parametro BU SIGLA_ASSUNTO_EMAIL");

		info("Initializer " + nameInitializer + " - FIM");

	}

	/**
	 * Criar os parametro de Log do sistema
	 * 
	 * @param sct
	 */
	private void update_29032021() {
		String nameInitializer = "UPDATE_CORE_29032021";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		fillParameter("SISTEMA", "TRACE_DEBUG", "false", "Habilita a escrita de debug no log");
		fillParameter("SISTEMA", "TRACE_INFO", "true", "Habilita a escrita de info no log");
		fillParameter("SISTEMA", "TRACE_WARN", "true", "Habilita a escrita de warn no log");
		fillParameter("SISTEMA", "TRACE_ERROR", "true", "Habilita a escrita de error no log");

		createInitializer(nameInitializer, "Criar os parametro de Log do sistema");

		info("Initializer " + nameInitializer + " - FIM");

	}

	/**
	 * Atualiza o parametro nome do sistema
	 * 
	 * @param sct
	 */
	private void update_12042021() {
		String nameInitializer = "UPDATE_CORE_12042021";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		Parameter parameter = parameterService.findByGroupAndKey("SISTEMA", "SYSTEM_NAME");

		if (parameter != null) {
			parameter.setValue("Application Suite ");

			parameterService.saveBySystem(parameter);

		}

		createInitializer(nameInitializer, "Atualiza o parametro nome do sistema");

		info("Initializer " + nameInitializer + " - FIM");

	}

	/**
	 * Atualiza o codSectorDataTaskParent com o codigo do setor pai
	 * 
	 * @param sct
	 */
	private void update_16042021() {
		String nameInitializer = "UPDATE_CORE_21042021";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		List<DataService> lst = dataServicesService.findAll();

		for (DataService dataService : lst) {

			if (dataService.getScope().equals("DATATASK") && dataService.getIdentifier().equals("LOCKED000000000")) {

				String nameDtt = valueParameterBusinessUnitOrNull("SYS_NANE_TAREFA_DE_DADOS_SETOR", dataService.getBusinessUnit()).trim();
				String name = dataService.getName().replace(nameDtt, "").trim();
				Sector sec = sectorService.findByNameAndBusinessUnit_Code(name, dataService.getBusinessUnit().getCode());

				if (sec != null) {
					dataService.setCodSectorDataTaskParent(sec.getCode());
					dataServicesService.saveBySystem(dataService);
				}

			}

		}

		createInitializer(nameInitializer, "Atualiza o codSectorDataTaskParent com o codigo do setor pai");

		info("Initializer " + nameInitializer + " - FIM");

	}

	/**
	 * Update Languages
	 */
	private void updateLanguages() {
		String nameInitializer = "UPDATE_CORE_LANGUAGES_28";

		if (findInitializer(nameInitializer))
			return;

		info("Initializer " + nameInitializer + " - INICIO");

		createLanguage();

		createInitializer(nameInitializer, "Update Languages");

		info("Initializer " + nameInitializer + " - FIM");

	}

}
