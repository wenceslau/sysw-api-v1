package com.suite.job.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.suite.core.util.UtilsCore;
import com.suite.job.util.InitializerJob;

/**
 * Dispara eventos da aplicacao
 * 
 * @author Wenceslau
 *
 */
@Component
public class JobAppListener {

	@Autowired
	private InitializerJob initializer;

	@EventListener
	private void applicationReadyEvent(ApplicationReadyEvent event) {

		System.out.println(LocalDateTime.now() + " JobAppListener.applicationReadyEvent()");

		// Processo que executa a inicializacao de dados
		UtilsCore.userSystem = true; // Bloqueia validacao de usuario logado na contexto
		initializer.start();
		UtilsCore.userSystem = false; // Reabilita a validacao do contexto

	}

	@EventListener
	private void applicationStartedEvent(ApplicationStartedEvent event) {

		System.out.println(LocalDateTime.now() + " JobAppListener.applicationStartedEvent()");

	}

	@EventListener
	private void applicationStartingEvent(ApplicationStartingEvent event) {

		System.out.println(LocalDateTime.now() + " JobAppListener.applicationStartingEvent()");

	}

}
