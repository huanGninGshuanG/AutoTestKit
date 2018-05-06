package com.autotest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

public class TestClassLoader extends ClassLoader {
	
	private byte[] fileContent;
	private String className;
	
	public TestClassLoader(String pathName, String packageName) throws ClassNotFoundException {
		fileContent = loadClassFile(pathName);
		this.className = packageName;
		loadClass(className);
	}
	
	//把class文件转成字节码，用于classloader动态加载  
    private byte[] loadClassFile(String classPath) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            BufferedInputStream bis = new BufferedInputStream(
            		new FileInputStream(classPath));
            byte[] data = new byte[1024 * 256];
            int ch = 0;
            while ((ch = bis.read(data, 0, data.length)) != -1) {
                bos.write(data, 0, ch);
            }
            bis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    
    @Override  
    protected Class<?> loadClass(String arg0, boolean arg1)
            throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(arg0);
        if (clazz == null) {
            if (getParent() != null) {
                try {
                    //这里我们要用父加载器加载如果加载不成功会抛异常  
                    clazz = getParent().loadClass(arg0);
                } catch (Exception e) {
                    //System.out.println("父类ClassLoader加载失败！");
                }
            }
            if (clazz == null) {
                clazz = defineClass(arg0, fileContent, 0, fileContent.length);
            }  
        }
		return clazz;
    }
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
    	try {
    		TestClassLoader tcl = new TestClassLoader(
    			"C:\\Users\\18359\\eclipse-workspace\\FirstJava\\bin\\com\\helloworld\\HelloWorld.class",
    			"com.helloworld.HelloWorld");
			Class<?> clazz = tcl.loadClass("com.helloworld.HelloWorld");
			Object o = clazz.newInstance();
			System.out.println(clazz.getSimpleName().toString());
			Method[] methods = clazz.getMethods();
			for(Method method: methods) {
				System.out.println(method.getName());
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
