
package com.suite.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.suite.app.util.StringUtils;
import com.suite.core.base.ServiceCore;
import com.suite.core.dto.ChartBarDTO;
import com.suite.core.dto.ChartBarDataSetDTO;
import com.suite.core.dto.LogonDTO;
import com.suite.core.model.User;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.repository.UserLogonHistoryRepository;
import com.suite.core.repository.filter.UserLoginHistoryFilter;

/**
 * Service responsavel por manipular os dados recurso UserLogonHistory
 * @author Wenceslau
 *
 */
@Service
public class UserLogonHistoryService extends ServiceCore {

	@Autowired
	private UserLogonHistoryRepository userLogonHistoryRepository;

	/**
	 * Retorna lista de objetos filtrado paginavel
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public Page<UserLogonHistory> listByFilter(UserLoginHistoryFilter filter, Pageable pageable) {
		filter.setDeviceIn("MOBILE,BROWSER");
		applyRuleFilter(filter);

		return userLogonHistoryRepository.filter(filter, pageable);

	}

	/**
	 * Retorna lista de objetos filtrado
	 * @param filter
	 * @return
	 */
	public List<UserLogonHistory> listByFilter(UserLoginHistoryFilter filter) {
		filter.setDeviceIn("MOBILE,BROWSER");
		applyRuleFilter(filter);

		return userLogonHistoryRepository.filter(filter);

	}

	/**
	 * Retorna objeto Chart para UI
	 * @return
	 */
	public ChartBarDTO chartLogonByDay(String device) {
		Long codeBusinessUnit = null;

		// Para o setor default traz de todas as BU, se nao tra da BU do setor logado
		if (!isDefaultSector())
			codeBusinessUnit = getSector().getBusinessUnit().getCode();

		// Recuper o historico de login da bu do usuario
		Integer days = 15;
		List<LogonDTO> list = userLogonHistoryRepository.listLogonByDay(codeBusinessUnit, days, device);

		// List labels para o grupo de barra do grafico
		List<LocalDate> ldates = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		// Map com o nome de cada barra do grupo e uma lista de valor para cada barra
		Map<String, List<Long>> map = new HashMap<>();

		for (int i = (days - 1); i >= 0; i--) {
			LocalDate ld = LocalDate.now().plusDays((i * -1));
			ldates.add(ld);
			labels.add(ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		}

		// Percorre a lis de logon e recupera o label que sao os dias
		// E add no map o nome o nome de cada barra, e na lista da barra o count do dia
		for (LogonDTO logonTO : list) {
			if (!map.containsKey(logonTO.getStatus()))
				map.put(logonTO.getStatus(), new ArrayList<>());

		}

		// Percorre a lista de datas para adicionar na lista da barra o valor do cout
		for (LocalDate localDate : ldates) {
			// Recupera do retorno da base referente ao dia do laco
			List<LogonDTO> lst = list.stream().filter(l -> l.getDateLogon().equals(localDate)).collect(Collectors.toList());

			// percorre o map
			for (String key : map.keySet()) {
				// localiza na lista de registros encontrado para o dia do laco, usando o status
				LogonDTO lg = lst.stream().filter(l -> l.getStatus().equals(key)).findFirst().orElse(null);

				// Se tem usa o count da base, se nao eh 0
				if (lg != null)
					map.get(key).add(lg.getCount());
				else
					map.get(key).add(0L);

			}

		}

		// Lista de dataset
		List<ChartBarDataSetDTO> datasets = new ArrayList<>();

		// para cada item do map, tera um dataset, um data set eh uma barra do grupo
		for (String key : map.keySet()) {
			ChartBarDataSetDTO clds = new ChartBarDataSetDTO();
			clds.setData(map.get(key));

			if (key.equals("SUCCESS")) {
				clds.setLabel("Sucesso");
				clds.setBackgroundColor("green");
				clds.setOrder(1);

			} else if (key.equals("INVALID_TOKEN")) {
				clds.setLabel("Sessão expirada");
				clds.setBackgroundColor("blue");
				clds.setOrder(2);

			} else if (key.equals("INVALID_PASSWORD")) {
				clds.setLabel("Senha Inválida");
				clds.setBackgroundColor("red");
				clds.setOrder(3);

			} else if (key.equals("INVALID_USER")) {
				clds.setLabel("Usuário Inválido");
				clds.setBackgroundColor("orange");
				clds.setOrder(4);

			} else if (key.equals("INACTIVE_USER")) {
				clds.setLabel("Usuário Inativo");
				clds.setBackgroundColor("yellow");
				clds.setOrder(5);

			} else {
				clds.setLabel("Desconhecido");
				clds.setBackgroundColor("grey");
				clds.setOrder(6);

			}

			clds.setBorderColor(clds.getBackgroundColor());
			datasets.add(clds);

		}

		Collections.sort(datasets, (x1, x2) -> x1.getOrder().compareTo(x2.getOrder()));

		// Objeto que contem a lista de labels e a lista de data set
		ChartBarDTO chartLogon = new ChartBarDTO();
		chartLogon.setLabels(labels);
		chartLogon.setDatasets(datasets);

		return chartLogon;

	}

	/**
	 * Cria e salva o objeto UserLogonHistory
	 * @param userLogon
	 * @param statusLogon
	 */
	public void auditLogon(String userLogon, String statusLogon, String userAgent, String codeSector) {
		info("Audit Logon. User: " + userLogon + ". Status: " + statusLogon);

		String ip = "Error to identify";

		// Recupera o IP do request do logon
		try {
			HttpServletRequest request;
			request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			ip = request.getRemoteAddr();

		} catch (Exception e) {}

		// Junto com o usuario recebido do token, vem o setor que ele logou
		String[] usr = userLogon.split(";");

		// Pega somente o usuario
		if (usr.length > 0)
			userLogon = usr[0];

		// Cria o objeto e seta seus dados
		UserLogonHistory userLogonHistory = (new UserLogonHistory());
		userLogonHistory.setDateRecord(LocalDateTime.now());
		userLogonHistory.setDateLogon(LocalDate.now());
		userLogonHistory.setUserLogon(userLogon);
		userLogonHistory.setIpAddress(ip);
		userLogonHistory.setDevice(identifyDevice(userAgent));
		userLogonHistory.setBrowser(identifyBrowser(userAgent));

		if (!StringUtils.isEmpty(codeSector))
			userLogonHistory.setCodeSector(Long.parseLong(codeSector));

		// Verifica se o status eh um erro de token
		if ("access-token".equals(userLogon))
			userLogonHistory.setStatusLogon("INVALID_TOKEN");
		else {
			// Recupera o o usuario da base de acordo com username
			User user = userService.findByUsernameOrNull(userLogon);
			userLogonHistory.setUserRecord(user);

			if ("SUCCESS".equals(statusLogon)) {
				userLogonHistory.setStatusLogon("SUCCESS");

				if (user != null) {

					if (user.getCountLocked() != null && user.getCountLocked() != 0) {
						user.setCountLocked(0);
						user.setLocked(false);
						userService.save(user);

					}

				}

			} else {

				// Se existe user a senha eh invalida se nao existe eh o user invalido
				if (user == null)
					userLogonHistory.setStatusLogon("INVALID_USER");
				else {
					Integer count = user.getCountLocked() != null ? user.getCountLocked() + 1 : 1;
					user.setCountLocked(count);
					if (count > 2)
						user.setLocked(true);
					userService.save(user);

					if (!user.getStatus())
						userLogonHistory.setStatusLogon("INACTIVE_USER");
					else
						userLogonHistory.setStatusLogon("INVALID_PASSWORD");

				}

			}

		}

		save(userLogonHistory);

	}

	private String identifyDevice(String value) {
		if (value == null)
			return "UNKNOWN";

		if (value.toLowerCase().contains("mobile"))
			return "MOBILE";

		if (value.toLowerCase().contains("mozilla"))
			return "BROWSER";

		return "API";

	}

	private String identifyBrowser(String value) {
		// PostmanRuntime/7.26.3
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 = BRAVE
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.193 Safari/537.36
		// Edg/86.0.622.68 - EDGE
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36
		// OPR/72.0.3815.320 - OPERA
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.193 Safari/537.36 - CHROME
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:82.0) Gecko/20100101 Firefox/82.0 - FIREFOX
		// Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko - IE
		// Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2 - SAFARI

		if (value == null)
			return "UNKNOWN";

		if (value.toLowerCase().contains("postman"))
			return "POSTMAN";

		if (value.toLowerCase().contains("java"))
			return "APIJAVA";

		if (value.toLowerCase().contains("chrome")) {
			String vs = "";
			int idx = value.toLowerCase().indexOf("chrome");

			if (idx > 0) {
				String vrl = value.substring(idx, value.length());
				String[] arr = vrl.split("/");
				if (arr.length > 1)
					vs = "/" + arr[1].split(" ")[0];

			}

			if (value.toLowerCase().contains("edg") || value.toLowerCase().contains("edge"))
				return "EDGE" + vs;

			if (value.toLowerCase().contains("opr") || value.toLowerCase().contains("opera"))
				return "OPERA" + vs;

			return "CHROME" + vs;

		} else if (value.toLowerCase().contains("firefox"))
			return "FIREFOX";
		else if (value.toLowerCase().contains("safari"))
			return "SAFARI";
		else if (value.toLowerCase().contains("trident"))
			return "IE";

		return "APIDOTNET";

	}

