package org.ilrt.nl.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		HttpSession session = req.getSession(false);
		if(session != null) {
			try {
				session.invalidate();
			} catch (IllegalStateException e) {}
		}
		resp.sendRedirect("/validate");

	}
}
