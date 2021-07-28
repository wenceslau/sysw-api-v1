
package com.suite.core.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.service.ApplicationService;
import com.suite.core.service.UserLogonHistoryService;

/**
 * Classe que permite potencializar o token JWT, Permite adicionar informacoes
 * adicional ao token
 * 
 * @author Wenceslau Neto
 *
 */
@Service
public class CustomTokenEnhancer implements TokenEnhancer {

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	@Autowired
	private ApplicationService applicationService;

	/**
	 * Metodo usuado para adicionar diversos detalhes no token
	 * Recebe o token padrao ja autenticado e adiciona informacoes relevantes
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.token.TokenEnhancer#enhance(org.
	 * springframework.security.oauth2.common.OAuth2AccessToken,
	 * org.springframework.security.oauth2.provider.OAuth2Authentication)
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accesToken, OAuth2Authentication authentication) {

		// Objects UserAccount, extenção do Objects User do Spring que implement o UserDetail, que
		// esta dentro do UserDetailService no caso implementada pela classe UserAccountDatailService
		UserCustomSystem userCustomSystem = (UserCustomSystem) authentication.getPrincipal();

		// Adiciona o informacao adicional em um map para adicionar e depois adiciona ao token
		Map<String, Object> addInfo = new HashMap<>();

		addInfo.put("codeUser", userCustomSystem.getUser().getCode());
		addInfo.put("displayName", userCustomSystem.getUser().getName());
		addInfo.put("profile", userCustomSystem.getUser().getProfile().getName());
		addInfo.put("typeProfile", userCustomSystem.getUser().getProfile().getType());
		addInfo.put("sectorName", userCustomSystem.getSector().getName());
		addInfo.put("sectorCode", userCustomSystem.getSector().getCode());
		addInfo.put("businessUnitCode", userCustomSystem.getSector().getBusinessUnit().getCode());

		addInfo.put("receiveNotify",
				userCustomSystem.getUser().getReceiveNotify() != null ? userCustomSystem.getUser().getReceiveNotify() : false);
		addInfo.put("viewerNotify",
				userCustomSystem.getUser().getViewNotify() != null ? userCustomSystem.getUser().getViewNotify() : false);
		addInfo.put("nameExternalDatabase", userCustomSystem.getSector().getNameExternalDatabase());

		BusinessUnit bu;
		if (userCustomSystem.getSector().getBusinessUnit() != null)
			bu = userCustomSystem.getSector().getBusinessUnit();
		else
			bu = userCustomSystem.getUser().getBusinessUnit();

		addInfo.put("imageBUnit", bu.getImage());
		addInfo.put("sectorRequiredDb", userCustomSystem.getSector().getRequiredDb());

		Application mainApp = applicationService.mainApplicatin(bu);

		if (mainApp != null) {
			addInfo.put("logoApp", mainApp.getImage());
			addInfo.put("nameApp", mainApp.getDisplayName());

		}

		System.out.println("CustomTokenEnhancer.enhance() - typeProfile " + userCustomSystem.getUser().getProfile().getType());

		((DefaultOAuth2AccessToken) accesToken).setAdditionalInformation(addInfo);

		System.out.println("token: " + accesToken.getValue());

		// Token gerado com sucesso, define que o user autenticou, gera a auditoria
		userLogonHistoryService.auditLogon(userCustomSystem.getUser().getUsername(), "SUCCESS", userCustomSystem.getDevice(),
				userCustomSystem.getSector().getCode() + "");

		return accesToken;

	}

}