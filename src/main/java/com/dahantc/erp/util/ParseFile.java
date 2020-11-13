package com.dahantc.erp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author 8536 解析文件工具
 */
public class ParseFile {

	private static final Logger logger = LogManager.getLogger(ParseFile.class);

	/**
	 * 解析excel2003文件
	 * 
	 * @param file
	 * @return
	 */
	public static List<String[]> parseExcel2003(File file) {
		return parseExcel2003(file, null);
	}

	/**
	 * 解析excel2003文件
	 * 
	 * @param file
	 * @param sheetFilter
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static List<String[]> parseExcel2003(File file, List<String> sheetFilter) {
		List<String[]> list = new ArrayList<String[]>();
		Workbook wb = null;
		try {
			wb = Workbook.getWorkbook(file);
			// 得到所有的sheet
			Sheet[] sheets = wb.getSheets();
			for (int i = 0; i < sheets.length; i++) {
				Sheet sheet = sheets[i];
				if (!sheet.isHidden() && sheetFilter != null && sheetFilter.contains(sheet.getName())) {
					// 得到每一个sheet的所有行数
					int rows = sheet.getRows();
					for (int j = 0; j < rows; j++) {
						// 得到每一行的所有单元格
						Cell[] cells = sheet.getRow(j);
						// 遍历每一行中所有单元格的内容
						String[] str = new String[cells.length];
						for (int k = 0; k < cells.length; k++) {
							str[k] = cells[k].getContents().trim();
						}
						list.add(str);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (null != wb) {
					wb.close();
					wb = null;
				}
			} catch (Exception e2) {
				logger.error(e2.getMessage(), e2);
			}
		}
		return list;
	}

	/**
	 * 解析excel2007文件
	 * 
	 * @param file
	 * @return
	 */
	public static List<String[]> parseExcel2007(File file) {
		return parseExcel2007(file, null, null);
	}

