package com.dahantc.erp.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.user.entity.User;

public class CreateBillExcelUtil {

	private static final Logger logger = LogManager.getLogger(CreateBillExcelUtil.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

	private static DecimalFormat df = new DecimalFormat(",###,##0.00"); // 保留2位小数

	private static DecimalFormat dff = new DecimalFormat(",###,##0.000000"); // 保留6位小数

	private static DecimalFormat dft = new DecimalFormat(",###,##0"); // 没有小数

	public static void createBillExcel(BillInfo billInfo, BankAccount bankAccount, String filePath, Customer customer, User saler) {
		df.setRoundingMode(RoundingMode.DOWN);// 舍去分的后一位
		FileOutputStream fileOut = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(Constants.EXCEL_BILL_TITLE);

			// 样式：水平、垂直居中（大汉三通短信云对账单）
			HSSFCellStyle cs = wb.createCellStyle();
			cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

			// 字体:
			HSSFFont font = wb.createFont();
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 20);// 设置字体大小
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
			cs.setFont(font);// 要用到的字体格式

			// 样式1：水平、垂直居中、大字、加粗
			HSSFCellStyle cs1 = wb.createCellStyle();
			cs1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 字体:
			HSSFFont font1 = wb.createFont();
			font1.setFontName("宋体");
			font1.setFontHeightInPoints((short) 11);
			font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cs1.setFont(font1);

			// 样式2：不加粗
			HSSFCellStyle cs2 = wb.createCellStyle();
			cs2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 字体:
			HSSFFont font2 = wb.createFont();
			font2.setFontName("宋体");
			font2.setFontHeightInPoints((short) 11);
			cs2.setFont(font2);

			// 样式3：加粗，居右
			HSSFCellStyle cs3 = wb.createCellStyle();
			cs3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cs3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
			cs3.setFont(font1);// 要用到的字体格式

			// 样式4：加粗，居中
			HSSFCellStyle cs4 = wb.createCellStyle();
			cs4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cs4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
			cs4.setFont(font1);// 要用到的字体格式

			// 样式5：不加粗，居中
			HSSFCellStyle cs5 = wb.createCellStyle();
			cs5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cs5.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cs5.setFont(font2);

			// 样式6：不加粗，居右
			HSSFCellStyle cs6 = wb.createCellStyle();
			cs6.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cs6.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cs6.setFont(font2);

			HSSFFont font3 = wb.createFont();
			font3.setFontName("宋体");
			font3.setFontHeightInPoints((short) 11);
			font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font3.setColor(HSSFColor.RED.index);

			// 样式7: 加粗，居中，红色
			HSSFCellStyle cs7 = wb.createCellStyle();
			cs7.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cs7.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
			cs7.setFont(font3);

			// 样式8: 加粗，居中，红色
			HSSFCellStyle cs8 = wb.createCellStyle();
			cs8.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cs8.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
			cs8.setFont(font3);

			// 第一行
			HSSFRow row = createRow(sheet, 0);
			createCell(row, 0, Constants.EXCEL_BILL_TITLE, cs);

			// 第二行
			row = createRow(sheet, 1);
			createCell(row, 0, "客户：" + billInfo.getCompanyName(), cs4);

			// 客户联系人
			row = createRow(sheet, 2);
			createCell(row, 0, "客户联系人：" + billInfo.getContactsName(), cs4);

			row = createRow(sheet, 3);
			// 联系人部门
			createCell(row, 0, "部门", cs4);
			if (StringUtil.isNotBlank(customer.getContactDept())) {
				createCell(row, 1, customer.getContactDept(), cs4);
			} else {
				createCell(row, 1, "", cs4);
			}
			// 联系人职务
			createCell(row, 2, "联系人职务", cs4);
			if (StringUtil.isNotBlank(customer.getContactPosition())) {
				createCell(row, 3, customer.getContactPosition(), cs4);
			} else {
				createCell(row, 3, "", cs4);
			}

			row = createRow(sheet, 4);
			// 联系人座机
			createCell(row, 0, "座机", cs4);
			if (StringUtil.isNotBlank(customer.getContactTelephone())) {
				createCell(row, 1, customer.getContactTelephone(), cs4);
			} else {
				createCell(row, 1, "", cs4);
			}
			// 联系人手机
			createCell(row, 2, "联系人手机", cs4);
			if (StringUtil.isNotBlank(billInfo.getPhone())) {
				createCell(row, 3, billInfo.getPhone(), cs4);
			} else {
				createCell(row, 3, "", cs4);
			}

			row = createRow(sheet, 5);
			String billStartTime = sdf.format(billInfo.getBillDate());
			String billEndTime = sdf.format(DateUtil.getThisMonthFinal(billInfo.getBillDate()));
			createCell(row, 0, "计费周期：" + billStartTime + "---" + billEndTime, cs4);

			row = createRow(sheet, 6);
			createCell(row, 0, "账单编号：" + billInfo.getBillNumber(), cs4);

			int rowNum = 7;

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, "账号", cs4);
			createCell(row, 1, "单价（元/条）", cs4);
			createCell(row, 2, "计费条数（条）", cs4);
			createCell(row, 3, "金额（元）", cs4);

