
package com.suite.core.auth;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
//import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.suite.app.base.Base;
import com.suite.core.model.User;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.model.Permission;
import com.suite.core.model.Sector;
import com.suite.core.service.CoreRulesService;
import com.suite.core.service.LicenseService;
import com.suite.core.service.SectorService;
import com.suite.core.service.UserLogonHistoryService;
import com.suite.core.service.UserService;

/**
 * Classe que implementa a interface UserDetailsService, responsavel por
 * realizar o load do usuario base ado no username
 * 
 * @author Wenceslau
 *
 */
@Service
public class UserAccountDatailService extends Base implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	@Autowired
	private SectorService sectorService;

	@Autowired
	private LicenseService licenseService;

	@Autowired
	protected CoreRulesService coreRulesService;

	/**
	 * Metodo é executado em toda requisicao recebida, quando aceita apos os
	 * parametros definidos no ResourceServerConfig.configure(HttpSecurity)
	 * 
	 * Recupera a conta pelo username no repositorio Retorna um objeto
	 * UserCustomSystem que é uma especializacao do objeto UserDetails do Spring que
	 * mantem todas as informacoes relevantes
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String strSectorCode = "";
		String language = "";
		String vlrLdp = "";
		String headerUserAgend = "";

		String[] userdata = username.split(";");

		if (userdata.length == 5) {
			username = userdata[0];
			strSectorCode = userdata[1];
			language = userdata[2].toUpperCase();
			vlrLdp = userdata[3];
			headerUserAgend = userdata[4];

			info("Auth: " + username + ";" + strSectorCode + ";" + language);

		} else {
			// O setor e a linguagem eh necessário para o login
			throw new RuntimeException(sectorService.formatTranslate("msg_o_setor_e_a_l_s_n_p_o_l"));

		}

		// Recupera o usuario da base, se nao existir dispara execao
		User user = userService.findByUsernameOrNull(username);

		if (user == null) {
			UserLogonHistory userLogonHistory = new UserLogonHistory();
			userLogonHistory.setDateRecord(LocalDateTime.now());
			userLogonHistoryService.auditLogon(username, "FAILURE", headerUserAgend, strSectorCode);
			throw new RuntimeException("invalido");

		}

		if (!user.checkUserHash())
			throw new RuntimeException("breached");

		if (user.getLocked() != null && user.getLocked())
			throw new RuntimeException("locked");

		if (!user.getStatus())
			throw new RuntimeException(sectorService.formatTranslate("msg_este_usuario_esta_i")); // Este usuario esta inativo

		if (user.getBusinessUnit() != null && !user.getBusinessUnit().getStatus())
			throw new RuntimeException(sectorService.formatTranslate("msg_a_unidade_de_n_d_u_e_i")); // A Unidade de Negocio deste usuario esta

		Long codeSector = null;
		List<UserLogonHistory> list = null;

		// Se veio o codigo 0, o setor nao foi informado no login
		if ("0".equals(strSectorCode)) {
			// Se for o sadm, usa sempre o default para logar
			if (user.getUsername().equalsIgnoreCase("sadm")) {
				codeSector = user.getSectors().stream().filter(x -> x.getName().equalsIgnoreCase("DEFAULT")).findAny().get().getCode();

			} else {
				// Se nao for o sadm, recupera o setor do ultimo login do usuario
				list = userLogonHistoryService.findAllTop2ByUserLogonAndStatusLogonOrderByCodeDesc(user.getUsername(), "SUCCESS");

				// teve login
				if (!list.isEmpty())
					codeSector = list.get(0).getCodeSector();

			}
		}
		else {
			// se veio diferente de 0, o setor foi informado no login
			codeSector = Long.parseLong(strSectorCode);
		}

		// Pesquisa na lista de stor do uuario o setor referente ao codeSector
		final Long code = codeSector;
		Optional<Sector> optSector = user.getSectors().stream().filter(x -> x.getCode().equals(code)).findAny();

		Sector sector;	
		if (optSector.isPresent()) 
			sector = optSector.get(); //if the last sector logged has been found, use its
		else
			sector = user.getSectors().get(0); //els, get the first sector of user.
		
		//if (!optSector.isPresent()) {		
//			if (list != null && !list.isEmpty()) {
//				codeSector = list.get(0).getCodeSector();
//				final Long code1 = codeSector;				
//				optSector = user.getSectors().stream().filter(x -> x.getCode().equals(code1)).findAny();
//				
//				if (!optSector.isPresent()) {
//					UserLogonHistory userLogonHistory = new UserLogonHistory();
//					userLogonHistory.setDateRecord(LocalDateTime.now());
//					userLogonHistoryService.auditLogon(username, "FAILURE", headerUserAgend, strSectorCode);
//					// Usuario invalido!
//					throw new RuntimeException("invalido");
//				}
//				
//			} else {
//				UserLogonHistory userLogonHistory = new UserLogonHistory();
//				userLogonHistory.setDateRecord(LocalDateTime.now());
//				userLogonHistoryService.auditLogon(username, "FAILURE", headerUserAgend, strSectorCode);
//				// Usuario invalido!
//				throw new RuntimeException("invalido");
//
//			}

		//}
		//Sector sector = optSector.get();

		info("User: [" + user.getName() + "] Sector: [" + sector.getName() + "] Profile: [" + user.getProfile().getName()
				+ "] Qtd Permissions: [" + user.getProfile().getPermissions().size() + "]");

		if (!sector.getName().toUpperCase().equals("DEFAULT"))
			licenseService.checkLicense(sector, null);

		String pass = user.getPassword();

		if (user.getWindowsAutentication() != null && user.getWindowsAutentication()) {
			if (autenticateLdap(user, vlrLdp))
				pass = new BCryptPasswordEncoder().encode(vlrLdp);
			else
				pass = "-";

		}

		return new UserCustomSystem(user, pass, sector, language, headerUserAgend, getPermission(user, sector, language));

	}

	/**
	 * Cria um collection com as permissoes do perfil do usuario
	 * 
	 * @param user
	 * @return
	 */
	private Collection<? extends GrantedAuthority> getPermission(User user, Sector sector, String language) {
		// Percorre a lista de permissoes e adiciona no Set
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();

		// Retorna a lista de permissoes ativas
		List<Permission> list = user.getProfile().getPermissions().stream().filter(p -> p.getStatus()).collect(Collectors.toList());
		coreRulesService.rulePermissionApplication(list, sector.getBusinessUnit(), sector, "Permissoes Ativas do Perfil do Usuario");

		list.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getKey().toUpperCase())));

		// Aidicona o o codigo do usuario na colecao de authorities
		authorities.add(new SimpleGrantedAuthority("userCode=" + user.getCode()));
		authorities.add(new SimpleGrantedAuthority("sectorCode=" + sector.getCode()));
		authorities.add(new SimpleGrantedAuthority("sectorName=" + sector.getName()));
		authorities.add(new SimpleGrantedAuthority("language=" + language));

		info("Authorities Token: " + authorities);

		return authorities;

	}

	private boolean autenticateLdap(User user, String pass) {
		String enable = coreRulesService.valueParameterBusinessUnitOrNull("LDAP_ENABLE", user.getBusinessUnit());
		String url = coreRulesService.valueParameterBusinessUnitOrNull("LDAP_PROVIDER_URL", user.getBusinessUnit());
		String domain = coreRulesService.valueParameterBusinessUnitOrNull("LDAP_DOMAIN", user.getBusinessUnit());
		String dn = coreRulesService.valueParameterBusinessUnitOrNull("LDAP_BASEDN", user.getBusinessUnit());
		String principal = coreRulesService.valueParameterBusinessUnitOrNull("LDAP_SECURITY_PRINCIPAL", user.getBusinessUnit());

		if (enable == null || !enable.toLowerCase().equals("true"))
			return false;

		Properties properties = new Properties();

		url = url + "/" + dn;
		principal = principal.replace("{user}", user.getUsername()).replace("{dn}", dn).replace("{domain}", domain);

		info("PROVIDER_URL: " + url);
		info("SECURITY_PRINCIPAL: " + principal);

		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, url);
		properties.put(Context.SECURITY_AUTHENTICATION, "simple");
		properties.put(Context.SECURITY_PRINCIPAL, principal);
		properties.put(Context.SECURITY_CREDENTIALS, pass);

		// initializing active directory LDAP connection
		try {
			DirContext dirContext = new InitialDirContext(properties);
			info("LDAP " + user.getUsername() + " autenticated...." + dirContext.getNameInNamespace());

			return true;

		} catch (NamingException exception) {
			String err = "FAIULRE Autenticate LDAP : \n";
			err += (exception.getExplanation()) + "\n";
			err += (exception.getMessage()) + "\n";

			Throwable cause = getCause(exception);
			if (cause != null)
				err += (cause.getMessage()) + "\n";

			error(err, exception);

			return false;

		}

	}

	Throwable getCause(Throwable e) {
		Throwable cause = null;
		Throwable result = e;

		while (null != (cause = result.getCause()) && (result != cause)) {
			System.out.println(cause.getMessage());
			result = cause;

		}

		return result;

	}

}
