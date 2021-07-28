package com.suite.application;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Classe de inicializacao da aplicacao usando web container externo.
 * @author Wenceslau
 *
 */
public class ServletInitializer extends SpringBootServletInitializer {

	
	private static final Logger logger = LoggerFactory.getLogger(ServletInitializer.class);

	/**
	 * Metodo sobrescrito do SpringBootServletInitializer, usado para configurar
	 * a aplicacao via container
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		logger.info("ServletInitializer.configure()");
				
	    application.initializers(new ContextInitializer());
		
		return application.sources(ApplicationMain.class);

	}

	/**
	 * Metodo sobrescrito do SpringBootServletInitializer que inicializa a aplicacao
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		logger.info("ServletInitializer.onStartup()");

		super.onStartup(servletContext);

	}

}
