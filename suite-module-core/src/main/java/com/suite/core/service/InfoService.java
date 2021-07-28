
package com.suite.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.app.dto.BoolDTO;
import com.suite.app.dto.InfoLoggedDTO;
import com.suite.app.dto.LoggedDTO;
import com.suite.core.base.EnumCore.Lang;
import com.suite.core.base.ServiceCore;
import com.suite.core.dao.DataDao;
import com.suite.core.dto.InfoConnectionDTO;
import com.suite.core.dto.InfoEnviormentDTO;
import com.suite.core.dto.InfoModelDTO;
import com.suite.core.dto.InfoSectorDTO;
import com.suite.core.dto.InfoStartDTO;
import com.suite.core.dto.InfoSystemDTO;
import com.suite.core.dto.InfoUserDTO;
import com.suite.core.model.Parameter;
import com.suite.core.model.UserActionCore;
import com.suite.core.model.resume.LanguageResume;
import com.suite.core.model.resume.PermissionResume;
import com.suite.core.model.Application;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.Permission;
import com.suite.core.model.Sector;
import com.suite.core.model.UserLogonHistory;
import com.suite.core.model.User;
import com.suite.security.cryptography.RSACryptography;
import com.suite.security.licence.AppLicence;
import com.suite.security.licence.Licence;

/**
 * Service responsavel por prover os dados do sistema para informacao
 * @author Wenceslau
 *
 */
@Service
public class InfoService extends ServiceCore {

	@Autowired
	private DataDao dataDao;

	/*
	 * Objeto que armazena os usuarios logados no sistema
	 * baseado na conexao websoket.
	 * O objeto eh injetado pelo spring e os dados sao atulizados na
	 * classe WebSocketEventListener no evento de conexao e desconexao
	 */
	@Autowired
	private InfoLoggedDTO infoLoggedDTO;

	@Autowired
	private UserLogonHistoryService userLogonHistoryService;

	private String version = "4.0.2.9";

	/**
	 * Retorna colecao de usuarios logados no sistema
	 * @return
	 */
	public Collection<LoggedDTO> logged() {

		try {
			Collection<LoggedDTO> list = infoLoggedDTO.getUsersLogged();

			System.out.println(list);

			if (isDefaultSector())
				return list;

			return list.stream().filter(x -> x.getCodSector().equals(getCodeSectorContext())).collect(Collectors.toList());

		} catch (Exception e) {
			error("Falha ao recuperar usuarios logados no sistema", e);

		}

		return Collections.emptyList();

	}

	/**
	 * Retorna info do usuario
	 * @param code
	 * @return
	 */
	public InfoUserDTO infoUser(Long code) {
		User usr = userService.findById(code);

		UserActionCore ua = userActionService.findByIdRecordAndNameObjectAndAction(UserActionCore.class, code, "User", "INSERT");

		// CHANGE: TROCADO PELO SELECT DO JPA
		// UserLoginHistoryFilter filter = new UserLoginHistoryFilter();
		// filter.setUserLogon(usr.getUsername());
		// filter.setStatusLogon("SUCCESS");
		// filter.setOrderBy("dateRecord");
		// filter.setDesc(true);
		// filter.setMaxResults(2);

		List<UserLogonHistory> list = userLogonHistoryService
				.findAllTop2ByUserLogonAndStatusLogonOrderByCodeDesc(usr.getUsername(), "SUCCESS");

		UserLogonHistory ulh = null;

		if (!list.isEmpty()) {
			if (list.size() == 1)
				ulh = list.get(0);
			else
				ulh = list.get(1);

		}

		InfoUserDTO info = new InfoUserDTO();
		info.setCode(usr.getCode());
		info.setName(usr.getName());
		info.setUsername(usr.getUsername());
		info.setEmail(usr.getEmail());
		info.setProfile(usr.getProfile().getName());
		info.setDateStart(ua != null ? ua.getDateRecord() : null);
		info.setLastAccess(ulh != null ? ulh.getDateRecord() : null);

		return info;

	}