	/**
	 * @param filter
	 */
	private void applyRuleFilter(UserLoginHistoryFilter filter) {
		// Traz dados apenas dos logons de usuarios de tipo maior que o do usuario
		// 1- SADM, 2- UNADM, 3- ADM, 4- Nativo, 5- User
		// Perfil 1 SADM ver todos, perfil 3 ADM ver apenas do seu perfil pra cima
		filter.setTypeProfile(getUser().getProfile().getType());

		// Se nao for o setor default filtra os user pela BU do Setos
		if (!isDefaultSector())
			filter.setCodBusinessUnit(getSector().getBusinessUnit().getCode());

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

	public UserLogonHistory save(UserLogonHistory entity) {
		UserLogonHistory ulh = userLogonHistoryRepository.save(entity);

		return findById(ulh.getCode());

	}

	public UserLogonHistory findById(Long id) {
		if (id == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "UserLogonHistory", id), 1);

		Optional<UserLogonHistory> optional = userLogonHistoryRepository.findById(id);

		if (!optional.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "UserLogonHistory", id), 1);

		UserLogonHistory entity = optional.get();

		return entity; // UserLogonHistory.build(optional.get());

	}

	public List<UserLogonHistory> findAllTop2ByUserLogonAndStatusLogonOrderByDateRecordDesc(String userLogon, String statusLogon) {
		return (userLogonHistoryRepository.findAllTop2ByUserLogonAndStatusLogonOrderByDateRecordDesc(userLogon, statusLogon));

	}

	public List<UserLogonHistory> findAllTop2ByUserLogonAndStatusLogonOrderByCodeDesc(String userLogon, String statusLogon) {
		return userLogonHistoryRepository.findAllTop2ByUserLogonAndStatusLogonOrderByCodeDesc(userLogon, statusLogon);

	}

	public List<UserLogonHistory> findAllByDateLogon(LocalDate dateLogon) {
		return userLogonHistoryRepository.findAllByDateLogon(dateLogon);
	}

}
