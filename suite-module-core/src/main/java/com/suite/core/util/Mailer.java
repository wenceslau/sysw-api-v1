package com.suite.core.util;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.suite.app.base.Base;
import com.suite.app.dto.BoolDTO;
import com.suite.app.service.EncodeService;
import com.suite.core.base.EnumCore.DataServiceType;
import com.suite.core.model.BusinessUnit;
import com.suite.core.model.DataService;
import com.suite.core.model.Property;
import com.suite.core.service.DataServiceService;
import com.suite.core.service.ParameterService;

/**
 * Classe de envio de emails
 * @author Wenceslau
 *
 */
@Component
public class Mailer extends Base {
 
	@Autowired
	private DataServiceService dataServiceService;
	
	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private EncodeService rsaService;

	/**
	 * Envia um email
	 * @param identifer  - Identidicador o data Service
	 * @param recipients - Lista de destinatarios
	 * @param subject    - Assunto do email
	 * @param message    - Corpo da mensagem
	 */
	public BoolDTO sendMail(String identifer, BusinessUnit bu, List<String> recipients, String subject, String message, File... files) {

		DataService dataService = dataServiceService.findTop1ByIdentifierAndType(identifer, DataServiceType.EMAILSERVICE);

		if (dataService == null) {

			String msg = parameterService.formatTranslate("msg_nenhum_servico_de_e_f_e_c_o_i_[%s]_p_e_e", identifer);
			warn(msg);
			return new BoolDTO(false, msg);

		}
		
		if (!dataService.getStatus()) {
			String msg = parameterService.formatTranslate("msg_o_servico_de_e_c_o_i_[%s]_e_i", identifer);
			warn(msg);
			return new BoolDTO(false, msg);
		}

		return sendMail(bu, dataService, recipients, subject, message, files);

	}

	/**
	 * Envia um email
	 * @param ds  - Data Service com os paramrtros de envio
	 * @param recipients - Lista de destinatarios
	 * @param subject    - Assunto do email
	 * @param message    - Corpo da mensagem
	 */
	private BoolDTO sendMail(BusinessUnit bu, DataService ds, List<String> recipients, String subject, String message, File... files) {

		try {

			info("Preparando email a enviar!");

			if (ds == null)
				throw new RuntimeException("msg_data_service_e_o_p_e_d_e");
			
			if (bu == null)
				bu = ds.getBusinessUnit();
					
			String acronymEmail = parameterService.valueParameterBusinessUnitOrNull("SIGLA_ASSUNTO_EMAIL", bu);			
			String parOfSubject = "["+(acronymEmail != null ? acronymEmail: "unknow") +"] ";
			subject = parOfSubject + subject;
								
			JavaMailSender mailSender = getMailSender(ds);

			String sender = getProp(ds, "EMAILSERVICE_SENDER");

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
//			DataSource fds = new FileDataSource("C:\\Users\\4931pc_neto\\Pictures\\flag-en.png");
//			mimeMessage.setDataHandler(new DataHandler(fds));
//			mimeMessage.setContentID("<image>");
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom(sender);
			helper.setTo(recipients.toArray(new String[recipients.size()]));
			helper.setSubject(subject);
			helper.setText(message, true);
			
			for (File file : files)
				helper.addAttachment(file.getName(), file);			
			
			info("Eenviando email sucesso.... ");
			mailSender.send(mimeMessage);

			info("E-mail enviado com sucesso.... ");
			
			return new BoolDTO(true, parameterService.formatTranslate("msg_email_enviado_com_s"));

		} catch (Exception e) {

			warn("Falha ao enviar e-mail" + e.getMessage());
			return new BoolDTO(false, "Failure!! " + e.getMessage());
		}
	}

	/**
	 * Instancia o objeto JavaMailSender com so parametros do data service
	 * @param dataService
	 * @return
	 */
	private JavaMailSender getMailSender(DataService dataService) {

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
	    props.put("mail.smtp.timeout", 10000);
		props.put("mail.smtp.connectiontimeout", 8000);
	    props.put("mail.smtp.writetimeout", 8000);
	    
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setJavaMailProperties(props);
		mailSender.setHost(getProp(dataService, "EMAILSERVICE_HOST"));
		mailSender.setPort(Integer.parseInt(getProp(dataService, "EMAILSERVICE_PORT")));
		mailSender.setUsername(getProp(dataService, "EMAILSERVICE_USER"));
		String strPwd = rsaService.getRsa().dencrypt(getProp(dataService, "EMAILSERVICE_PASSWORD"));
		mailSender.setPassword(strPwd);
		return mailSender;

	}

	/**
	 * Identifica e retorna a propriedade do data service
	 * @param dataService
	 * @param nameProperty
	 * @return
	 */
	private String getProp(DataService dataService, String nameProperty) {

		Property property = dataService.getProperties().stream().filter(x -> x.getName().equals(nameProperty)).findFirst()
				.orElse(null);

		if (property == null)
			throw new RuntimeException("Property " + nameProperty + " from Data Service " + dataService.getName() + " not found");

		return property.getValue();

	}

}
