package com.suite.core.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.suite.app.base.Service;
import com.suite.app.exception.WarningException;
import com.suite.app.util.DateTimeUtils;
import com.suite.app.util.StringUtils;
import com.suite.app.util.Utils;
import com.suite.core.base.EnumCore.Lang;
import com.suite.core.dto.MenuItemDTO;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.BusinessUnitParameter;
import com.suite.core.model.Language;
import com.suite.core.model.Profile;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.service.ApplicationService;
import com.suite.core.service.BusinessUnitService;
import com.suite.core.service.DataListService;
import com.suite.core.service.DataServiceService;
import com.suite.core.service.LanguageService;
import com.suite.core.service.LicenseService;
import com.suite.core.service.NotifyService;
import com.suite.core.service.ParameterService;
import com.suite.core.service.PermissionService;
import com.suite.core.service.ProfileService;
import com.suite.core.service.SectorService;
import com.suite.core.service.UserActionCoreService;
import com.suite.core.service.UserGroupService;
import com.suite.core.service.UserService;

/**
 * Classe base Service do Modulo Core
 * Estende a classe base Service
 * @author Wenceslau
 *
 */
@org.springframework.stereotype.Service
public class ServiceCore extends Service {

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected LanguageService languageService;

	@Autowired
	protected ParameterService parameterService;

	@Autowired
	protected DataListService dataListService;

	@Autowired
	protected ApplicationService applicationService;

	@Autowired
	protected BusinessUnitService businessUnitService;

	@Autowired
	protected DataServiceService dataServiceService;

	@Autowired
	protected SectorService sectorService;

	@Autowired
	protected ProfileService profileService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected UserGroupService groupService;

	@Autowired
	protected NotifyService notifyService;

	@Autowired
	protected LicenseService licenseService;

	@Autowired
	protected UserGroupService userGroupService;

	@Autowired
	protected UserActionCoreService userActionService;

	protected final String SYS_NOME_ATRIBUTO_ROTULO_OBJETO_EXTERNO = "SYS_NOME_ATRIBUTO_ROTULO_OBJETO_EXTERNO";

	/*
	 * Strings de altertas
	 */
	protected final String msgResourceNotFound = "msg_nenhum_recurso_[%s]_econtrado_p_o_c_[%s]";
	protected final String msgValidty1 = "msg_a_data_de_f_d_v_d_s_m_o_i_a_d_d_i_d_v";

	/**
	 * Retorna message da chave informada no idiona do contexto
	 * @param key
	 * @return
	 */
	private String translate(String key) {
		String lang = Utils.getContextLanguage();
		String value = languageService.findValueByLangAndKey(lang, key);

		if (value == null || value.isEmpty())
			value = key;

		return value;

	}

	/**
	 * Formata objeto LocalDate em string dd/MM/yyyy
	 * @param date
	 * @return
	 */
	private String formatDt(Temporal date) {
		if (date instanceof LocalDate)
			return DateTimeUtils.getStringFromTemporal(date, "dd/MM/yyyy");

		if (date instanceof LocalDateTime)
			return DateTimeUtils.getStringFromTemporal(date, "dd/MM/yyyy HH:mm:ss");

		return "unknown";

	}

