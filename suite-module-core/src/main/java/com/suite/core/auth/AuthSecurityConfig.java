package com.suite.core.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.suite.core.service.UserLogonHistoryService;

/**
 * Classe usada pelo Profile OAuth Authentication, Apenas em OAuth2
 * Para configurar o processo de autenticacao com token
 * @author Wenceslau Neto
 *
 */
@Profile("oauth-security")
@Configuration
public class AuthSecurityConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	/**
	 * Metodo é chamado apos o ResourceServerConfig.createExpressionHandler()
	 * Configura o local de amazenamento do token Metodo tambem gera o token
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		System.out.println("AutorizationServerConfig.configure(AuthorizationServerEndpointsConfigurer)");

		// Objects para manipular o conteudo do token, a Classe CustomTokenEnhancer
		// adiciona detalhes ao token
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

		// Armazena o toke em uma "loja", as requisicoes validam o token nessa Store
		endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain) // O token é gerado e tem dados adicionais
																				// setado pelo Enhancer criado acima
				.userDetailsService(userDetailsService).reuseRefreshTokens(false) // ??
				.authenticationManager(authenticationManager); // Adiciona o tone ao authenticationManager

	}

	/**
	 * Metodo chamado apos
	 * AutorizationServerConfig.configure(ClientDetailsServiceConfigurer) Configura
	 * as autorizações que ira usar essa API
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		System.out.println("AutorizationServerConfig.configure(ClientDetailsServiceConfigurer)");

		clients.inMemory().withClient("angular") // Nome da aplicacao
				.secret("$2a$10$aAaagNhSfW8j.actedKZLOFa.wavntb7LSs9GXzLrs2CrETC2m3wa") // @ngul@r0 senha da aplicacao
				.scopes("read", "write", "delete") // Scopo de permissao da aplicacao
				.authorizedGrantTypes("password") // A aplicação que consome a api, tem acesso as dados
				//.authorizedGrantTypes("password", "refresh_token") // A aplicação que consome a api, tem acesso as dados
																	// do usuario para solicitar o token
				.accessTokenValiditySeconds(3600 * 4); // tempo de vida do token 240 minutos
				//.refreshTokenValiditySeconds(3600 * 24) // tempo de vida do refresh token
				//.and().withClient("mobile")// Nome de outra aplicacao
				//.secret("$2a$10$g3QVgWFtrDMzDfdwyLo5veDzAoFIZapFtlf9t/e5N/pJbra5e7BRm") // m0b1l30
				//.scopes("read") // Scopo
				//.authorizedGrantTypes("password", "refresh_token"); // A aplicação que consome a api, tem acesso as dados
																	// do usuario para solicitar o token
//				.accessTokenValiditySeconds(60 * 60) // tempo de vida do token 60 minutos
//				.refreshTokenValiditySeconds(3600 * 24); // ??

	}

	/**
	 * Metodo chamado durante o start da aplicacao Cria uma "Store" para tokens JWT
	 * A notacao bean indica que o spring pode injetar esse objeto em toda aplicacao
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {

		System.out.println("AutorizationServerConfig.tokenStore()");
		return new JwtTokenStore(accessTokenConverter());

	}

	/**
	 * Chamado dentro do metodo configure desta classe.
	 * Disponibiliza uma instancia do CustomTokenEnhancer
	 * @return
	 */
	@Bean
	public TokenEnhancer tokenEnhancer() {

		return new CustomTokenEnhancer();

	}

	/**
	 * Metodo chamado durante o start da aplicação.
	 * Adiciona uma assinatura para relacionar o Token com o Oauth2
	 * A notacao bean indica que o spring pode injetar esse objeto em toda aplicacao
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {

		System.out.println("AutorizationServerConfig.accessTokenConverter()");
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("cdpgto");
		return accessTokenConverter;

	}

	/**
	 * Evento dispara quando ha sucesso na autenticacao, via user ou token
	 * @param authorizedEvent
	 */
	@EventListener
	public void authSuccessEventListener(AuthenticationSuccessEvent authorizedEvent) {
//		System.out.println(">>>>authorizedEvent.getAuthentication().getPrincipal..: "+authorizedEvent.getAuthentication().getPrincipal());
//		System.out.println(">>>>authorizedEvent.getTimestamp......................: "+new Date(authorizedEvent.getTimestamp()));
//		System.out.println(">>>>authorizedEvent.getSource.........................: "+authorizedEvent.getSource());
//		System.out.println(">>>>authorizedEvent.getAuthentication().getName.......: "+authorizedEvent.getAuthentication().getName());
//		System.out.println(">>>>authorizedEvent.getAuthentication().getAuthorities: "+authorizedEvent.getAuthentication().getAuthorities());
//		System.out.println(">>>>authorizedEvent.getAuthentication().getCredentials: "+authorizedEvent.getAuthentication().getCredentials());
//		System.out.println(">>>>authorizedEvent.getAuthentication().getDetails....: "+authorizedEvent.getAuthentication().getDetails());
//		System.out.println(">>>>authorizedEvent.getAuthentication().isAuthenticate: "+authorizedEvent.getAuthentication().isAuthenticated());
//		System.out.println(">>>>authorizedEvent...................................: "+authorizedEvent);

	}

	/**
	 * Evento dispara quando ocorre falha na autenticacao
	 * @param oAuth2AuthenticationFailureEvent
	 */
	@EventListener
	public void authFailedEventListener(AbstractAuthenticationFailureEvent oAuth2AuthenticationFailureEvent) {

//		System.out.println(">>>>oAuth2AuthenticationFailureEvent.getTimestamp......................: "+new Date(oAuth2AuthenticationFailureEvent.getTimestamp()));
//		System.out.println(">>>>oAuth2AuthenticationFailureEvent...................................: "+(oAuth2AuthenticationFailureEvent));
//
//		
//		// Gera auditoria de falha de acesso
//		System.out.println(oAuth2AuthenticationFailureEvent.getAuthentication().getPrincipal());
		String device = "UNKNOWN";
		String sectorCode = "-1";
		String name = oAuth2AuthenticationFailureEvent.getAuthentication().getName();
		if (name != null) {
			String[] arr = name.split(";");
			if (arr.length == 5) {
				device = arr[4];
				sectorCode = arr[1];
			}
		}
		
		userLogonHistoryService.auditLogon(name, "FAILURE", device, sectorCode);

	}

}
