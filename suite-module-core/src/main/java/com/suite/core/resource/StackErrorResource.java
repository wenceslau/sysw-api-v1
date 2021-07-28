package com.suite.core.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.exception.WarningException;
import com.suite.core.base.ResourceCore;
import com.suite.core.model.viewer.StackErrorVwr;
import com.suite.core.repository.filter.StackErrorFillter;
import com.suite.core.service.StackErrorService;

/**
 * Endpoint de acesso a Informacoes
 * TODO: Avaliar migrar par Controller
 * @author Wenceslau
 *
 */
@RestController
@RequestMapping("/stackError")
public class StackErrorResource extends ResourceCore {

	@Autowired
	private Environment env;

	@Autowired
	private StackErrorService stackErrorService;

	/**
	 * Retorna informacoes sobre a entidade
	 * @param entity
	 * @param router
	 * @return
	 */
	@GetMapping("/listFileLog")
	public ResponseEntity<?> listFileLog() {
		return ResponseEntity.status(HttpStatus.OK).body(stackErrorService.listFileLog());

	}

	@GetMapping("/downloaFileLog/{name}")
	public void downloaFileLog(HttpServletRequest request, HttpServletResponse response, @PathVariable String name) {
		info("Download arquivo log " + name);

		String path = env.getProperty("logging.file", "");

		File file = new File(path);

		if (file.isFile())
			path = file.getParent();

		file = new File(path + "/" + name);

		if (!file.exists())
			throw new WarningException("O arquivo não esta disponivel");

		try {

			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());

			if (mimeType == null) {

				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";

			}

			response.setContentType(mimeType);

			/**
			 * In a regular HTTP response, the Content-Disposition response header is a
			 * header indicating if the content is expected to be displayed inline in the
			 * browser, that is, as a Web page or as part of a Web page, or as an
			 * attachment, that is downloaded and saved locally.
			 * 
			 */

			/**
			 * Here we have mentioned it to show inline
			 */
			// Here we have mentioned it to show as attachment
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());

		} catch (IOException e) {

			throw new RuntimeException("Erro ao realizar download file " + name, e);

		}

	}

	/**
	 * Retorna Lista Erros em formato
	 * objeto ChartBarDTO
	 * MARK: Nao requer permissao
	 * @return
	 */
	@GetMapping("/chartError")
	public ResponseEntity<?> chartErrorByDay() {
		return ResponseEntity.status(HttpStatus.OK).body(stackErrorService.chartErrorByDay());

	}

	@GetMapping("/listError")
	public List<StackErrorVwr> filter() {
		return StackErrorVwr.build(stackErrorService.filter(new StackErrorFillter()));

	}

}
