package com.hao.demo.javassit;

import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class Test {
	
	public static void main(String[] args) throws Exception {
		System.out.println("====== 测试1 ======");
		test1();
		
		System.out.println("\n====== 测试2======");
		test2();
	}
	//测试一：创建新类
	private static void test1() throws Exception{
		//首先获取到class定义的容器ClassPool
		ClassPool pool = ClassPool.getDefault();
		
		//创建新类
		CtClass cc = pool.makeClass("Haoguowei");

		//创建属性
		CtField field = CtField.make("private String hello = \"hello,\";", cc);
		cc.addField(field);
		
		//创建方法
		CtMethod method = CtMethod.make("public String getName(){return this.hello + \"郝国伟\";}", cc);
		cc.addMethod(method);
		
		//创建类的实例
		Class clazz = cc.toClass();
		Object instance = clazz.newInstance();
		
		//获取到方法
		Method m = clazz.getDeclaredMethod("getName");
		//执行方法
		System.out.println(m.invoke(instance));
	}
	
	//测试二：做方法调用的埋点
	private static void test2()throws Exception{
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("com.hao.demo.javassit.Point");
		CtMethod method = cc.getDeclaredMethod("move");
		method.insertBefore("System.out.println(\"第二个参数：\"+$2);"); //$2相当于dy
		method.insertBefore("System.out.println(\"第一个参数：\"+$1);");  //$1相当于dx
		method.insertAfter("System.out.println(\"end\");");
		cc.toClass(); //不可省略
		cc.writeFile();
		
		//测试
		new Point().move(10, 20);
	}
}


class Point {
	public void move(int dx, int dy) {
		System.out.println("执行move");
	}
}
