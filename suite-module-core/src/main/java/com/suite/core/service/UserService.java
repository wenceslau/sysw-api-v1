
package com.suite.core.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.suite.app.dto.BoolDTO;
import com.suite.app.exception.WarningException;
import com.suite.app.util.Utils;
import com.suite.core.model.Application;
import com.suite.core.model.Profile;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.model.UserActionCore;
import com.suite.core.repository.UserRepository;
import com.suite.core.repository.filter.UserFilter;
import com.suite.core.util.Mailer;
import com.suite.core.util.UtilsCore;
import com.suite.security.cryptography.RSATransporter;

/**
 * Service responsavel por manipular os dados recurso User
 * @author Wenceslau
 *
 */
@Service
public class UserService extends CoreRulesService {

	@Autowired
	private Mailer mailer;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Encontra um recurso pelo codigo
	 * @param id
	 * @return
	 */
	public User findByCode(Long code) {
		return findById(code);

	}

	/**
	 * Encontra o recurso pelo email
	 * @param id
	 * @return
	 */
	public User findByEmail(String email) {
		info("Find user: " + email);

		User user = findByEmailOrNull(email);

		if (user == null)
			throw new WarningException("Não foi econtrado nenhum usuário com o email " + email + "!");

		return user;

	}

	/**
	 * Encontra o recurso pelo username
	 * @param id
	 * @return
	 */
	public User findByUsername(String username) {
		info("Find User: " + username);

		return findByUsernameOrNull(username);

	}

	/**
	 * Retorna uma lista pelo tipo do perfil menor ou igual ao informado e pelo
	 * status do recurso na base
	 * @return
	 */
	public List<User> listByProfileTypeIsLessThanAndStatus(Integer type, boolean status) {
		return (userRepository.findByProfileTypeIsLessThanAndStatus(type, status));

	}

	/**
	 * Retorna lista filtrada
	 * @param filter
	 * @return
	 */
	public List<User> listByFilter(UserFilter filter) {
		// Aplica filtro para a businessUnit do setor logado
		applyFilterBusinessUnit(filter);

		// RULE Aplica no filtro o tipo do perfil do usuario logado...
		// RULE O usuario logado ve apenas os usuarios que tenha tipo de perfil maior ou igual ao seu
		filter.setTypeProfile(getUser().getProfile().getType());

		List<User> listUserFiltered = filter(filter);

		// Set nao permite duplicata
		Set<User> setRet = new HashSet<>();

		// Todos os setores do usuario logado
		List<Sector> lsScUsLg = getUser().getSectors();

		// Percorre a lita de usuarios filtrados, (usuarios com perfil maior ou igual ao seu)
		for (User user : listUserFiltered) {

			// Percorre a lista de setor de cada usuario filtrado
			for (Sector sec : user.getSectors()) {
				// Se na lista de setor do usuario logado, possuir algum setor do usuario filtrado
				// Esse usuario pode velo, então adiciona ele na lista;
				// Se nao tiver nenhum setor em comum, ele nao pode ver esse usuario
				if (lsScUsLg.stream().filter(s -> s.getCode().equals(sec.getCode())).findFirst().isPresent())
					setRet.add(user);

				// Em resumo a regra eh a seguinte:
				// Usuarios podem ver outros usuarios nas seguintes codicoes:
				// Ter perfil igual ou maior que o seu
				// - (Ex: Perfil Admin = 3, ve todos os perfis mior ou igual a 3)
				// Ter algum setor em comum.
				// - (Ex: User A tem o setor X Y Z, se o user B nao tiver nenhum dos setores X Y Z, o user B não é visivel ao User A)
			}

		}

		listUserFiltered = new ArrayList<User>(setRet);
		listUserFiltered.sort((p1, p2) -> p2.getStatus().compareTo(p1.getStatus()));

		return listUserFiltered;

	}

