package com.ifatter.andorm.model;

public class MyClassLoader extends ClassLoader {

	@Override
	protected Class<?> findClass(String className)
			throws ClassNotFoundException {
		return super.findClass(className);
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return super.loadClass(className);
	}

	
	

}