			if (!CollectionUtils.isEmpty(billInfo.getAccountInfos())) {
				for (DetailInfo detail : billInfo.getAccountInfos()) {
					row = createRow(sheet, rowNum);
					rowNum++;
					// 一个账号的金额
					createCell(row, 0, detail.getAccountName(), cs5);
					createCell(row, 1, dff.format(detail.getUnitPrice()), cs5);
					createCell(row, 2, dft.format(detail.getFeeCount()), cs6);
					createCell(row, 3, "¥" + df.format(detail.getFee()), cs6);
				}
			}

			row = createRow(sheet, rowNum);
			rowNum++;
			// 一个账号的金额
			createCell(row, 0, "实际计费", cs4);
			createCell(row, 1, dff.format(billInfo.getRealFeeInfo().getUnitPrice()), cs5);
			createCell(row, 2, dft.format(billInfo.getRealFeeInfo().getFeeCount()), cs6);
			createCell(row, 3, "¥" + df.format(billInfo.getRealFeeInfo().getFee().setScale(2, BigDecimal.ROUND_UP)), cs6);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, "本期应付（大写金额）", cs4);
			createCell(row, 1, Money2ChineseUtil.convert(billInfo.getRealFeeInfo().getFee().setScale(2, BigDecimal.ROUND_UP)), cs7);
			createCell(row, 3, "¥" + df.format(billInfo.getRealFeeInfo().getFee()), cs8);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, "出账日期：" + sdf.format(billInfo.getCreateDate()) + "       最后付款日期：" + sdf.format(billInfo.getFinalPayDate()), cs4);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, "收款银行账号信息", cs4);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, bankAccount.getAccountName(), cs5);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, bankAccount.getAccountBank(), cs5);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, bankAccount.getBankAccount(), cs5);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, bankAccount.getCompanyAddress(), cs5);

			row = createRow(sheet, rowNum);
			rowNum++;
			createCell(row, 0, "销售经理：" + billInfo.getSaleName(), cs4);
			rowNum++;
			createCell(row, 0, "销售经理座机：" + (saler == null || StringUtils.isBlank(saler.getContactPhone()) ? "" : saler.getContactPhone()), cs4);
			rowNum++;
			createCell(row, 0, "销售经理手机：" + billInfo.getSalePhone(), cs4);

			row = createRow(sheet, rowNum);
			createCell(row, 0, "温馨提示：本单据为您电子渠道的对账单，请妥善保管。", cs2);

			// 设置列宽
			sheet.setColumnWidth(0, 20 * 300);
			sheet.setColumnWidth(1, 20 * 300);
			sheet.setColumnWidth(2, 20 * 300);
			sheet.setColumnWidth(3, 20 * 300);

			// 设置合并
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 10, rowNum - 10, 1, 2));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 9, rowNum - 9, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 8, rowNum - 8, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 7, rowNum - 7, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 6, rowNum - 6, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 5, rowNum - 5, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 4, rowNum - 4, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 3, rowNum - 3, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 2, rowNum - 2, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));

			// 添加外边框
			CellRangeAddress region = new CellRangeAddress(0, rowNum, 0, 3);
			setRegionBorder(HSSFCellStyle.BORDER_THIN, region, sheet, wb);

			fileOut = new FileOutputStream(filePath);
			wb.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e1) {
					logger.error("Excel账单文件生成失败!" + e);
				}
			}
			logger.error("Excel账单文件生成失败!" + e);
		}
	}

	/**
	 * * @desc 设置行
	 * 
	 * @author 8515
	 * @date 2019年3月13日 上午11:34:56
	 */
	private static HSSFRow createRow(HSSFSheet sheet, int rowNum) {
		HSSFRow row = sheet.createRow((short) rowNum);
		row.setHeight((short) 530);
		return row;
	}

	/**
	 * * @desc 设置列
	 * 
	 * @author 8515
	 * @date 2019年3月13日 上午11:34:56
	 */
	private static void createCell(HSSFRow row, int cellnum, String value, HSSFCellStyle cs) {
		HSSFCell cell6 = row.createCell(cellnum);
		cell6.setCellValue(value);
		cell6.setCellStyle(cs);
	}

	/**
	 * * @desc 添加外边框
	 * 
	 * @author 8515
	 * @date 2019年3月13日 上午11:14:52
	 */
	private static void setRegionBorder(int border, CellRangeAddress region, Sheet sheet, Workbook wb) {
		RegionUtil.setBorderBottom(border, region, sheet, wb);
		RegionUtil.setBorderLeft(border, region, sheet, wb);
		RegionUtil.setBorderRight(border, region, sheet, wb);
		RegionUtil.setBorderTop(border, region, sheet, wb);

	}

}