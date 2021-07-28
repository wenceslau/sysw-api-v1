package com.suite.app.config;

import javax.annotation.PostConstruct;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.suite.app.base.Base;
import com.suite.app.util.Utils;

/**
 * Classe de configuracao FlayWay MIgration
 * @author Wenceslau
 *
 */
@Configuration
public class FlywaySlaveInitializer extends Base {

	@Autowired
	private Environment env;

	@PostConstruct
	public void migrateFlyway() {
		String appPath = env.getProperty("application.path", "");

		info("Operation System: " + Utils.getOsName());
		info("Application Package: " + Utils.packaging);
		info("Application currentPath: " + appPath);

		try {

			Utils.getPathTemp();

		} catch (Exception e) {

			error("Erro ao identifiar PathTemp ", e);
			throw new RuntimeException(e);

		}

		try {

			//forca a leitura do pom, caso a leitura nao tenha sido feita no metodo main
			Utils.readPomXml("migrateFlyway", appPath);

		} catch (Exception e) {

			error("Erro ao ler pom.xml: ", e);
			throw new RuntimeException(e);

		}

		info("ContextMap: [" + Utils.contextMap + "]");

		boolean moduleEnable = Utils.contextMap.containsKey("suite-module-core");
		boolean flywayEnabled = Boolean.parseBoolean(env.getProperty("core.flyway.enabled", "false"));

		if (moduleEnable && flywayEnabled) {

			String provider = env.getProperty("core.datasource.platform", "");
			String url = env.getProperty("core.datasource.jdbc-url", "");
			String user = env.getProperty("core.datasource.username", "");
			String password = env.getProperty("core.datasource.password", "");
			String tableFly = env.getProperty("core.flyway.table", "flyway_schema_history");
			Flyway flyway = new Flyway();
			// flyway.setSchemas("fly-core");
			flyway.setBaselineOnMigrate(true);
			flyway.setBaselineVersionAsString("0000");
			flyway.setTable(tableFly);
			flyway.setDataSource(url, user, password);
			flyway.setLocations("db/core/" + provider.toLowerCase());
			flyway.repair();
			flyway.migrate();

		}


		moduleEnable = Utils.contextMap.containsKey("suite-module-job");
		flywayEnabled = Boolean.parseBoolean(env.getProperty("job.flyway.enabled", "false"));

		if (moduleEnable && flywayEnabled) {

			String provider = env.getProperty("job.datasource.platform", "");
			String url = env.getProperty("job.datasource.jdbc-url", "");
			String user = env.getProperty("job.datasource.username", "");
			String password = env.getProperty("job.datasource.password", "");
			String tableFly = env.getProperty("job.flyway.table", "flyway_schema_history");
			Flyway flyway = new Flyway();
			// flyway.setSchemas("fly-job");
			flyway.setBaselineOnMigrate(true);
			flyway.setBaselineVersionAsString("0000");
			flyway.setTable(tableFly);
			flyway.setDataSource(url, user, password);
			flyway.setLocations("db/job/" + provider.toLowerCase());
			flyway.repair();
			flyway.migrate();

		}

	}

}