	/**
	 * Retorna info do ambiente
	 * @param code
	 * @return
	 */
	public InfoEnviormentDTO infoEnviorment(Long code) {
		// DataDao dataDao = new DataDao(true);

		try {
			Sector sector = getSector();
			InfoEnviormentDTO info = new InfoEnviormentDTO();
			BusinessUnit bu = (sector.getBusinessUnit() != null ? sector.getBusinessUnit() : getUser().getBusinessUnit());
			info.setBusiessUnitCode(bu.getCode());
			info.setBusiessUnit(bu.getName());
			info.setSector(sector.getName());
			info.setDataBaseSector(sector.getNameExternalDatabase());
			info.setHasDatabaseSector(sector.getRequiredDb());
			info.setMaxFileUploadBytes(5000000);

			Parameter parMaxFileUpload = parameterService.findByKeyOrNull("MAX_FILE_UPLOAD");

			if (parMaxFileUpload != null)
				info.setMaxFileUploadBytes(Integer.parseInt(parMaxFileUpload.getValue()));

			if (sector.getDataService() != null) {
				info.setDataService(sector.getDataService().getName());
				info.setCountObject("");

			}

			String nameBU = bu.getName().toUpperCase();

			// A leitura da licença abaixo eh para visualizacao, por isso os dados sao alterados para um melhor formato

			if (!nameBU.equals("DEFAULT")) {
				String txUser = formatTranslate("lbl_usuarios");

				int countUsr = 0;
				
				Licence license = licenseService.getLicense(bu.getLicense());

				for (AppLicence appLic : license.getAppLicences()) {
					String[] arrDetail = appLic.getDetailLicenseNumber().split(";");

					if (appLic.getApplicationName().equals("CORE")) {
						countUsr = userService.countUserOnApp(bu.getCode(), "CORE");
						appLic.setLicenseUse(countUsr);
						appLic.setDetailLicenseUse(txUser + ": " + countUsr);
						appLic.setDetailLicenseNumber(txUser + ": " + arrDetail[0].split("=")[1]);

					}

				}

				info.setSysMonkey(license);

			}

			return info;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao contar lista de tabelas do setor", e);

		} finally {
			// dataDao.closeConnection();

		}

	}

	/**
	 * Retorna info do sistema
	 * @param code
	 * @return
	 */
	public InfoSystemDTO infoSystem() {
		Parameter parSysName = parameterService.findByKeyOrNull("SYSTEM_NAME");
		Parameter parCopyRigth = parameterService.findByKeyOrNull("SYSTEM_COPY_RIGTH");

		InfoSystemDTO info = new InfoSystemDTO();
		info.setApplication(parSysName != null ? parSysName.getValue() : "undefined");
		info.setVersion(version());
		info.setCopyrigth(LocalDate.now().getYear() + " " + (parCopyRigth != null ? parCopyRigth.getValue() : "undefined"));

		return info;

	}

	public List<InfoConnectionDTO> infoConnection() {
		return dataDao.infoConnection();

	}

	public void forceCloseConnection(String key) {
		dataDao.forceCloseConnection(key);

	}

	public void forceRoolback(String key) {
		dataDao.forceRoolback(key);

	}

	/**
	 * Retorna a versao Recupera o valor do pom.xml
	 */
	public String version() {
		// try {
		// MavenXpp3Reader reader = new MavenXpp3Reader();
		// Model model = reader.read(new FileReader("pom.xml"));
		// version = model.getParent().getVersion();
		// } catch (IOException | XmlPullParserException e) {
		// logger.warn("Error a ler pom.xml! " + e.getMessage());
		// }

		return version;

	}

	/**
	 * Recupera dados da entidade Retorna a permissao pelo router, usada para pegar
	 * o label e icon Retorna a ultima atualizacao e que atualizou
	 * @param entity
	 * @param router
	 * @return
	 */
	public InfoModelDTO infoModel(String entity, String router) {
		InfoModelDTO info = new InfoModelDTO();

		try {
			UserActionCore ua = userActionService.findFirstByNameObjectAndCodeSectorOrderByCodeDesc(UserActionCore.class, entity,
					getCodeSectorContext());
			Permission p = permissionService.findByRouter(router);

			info.setPermission(PermissionResume.build(p));

			if (ua != null) {
				info.setLastUpdate(ua.getDateRecord());
				info.setUpdatedBy(ua.getUserRecord() != null ? ua.getUserRecord().getName() : "");

			}

		} catch (Exception e) {
			error("Erro ao identificar dados entidade " + entity, e);

		}

		return info;

	}