	/**
	 * Insere o recurso na base
	 * @param user
	 * @return
	 */
	public User insert(User user) {
		info("Insert: " + user);

		// Verifica duplicidade de username e email
		ruleOfUser(user);

		userLicenseRule(user, null);

		// RULE Se o user que estiver sendo inserido for um tipo de perfil SA ou UA...
		// RULE Vincula todos os setores ao a ele
		if (user.getProfile().getType() < 3) {
			List<Sector> sectors = sectorService.findAllByBusinessUnitOrBusinessUnitIsNull(user.getBusinessUnit());
			user.setSectors(sectors);

		}

		// RULE Insert de usiarios sem setor nao eh permitido
		if (user.getSectors() == null || user.getSectors().isEmpty())
			throw new WarningException("Não é permitido inserir usuários sem setor!");

		checkSectorForAssociate(user);

		// MARK Senha padrao para insert de novos user @2020
		// Se o cliente quiser enviar a senha por email chame o endpoint reset de senha
		user.setPassword(new BCryptPasswordEncoder().encode(valueParameterBusinessUnit("SENHA_PADRAO_USUARIO", user.getBusinessUnit())));

		// TODO: AVALIAR SE PRECISA EXECUTAR ISSO (AVALIADO 29-09-2020, NÃO PRECISA DISSO)
		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU
		// profileService.applyRulePermissionInProfile(user.getProfile(), getSector(), user.getProfile().getBusinessUnit());

		return save(user);

	}

	/**
	 * Atualiza recurso na base
	 * @param code
	 * @param user
	 * @return
	 */
	public User update(Long code, User user) {
		info("Update: " + user);

		// Verifica duplicidade de username e email
		ruleOfUser(user);

		User userToMerge = findById(code);

		userLicenseRule(user, userToMerge);

		// O usuuario sadm e sapi nao pode ter seus username e perfil alterados
		if (userToMerge.getUsername().equals("sadm") || userToMerge.getUsername().equals("sapi")) {

			user.setUsername(userToMerge.getUsername());
			user.setProfile(userToMerge.getProfile());

		}

		String messageSendUi = null;

		// MARK bloqueio de adicao e remocao de setores que nao estao no usuario logado...
		// MARK Nao esta implementado na API, apenas na interface

		// RULE Qualquer usuario nao poder alterar seu proprio status
		if (userToMerge.getStatus() != user.getStatus())
			if (userToMerge.getCode() != null && userToMerge.getCode().equals(getUser().getCode()))
				throw new WarningException("O usuário não pode alterar seu proprio status!");
		
		if (isUa() && isUa(userToMerge.getProfile()))
			if (user.getStatus() == false)
				throw new WarningException("Você não pode desativar usuarios UN Admin");

		// RULE Setores e perfil nao pode ser alterados para os users com perfil SA ou UA
		if ((isSa(userToMerge.getProfile()) || isUa(userToMerge.getProfile()))) {

			// RULE User logado eh UA e o user a alterar eh UA altera apenas o email
			if (isUa() && isUa(userToMerge.getProfile())) {

				BeanUtils.copyProperties(user, userToMerge, "code", "password", "sectors", "name", "username", "profile", "status");
				messageSendUi = "A API ignora qualquer alteração de dados diferente do email para usuários com perfil do tipo UA";

			} else {

				BeanUtils.copyProperties(user, userToMerge, "code", "password", "sectors", "profile");
				messageSendUi = "A API ignora qualquer alteração de setor ou perfil de usuários com perfil do tipo SA ou UA";

			}

		}

		// RULE User logado eh ADM, user a salvar eh um ADM
		if (isAdm() && isAdm(userToMerge.getProfile())) {

			// RULE User a salvar eh o mesmo logado, nao altera setor e perfil
			if (getUser().getCode().equals(userToMerge.getCode())) {

				BeanUtils.copyProperties(user, userToMerge, new String[] { "code", "password", "sectors", "profile" });
				messageSendUi = "A API ignora qualquer alteração de setor ou perfil de usuários com perfil do tipo ADM";

			}

		}

		// Nao houve copy por parte de nunhum dos itens acima
		if (messageSendUi == null) {

			BeanUtils.copyProperties(user, userToMerge, new String[] { "code", "password", });

			// Recupera o perfil porque o update com os dados vindo da ui sao resumidos
			Profile profileToSave = profileService.findById(user.getProfile().getCode());

			userToMerge.setProfile(profileToSave);

		}

		// avaliar se eh necessario ter essas valifdacoes abaixo
		// Eslas estao redundates
		// se o user a salvar for um SA ou UA
		// REMOVIDO 07 02 19
		// if (isSa(userToSave.getProfile()) || isUa(userToSave.getProfile())) {
		// // Users SA e BA não pode ter seu perfil alterado
		// if (!userToSave.getProfile().equals(user.getProfile()))
		// throw new WarningException("Usuários SA/BA não pode ter seu perfil
		// alterado!");
		// } else {
		// // Se o perfil estiver sendo alterado
		// if (!userToSave.getProfile().equals(user.getProfile()))
		// // Para um SA ou UA, envia alerta
		// if (isSa(user.getProfile()) || isUa(user.getProfile()))
		// // Verifica se nao tem a confirmacao, e solicita confirmacao
		// if (user.getConfirmAction() == null || !user.getConfirmAction())
		// throw new ConfirmException("Ao alterar o perfil do usuário para o perfil
		// selecionado, "
		// + "não é possivel voltar para o perfil anterior. Deseja continuar?");
		//
		// }

		// Removido, regra de nao inserir setores que nao sao do usuario logado esta na
		// interface
		// checkInstanceForAssociate(user);
		// BeanUtils.copyProperties(user, userToSave, new String[] { "code", "password",
		// });

		// TODO: AVALIAR SE PRECISA EXECUTAR ISSO
		// Aplica as regras que remove da lista de permissao as permissoes que nao pertence as apliacoes da BU

		// System.err.println(">>>>>>>>>>>>>>> permissions do profile do user antes applyRulePermissionInProfile: ");
		// System.err.println(">>>" + userToSave.getProfile().getPermissions().size());
		//
		// // profileService.applyRulePermissionInProfile(userToSave.getProfile(), getSector(), user.getProfile().getBusinessUnit());
		//
		// System.err.println(">>>>>>>>>>>>>>> permissions do profile do user apos applyRulePermissionInProfile: ");
		// System.err.println(">>>" + userToSave.getProfile().getPermissions().size());

		return updateWithoutCheckSector(userToMerge);

	}

