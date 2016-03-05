package com.huami.hessian;


import com.caucho.hessian.server.HessianServlet;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HessianServlet implements CommunicationService {
	private static final long serialVersionUID = 1L;

	public String communicate(String str) {
		return "Hello World! " + str;
	}
}