	// /**
	// * Verifica se data esta entre
	// * @param date
	// * @param dateStart
	// * @param dateEnd
	// * @return
	// */
	// private boolean between(LocalDate date, LocalDate dateStart, LocalDate dateEnd) {
	// if (dateEnd == null)
	// dateEnd = LocalDate.MAX;
	//
	// if (date == null)
	// date = LocalDate.MAX;
	//
	// if (date != null && dateStart != null && dateEnd != null) {
	//
	// if (date.isAfter(dateStart) && date.isBefore(dateEnd))
	// return true;
	//
	// if (date.isEqual(dateStart) || date.isEqual(dateEnd))
	// return true;
	//
	// }
	//
	// return false;
	//
	// }
	//
	/**
	 * Verifica se data esta entre
	 * @param date
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	private boolean between(LocalDateTime date, LocalDateTime dateStart, LocalDateTime dateEnd) {
		if (dateEnd == null)
			dateEnd = LocalDateTime.MAX;

		if (date == null)
			date = LocalDateTime.MAX;

		if (date != null && dateStart != null && dateEnd != null) {

			if (date.isAfter(dateStart) && date.isBefore(dateEnd))
				return true;

			if (date.isEqual(dateStart) || date.isEqual(dateEnd))
				return true;

		}

		return false;

	}

	// /**
	// * Processo generico de analise de vigencia, O algoritmo valida a vigencia a
	// * seri inserida comparando com uma vigencia que ja existe do registro que tem a
	// * mesma chave As msg lanacada como excecao sao genericas e pode ser usadas para
	// * qualquer objeto
	// * @param idRecordoToSave
	// * @param ldIniValididyToSave
	// * @param ldEndValidityToSave
	// * @param idRecord
	// * @param ldIniValidity
	// * @param ldEndValidity
	// */
	// protected void analyzeValidity(Long idRecordoToSave, LocalDate ldIniValididyToSave, LocalDate ldEndValidityToSave,
	// Long idRecord, LocalDate ldIniValidity, LocalDate ldEndValidity) {
	// // Já existe um registro com a mesma chave com a vigência em aberto, para iniciar outro é necessário fechar a anterior
	// String msgValidty2 = ("msg_ja_existe_um_r_c_a_m_c_e_c_a_v_e_a_p_i_o_e_n_f_a_a");
	// String msgValidty3 = ("msg_a_data_inicio_[%s]_e_e_o_p_[%s_%s]_d_r_i_[%s]");
	// String msgValidty4 = ("msg_a_data_fim_[%s]_e_e_o_p_[%s_%s]_d_r_i_[%s]");
	// String msgValidty5 = ("msg_o_intervalo_solicitado_e_a_a_u_i_j_f_p_e_s_a_d_f_d_v_e_o");
	// String msgValidty6 = ("msg_o_periodo_[%s_%s]_sobrepoe_a_d_i_[%s]_d_r_i_[%s]");
	// String msgValidty7 = ("msg_o_periodo_[%s_%s]_sobrepoe_a_d_f_[%s]_d_r_i_[%s]");
	// String msgValidty8 = ("msg_ja_existe_um_r_d_m_c_c_u_v_i_p_a_i_d_v_d_r_i_a_c_e_o_f_a_d_v");
	//
	// // Verifica se a vigencia final a salvar eh nula, e se eh insert
	// if (ldEndValidityToSave == null && idRecordoToSave == null) {
	//
	// // verifica se o inicio da vigencia a ser salva é anterior ao inicio da vigencia ja existente
	// if (ldIniValididyToSave.isBefore(ldIniValidity))
	// throw new WarningException(msgValidty5);
	//
	// // verifica se o final da vigencia do registro atual eh nula, devolve a execao 101
	// if (ldEndValidity == null)
	// throw new WarningException(101, msgValidty2);
	//
	// }
	//
	// // Valida o intervalo da vigencia apenas para os registros diferente do que
	// // estou inserindo ou atualizando. Se for insert o idRecordoToSave eh null logo
	// // entra no if se for update so pode se nao form edicao do proprio registro
	// if (!idRecord.equals(idRecordoToSave)) {
	//
	// // verifica se o inicio da vigencia a ser salva é anterior ao inicio da vigencia ja existente e se o processo nao eh
	// // insert
	// if (ldIniValididyToSave.isBefore(ldIniValidity) && idRecordoToSave != null
	// && ldEndValidityToSave == null)
	// throw new WarningException(msgValidty8);
	//
	// // Verifica se os dois registros estao com data de fim de vigencia em aberto,
	// // apenas 1 eh permitido
	// if (ldEndValidityToSave == null && ldEndValidity == null)
	// throw new WarningException(101, msgValidty2);
	//
	// // Vaida se a data de inicio esta entre o periodo de um registro com a mesma
	// // chave
	// boolean check = between(ldIniValididyToSave, ldIniValidity, ldEndValidity);
	// if (check)
	// throw new WarningException(formatTranslate(msgValidty3, formatDt(ldIniValididyToSave), formatDt(ldIniValidity),
	// ldEndValidity != null ? formatDt(ldEndValidity) : "-", idRecord));
	//
	// // Vaida se a data fim esta entre o periodo de um registro com a mesma chave
	// check = between(ldEndValidityToSave, ldIniValidity, ldEndValidity);
	// if (check)
	// throw new WarningException(formatTranslate(msgValidty4, formatDt(ldEndValidityToSave), formatDt(ldIniValidity),
	// formatDt(ldEndValidity), idRecord));
	//
	// // Valida se a data ini e antes a da data de inicio de im periodo ja fechado
	// if (ldIniValididyToSave.isBefore(ldIniValidity) && ldEndValidityToSave == null)
	// throw new WarningException(msgValidty5);
	//
	// if (ldEndValidityToSave != null) {
	//
	// // valida se a data de inicio de um registro nao esta no meio do periodo a ser
	// // salvo
	// check = between(ldIniValidity, ldIniValididyToSave, ldEndValidityToSave);
	// if (check)
	// throw new WarningException(formatTranslate(msgValidty6, formatDt(ldIniValididyToSave),
	// formatDt(ldEndValidityToSave), formatDt(ldIniValidity), idRecord));
	//
	// // valida se a data fim de um registro nao esta no meio do periodo a ser salvo
	// check = between(ldEndValidity, ldIniValididyToSave, ldEndValidityToSave);
	// if (check)
	// throw new WarningException(formatTranslate(msgValidty7, formatDt(ldIniValididyToSave),
	// formatDt(ldEndValidityToSave), formatDt(ldEndValidity), idRecord));
	//
	// }
	//
	// }
	//
	// }

