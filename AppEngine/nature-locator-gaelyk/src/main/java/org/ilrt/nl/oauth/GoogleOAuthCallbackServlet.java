package org.ilrt.nl.oauth;

import java.io.IOException;
import com.google.gson.Gson;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.scribe.exceptions.OAuthException;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class GoogleOAuthCallbackServlet extends HttpServlet {

	private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";

	private static final String PREFIX = "go";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			
			String apiKey = getServletConfig().getInitParameter("api-key");
			String apiSecret = getServletConfig().getInitParameter("api-secret");

			OAuthService service = new ServiceBuilder()
					.provider(GoogleApi.class).apiKey(apiKey)
					.apiSecret(apiSecret).scope(SCOPE)
					.callback(req.getRequestURL().toString()).build();

			String verifier = req.getParameter("oauth_verifier");
			Verifier v = new Verifier(verifier);

			Token requestToken = (Token) req.getSession().getAttribute(
					"requestToken");

			Token accessToken = service.getAccessToken(requestToken, v);

			OAuthRequest request = new OAuthRequest(Verb.GET,
					PROTECTED_RESOURCE_URL);
			service.signRequest(accessToken, request);
			Response response = request.send();

			OAuthUser user = new Gson().fromJson(response.getBody(),
					OAuthUser.class);

			if (user.getName() != null && user.getName().trim().length() > 0) {
				req.getSession(true).setAttribute("USERNAME", user.getName());
			}
			if (user.getUserId() != null
					&& user.getUserId().trim().length() > 0) {
				req.getSession(true).setAttribute("USERID",
						PREFIX + user.getUserId());
			}
		} catch (OAuthException oae) {
			// triggered if a user denies access
		} catch (Exception e) {
		}

		resp.sendRedirect("/validate");
	}
}
