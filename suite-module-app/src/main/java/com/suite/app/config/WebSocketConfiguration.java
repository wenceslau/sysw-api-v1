package com.suite.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.suite.app.dto.InfoLoggedDTO;
import com.suite.app.listener.WebSocketEventListener;

/**
 * Classe de configuracao WebSocket
 * @author wbane
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	/**
	 * Registra o end poit
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		System.out.println("WebSocketConfiguration.registerStompEndpoints()");
		registry.addEndpoint("/socket")
				.setAllowedOrigins("*")
				.withSockJS();

	}

	@Override
	@Description("Define os prefixos de destino e recebimento das msg socket")
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		System.out.println("WebSocketConfiguration.configureMessageBroker()");
		registry.setApplicationDestinationPrefixes("/app")
				.enableSimpleBroker("/chat");

	}

	@Bean
	@Description("Registra a classe que escutara os eventos de conexao e desconexao do socket")
	public WebSocketEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {

		WebSocketEventListener listener = new WebSocketEventListener();
		return listener;

	}

	/**
	 * Validar se quando o objeto é um componente precisa ter o bean dele
	 * @return
	 */
	@Bean
	@Description("Bean que prove uma nova instancia do objeto que armazena os clients webscoket conectados")
	public InfoLoggedDTO configureInfoLogged() {

		return new InfoLoggedDTO();

	}

}