	/**
	 * Processo generico de analise de vigencia, O algoritmo valida a vigencia a
	 * seri inserida comparando com uma vigencia que ja existe do registro que tem a
	 * mesma chave As msg lanacada como excecao sao genericas e pode ser usadas para
	 * qualquer objeto
	 * @param idRecordoToSave
	 * @param ldIniValididyToSave
	 * @param ldEndValidityToSave
	 * @param idRecord
	 * @param ldIniValidity
	 * @param ldEndValidity
	 */
	protected void analyzeValidity(Long idRecordoToSave, LocalDateTime ldIniValididyToSave, LocalDateTime ldEndValidityToSave,
			Long idRecord, LocalDateTime ldIniValidity, LocalDateTime ldEndValidity) {
		// Já existe um registro com a mesma chave com a vigência em aberto, para iniciar outro é necessário fechar a anterior
		String msgValidty2 = ("msg_ja_existe_um_r_c_a_m_c_e_c_a_v_e_a_p_i_o_e_n_f_a_a");
		String msgValidty3 = ("msg_a_data_inicio_[%s]_e_e_o_p_[%s_%s]_d_r_i_[%s]");
		String msgValidty4 = ("msg_a_data_fim_[%s]_e_e_o_p_[%s_%s]_d_r_i_[%s]");
		String msgValidty5 = ("msg_o_intervalo_solicitado_e_a_a_u_i_j_f_p_e_s_a_d_f_d_v_e_o");
		String msgValidty6 = ("msg_o_periodo_[%s_%s]_sobrepoe_a_d_i_[%s]_d_r_i_[%s]");
		String msgValidty7 = ("msg_o_periodo_[%s_%s]_sobrepoe_a_d_f_[%s]_d_r_i_[%s]");
		String msgValidty8 = ("msg_ja_existe_um_r_d_m_c_c_u_v_i_p_a_i_d_v_d_r_i_a_c_e_o_f_a_d_v");

		// Verifica se a vigencia final a salvar eh nula, e se eh insert
		if (ldEndValidityToSave == null && idRecordoToSave == null) {

			// verifica se o inicio da vigencia a ser salva é anterior ao inicio da vigencia ja existente
			if (ldIniValididyToSave.isBefore(ldIniValidity))
				throw new WarningException(msgValidty5);

			// verifica se o final da vigencia do registro atual eh nula, devolve a execao 101
			if (ldEndValidity == null)
				throw new WarningException(101, msgValidty2);

		}

		// Valida o intervalo da vigencia apenas para os registros diferente do que
		// estou inserindo ou atualizando. Se for insert o idRecordoToSave eh null logo
		// entra no if se for update so pode se nao form edicao do proprio registro
		if (!idRecord.equals(idRecordoToSave)) {

			// verifica se o inicio da vigencia a ser salva é anterior ao inicio da vigencia ja existente e se o processo nao eh
			// insert
			if (ldIniValididyToSave.isBefore(ldIniValidity) && idRecordoToSave != null
					&& ldEndValidityToSave == null)
				throw new WarningException(msgValidty8);

			// Verifica se os dois registros estao com data de fim de vigencia em aberto,
			// apenas 1 eh permitido
			if (ldEndValidityToSave == null && ldEndValidity == null)
				throw new WarningException(101, msgValidty2);

			// Vaida se a data de inicio esta entre o periodo de um registro com a mesma
			// chave
			boolean check = between(ldIniValididyToSave, ldIniValidity, ldEndValidity);
			if (check)
				throw new WarningException(formatTranslate(msgValidty3, formatDt(ldIniValididyToSave), formatDt(ldIniValidity),
						ldEndValidity != null ? formatDt(ldEndValidity) : "-", idRecord));

			// Vaida se a data fim esta entre o periodo de um registro com a mesma chave
			check = between(ldEndValidityToSave, ldIniValidity, ldEndValidity);
			if (check)
				throw new WarningException(formatTranslate(msgValidty4, formatDt(ldEndValidityToSave), formatDt(ldIniValidity),
						formatDt(ldEndValidity), idRecord));

			// Valida se a data ini e antes a da data de inicio de im periodo ja fechado
			if (ldIniValididyToSave.isBefore(ldIniValidity) && ldEndValidityToSave == null)
				throw new WarningException(msgValidty5);

			if (ldEndValidityToSave != null) {

				// valida se a data de inicio de um registro nao esta no meio do periodo a ser
				// salvo
				check = between(ldIniValidity, ldIniValididyToSave, ldEndValidityToSave);
				if (check)
					throw new WarningException(formatTranslate(msgValidty6, formatDt(ldIniValididyToSave),
							formatDt(ldEndValidityToSave), formatDt(ldIniValidity), idRecord));

				// valida se a data fim de um registro nao esta no meio do periodo a ser salvo
				check = between(ldEndValidity, ldIniValididyToSave, ldEndValidityToSave);
				if (check)
					throw new WarningException(formatTranslate(msgValidty7, formatDt(ldIniValididyToSave),
							formatDt(ldEndValidityToSave), formatDt(ldEndValidity), idRecord));

			}

		}

	}

