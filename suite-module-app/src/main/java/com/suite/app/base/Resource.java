package com.suite.app.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;

/**
 * Super Classe a ser estendida por todas as classe resources da suite
 * @author Wenceslau
 *
 */
public abstract class Resource extends Base {

	/**
	 * Adiciona no respose o arquivo para download no browser
	 * @param response
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void addFileResponse(HttpServletResponse response, File file) {

		if (file.exists()) {

			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());

			if (mimeType == null) {

				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";

			}

			response.setContentType(mimeType);

			// Here we have mentioned it to show as attachment
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

			response.setContentLength((int) file.length());

			try {

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				file.delete();

			} catch (IOException e) {

				throw new RuntimeException("msg_erro_ao_adicionar_o_a_n_r_h", e);

			}

		}

	}
	
	protected abstract String formatTranslate(String key, Object... args);


}
