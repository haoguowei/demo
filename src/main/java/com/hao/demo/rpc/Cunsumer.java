package com.hao.demo.rpc;

public class Cunsumer {

	public static void main(String[] args) throws Exception {
		HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
		for (int i = 0; i < 10; i++) {
			System.out.println(service.sayHello("张三丰-" + i));
			Thread.sleep(1000);
		}
	}
}
