package com.dahantc.erp.controller.role;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static String className = "SpecialAttendanceRecord";
	public static String packages = "specialAttendance";
	public static String classNameLow = "specialAttendanceRecord";
	public static String classNameChinese = "特殊出勤报备";

	public static void main(String[] args) {
		File file = new File("");
		String abPath = file.getAbsolutePath();
		creatServiceAndDao(abPath);
	}

	private static void creatServiceAndDao(String abPath) {
		String templateServiceFile = abPath + "/src/main/resources/temp/templateService.txt";
		String templateInterfaceFile = abPath + "/src/main/resources/temp/templateInterface.txt";
		String templateDaoFile = abPath + "/src/main/resources/temp/templateDao.txt";
		String templateDaoInterfaceFile = abPath + "/src/main/resources/temp/templateDaoInterface.txt";

		String newJavaFile = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/service/impl/" + className + "ServiceImpl.java";
		String newinterfaceFile = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/service/I" + className + "Service.java";
		String newJavaDaoFile = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/dao/impl/" + className + "DaoImpl.java";
		String newinterfaceDaoFile = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/dao/I" + className + "Dao.java";

		String filePath1 = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/service/impl";
		String filePath2 = abPath + "/src/main/java/com/dahantc/erp/vo/" + packages + "/dao/impl";
		creatPath(filePath1);
		creatPath(filePath2);
		creatJavaFile(templateServiceFile, newJavaFile);
		creatJavaFile(templateInterfaceFile, newinterfaceFile);
		creatJavaFile(templateDaoFile, newJavaDaoFile);
		creatJavaFile(templateDaoInterfaceFile, newinterfaceDaoFile);
	}

	private static void creatPath(String filePath1) {
		File file1 = new File(filePath1);
		if (!file1.exists()) {
			file1.mkdirs();
		}
	}

	private static void creatJavaFile(String templateServiceFile, String newJavaFile) {
		String str = readFile(templateServiceFile);

		FileWriter infoFile = null;
		try {

			String newStr = str.replace("##WW##", className).replace("##ss##", classNameLow).replace("##xx##", classNameChinese).replace("##dd##", packages);

			infoFile = new FileWriter(newJavaFile);
			infoFile.write(newStr);
			System.out.println("生成" + newJavaFile + "成功");
		} catch (Exception e1) {
			System.out.println("文件写入异常" + e1);
			e1.printStackTrace();
		} finally {
			try {
				infoFile.close();
			} catch (IOException e) {
				System.out.println("文件流关闭异常");
			}
		}
	}

	private static String readFile(String infoFileName) {
		File file = new File(infoFileName);
		BufferedReader reader = null;
		String str = new String();
		if (file.exists()) {
			String temp = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				while ((temp = reader.readLine()) != null) {
					str = str + temp + "\n";
				}
				reader.close();
			} catch (Exception e) {
				System.out.println("error" + e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						System.out.println("error" + e1);
					}
				}
			}
		}
		return str;
	}
}