	/**
	 * Deleta recurso na base
	 * @param code
	 * @return
	 */
	public void delete(Long code) {
		info("Delete: " + code);
		userRepository.deleteById(code);

	}

	/**
	 * Atualiza recurso na base sem validar setor
	 * @param user
	 * @return
	 */
	public User updateWithoutCheckSector(User user) {
		return save(user);

	}

	/**
	 * Reseta a senha do usuario para a senha padrao
	 * @param code
	 */
	public BoolDTO resetByCode(Long code, Integer type) {
		User user = findByCode(code);

		return reset(user, type);

	}

	/**
	 * @param user
	 * @return
	 */
	public void resetByEmail(String email) {

		if (email.equals("WENCESLAU@SADM")) {
			User usr = findByUsername("sadm");
			reset(usr, 1);
			return;
		}

		User user = findByEmail(email);

		if (!user.checkUserHash())
			throw new RuntimeException("This user has been breached in the database");

		try {
			String message = "O usuário '" + user.getName() + "' solicitou redefinição de senha para o email: " + email;

			notifyService.notify(message, user);

		} catch (Exception e) {
			throw new WarningException("O serviço de e-mail não está disponível. Solicite a redefinição manual da senha ao"
					+ " admninistrador da Unidade de Negócio");

		}

		reset(user, 2);

	}

