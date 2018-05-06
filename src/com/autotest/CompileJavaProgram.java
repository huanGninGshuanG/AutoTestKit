package com.autotest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CompileJavaProgram {
	
	private TestClassLoader tcl;
	private Class<?> testedClass;
	private Method[] methods;
	private String methodToTest;
	private Object obj; //实例化被测试对象
	private Class<?>[] methodToTestParam;
	
	public CompileJavaProgram(String className, String methodName, String classPath) throws ClassNotFoundException, 
		InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException {
		tcl = new TestClassLoader(classPath,className);
		testedClass = tcl.loadClass(className);
		methods = testedClass.getMethods();
		methodToTest = methodName;
//		for(Method method : methods) {
//            System.out.println(method.getName());
//        }
		obj = testedClass.newInstance(); //newInstance必须要求被测类拥有默认构造函数
		getMethodParameterType();
		//obj = Constructor.newInstance();
	}
	
	public CompileJavaProgram(String className, String classPath) throws ClassNotFoundException, 
						InstantiationException, IllegalAccessException {
		tcl = new TestClassLoader(classPath,className);
		testedClass = tcl.loadClass(className);
		methods = testedClass.getMethods();
		obj = testedClass.newInstance(); //newInstance必须要求被测类拥有默认构造函数
	}
	
	//接收用户选择的方法
	public void setMethodName(String methodName) {
		this.methodToTest = methodName;
		getMethodParameterType();
	}
	
	public Method[] methodsOfClass() {
		return methods;
	}
	
	public int paramNum() {
		return methodToTestParam.length;
	}
	
	private void getMethodParameterType() {
		for(Method method : methods) {
            if(methodToTest.equals(method.getName())) {
                methodToTestParam = method.getParameterTypes();
//                for(int i=0;i<methodToTestParam.length;++i) {
//                	System.out.println(methodToTestParam[i]);
//                }
            }
        }
	}
	
	//测试被测函数（待补充其他类型）
	public Object testClassMethod(Object... params) throws IllegalAccessException, IllegalArgumentException, 
					InvocationTargetException {
		for(Method method : methods) {
            if(methodToTest.equals(method.getName())) {
            	for(int i=0;i<methodToTestParam.length;++i) {
            		if(methodToTestParam[i].toString().equals("int")){
            			params[i] = Integer.parseInt(params[i].toString());
            		}else if(methodToTestParam[i].toString().equals("double")) {
            			params[i] = Double.parseDouble(params[i].toString());
            		}
            	}
                // 调用这个方法，invoke第一个参数是类名，后面是方法需要的参数
                //method.invoke(obj, new Object[] {new String[] {"Tom", "Jenus"}});
            	return method.invoke(obj, params);
            }
        }
		return null;
	}
	public static void main(String[] args) throws IllegalArgumentException, 
				InvocationTargetException, NoSuchMethodException, SecurityException { 
		try {
			CompileJavaProgram prog = new CompileJavaProgram("com.helloworld.HelloWorld", "add",
					"C:\\Users\\18359\\eclipse-workspace\\FirstJava\\bin\\com\\helloworld\\HelloWorld.class");
		} catch (ClassNotFoundException e) {
			System.out.println("can not found the class");
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    }
}
