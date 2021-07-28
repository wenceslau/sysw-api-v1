package com.suite.core.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.suite.app.base.Enum.ItemLicense;
import com.suite.app.exception.WarningException;
import com.suite.core.base.ServiceCore;
import com.suite.core.model.Application;
import com.suite.core.model.Sector;
import com.suite.security.cryptography.RSACryptography;
import com.suite.security.licence.AppLicence;
import com.suite.security.licence.Licence;

@Service
public class LicenseService extends ServiceCore {

	/**
	 * Valida a data de expiracao
	 * Se exiprou a licenca tenha qq erro na validacao
	 * devolve msg de licenca expirada
	 * @param bu
	 */
	public void checkLicense(Sector sector, String keyLincense) {

		try {

			// info("Exec check: " + sector.getName());

			// Get license from BU
			Licence license;

			if (keyLincense == null)
				license = getLicense(sector.getBusinessUnit().getLicense());
			else
				license = getLicense(keyLincense);

			// Get all applications from BU
			List<Application> listApp = sector.getBusinessUnit().getApplications();

			// if doesn't exist none applications
			if (listApp.isEmpty())
				throw new WarningException("licenca_expirada");

			// iterator on list application
			for (Application app : listApp) {

				// Valida somente para a aplicacao que se esta logando
				if (!app.getCode().equals(sector.getApplication().getCode()))
					continue;

				// info(app.getName());
				// info(license.getAppLicences() + "");

				// Find the license for application from iterator
				AppLicence al = license.getAppLicences()
						.stream()
						.filter(x -> x.getApplicationUniqueId().equals(app.getLicence())) // O LICENCE DA APP EH O IDENTFICADOR
						.findFirst()
						.orElse(null);

				// If exist check if not expired
				if (al != null) {

					LocalDate ld = al.getExpiration();
					if (ld != null && ld.plusDays(1).isBefore(LocalDate.now()))
						throw new WarningException("licenca_expirada");

				}

			}

		} catch (Exception e) {

			error("Error check: ", e);
			// Aqui nao pode ter traducao, o texto licenca_expirada eh uma key
			throw new WarningException("licenca_expirada");

		}

	}

	/**
	 * Retorna o objeto licença basedo na stringy
	 * @param keyLicense
	 * @return
	 * @throws IOException
	 */
	public Licence getLicense(String keyLicense) {

		try {

			String json = new RSACryptography().dencrypt(keyLicense, "");

			info("JSON: " + json);

			return Licence.toLicence(json);

		} catch (IOException e) {

			throw new RuntimeException("Invalid format for string lincense", e);
		}

	}

	/**
	 * Retorna o objeto AppLicence da licenca da BU que
	 * corresponda a aplicacao do setor logado
	 * @param license
	 * @param appUniqueId
	 * @return
	 */
	public AppLicence getAppLicenseByAppLogged() {
		Sector sector = getSector();
		Licence license;

		try {

			license = getLicense(sector.getBusinessUnit().getLicense());

			info(license.getAppLicences() + "");

			// iterator on list application
			for (AppLicence appLicense : license.getAppLicences()) {

				if (appLicense.getApplicationUniqueId().equals(sector.getApplication().getLicence())) // The license app is a uniqueId
					return appLicense;

			}

			throw new RuntimeException("No applications found with appUniqueId in this license");

		} catch (RuntimeException e) {

			throw new RuntimeException(e.getMessage());

		}

	}

	/**
	 * Retorna a quantidade de item da licenca para o target informado
	 * target = user, object, routine
	 * @param appLicense
	 * @return
	 */
	public int getNumItemLicense(String target) {

		try {

			AppLicence appLicense = getAppLicenseByAppLogged();

			String detailLiceneNumber = appLicense.getDetailLicenseNumber();

			String[] arr = detailLiceneNumber.split(";");

			for (String string : arr)
				if (string.contains(target))
					return Integer.parseInt(string.split("=")[1]);

		} catch (Exception e) {

			// Nao foi possivel efetuar a operação verifique se sua linceça é valida
			throw new RuntimeException("msg_nao_possivel_efetuar_a_o_v_s_s_l_e_v", e);

		}

		return 0;

	}

	/**
	 * Valida se o numero de itens ulprassou o permitido pela licenca
	 * @param item
	 * @param numItem
	 */
	public void checkCountItemLicense(ItemLicense item, long numItem) {
		int numItemLincese = getNumItemLicense(item.getValue());

		if (numItem >= numItemLincese)
			throw new WarningException(formatTranslate("msg_o_numero_de_o_u_o_m_p_p_s_l"));

		// O numero de itens ultrapassou o maximo permitido pela sua licença
	}

	/**
	 * Get number License
	 * @param appLicense
	 * @return
	 */
	@Deprecated
	public int getAppSysMonkeyLoggedCount() {

		try {

			AppLicence appLicense = getAppLicenseByAppLogged();
			String detailLiceneNumber = appLicense.getDetailLicenseNumber();

			int count = Integer.parseInt(detailLiceneNumber);

			return count;

		} catch (Exception e) {

			// Nao foi possivel efetuar a operação verifique se sua linceça é valida
			throw new WarningException("msg_nao_possivel_efetuar_a_o_v_s_s_l_e_v");

		}

	}

}