	/**
	 * Se a exception for warning, faz o cast e relanca Se nao, relanca coom Runtime
	 * adicionado a msg e a propria exception
	 * @param e
	 */
	protected void throwException(Throwable e, String msg) {
		if (e instanceof WarningException)
			throw (WarningException) e;

		if (e instanceof NullPointerException)
			msg += " (Algum objeto não foi instanciado)";

		throw new RuntimeException(msg, e);

	}

	/**
	 * Usado para distinct em listas
	 * @param <T>
	 * @param keyExtractor
	 * @return
	 */
	protected <T> Predicate<T> distinctByProperties(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));

	}

	/**
	 * Retorna um objeto User do repositorio a partir do UserCore do contexto
	 * @return
	 */
	public User getUser() {

		try {

			return userService.findById(getCodeUserContext());

		} catch (EmptyResultDataAccessException e) {

			throw new RuntimeException("msg_nao_foi_encontrado_u_n_c_d_s", e);

		}

	}

	/**
	 * Retorna um objeto Sector do repositorio a partir do SectorCore do contexto
	 * @return
	 */
	public Sector getSector() {

		try {

			return sectorService.findById(getCodeSectorContext());

		} catch (EmptyResultDataAccessException e) {

			throw new RuntimeException("msg_nao_foi_encontrado_s_n_c_d_s", e);

		}

	}

	/**
	 * Retorna um objeto Sector do repositorio a partir do SectorCore do contexto
	 * @return
	 */
	protected String getAppLogged() {

		try {

			return getSector().getApplication().getName();

		} catch (Exception e) {

			return "CORE";

		}

	}

	/**
	 * Valida se o user do contexto eh um SA
	 * @return
	 */
	protected boolean isSa() {
		return isSa(getUser().getProfile());

	}

	/**
	 * Valida se o user do contexto eh um SA olhando seu perfil
	 * @return
	 */
	protected boolean isSa(Profile profile) {
		if (profile.getType() != null && profile.getType().intValue() == 1)
			return true;

		return false;

	}

	/**
	 * Valida se o user do contexto eh um UN Admin
	 * @return
	 */
	protected boolean isUa() {
		return isUa(getUser().getProfile());

	}

	/**
	 * Valida se o user do contexto eh um UN Admin olhando seu perfil
	 * @return
	 */
	protected boolean isUa(Profile profile) {
		if (profile.getType() != null && profile.getType().intValue() == 2)
			return true;

		return false;

	}

	/**
	 * Valida se o user do contexto eh um Admin
	 * @return
	 */
	protected boolean isAdm() {
		return isAdm(getUser().getProfile());

	}

	/**
	 * Valida se o user do contexto eh um Admin olhando seu perfil
	 * @return
	 */
	protected boolean isAdm(Profile profile) {
		if (profile.getType() != null && profile.getType().intValue() == 3)
			return true;

		return false;

	}

	/**
	 * 
	 * @param role
	 * @return
	 */
	protected boolean hasAuthority(String role) {
		// get security context from thread local
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null)
			return false;

		Authentication authentication = context.getAuthentication();
		if (authentication == null)
			return false;

		for (GrantedAuthority auth : authentication.getAuthorities()) {

			if (role.equals(auth.getAuthority()))
				return true;

		}

		return false;

	}

	/**
	 * Retorna o parametro de acordo com o BU, se a BU for null ele pega a do setor logado
	 * se nao encontrar o parametro ele usa o a BU default.
	 * @param key
	 * @return
	 */
	protected String valueParameterBusinessUnit(String key, BusinessUnit bu) {
		if (bu == null)
			bu = getSector().getBusinessUnit();

		BusinessUnitParameter buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst()
				.orElse(null);

		if (buPar == null) {

			bu = businessUnitService.findByName("DEFAULT");
			buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);

		}

		if (buPar == null)
			throw new WarningException(
					"Os parametros de definição dos nomes/descrição das colunas de attributo com a chave [" + key + "] não foram encontrados");

		return buPar.getValue();

	}

	/**
	 * Aplica a linguagem aos labels do menu
	 * @param lang
	 * @param itens
	 */
	protected void applyLanguage(Lang lang, List<MenuItemDTO> itens) {

		for (MenuItemDTO mi : itens) {

			// Verifica se tem label a ser traduzido
			if (mi.getLabel() != null && !mi.getLabel().isEmpty()) {

				String key = StringUtils.deAccent(mi.getLabel());
				key = "lbl_" + StringUtils.lowerAndUnderscor(key);
				mi.setLabel(key);

				String value = defineValueLang(lang, key);

				if (value != null)
					mi.setLabel(value);

			}

			// Verifica se tem label a ser traduzido
			if (mi.getTitle() != null && !mi.getTitle().isEmpty()) {

				String key = StringUtils.deAccent(mi.getTitle());
				key = "lbl_" + StringUtils.lowerAndUnderscor(key);
				mi.setTitle(key);

				String value = defineValueLang(lang, key);

				if (value != null)
					mi.setTitle(value);

			}

			// Aplica a linguagem nos items do menu, recursivamente
			if (mi.getItems() != null && !mi.getItems().isEmpty())
				applyLanguage(lang, mi.getItems());

		}

	}

	/**
	 * @param lang
	 * @param value
	 * @param key
	 * @return
	 */
	protected String defineValueLang(Lang lang, String key) {
		String value = null;

		Language language = languageService.findByKey(key);

		if (language != null) {

			if (lang.equals(Lang.PT))
				value = language.getPortugues();
			else if (lang.equals(Lang.EN))
				value = language.getEnglish();
			else if (lang.equals(Lang.ES))
				value = language.getSpanish();

		}

		return value;

	}

	/**
	 * Envia uma notificacao usando o NotifyService
	 * @param message
	 */
	protected void notify(String message) {
		notifyService.notify(message);

	}

	/**
	 * Envia uma notificacao tipada usando o NotifyService
	 * @param message
	 * @param type
	 */
	protected void notifyUser(String message, String type) {
		notifyService.notifyUser(message, type);

	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String valueParameterBusinessUnitOrNull(String key, BusinessUnit bu) {
		if (bu == null)
			bu = getSector().getBusinessUnit();

		BusinessUnitParameter buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst()
				.orElse(null);

		if (buPar == null) {

			bu = businessUnitService.findByName("DEFAULT");
			buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);

		}

		if (buPar == null)
			return null;

		return buPar.getValue();

	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String valueParameterBusinessUnitOrDefaultl(String key, BusinessUnit bu, String _default) {
		if (bu == null)
			bu = getSector().getBusinessUnit();

		BusinessUnitParameter buPar = bu.getBusinessUnitParameters().stream().filter(x -> x.getKey().equals(key)).findFirst()
				.orElse(null);

		if (buPar == null)
			return _default;

		return buPar.getValue();

	}

	/**
	 * Procura o valor da chave e formata se possuir parametros
	 * @param key
	 * @param args
	 * @return
	 */
	public String formatTranslate(String key, Object... args) {
		if (key == null)
			return key;

		// Se for diferente de msg_ e lbl_ ja esta traduzida
		if (!key.startsWith("msg_") && !key.startsWith("lbl_"))
			return key;

		String value = translate(key);

		if (args.length != 0) {

			try {

				value = String.format(value, args);

			} catch (Exception e) {

				warn("Erro ao realizar formatacao da string [" + value + "] " + e.getMessage());

			}

		}

		return value;

	}

}