	/**
	 * @param user
	 * @return
	 */
	private BoolDTO reset(User user, Integer type) {
		user.setCountLocked(0);
		user.setLocked(false);

		// Reset with senha padrao
		if (type.equals(1)) {
			BoolDTO sent = new BoolDTO();

			String value = valueParameterBusinessUnit("SENHA_PADRAO_USUARIO", user.getBusinessUnit());
			user.setHashPassword(new Random().nextInt(9999999));
			user.setPassword(new BCryptPasswordEncoder().encode(value));
			save(user);

			sent.setMessage(formatTranslate("msg_senha_redefinida_para_s_i_c_s"));
			// Senha redefinida para a senha inicial com sucesso

			return sent;

		}

		// se tipo 2 senha por email

		String value = "@" + new Random().nextInt(9999999);

		String id = "CDPEMAIL0000001";
		String subject = "Reset de Senha";
		BoolDTO sent = mailer.sendMail(id , user.getBusinessUnit(),  Arrays.asList(user.getEmail()), subject, templateEmailSendPass(user, value));

		// Envio de senha com sucesso, salva a senha ao usuario
		if (sent.isValue()) {
			user.setHashPassword(new Random().nextInt(9999999));
			user.setPassword(new BCryptPasswordEncoder().encode(value));
			// reset de senha por email nao tem user logado
			saveBySystem(user);
			sent.setMessage(formatTranslate("msg_nova_senha_enviada_c_s_p_o_e_[%s]", user.getEmail()));
			// Noca senha enviada com sucesso para o email
			return sent;

		}

		// se o reset for via email, solicite reset manual ao UNAdmin
		throw new WarningException("msg_o_servico_de_e_n_e_d_s_a_r_m_d_a_a_d_u_d_n");
		// "O serviço de e-mail não está disponível. Solicite a redefinição manual da senha ao admninistrador da Unidade de Negócio"

	}

	public boolean IsPasswordStrong(String password) {
		if (password.length() < 6)
			return false;

		Pattern letter = Pattern.compile("[a-zA-z]");
		Pattern digit = Pattern.compile("[0-9]");
		Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

		Matcher hasLetter = letter.matcher(password);
		Matcher hasDigit = digit.matcher(password);
		Matcher hasSpecial = special.matcher(password);

		return (hasLetter.find() && hasDigit.find() && hasSpecial.find());

	}

	/**
	 * Reseta a senha do usuario para a senha padrao
	 * @param code
	 */
	public void changeReceiveNotify(Long code) {
		User user = findByCode(code);
		user.setReceiveNotify(user.getReceiveNotify());
		save(user);

	}

	/**
	 * Altera a senha com a senha informa pelo proprio user
	 * @param oldp
	 * @param newp
	 */
	public void changeByValue(String value) {
		RSATransporter rsaTransporter = new RSATransporter();

		value = rsaTransporter.dencrypt(value);

		String[] values = value.split(",");

		if (!IsPasswordStrong(values[1]))
			throw new WarningException("A senha deve ter mais de 6 caracteres, conter letras, numeros e caracter especial");

		User user = getUser();

		BCryptPasswordEncoder econder = new BCryptPasswordEncoder();

		if (!econder.matches(values[0], user.getPassword()))
			throw new WarningException("Senha atual incorreta");

		user.setHashPassword(new Random().nextInt(9999999));
		user.setPassword(econder.encode(values[1]));

		save(user);

	}

	/**
	 * Valida se o usuario logado pode associar a instancia que foi informada
	 * @param user
	 */
	private void checkSectorForAssociate(User user) {
		// Usuario super admin nao entra na regra
		if (isSa())
			return;

		if (user.getSectors() != null) {
			// Recupera o usuario logado para ver a instancia dele
			final User userLogged = getUser();

			user.getSectors().forEach(sector -> {

				// RULE O user logado tem o setor que associou ao user que esta cadastrando?
				if (!userLogged.getSectors().contains(sector)) {
					throw new RuntimeException("O usuário logado não tem permissao para associar o setor [" + sector.getCode()
							+ "] a esse usuário. Apenas setore proprios podem ser associadas a outros usuários");

				}

			});

		}

	}

