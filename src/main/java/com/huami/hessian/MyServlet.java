package com.huami.hessian;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;

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
		System.out.println("rev: " + str);
		return "Hello World! " + str;
	}

	@Override
	public void service (ServletRequest request,ServletResponse response) throws IOException, ServletException{
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		java.util.Date date1 = new java.util.Date();
		
		X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
		if (null != certs && certs.length > 0 && isValidCert(certs)) {
			System.out.println("scott servlet: cert passed");			
		} else {
			System.out.println("scott servlet: cert failed");
		}
		java.util.Date date2 = new java.util.Date();

		super.service(request, response);
		
		java.util.Date date3 = new java.util.Date();

		System.out.println("enter at: " + new Timestamp(date1.getTime()));
		System.out.println("authed at: " + new Timestamp(date2.getTime()));
		System.out.println("leave at: " + new Timestamp(date3.getTime()));

		return;

//		res.setStatus(403); //403 Forbidden response
//	    PrintWriter out = res.getWriter();
//	    res.setContentType("text/html");
//	    out.println("<h1>Authentication failed</h1>");
//	    return;
	}

	private boolean isValidCert(X509Certificate[] certs) {
		final String subjectCName = "client.com";
		boolean result = false;

		for(X509Certificate c : certs) {
			try {
				//Subject DN
				String subjectDn = c.getSubjectDN().getName();
				LdapName subjectLn = new LdapName(subjectDn);
				System.out.println("servlet Subject DN: "+ c.getSubjectDN().getName());
				for(Rdn rdn : subjectLn.getRdns()) {
					if(rdn.getType().equalsIgnoreCase("CN")) {
						System.out.println("servlet Subject CN is: " + rdn.getValue());
						if(subjectCName.equals(rdn.getValue())) {
							result = true;
						}
					}
				}

				//Issuer DN
				String issuerDn = c.getIssuerDN().getName();
				LdapName issuerLn = new LdapName(issuerDn);
				System.out.println("servlet Issuer DN: " + c.getIssuerDN().getName());
				for(Rdn rdn : issuerLn.getRdns()) {
					if(rdn.getType().equalsIgnoreCase("CN")) {
						System.out.println("servlet Issuer CN is: " + rdn.getValue());
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
