package com.hao.classloader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义类加载器，只需重写findClass方法即可
 *
 * @author yanwei
 * @since 1.0.0
 */
public class CustomClassLoader extends ClassLoader{
	
	//类加载器名称
	private String name;
	
	//类加载器加载类的路径
	private String path;
	
	//文件类型
	private final String fileType = ".class";
	
	public CustomClassLoader(String name){
		super(); // 让系统类加载器成为该类加载器的父加载器
		this.name = name;
	}
	
	public CustomClassLoader(ClassLoader parent, String name){
		super(parent); // 显式指定该类加载器的父加载器
		this.name = name;
	}

	/**
	 * 根据name来查找到class文件，自定义类加载器，只需重写findClass方法即可
	 *
	 * 	加载器执行流程：loadClass->findClass->defineClass
	 * 
	 * 	loadClass 父加载器中调用
	 * 	findClass  根据name来查找到class文件,要重写
	 * 	defineClass 就是将class文件的字节byte数组编程一个class对象，这个方法肯定不能重写，内部实现是在C/C++代码中实现的
	 *   参数： name - 所需要的类的二进制名称，如果不知道此名称，则该参数为 null
     *		b - 组成类数据的字节。off 与 off+len-1 之间的字节应该具有《Java Virtual Machine Specification》定义的有效类文件的格式。
     * 		off - 类数据的 b 中的起始偏移量
     * 		len - 类数据的长度 
     * 
	 * @author yanwei
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = getClassByte(name);
		return defineClass(name, b, 0, b.length);
	}
	

	/**
	 * 获取class文件byte
	 * 
	 * @author yanwei
	 * @param name
	 * @return
	 */
	private byte[] getClassByte(String name){
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(path + name + fileType);
			out = new ByteArrayOutputStream();

			int ch = 0;
			while (-1 != (ch = in.read())) {
				out.write(ch);
			}
			
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "CustomClassLoader [name=" + name + ", path=" + path + "]";
	}
}
