package org.ilrt.nl.oauth;

import java.io.IOException;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class FacebookOAuthServlet extends HttpServlet {

	private static final Token EMPTY_TOKEN = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			
			String apiKey = getServletConfig().getInitParameter("api-key");
			String apiSecret = getServletConfig().getInitParameter("api-secret");

			OAuthService service = new ServiceBuilder()
					.provider(FacebookApi.class).apiKey(apiKey)
					.apiSecret(apiSecret)
					.callback(req.getRequestURL() + "/callback").build();

			String authUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

			resp.sendRedirect(authUrl);
		} catch (Exception e) {
			resp.sendRedirect("/error");
		}
	}
}
