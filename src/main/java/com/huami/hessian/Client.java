package com.huami.hessian;

import java.sql.Timestamp;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @author Peter Karich, peat_hal ‘at’ users ‘dot’ sourceforge ‘dot’ net
 */
public class Client {

	public static void test(String url) throws Exception {
		java.util.Date date1 = new java.util.Date();
		
//		String url = "http://account-huami-test.mi-ae.cn/test";
//		String url = "http://localhost:8080/server/test";
		HessianProxyFactory factory = new HessianProxyFactory();
		final CommunicationService basic = (CommunicationService) factory.create(CommunicationService.class, url);
		java.util.Date date2 = new java.util.Date();
		String reply = basic.communicate("client timestamp = " +  new Timestamp(date2.getTime()));
		java.util.Date date3 = new java.util.Date();
		System.out.println("Client# url = " + url);
		System.out.println("Client# Server said, " + reply);
		System.out.println("Client# enter at: " + new Timestamp(date1.getTime()));
		System.out.println("Client# send at: " + new Timestamp(date2.getTime()));
		System.out.println("Client# return at: " + new Timestamp(date3.getTime()));
		System.out.println("Client# delay: " + (date3.getTime() - date2.getTime()));
		return;
	}
}