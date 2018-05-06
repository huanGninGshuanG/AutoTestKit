package com.autotest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestMethodUsingTestCase {
	//组合两个基本类
	POIExcelUtilHelper helper;
	CompileJavaProgram prog;
	ArrayList<Object> inputs;
	ArrayList<Object> outputs;
	ArrayList<Object> results;
	int paramNum;
	
	public TestMethodUsingTestCase(String testCaseFileName, String className, String methodName, String classPath) 
			throws Exception{
		helper = new POIExcelUtilHelper(testCaseFileName);
		prog = new CompileJavaProgram(className, methodName,classPath);
		inputs = helper.getTestCaseInput();
		outputs = helper.getTestCaseOutput();
		paramNum = prog.paramNum();
		results = new ArrayList<Object>();
		//System.out.println(paramNum);
	}
	
	public TestMethodUsingTestCase(String className, String classPath, String testCaseFileName) throws Exception {
		prog = new CompileJavaProgram(className,classPath);
		helper = new POIExcelUtilHelper(testCaseFileName);
		inputs = helper.getTestCaseInput();
		outputs = helper.getTestCaseOutput();
		results = new ArrayList<Object>();
	}
	
	public void setMethodName(String methodName) {
		prog.setMethodName(methodName);
		paramNum = prog.paramNum();
	}
	
	public Method[] showMethods() {
		return prog.methodsOfClass();
	}
	
	//调用excel中的测试用例对被测函数进行测试
	public void test() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		for(Object obj:outputs) {
//			System.out.println(obj.toString());
//		}
		for(int k=0; k<helper.getTestCaseNum(); k++) {
			Object[] funcInput = new Object[paramNum];
			for(int j=0;j<paramNum;++j) {
				funcInput[j] = inputs.get(k*paramNum+j);
			}
			//System.out.println(prog.testClassMethod(funcInput));
			results.add(prog.testClassMethod(funcInput));
		}
	}	
	
	//每次点击UI的测试按钮时应该清空结果数组
	public void clearResultArray() {
		results.clear();
	}
	
	public void compareAndWrite() throws Exception {
		for(int i=0;i<results.size();++i) {
			Object result = results.get(i);
			Object output = outputs.get(i);
			System.out.println(result.toString() + " " + output.toString());
			if(result.toString().equals(output.toString())) {
				helper.writeExcel(true, result.toString(),i+1);
			}else {
				helper.writeExcel(false, result.toString(),i+1);
			}
		}
		System.out.println("task complete");
	}
	
	public static void main(String[] args) throws Exception {
//		TestMethodUsingTestCase tmutc =  new TestMethodUsingTestCase(
//				"C:\\Users\\18359\\eclipse-workspace\\SoftwareTest\\src\\com\\autotest\\test.xlsx",
//				"com.helloworld.HelloWorld",
//				"add",
//				"C:\\Users\\18359\\eclipse-workspace\\FirstJava\\bin\\com\\helloworld\\HelloWorld.class");
//		tmutc.test();
//		tmutc.compareAndWrite();
		
//		TestMethodUsingTestCase tmutc = new TestMethodUsingTestCase(
//				"com.helloworld.HelloWorld",
//				"C:\\Users\\18359\\eclipse-workspace\\FirstJava\\bin\\com\\helloworld\\HelloWorld.class",
//				"C:\\Users\\18359\\eclipse-workspace\\SoftwareTest\\src\\com\\autotest\\test.xlsx");
//		tmutc.setMethodName("add");
//		tmutc.test();
//		tmutc.compareAndWrite();
	}
}
