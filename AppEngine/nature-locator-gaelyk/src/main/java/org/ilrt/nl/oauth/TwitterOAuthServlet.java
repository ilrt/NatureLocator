package org.ilrt.nl.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

@SuppressWarnings("serial")
public class TwitterOAuthServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			
			String apiKey = getServletConfig().getInitParameter("api-key");
			String apiSecret = getServletConfig().getInitParameter("api-secret");

			OAuthService service = new ServiceBuilder()
					.provider(TwitterApi.class)
					.apiKey(apiKey)
					.apiSecret(apiSecret)
					.callback(req.getRequestURL() + "/callback").build();

			Token requestToken = service.getRequestToken();

			req.getSession(true).setAttribute("requestToken", requestToken);

			String authUrl = service.getAuthorizationUrl(requestToken);
			authUrl = authUrl.replace("authorize", "authenticate");

			resp.sendRedirect(authUrl);
		} catch (Exception e) {
			resp.sendRedirect("/error");
		}
	}
}
