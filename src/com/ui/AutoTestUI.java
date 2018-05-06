package com.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.autotest.TestMethodUsingTestCase;
import com.ui.util.*;

public class AutoTestUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private TestMethodUsingTestCase tmut;
	
	private JButton testCaseFileChooser = new JButton("choose test case file(xslx file)");
	private String testCaseFilePath;
	private JTextField testCaseFiletxt = new JTextField(30);
	private ActionListener blTestCase = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "choose");
			File file = chooser.getSelectedFile();
			testCaseFiletxt.setText(file.getAbsolutePath().toString());
			testCaseFilePath = file.getAbsolutePath().toString();
		}
	};
	
	private JButton fileChooser = new JButton("choose file to test(class file)");
	private String filePath;
	private JTextField txt = new JTextField(30);
	private ActionListener bl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "choose");
			File file = chooser.getSelectedFile();
			txt.setText(file.getAbsolutePath().toString());
			filePath = file.getAbsolutePath().toString();
		}
	};
	
	private JTextField classNameTxt = new JTextField(30);
	private String className;
	
	private JButton showMethods = new JButton("show methods");
	private JComboBox<String> c = new JComboBox<String>();
	//点击show methods按钮过后显示被测试类的所有方法
	private ActionListener blShowMethods = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			className = classNameTxt.getText();
			try {
				tmut = new TestMethodUsingTestCase(className, filePath, testCaseFilePath);
				for(Method method: tmut.showMethods()) {
					c.addItem(method.getName().toString());
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	
	private String methodName;
	private JButton testMethod = new JButton("test");
	
	
	public AutoTestUI() {
		testCaseFileChooser.addActionListener(blTestCase);
		fileChooser.addActionListener(bl);
		showMethods.addActionListener(blShowMethods);
		c.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				methodName = ((JComboBox)e.getSource()).getSelectedItem().toString();
				System.out.println(methodName);
			}
		});
		testMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tmut.setMethodName(methodName);
				try {
					tmut.clearResultArray();
					tmut.test();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					tmut.compareAndWrite();
					JOptionPane.showMessageDialog(null, "TASK COMPLETE", "hey!", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		setLayout(new FlowLayout());
		add(testCaseFiletxt);
		add(testCaseFileChooser);
		add(txt);
		add(fileChooser);
		add(classNameTxt);
		add(showMethods);
		add(c);
		add(testMethod);
	}
	
	public static void main(String[] args) {
		SwingConsole.run(new AutoTestUI(), "file chooser" , 800, 800);
//		AutoTestUI ui = new AutoTestUI();
//		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		ui.setSize(800,400);
//		ui.setVisible(true);
//		ui.setTitle("Auto test");
	}
}