	public static List<String[]> parseExcel2007(File file, List<String> sheetFilter, String format) {
		List<String[]> list = new ArrayList<String[]>();
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = null;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			xwb = new XSSFWorkbook(inStream);
			// 循环工作表Sheet
			for (int numSheet = 0; numSheet < xwb.getNumberOfSheets(); numSheet++) {
				XSSFSheet xSheet = xwb.getSheetAt(numSheet);
				if (xSheet == null) {
					continue;
				}
				// 有filter，但本页不在filter中
				if (sheetFilter != null && !sheetFilter.contains(xSheet.getSheetName())) {
					continue;
				} // else 无filter，或本页在filter中
				if (!xwb.isSheetHidden(numSheet)) {
					// 循环行Row
					for (int rowNum = 0; rowNum <= xSheet.getLastRowNum(); rowNum++) {
						XSSFRow xRow = xSheet.getRow(rowNum);
						if (xRow == null) {
							continue;
						}
						if (xRow.getLastCellNum() < 1) {
							continue;
						}
						String[] str = new String[xRow.getLastCellNum()];
						// 循环列Cell
						for (int cellNum = 0; cellNum < xRow.getLastCellNum(); cellNum++) {
							XSSFCell xCell = xRow.getCell(cellNum);
							if (xCell == null) {
								continue;
							}
							if (xCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								str[cellNum] = xCell.getStringCellValue();
							} else if (xCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								if (StringUtils.isBlank(format)) {
									format = DateUtil.format2;
								}
								if (HSSFDateUtil.isCellDateFormatted(xCell)) {
									if (xCell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
										str[cellNum] = sdf.format(xCell.getDateCellValue());
									} else {
										Date date = xCell.getDateCellValue();
										str[cellNum] = DateUtil.convert(date, format);
									}
								} else if (xCell.getCellStyle().getDataFormat() == 58 || xCell.getCellStyle().getDataFormat() == 14
										|| xCell.getCellStyle().getDataFormat() == 57 || xCell.getCellStyle().getDataFormat() == 31) {
									double value = xCell.getNumericCellValue();
									Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
									str[cellNum] = DateUtil.convert(date, format);
								} else {
									str[cellNum] = NumberToTextConverter.toText(xCell.getNumericCellValue());
								}
							} else if (xCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
								str[cellNum] = "";
							} else if (xCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
								str[cellNum] = xCell.getErrorCellString();
							} else if (xCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
								str[cellNum] = String.valueOf(xCell.getBooleanCellValue());
							} else {
								str[cellNum] = xCell.getCellFormula();
							}
						}
						list.add(str);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (xwb != null) {
					xwb.close();
					xwb = null;
				}
				if (inStream != null) {
					inStream.close();
					inStream = null;
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return list;
	}

	/**
	 * 导出内容写入Excel文件
	 * 
	 * @param exportDatas
	 *            待导出数据
	 * @param file
	 *            导出文件
	 * @param titles
	 *            Excel title
	 * @throws Exception
	 */
	public static void exportDataToExcel(List<String[]> exportDatas, File file, String[] titles) throws Exception {

		List<String[]> errorDatasTemp = new ArrayList<String[]>();
		int g = 0;
		String sheelName = "sheet";
		WritableWorkbook wb = Workbook.createWorkbook(file);
		for (int h = 0; h < exportDatas.size(); h++) {
			errorDatasTemp.add(exportDatas.get(h));
			if (h != 0 && h % 65000 == 0) {
				g++;
				String sheelX = sheelName + g;
				// 处理
				printExcel(wb, errorDatasTemp, titles, sheelX);
				errorDatasTemp.clear();
			}
		}
		String sheelX = sheelName + (g + 1);
		// 处理
		printExcel(wb, errorDatasTemp, titles, sheelX);

		wb.write();
		wb.close();

	}

	public static void printExcel(WritableWorkbook wb, List<String[]> exportDatas, String[] titles, String sheelX)
			throws WriteException, IOException, RowsExceededException {

		// 定义Title样式
		WritableFont wfTitle = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
		WritableCellFormat wcfTitle = new WritableCellFormat(wfTitle);
		wcfTitle.setBackground(Colour.GRAY_25);
		wcfTitle.setAlignment(Alignment.CENTRE);

		// 定义Lable样式
		WritableFont wfLable = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
		WritableCellFormat wcfLable = new WritableCellFormat(wfLable);

		WritableSheet sheet = wb.createSheet(sheelX, 0);

		for (int i = 0; i < titles.length; i++) {
			// 设置列宽度
			sheet.setColumnView(i, 20);
			sheet.addCell(new Label(i, 0, titles[i], wcfTitle));

		}

		Label label = null;
		for (int i = 1; i <= exportDatas.size(); i++) {
			String[] datas = exportDatas.get(i - 1);
			for (int j = 0; j < datas.length; j++) {
				label = new Label(j, i, datas[j], wcfLable);
				sheet.addCell(label);
			}
		}
	}

	public static File zipFile(List<File> fileList, String zipName) throws IOException {
		File file = null;
		if (fileList != null && !fileList.isEmpty()) {
			// 导出的压缩包
			file = new File(zipName);
			if (!file.exists()) {
				file.createNewFile();
			}
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
			int len = 0;
			byte[] buffers = new byte[1024];
			try {
				for (File f : fileList) {
					ZipEntry entry = new ZipEntry(f.getName());
					zos.putNextEntry(entry);
					InputStream in = new FileInputStream(f);
					while ((len = in.read(buffers)) != -1) {
						zos.write(buffers, 0, len);
					}
					zos.closeEntry();
					in.close();
				}
				zos.flush();
			} catch (Exception e) {
				logger.error("压缩文件失败", e);
			} finally {
				if (zos != null) {
					try {
						zos.close();
					} catch (IOException e) {
						logger.error("", e);
					}
					zos = null;
				}
			}
		}
		return file;
	}
}
