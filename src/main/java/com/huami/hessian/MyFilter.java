package com.huami.hessian;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class MyFilter implements Filter  {
	public void  init(FilterConfig config) throws ServletException{
		//TODO

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
		//wrappedRequest.getInputStream().read();
		String body = IOUtils.toString(wrappedRequest.getReader());
		HttpServletRequest req = (HttpServletRequest)wrappedRequest;
		String url = req.getRequestURL().toString();
		System.out.println("scott: " + url);
		X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
		if (null != certs && certs.length > 0) {
			System.out.println("scott: cert found");
			for(X509Certificate c : certs) {
 				try {
 					//Subject DN
					String subjectDn = c.getSubjectDN().getName();
					LdapName subjectLn = new LdapName(subjectDn);
					System.out.println("Subject DN: "+ c.getSubjectDN().getName());
					for(Rdn rdn : subjectLn.getRdns()) {
						if(rdn.getType().equalsIgnoreCase("CN")) {
							System.out.println("Subject CN is: " + rdn.getValue());
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
		}
		
		//reset the req input stream
		wrappedRequest.resetInputStream();
		chain.doFilter(wrappedRequest, response);

	}
	public void destroy( ) {
		/* Called before the Filter instance is removed 
      from service by the web container*/
	}

	private static class ResettableStreamHttpServletRequest extends
	HttpServletRequestWrapper {

		private byte[] rawData;
		private HttpServletRequest request;
		private ResettableServletInputStream servletStream;

		public ResettableStreamHttpServletRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
			this.servletStream = new ResettableServletInputStream();
		}

		public void resetInputStream() {
			servletStream.stream = new ByteArrayInputStream(rawData);
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			if (rawData == null) {
				rawData = IOUtils.toByteArray(this.request.getReader());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return servletStream;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			if (rawData == null) {
				rawData = IOUtils.toByteArray(this.request.getReader());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return new BufferedReader(new InputStreamReader(servletStream));
		}


		private class ResettableServletInputStream extends ServletInputStream {

			private InputStream stream;

			@Override
			public int read() throws IOException {
				return stream.read();
			}

			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// TODO Auto-generated method stub		
			}
		}
	}
}