	private String templateEmailSendPass(User usr, String value) {
		UserActionCore ua = userActionService.findByIdRecordAndNameObjectAndAction(UserActionCore.class, usr.getCode(), "User",
				"INSERT");

		User usrRec = null;
		if (ua != null && ua.getUserRecordCode() != null)
			usrRec = findById(ua.getUserRecordCode());

		String userRec = (usrRec != null ? usrRec.getName() : "unknow");
		String dateTime = (ua != null && ua.getDateRecord() != null
				? ua.getDateRecord().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
				: "unknow");

		String appLogged = getAppLogged();
		String body = "Você está recebendo uma senha para acesso ao sistema Suite Application na aplicação " + appLogged;
		String body1 = "Após o login altere sua senha na opção 'Alterar Senha' do sistema.";

		String url = parameterService.findByKeyOrEmpty("SYSTEM_URL");

		return "<div>\r\n" +
				UtilsCore.headerTemplateEmail(appLogged) +
				"  <br>\r\n" +
				"  <hr>\r\n" +
				"  <br>\r\n" +
				"  <div style=\"font-size: 13px; text-align: center;\">\r\n" +
				"    <span style=\"font-weight: bold;\">" + body + "</span>\r\n" +
				"  <br>\r\n" +
				"    <span style=\"font-weight: bold;\">" + body1 + "</span>\r\n" +
				"  </div>\r\n" +
				"  <br>\r\n" +
				"  <div style=\"font-size: 13px; text-align: center;\">\r\n" +
				"    <span style=\"font-weight: bold;\">Usuário: </span>" + usr.getUsername() + "<br>\r\n" +
				"    <span style=\"font-weight: bold;\">Senha: </span>" + value + "<br>" +
				"    <span style=\"font-weight: bold;\">Perfil: </span>" + usr.getProfile().getName() + "<br>\r\n" +
				"    <span style=\"font-weight: bold;\">Email: </span>" + usr.getEmail() + "<br>\r\n" +
				"    <span style=\"font-weight: bold;\">Usuário desde: </span> " + dateTime + "<br>\r\n" +
				"    <span style=\"font-weight: bold;\">Criado por: </span>" + userRec + "<br>\r\n<br>\r\n" +

				"    <span style=\"font-weight: bold;\">URL: </span> <a href=\"" + url + "\">" + url + "</a><br><br>" +

				"    <span style=\"font-weight: bold;\">(Observação. Ao copiar a senha, certificar que não esta copiando junto o espaço)</span><br>\r\n"
				+
				"  </div>\r\n" +
				"  <br>\r\n" +
				"  <hr>\r\n" +
				"  <br>\r\n" +
				UtilsCore.footerTemplateEmail(appLogged) +
				"</div>";

	}

	int countUserOnAppLogged(Long codeBusinessUnit) {
		List<User> lstUser = findByBusinessUnit_Code(codeBusinessUnit);

		Sector sectorLogged = getSector();

		int count = 0;
		boolean hasAppSector = false;

		// Percorre todos os users
		for (User user : lstUser) {
			if (!user.getStatus())
				continue;

			List<Sector> lstSector = user.getSectors();

			// Percorre os setores do user
			for (Sector sector : lstSector) {

				// O numero de usuarios eh por aplicacao, a licenca contabiliza o num de user por aplicacao

				// se um dos setore do user for igual a aplicacao do setor logado
				// indica que devo considerar um item do tipo user em uso na licenca
				if (sector.getApplication().getCode().equals(sectorLogged.getApplication().getCode())) {
					hasAppSector = true;
					break;

				}

			}

			if (hasAppSector)
				count++;

			hasAppSector = false;

		}

		return count;

	}

	int countUserOnApp(Long codeBusinessUnit, String appName) {
		List<User> lstUser = findByBusinessUnit_Code(codeBusinessUnit);

		Application app = applicationService.findByName(appName);

		int count = 0;
		boolean hasAppSector = false;

		// Percorre todos os users
		for (User user : lstUser) {
			if (!user.getStatus())
				continue;

			List<Sector> lstSector = user.getSectors();

			// Percorre os setores do user
			for (Sector sector : lstSector) {

				// se um dos setore do user for igual ao setor logado
				// indica que devo considerar um item do tipo user em uso na licenca
				if (sector.getApplication().getCode().equals(app.getCode())) {
					hasAppSector = true;
					break;

				}

			}

			if (hasAppSector)
				count++;

			hasAppSector = false;

		}

		return count;

	}

