package com.suite.core.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.suite.core.util.Initializer;
import com.suite.core.util.UtilsCore;

/**
 * Classe para escutar eventos da aplicacao na inicializacao do container
 * @author Wenceslau
 *
 */
@Component
public class CoreAppListener {

	/*
	 * Contexto da aplicacao inicializada injetado pelo spring
	 * eh atribuido o contexto defindo na classe UtilsCore
	 * ver comentario dentro do metodo applicationReadyEvent
	 */
	@Autowired
	private ApplicationContext context;

	@Autowired
	private Initializer initializer;

	/**
	 * Evento disparado quando a aplicacao esta inicilizada e disponivel
	 * @param event
	 */
	@EventListener
	private void applicationReadyEvent(ApplicationReadyEvent event) {

		System.out.println(LocalDateTime.now() + " CoreAppListener.applicationReadyEvent() - INIT");

		// O contexto eh atribuido a variavel statica da aplicacao
		// As classes que possuem a notacao @EntityListeners, sao classes do Hibernate
		// O Hibernate nao tem acesso ao contexto do Spring, e o contexto eh necessario nos
		// nos metodos das classes com essa notacao.
		UtilsCore.context = context;

		if (context != null)
			System.out.println("Utils.context.getId(): " + UtilsCore.context.getId());

		System.out.println("Utils.context: " + UtilsCore.context);

		//Processo que executa a inicializacao de dados
		UtilsCore.userSystem = true; //Bloqueia validacao de usuario logado na contexto
		initializer.start();
		initializer.initStaticValues();
		initializer.notifyStart();
		UtilsCore.userSystem = false; //Reabilita a validacao do contexto
		
		System.out.println(LocalDateTime.now() + " CoreAppListener.applicationReadyEvent() - END");

	}

	/**
	 * Evento disparado quando a aplicacao esta inicializada
	 * @param event
	 */
	@EventListener
	private void applicationStartedEvent(ApplicationStartedEvent event) {

		System.out.println(LocalDateTime.now() + " CoreAppListener.applicationStartedEvent()");

	}

	/**
	 * Evento disparado quando a aplicacao esta sendo inicializada
	 * @param event
	 */
	@EventListener
	private void applicationStartingEvent(ApplicationStartingEvent event) {

		System.out.println(LocalDateTime.now() + " CoreAppListener.applicationStartingEvent()");

	}

}