	/**
	 * Retorna info do sistema
	 * @param code
	 * @return
	 */
	public BoolDTO infoLicenceOld(String buUniqueId, String appUniqueId, String machineInfo) {
		BoolDTO bool = new BoolDTO(true, "Licence OK");

		try {
			BusinessUnit bu = businessUnitService.findByUniqueId(buUniqueId);

			Licence licence = Licence.toLicence(bu.getLicense());
			List<Application> listApp = bu.getApplications();

			for (Application app : listApp) {
				AppLicence al = licence.getAppLicences()
						.stream()
						.filter(x -> x.getApplicationName().equals(app.getName()))
						.findFirst()
						.orElse(null);

				if (al != null) {

					if (al.getApplicationUniqueId().equals(appUniqueId)) {
						LocalDate ld = al.getExpiration();

						if (ld != null && ld.plusDays(1).isBefore(LocalDate.now())) {
							bool = new BoolDTO(false, "Licence Expired");
							break;

						}

						if (machineInfo != null && !machineInfo.isEmpty())
							if (!al.getMachineInfo().equals(machineInfo)) {
								bool = new BoolDTO(false, "Machine Info Invalid");
								break;

							}

					}

				}

			}

		} catch (Exception e) {
			bool = new BoolDTO(false, "Licence Invalid");

		}

		return bool;

	}

	/**
	 * Retorna info do sistema
	 * @param code
	 * @return
	 */
	public BoolDTO infoLicense(String value, String appUniqueId, String machineInfo) {
		BoolDTO bool = new BoolDTO(true, "Licence OK");

		try {
			String json = new RSACryptography().dencrypt(value, "");

			Licence licence = Licence.toLicence(json);
			bool.setObject(licence);

			AppLicence al = licence.getAppLicences()
					.stream()
					.filter(x -> x.getApplicationUniqueId().equals(appUniqueId))
					.findFirst()
					.orElse(null);

			if (al == null)
				return new BoolDTO(false, "Application not found");

			LocalDate ld = al.getExpiration();

			if (ld != null && ld.plusDays(1).isBefore(LocalDate.now()))
				return new BoolDTO(false, "License Expired");

			if (machineInfo != null && !machineInfo.isEmpty())
				if (!al.getMachineInfo().equals(machineInfo))
					return new BoolDTO(false, "Machine Info Invalid");

		} catch (Exception e) {
			bool = new BoolDTO(false, "License Invalid");
			bool.setObject(e);

		}

		return bool;

	}

	public InfoStartDTO infoStart(String lang) {
		InfoStartDTO is = new InfoStartDTO();

		is.setInfoSystem(infoSystem());
		is.setLanguages(LanguageResume.buildResume(languageService.list(), Lang.valueOf(lang)));

		return is;

	}

	public List<InfoSectorDTO> infoSector(String username) {
		List<InfoSectorDTO> lstInfo = new ArrayList<>();
		List<Sector> lst = sectorService.listByUsername(username);

		for (Sector sector : lst) {

			if (sector.getBusinessUnit().getStatus() == false)
				continue;

			InfoSectorDTO is = new InfoSectorDTO();
			is.setCode(sector.getCode());
			is.setName(sector.getName());

			if (username.equals("sadm"))
				is.setName(sector.getName() + " (" + sector.getBusinessUnit().getName() + ")");

			is.setBussinesName(sector.getBusinessUnit().getName());
			is.setBuHasDc(sector.getBusinessUnit().isHasDomainControl());

			// String appsName = "";

			// for (Application app : sector.getBusinessUnit().getApplications())
			// appsName += app.getName() + " ";

			is.setAppsName(sector.getApplication().getDisplayName());

			lstInfo.add(is);

		}

		return lstInfo;

	}

}
