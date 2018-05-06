package com.autotest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
 
public class POIExcelUtilHelper {
	
	private class TestCaseStructure{
		int funcExpectedOutputPos;
		int funcActuallyOutputPos;
		int pass;
		ArrayList<Integer> funcInputPos = new ArrayList<Integer>(); //测试用例中函数参数在excel中位置
	}
	
	private String fileName;
	private XSSFWorkbook workbook;
	private XSSFCellStyle cellWrongStyle;
	private ArrayList<Object> inputs; //获取表格中的输入项
	private ArrayList<Object> expectedOutput;
	private int testCaseNum;
	private TestCaseStructure tcs = new TestCaseStructure();
	
	public int getTestCaseNum() {
		return testCaseNum;
	}
	
	public ArrayList<Object> getTestCaseInput() throws Exception{		
		return inputs;
	}
	
	public ArrayList<Object> getTestCaseOutput(){
		return expectedOutput;
	}
	
	POIExcelUtilHelper(String fileName) throws Exception{
		this.fileName = fileName;
		workbook = new XSSFWorkbook(new FileInputStream(new File(fileName)));
		//设置输出错误时字体格式
		cellWrongStyle = workbook.createCellStyle();
		XSSFFont fontWrong = workbook.createFont();
		fontWrong.setColor(XSSFFont.COLOR_RED);
		cellWrongStyle.setFont(fontWrong);
		inputs = new ArrayList<Object>();
		expectedOutput = new ArrayList<Object>();
        processTestCaseStructure();
        readExcelInputAndOutput();	//只读取一遍
	}
	
	//获取每一行输入参数在excel中位置
	private void processTestCaseStructure() {
		XSSFSheet sheet = null;
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			XSSFRow row = sheet.getRow(0);
			for(int j=0;j<row.getLastCellNum();++j) {
				Cell cell = row.getCell(j);
				String cellContent = cell.getRichStringCellValue().getString();
				if(cellContent.contains("input")) {
					tcs.funcInputPos.add(j);
					//System.out.println(j);
				}else if(cellContent.contains("expected")) {
					tcs.funcExpectedOutputPos = j;
				}else if(cellContent.contains("actual")) {
					tcs.funcActuallyOutputPos = j;
				}else if(cellContent.contains("pass")) {
					tcs.pass = j;
				}
			}
		}
	}
	
	private void processCellValue(Cell cell, Boolean isInput) {
		switch(cell.getCellTypeEnum()) {
		case BOOLEAN:
			if(isInput)
				inputs.add(cell.getBooleanCellValue());
			else
				expectedOutput.add(cell.getBooleanCellValue());
			break;
		case NUMERIC:
			DecimalFormat df = new DecimalFormat("#");
			if(isInput)
				inputs.add(df.format(cell.getNumericCellValue()));
			else 
				expectedOutput.add(df.format(cell.getNumericCellValue()));
			break;
		case STRING:
			if(isInput)
				inputs.add(cell.getRichStringCellValue().getString());
			else
				expectedOutput.add(cell.getRichStringCellValue().getString());
			break;
		case _NONE:
			break;
		default:
			break;
        }
	}
	
	//读取某一行测试用例的输入
	private void readExcelLine(int rowNum, XSSFSheet sheet) throws Exception{
		XSSFRow row = sheet.getRow(rowNum);
        if (row != null) {
            for(int pos: tcs.funcInputPos) {
            	Cell cell = row.getCell(pos);
                if (cell != null) { // getCell 获取单元格数据
                    processCellValue(cell, true);
                }
            }
            Cell outputCell = row.getCell(tcs.funcExpectedOutputPos);
            if(outputCell != null) {
            	processCellValue(outputCell,false);
            }
        }
	}
	
	private void showLineInput() {
		System.out.println("input pos: ");
		for(int pos: tcs.funcInputPos) {
			System.out.println(pos);
		}
		System.out.println("inputs:");
		for(Object obj:inputs) {
			System.out.println(obj.toString());
		}
		System.out.println("output pos: " + tcs.funcExpectedOutputPos);
		System.out.println("output: ");
		for(Object obj : expectedOutput) {
			System.out.println(obj.toString());
		}
	}
	
	//读取excel每行测试用例的输入和期望的输入
    private void readExcelInputAndOutput() throws Exception {
        XSSFSheet sheet = null;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
            sheet = workbook.getSheetAt(i);
            testCaseNum = sheet.getLastRowNum();
            for (int j = 1; j < testCaseNum + 1; j++) {// getLastRowNum，获取最后一行的行标
                readExcelLine(j,sheet);
            }
            //showLineInput();
        }
    }

    // 写入，往指定sheet表的单元格
    public void writeExcel(Boolean isRight, String content, int rowNum) throws Exception {
        XSSFSheet sheet = null;
        sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(rowNum);
       	if (row == null) {
            row = sheet.createRow(rowNum); // 该行无数据，创建行对象
        }
        Cell cellIsPass = row.createCell(tcs.pass); // 创建指定单元格对象。如本身有数据会替换掉
        if(!isRight) {
        	cellIsPass.setCellStyle(cellWrongStyle);
           	cellIsPass.setCellValue("false");
        }else {
        	cellIsPass.setCellValue("true");
        }
        Cell cellActualRes = row.createCell(tcs.funcActuallyOutputPos);
        cellActualRes.setCellValue(content);
        try {
        	FileOutputStream fo = new FileOutputStream(fileName); // 输出到文件
            workbook.write(fo);
        }catch(FileNotFoundException e) {
        	System.out.println("an other proess is using the file!");
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
    	POIExcelUtilHelper helper = new POIExcelUtilHelper(
    			"C:\\Users\\18359\\eclipse-workspace\\SoftwareTest\\src\\com\\autotest\\test.xlsx");
    	//helper.writeExcel(false);
    }
}