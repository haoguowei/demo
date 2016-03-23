package com.hao.demo.rpc;

public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String name) {
		return "Hello," + name;
	}
}
