package com.suite.application;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import com.suite.core.util.UtilsCore;
import com.suite.security.cryptography.RSATransporter;

@Component
public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private String platform = System.getenv("SUITE_DS_PLATFORM");
	private String host = System.getenv("SUITE_DS_HOST");
	private String port = System.getenv("SUITE_DS_PORT");
	private String instance = System.getenv("SUITE_DS_INSTANCE");
	private String user = System.getenv("SUITE_DS_USER");
	private String pass = System.getenv("SUITE_DS_PASS");
	private String databases = System.getenv("SUITE_DS_DATABASES");

	private RSATransporter rs = new RSATransporter();

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();

		for (PropertySource<?> propertySource : environment.getPropertySources()) {

			Map<String, Object> propertyOverrides = new LinkedHashMap<>();
			overrrideValues(propertySource, propertyOverrides);

			if (!propertyOverrides.isEmpty()) {

				PropertySource<?> decodedProperties = new MapPropertySource("decoded " + propertySource.getName(), propertyOverrides);
				environment.getPropertySources().addBefore(propertySource.getName(), decodedProperties);

			}

		}

	}

	private void overrrideValues(PropertySource<?> source, Map<String, Object> propertyOverrides) {
		DatasourceProp dsp = new DatasourceProp();

		if (source instanceof EnumerablePropertySource) {

			EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) source;

			for (String key : enumerablePropertySource.getPropertyNames()) {

				// Retrieve data from app properties to fill properties datasourse
				if (key.equals("datasource.platform"))
					dsp.setPlatform((String) source.getProperty(key));
				else if (key.equals("datasource.address"))
					dsp.setHost((String) source.getProperty(key));
				else if (key.equals("datasource.port"))
					dsp.setPort((String) source.getProperty(key));
				else if (key.equals("datasource.instance"))
					dsp.setInstance((String) source.getProperty(key));
				else if (key.equals("datasource.database"))
					dsp.setDatabase((String) source.getProperty(key));
				else if (key.equals("datasource.user"))
					dsp.setUser((String) source.getProperty(key));
				else if (key.equals("datasource.pass"))
					dsp.setPass((String) source.getProperty(key));

			}

			for (String key : enumerablePropertySource.getPropertyNames()) {

				Object rawValue = source.getProperty(key);

				if (rawValue instanceof String) {

					String decodedValue = enviormentValues(key, dsp, (String) rawValue);
					propertyOverrides.put(key, decodedValue);

				}

			}

		}

	}

	private String enviormentValues(String key, DatasourceProp dsp, String input) {
		if (input == null)
			return null;

		// retrieve from enviorment values variabel, if not found use default value

		if (key.contains("datasource.jdbc-url")) {

			if (platform == null)
				platform = dsp.getPlatform();
			if (host == null)
				host = dsp.getHost();
			if (port == null)
				port = dsp.getPort();
			if (instance == null)
				instance = dsp.getInstance();
			if (databases == null)
				databases = dsp.getDatabase();

			String[] dbs = databases.split(",");

			for (String string : dbs) {
				String[] arr = string.split("=");

				boolean ok = false;
				for (String string2 : UtilsCore.contextMap.keySet()) {
					if (string2.contains(arr[0])) {
						ok = true;
						break;
					}
				}
				
				if (ok == false)
					throw new RuntimeException("Algum modulo definido na variavel de ambiente para iniciar um banco nao existe no pom da aplicacao");

				if (key.startsWith(arr[0])) {
					input = dsp.getUrl(platform.trim(), host.trim().toLowerCase(), port.trim().toLowerCase(), instance,
							arr[1].trim().toLowerCase());
					break;
				}

			}

		} else if (key.contains("datasource.hibernate.dialect")) {
			if (platform == null)
				platform = dsp.getPlatform();
			input = dsp.getDialect(platform.trim());

		} else if (key.contains("datasource.driver-class-name")) {
			if (platform == null)
				platform = dsp.getPlatform();
			input = dsp.getDriver(platform.trim());

		} else if (key.contains("datasource.username")) {
			if (user == null)
				user = dsp.getUser();
			input = user;

		} else if (key.contains("datasource.password")) {
			if (pass == null)
				pass = dsp.getPass();
			input = rs.dencrypt(pass);

		} else if (key.contains("datasource.platform")) {
			if (platform == null)
				platform = dsp.getPlatform();
			input = platform;
		}

		return input.trim();

	}

}

class DatasourceProp {

	private String host;
	private String port;
	private String instance;
	private String user;
	private String pass;
	private String platform;
	private String database;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUrl(String platform, String host, String port, String instance, String dbName) {
		String url = "";

		switch (platform.toUpperCase()) {

		case "MSSQL":
			if (instance != null && !instance.trim().isEmpty())
				instance = ";instanceName=" + instance;
			url = "jdbc:sqlserver://" + host + ":" + port + instance + ";databaseName=" + dbName;
			break;
		case "ORACLE":
			url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName + "";
			break;
		case "MYSQL":
			url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";
			break;
		case "PGSQL":
			url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			break;
		case "ODBC":
			url = "jdbc:odbc:" + dbName + "";
			break;
		default:
			break;

		}

		return url;

	}

	public String getDriver(String platform) {
		String driver = "";

		switch (platform.toUpperCase()) {

		case "MSSQL":
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			break;
		case "ORACLE":
			driver = "";
			break;
		case "MYSQL":
			driver = "com.mysql.jdbc.Driver";
			break;
		case "PGSQL":
			driver = "org.postgresql.Driver";
			break;
		case "ODBC":
			driver = "";
			break;
		default:
			break;
		}

		return driver;

	}

	public String getDialect(String platform) {
		String dialiect = "";

		switch (platform.toUpperCase()) {

		case "MSSQL":
			dialiect = "org.hibernate.dialect.SQLServer2008Dialect";
			break;
		case "ORACLE":
			dialiect = "";
			break;
		case "MYSQL":
			dialiect = "org.hibernate.dialect.MySQLDialect";
			break;
		case "PGSQL":
			dialiect = "org.hibernate.dialect.PostgreSQLDialect";
			break;
		case "ODBC":
			dialiect = "";
			break;
		default:
			break;
		}

		return dialiect;
	}

}
