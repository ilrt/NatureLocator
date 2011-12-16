package org.ilrt.nl.oauth;

import java.io.IOException;
import com.google.gson.Gson;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TwitterOAuthCallbackServlet extends HttpServlet {

	private static final String PROTECTED_RESOURCE_URL = "http://api.twitter.com/1/account/verify_credentials.json";

	private static final String PREFIX = "tw";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			
			String apiKey = getServletConfig().getInitParameter("api-key");
			String apiSecret = getServletConfig().getInitParameter("api-secret");

			OAuthService service = new ServiceBuilder()
					.provider(TwitterApi.class)
					.apiKey(apiKey)
					.apiSecret(apiSecret)
					.callback(req.getRequestURL().toString()).build();

			String verifier = req.getParameter("oauth_verifier");

			if (verifier == null || verifier.trim().length() == 0) {
				resp.sendRedirect("/validate");
				return;
			}

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

			resp.sendRedirect("/validate");
		} catch (Exception e) {
			resp.sendRedirect("/error");
		}
	}
}
