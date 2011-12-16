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
public class GoogleOAuthServlet extends HttpServlet {

	private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			
			String apiKey = getServletConfig().getInitParameter("api-key");
			String apiSecret = getServletConfig().getInitParameter("api-secret");
			
			OAuthService service = new ServiceBuilder()
					.provider(GoogleApi.class).apiKey(apiKey)
					.apiSecret(apiSecret).scope(SCOPE)
					.callback(req.getRequestURL() + "/callback").build();

			Token requestToken = service.getRequestToken();

			req.getSession(true).setAttribute("requestToken", requestToken);

			String authUrl = service.getAuthorizationUrl(requestToken);

			resp.sendRedirect(authUrl);
		} catch (Exception e) {
			resp.sendRedirect("/error");
		}

	}
}
