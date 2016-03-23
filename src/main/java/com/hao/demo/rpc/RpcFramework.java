package com.hao.demo.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {
	
	/**
	 * 暴露服务
	 * 
	 * @param service 服务的实现
	 * @param port 服务端口
	 */
	public static void export(final Object service, int port) throws Exception{
		if(service == null){
			throw new IllegalArgumentException("service instance == null ");
		}
		
		if(port <= 0 || port > 65535){
			throw new IllegalArgumentException("Invalid port " + port);
		}
		
		System.out.println("Export service " + service.getClass().getName() + " on port " + port);
		
		ServerSocket server = new ServerSocket(port);
		for(;;){
			final Socket socket = server.accept();
			new Thread(new Runnable() {
				public void run() {
					try {
						try {
							ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
							
							try {
								//获取方法信息
								String methodName = input.readUTF();
								Class<?>[] parameterTypes = (Class<?>[])input.readObject();
								Object[] args = (Object[])input.readObject();
								
								//socket输出流
								ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
								
								try {
									//执行方法
									Method method = service.getClass().getMethod(methodName, parameterTypes);
									Object result = method.invoke(service, args);
									
									//回写方法执行结果
									output.writeObject(result);
								}finally {
									output.close();
								}
							}finally {
								input.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							socket.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
	}
	
	/**
	 * 引用服务
	 * 
	 * @param <T> 接口泛型
	 * @param interfaceClass 接口类型
	 * @param host 服务器主机名
	 * @param port 服务器端口
	 * @return 远程服务
	 * @throws Exception
	 */
	public static <T> T refer(Class<T> interfaceClass, final String host, final int port) throws Exception{
		if(interfaceClass == null){
			throw new IllegalArgumentException("interface class == null ");
		}
		
		if(!interfaceClass.isInterface()){
			throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
		}
		
		if(host == null || host.length() == 0){
			throw new IllegalArgumentException("host == null ");
		}
		
		if(port <= 0 || port > 65535){
			throw new IllegalArgumentException("Invalid port " + port);
		}
		
		System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);
		
		ClassLoader classLoader = interfaceClass.getClassLoader();
		Class<?>[] interfaces = new Class<?>[]{ interfaceClass };
		InvocationHandler handler = new InvocationHandler(){
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Socket socket = new Socket(host, port);
				
				try {
					//调用服务
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					
					try {
						output.writeUTF(method.getName());
						output.writeObject(method.getParameterTypes());
						output.writeObject(args);
						
						//获取执行结果
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
						
						try {
							Object result = input.readObject();
							if(result instanceof Throwable){
								throw (Throwable)result;
							}
							return result;
						}finally{
							input.close();
						}
					}finally {
						output.close();
					}
				}finally {
					socket.close();
				}
			}
		};
			
		return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
	}

}
