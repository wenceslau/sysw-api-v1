
package com.suite.app.config;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.suite.app.service.EncodeService;
import com.suite.app.service.UserRequestService;
import com.suite.app.util.Utils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	@Autowired
	private EncodeService rsa;

	@Autowired
	private UserRequestService userRequestService;

	/**
	 * Intercepta a requisição e autoriza o dominio definido no properties
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String headerUserAgend = req.getHeader("User-Agent");
		
		//HttpSession session = req.getSession();
		
		//Principal principal = req.getUserPrincipal();

//		SecurityContextImpl sci = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
//
//		if (sci != null) {
//		        UserDetails cud = (UserDetails) sci.getAuthentication().getPrincipal();
//		       System.out.println(cud.getUsername());
//		       System.out.println(cud.getPassword());
//
//		        // do whatever you need here with the UserDetails
//		}

		userRequestService.saveRequest(LocalDateTime.now(),
				req.getRemoteAddr(),
				req.getRemoteHost(),
				headerUserAgend,
				req.getServletPath(),
				req.getRequestURL() + "",
				req.getMethod(),
				Utils.getUserRequest());

		//for (String key : req.getParameterMap().keySet())
			//System.out.println(key + ": " + req.getParameterMap().get(key));

		if (req.getParameterMap().containsKey("password")) {
			String vlr = req.getParameterMap().get("password")[0];

			try {
				if (vlr.length() > 50)
					vlr = rsa.getRsa().dencrypt(vlr.replaceAll(" ", "+"));

			} catch (Exception e) {
				userRequestService.log("WARN, no decript data. " + e.getMessage());

			}

			if (req.getParameterMap().containsKey("username")) {
				if (headerUserAgend == null)
					headerUserAgend = "";
				String usr = req.getParameterMap().get("username")[0];
				req.getParameterMap().get("username")[0] = usr + ";" + vlr + ";" + headerUserAgend.replace(";", "");

				// o username eh composto do usename;sectorCode;language;vlrLdp;headerUserAgend
			}

			req.getParameterMap().get("password")[0] = vlr;

		}

		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		resp.setHeader("Access-Control-Allow-Credentials", "true");

		// Se a requisição for um options e a origem for permitida
		if ("OPTIONS".equals(req.getMethod())) {
			// Permite os metodos abaixos e os headers
			resp.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, PATCH, OPTIONS");
			resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			resp.setHeader("Access-Control-Max-Age", "3600");

			resp.setStatus(HttpServletResponse.SC_OK);

		} else {
			chain.doFilter(req, resp);

		}

	}

	/**
	 * Metodo obrigatorio da implementacao da interface Sem necessidade de
	 * escreve-lo
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	/**
	 * Metodo obrigatorio da implementacao da interface Sem necessidade de
	 * escreve-lo
	 */
	@Override
	public void destroy() {}

}