	/*
	 * < Metodos expostos pelo repository >
	 * 
	 * Apenas o service da entidade pode acessar
	 * o repositorio da entidade.
	 * Os metodos sao expostos para serem usados
	 * por outras classes service que precisar acessar
	 * os dados da entidade
	 */

	public User save(User entity) {
		applyUserHash(entity);

		User useSave = userRepository.save(entity);

		return findById(useSave.getCode());

	}

	public User saveBySystem(User entity) {
		applyUserHash(entity);

		// Define que o save exectuado esta sendo feito pelo sitema, assim o usuario da acao é definido como sistema
		Utils.userSystem = true;
		User useSave = userRepository.save(entity);

		return findById(useSave.getCode());

	}

	private void applyUserHash(User entity) {

		try {
			entity.setUserHash(entity.generateUserHash());

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | NullPointerException e) {
			warn("Falha ao gerar userhash. " + e.getMessage());
			entity.setUserHash("?");

		}

	}

	public User findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "User", id), 1);

		Optional<User> optional = userRepository.findById(id);
		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "User", id), 1);

		User entity = optional.get();
		return entity;

	}

	public User findByIdOrNull(Long id) {
		// Optional<User> opt = userRepository.findById(id);
		// if (opt.isPresent())
		// userRepository.refreshEntity(opt.get());

		if (id == null)
			return null;

		Optional<User> optional = userRepository.findById(id);
		if (!optional.isPresent())
			return null;

		User user = optional.get();
		userRepository.detachEntity(user);

		return user; // User.build(user);

	}

	public User findByUsernameOrNull(String username) {
		if (username == null)
			return null;

		Optional<User> optional = userRepository.findByUsernameIgnoreCase(username);
		if (!optional.isPresent())
			return null;

		User user = optional.get();
		userRepository.detachEntity(user);

		return user; // User.build(user);

	}

	public User findByEmailOrNull(String email) {
		if (email == null)
			return null;

		Optional<User> optional = userRepository.findByEmailIgnoreCase(email);
		if (!optional.isPresent())
			return null;

		User user = optional.get();
		userRepository.detachEntity(user);

		return user; // User.build(user);

	}

	public List<User> findByBusinessUnit_Code(Long code) {
		return userRepository.findByBusinessUnit_Code(code);

	}

	private List<User> filter(UserFilter filter) {
		return userRepository.filter(filter);

	}

	public int countByBusinessUnit_Code(Long code) {
		return userRepository.countByBusinessUnit_Code(code);

	}

	public boolean existsByUsername(String username) {
		return userRepository.existsByUsernameIgnoreCase(username);

	}

	public boolean existsByUsernameAndCodeIsNotIn(String username, Long code) {
		return userRepository.existsByUsernameIgnoreCaseAndCodeIsNotIn(username, code);

	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmailIgnoreCase(email);

	}

	public boolean existsByEmailAndCodeIsNotIn(String email, Long code) {
		return userRepository.existsByEmailIgnoreCaseAndCodeIsNotIn(email, code);

	}

	public boolean existsByProfile(Profile profile) {
		return userRepository.existsByProfile(profile);
	}

	public String findNameByCode(Long code) {
		return userRepository.findNameByCode(code);
	}

	// public static void main(String[] args) {
	//
	// // Pattern p = Pattern.compile("\\d+");
	// // Matcher m = p.matcher("string1234more567string890");
	// // while(m.find()) {
	// // System.out.println(m.group());
	// // }
	//
	// // System.out.println(new UserService().IsPasswordStrong("AaA1AA"));
	//
	// String password = "senha9@";
	//
	// }

}
