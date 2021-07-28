package com.suite.app.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.suite.app.base.Enum.DataType;
import com.suite.app.dto.RequestDTO;

/**
 * Metodos uteis a suite
 * @author Wenceslau
 *
 */
public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	private static String osName = System.getProperty("os.name").toLowerCase();
	public static String packaging;
	public static String versionParent;
	public static String warname;
	
	public static boolean debug = true;
	public static boolean info = true;
	public static boolean warn = true;
	public static boolean error = true;

	/*
	 * Variavel definida para true quando a execucao de um inializer esta em curso
	 * Usada para nao recuperar usuario do contexto. no initializer algumas acoees
	 * procuram o ususrio do contexto, e nesse processo ele nao existe
	 */
	public static boolean userSystem;
	
	/*
	 * Variavel definida para true quando a execucao de um JOB esta em curso
	 * Usada para nao recuperar usuario do contexto. no JOB algumas acoees
	 * procuram o ususrio do contexto, e nesse processo ele nao existe
	 */
	public static boolean userJob;
	
	/* Leitura do POM XML para definidi os modulos ativos */
	public static Map<String, Object> contextMap = new HashMap<>();

	/**
	 * Le todos os pom do diretorio
	 */
	public static void readPomXml(String source, String appPath) {
		logger.info("Reading pom.xml. ContextMap: " + Utils.contextMap);

		if (!Utils.contextMap.isEmpty())
			return;

		try {

			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model;
			boolean fileExist = (new File("pom.xml")).exists();
			if (fileExist)
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(new InputStreamReader(Utils.class.getResourceAsStream(
						"/META-INF/maven/com.suite/suite-application/pom.xml")));

			logger.info("pom.xml exist no path root: " + fileExist);
			logger.info("Model to read pom: " + model);

			readPomModel(model);

		} catch (Exception e) {

		}

		// Procura recursivamente no path definio no app prop se nao achou usando o resource stream
		if (Utils.contextMap.isEmpty() && appPath != null)
			searchPom(new File(appPath), "pom.xml");

	}

	/**
	 * Metodo recursivo de leitura no diretorio
	 * @param file
	 * @param search
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static File searchPom(File file, String search) {

		if (file.isDirectory()) {

			File[] arr = file.listFiles();

			for (File f : arr) {

				File found = searchPom(f, search);
				if (found != null)
					return found;

			}

		} else {

			if (file.getName().equals(search)) // verfica se o arquivo tem o nome de pom.xml
				if (readModules(file))
					return file;

		}

		return null;

	}

	/**
	 * Le o pom.xml e procura os modulos
	 * @param file
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws FileNotFoundException
	 */
	private static boolean readModules(File file) {

		try {

			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileReader(file));

			if (readPomModel(model))
				return true;

		} catch (IOException | XmlPullParserException e) {

			throw new RuntimeException(e);

		}

		return false;

	}

	/**
	 * Ler o mode do pom
	 * @param model
	 * @return
	 */
	private static boolean readPomModel(Model model) {

		// Verifica se o pom tem a tag parent
		if (model.getParent() != null) {

			logger.info("Parent Version " + model.getParent().getVersion());
			logger.info("Parent ArtifactId " + model.getParent().getArtifactId());

			// Se a tag for a da suite-api
			if ("suite-application".equalsIgnoreCase(model.getArtifactId()) || "suite-api".equalsIgnoreCase(model.getParent().getArtifactId())) {

				// Pega os modulos ativos da suite
				for (Dependency dependency : model.getDependencies())
					if (dependency.getArtifactId().startsWith("suite"))
						Utils.contextMap.put(dependency.getArtifactId(), model.getVersion());

				if (model.getPackaging() != null)
					packaging = model.getPackaging().toLowerCase();
				versionParent = model.getParent().getVersion();

				return true;

			}

		}

		return false;

	}

	// ******************************************************//

	/* OS GET INFO CONTEXT */
	/**
	 * Identifica o usuario logado no SecurityContextHolder
	 * Autenticadio com o token
	 * @return
	 */
	public static String getUserRequest() {
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			return SecurityContextHolder.getContext().getAuthentication().getName();
		return null;

	}

	/**
	 * Identifica o codigo do usuario que esta logado
	 * @return
	 */
	public static Long getContextUserCode() {
		String valuAuthority = "userCode";
		String value = getValueAuthority(null, valuAuthority);
		return (value != null ? Long.parseLong(value) : null);

	}

	/**
	 * Identifica a codigo do setor do usuario logado
	 * @return
	 */
	public static Long getContextSectorCode() {
		String valuAuthority = "sectorCode";
		String value = getValueAuthority(null, valuAuthority);
		return (value != null ? Long.parseLong(value) : null);

	}

	/**
	 * Identifica a codigo do setor do usuario logado
	 * @return
	 */
	public static String getContextSectorName() {
		String valuAuthority = "sectorName";
		String value = getValueAuthority(null, valuAuthority);
		return (value != null ? value : "");

	}

	/**
	 * Identifica o codigo do usuario que esta logado
	 * @return
	 */
	public static String getContextLanguage() {
		String valuAuthority = "language";
		String value = getValueAuthority(null, valuAuthority);
		return (value != "-1" ? value : "PT");

	}

	/**
	 * Retorna um diretorio temporario onde sera gravados arquivos temporarios
	 * @return
	 */
	public static String getPathTemp() {
		String tempDir = "";

		try {

			File f;

			if (isWindows())
				f = new File("/temp/");
			else
				f = new File("/tmp/");

			try {

				if (!f.exists())
					f.mkdir();

				tempDir = f.getAbsolutePath() + "/";

			} catch (Exception e) {

				throw new RuntimeException("Erro ao criar dir temp", e);

			}

		} catch (Exception e) {

			throw new RuntimeException("Erro ao identificar diretorio temp.", e);

		}

		return tempDir;

	}

	/**
	 * Procura no ecurityContext.getAuthentication().getAuthorities()
	 * o registro de acordo com o valuAuthority informado
	 * A lista de objeto Authorities contem dados que sao adicionado no token
	 * o token contem o codigo usuario e o codigo do setor logado
	 * @param valuAuthority
	 * @return
	 */
	private static String getValueAuthority(RequestDTO request, String valuAuthority) {
		SecurityContext securityContext = SecurityContextHolder.getContext();

		if (securityContext == null || securityContext.getAuthentication() == null) {

			//O request eh um objeto criado no resource do endpoint
			//contem os dados do setor e do usuario que realizou a requisicao
			//Usando em processamentos de thread que precisam rodar apos a requisicao do usuario
//			if (request != null) {
//				if (valuAuthority.equals("sectorCode"))
//					return request.+"";
//				else if (valuAuthority.equals("userCode"))
//					return request.getUser()+"";
//			}

			return "-1";
		}

		Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

		// Recupera o codigo do usuario logado dentro da colecao de autorizacoes
		// Valor adicionado na classe UserAccountDatailService metodo getPermission
		Optional<? extends GrantedAuthority> result = authorities.stream()
				.filter(name -> name.getAuthority().startsWith(valuAuthority)).findAny();

		if (result.isPresent()) {

			String value = result.get().getAuthority().split("=")[1];
			return value;

		}

		return "-1";

	}

	// ******************************************************//

	/* OS INFO */
	public static String getOsName() {
		return osName;

	}

	public static boolean isWindows() {
		return (osName.indexOf("win") >= 0);

	}

	public static boolean isMac() {
		return (osName.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {
		return (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0);

	}
	// ******************************************************//

	/* JSON */

	/**
	 * Format object to json
	 * @param object
	 * @return
	 */
	public static String objectToJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		String jsonInString = "Error convert object to json. ";

		try {

			jsonInString = mapper.writeValueAsString(object);

		} catch (Exception e) {

			jsonInString += e.getMessage();

		}

		return jsonInString;

	}

	/**
	 * Converte Objeto para JSON e nao mostra as propriedade com valor null
	 * @param object
	 * @return
	 */
	public static String objectToJsonNotIncludNull(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);

		String jsonInString = "Error convert object to json ";

		try {

			jsonInString = mapper.writeValueAsString(object);

		} catch (Exception e) {

			jsonInString += e.getMessage();

		}

		return jsonInString;

	}

	/* DATA TYPE */

	/**
	 * Convert sigla data type to EnumD DataType
	 * @param name
	 * @return
	 */
	public static DataType stringToDataType(String name) {

		switch (name) {

		case "string":
		case "file":
			return DataType.STRING;
		case "int":
			return DataType.INTEGER;
		case "long":
			return DataType.LONG;
		case "decimal":
			return DataType.BIGDECIMAL;
		case "date":
			return DataType.DATE;
		case "datetime":
			return DataType.DATETIME;
		case "time":
			return DataType.TIME;
		case "boolean":
			return DataType.BOOLEAN;
		case "object":
			return DataType.OBJECT;
		case "externalobject":
			return DataType.EXTERNALOBJECT;
		default:
			return DataType.UNKNOW;

		}

	}

	/**
	 * Convert String dataType to sigla datatype
	 * @param name
	 * @return
	 */
	public static String fulloResume(String name) {

		switch (name.toUpperCase()) {

		case "STRING":
		case "VARCHAR":
		case "NVARCHAR":
		case "CHARACTER VARYING":
			return "string";
		case "INTEGER":
		case "INT":
			return "int";
		case "LONG":
		case "BIGINT":
			return "long";
		case "BIGDECIMAL":
		case "NUMERIC":
		case "DECIMAL":
			return "decimal";
		case "DATE":
			return "date";
		case "DATETIME":
		case "TIMESTAMP WITHOUT TIME ZONE":
			return "datetime";
		case "TIME":
			return "time";
		case "BOOLEAN":
		case "BIT":
			return "boolean";
		case "OBJECT":
			return "object";
		case "EXTERNALOBJECT":
			return "externalobject";
		default:
			return "";

		}

	}

}
