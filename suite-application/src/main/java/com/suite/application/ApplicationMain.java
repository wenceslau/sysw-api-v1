package com.suite.application;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.suite.app.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe de Inicialização da aplicacao usando o SprintBoot
 * @author Wenceslau
 */
@ComponentScan(basePackages = { 
		"com.suite.app", 
		"com.suite.core", 
		"com.suite.job",
		"com.suite.simc"})
@EntityScan(basePackages = { 
		"com.suite.core", 
		"com.suite.job" })
@SpringBootApplication
@EnableScheduling
public class ApplicationMain {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationMain.class);

	private static ApplicationContext applicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * setUp executado apos o construtor. Registra o modulo
	 * JavaTime permite serializar objetos java time com jackson core
	 */
	@PostConstruct
	public void setUp() {

		logger.info("ApplicationMain.setUp()");
		
		objectMapper.registerModule(new JavaTimeModule());

	}

	/**
	 * Metodo main
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.info("ApplicationMain.main()");

		Utils.readPomXml("Main", null);
		
		if (Utils.packaging != null && Utils.packaging.equals("jar") && Utils.isWindows()) {

			SysTray sysTray = new SysTray();
			sysTray.init("Starting...");
			
			SpringApplicationBuilder builder = new SpringApplicationBuilder(ApplicationMain.class);
			builder.application().addInitializers(new ContextInitializer());
			applicationContext = builder.headless(false).run(args);
			
			sysTray.setStatus("Running");


		} else {

			logger.info("ApplicationMain.main() - ContextInitializer");

		    SpringApplication application = new SpringApplication(ApplicationMain.class);
		    application.addInitializers(new ContextInitializer());
			applicationContext = application.run(args);

		}

		logger.info("ApplicationMain.main() " + applicationContext.getId());

	}

	/**
	 * Recupera um bean no contexto do spring
	 * Qualquer objeto mapeado com notacao spring
	 * pode ser encontrado usando o getBean do contexto
	 * @param <T>
	 * @param type
	 * @return
	 */
	public static <T> T getBean(Class<T> type) {

		if (applicationContext != null)
			return applicationContext.getBean(type);

		throw new RuntimeException("ApplicationContext not found");

	}

}
