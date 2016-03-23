package com.hao.demo.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hao.app.rpc.service.MyRPCService;

public class DubboConsumerTest {

	private static ClassPathXmlApplicationContext context;

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext(new String[] { "classpath:spring-*.xml" });
		context.start();

		MyRPCService service = (MyRPCService) context.getBean("myRPCService");
		String hello = service.sayHello("武则天");

		System.out.println(hello);
	}

}
