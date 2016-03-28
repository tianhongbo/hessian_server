package com.huami.hessian;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.server.HessianServlet;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HessianServlet implements CommunicationService {
	private static final long serialVersionUID = 1L;

	public String communicate(String str) {
		return "Hello World! " + str;
	}

	@Override
	public void service (ServletRequest request,ServletResponse response) throws IOException, ServletException{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
		if (null != certs && certs.length > 0) {
			System.out.println("scott: cert found");
			if(!checkClientCert(certs)) {
				System.out.println("scott: cert failed");
				res.setStatus(403); //403 Forbidden response
			    PrintWriter out = res.getWriter();
			    res.setContentType("text/html");
			    out.println("<h1>Authentication failed</h1>");
			    return;
			}
			System.out.println("scott: cert passed");
		}

		super.service(request, response);
		return;
	}

	private boolean checkClientCert(X509Certificate[] certs) {
		final String subjectCName = "client.com";
		boolean result = false;

		for(X509Certificate c : certs) {
			try {
				//Subject DN
				String subjectDn = c.getSubjectDN().getName();
				LdapName subjectLn = new LdapName(subjectDn);
				System.out.println("Subject DN: "+ c.getSubjectDN().getName());
				for(Rdn rdn : subjectLn.getRdns()) {
					if(rdn.getType().equalsIgnoreCase("CN")) {
						System.out.println("Subject CN is: " + rdn.getValue());
						if(subjectCName.equals(rdn.getValue())) {
							result = true;
						}
					}
				}

				//Issuer DN
				String issuerDn = c.getIssuerDN().getName();
				LdapName issuerLn = new LdapName(issuerDn);
				System.out.println("Issuer DN: " + c.getIssuerDN().getName());
				for(Rdn rdn : issuerLn.getRdns()) {
					if(rdn.getType().equalsIgnoreCase("CN")) {
						System.out.println("Issuer CN is: " + rdn.getValue());
					}
				}

			} catch (InvalidNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
