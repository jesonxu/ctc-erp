package com.dahantc.erp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.UrlResource;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.user.entity.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class IText5PdfUtil {

	protected static final Logger logger = LogManager.getLogger(IText5PdfUtil.class);

	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

	protected static SimpleDateFormat sdfDayInt = new SimpleDateFormat("dd");

	protected static DecimalFormat df = new DecimalFormat(",###,##0.00"); // 保留2位小数

	protected static DecimalFormat dff = new DecimalFormat(",###,##0.000000"); // 保留4位小数

	protected static DecimalFormat dft = new DecimalFormat(",###,##0"); // 没有小数

	public static String getNextCopyFileName(String oldName) {
		if (StringUtils.isNotBlank(oldName)) {
			String oldPath = oldName.substring(0, oldName.lastIndexOf("."));
			String extendName = oldName.substring(oldName.lastIndexOf("."));
			char lastChar = oldPath.charAt(oldPath.length() - 1);
			if (lastChar >= 65) {
				return oldPath.substring(0, oldPath.length() - 1) + (char) (lastChar + 1) + extendName;
			} else {
				return oldPath + "-A" + extendName;
			}
		}
		return null;
	}

	public static String getFinalFileName(String oldName) {
		if (StringUtils.isNotBlank(oldName)) {
			String oldPath = oldName.substring(0, oldName.lastIndexOf("."));
			String extendName = oldName.substring(oldName.lastIndexOf("."));
			char lastChar = oldPath.charAt(oldPath.length() - 1);
			if (lastChar >= 65) {
				return oldPath.substring(0, oldPath.length() - 2) + extendName;
			} else {
				return oldPath + extendName;
			}
		}
		return null;
	}

	public static void createBillPdf(BillInfo billInfo, BankAccount bankAccount, String filePath, Customer customer, User saler) {
		df.setRoundingMode(RoundingMode.DOWN);// 舍去分的后一位

		Document document = new Document(PageSize.A4, 36, 36, 126, 36);
		File file = null;
		try {
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 设置字体样式
			Font textFont = new Font(bfChinese, 10, Font.NORMAL);// 正常
			Font textBoldFont = new Font(bfChinese, 10, Font.BOLD);// 加粗
			Font boldFont = new Font(bfChinese, 18, Font.BOLD); // 加粗

			file = File.createTempFile("temp", ".pdf");
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			Paragraph p0 = new Paragraph();

			Paragraph p1 = new Paragraph("", textFont);
			p1.setLeading(40);
			p0.add(p1);

			// 创建table对象
			PdfPTable table = new PdfPTable(4);
			table.setSpacingBefore(10);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.setTotalWidth(new float[] { 110, 110, 110, 110 }); // 设置列宽
			table.setLockedWidth(true); // 锁定列宽

			PdfPCell cell = new PdfPCell();
			// 添加表格内容
			cell = PDFUtil.mergeCol(Constants.EXCEL_BILL_TITLE, boldFont, 4);
			cell.setFixedHeight(60);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingTop(25);
			table.addCell(cell);

			table.addCell(PDFUtil.mergeCol("客户：" + billInfo.getCompanyName(), textBoldFont, 4));

			// 客户联系人
			// 联系人
			table.addCell(PDFUtil.mergeCol("客户联系人：" + (StringUtils.isBlank(billInfo.getContactsName()) ? "" : billInfo.getContactsName()), textBoldFont, 4));

			// 联系人部门
			table.addCell(PDFUtil.getPDFCell("部门", textBoldFont));
			if (StringUtil.isNotBlank(customer.getContactDept())) {
				table.addCell(PDFUtil.getPDFCell(customer.getContactDept(), textBoldFont));
			} else {
				table.addCell(PDFUtil.getPDFCell("", textBoldFont));
			}
			// 联系人职务
			table.addCell(PDFUtil.getPDFCell("联系人职务", textBoldFont));
			if (StringUtil.isNotBlank(customer.getContactPosition())) {
				table.addCell(PDFUtil.getPDFCell(customer.getContactPosition(), textBoldFont));
			} else {
				table.addCell(PDFUtil.getPDFCell("", textBoldFont));
			}
			// 联系人座机
			table.addCell(PDFUtil.getPDFCell("座机", textBoldFont));
			if (StringUtil.isNotBlank(customer.getContactTelephone())) {
				table.addCell(PDFUtil.getPDFCell(customer.getContactTelephone(), textBoldFont));
			} else {
				table.addCell(PDFUtil.getPDFCell("", textBoldFont));
			}
			// 联系人手机
			table.addCell(PDFUtil.getPDFCell("联系人手机", textBoldFont));
			if (StringUtil.isNotBlank(billInfo.getPhone())) {
				table.addCell(PDFUtil.getPDFCell(billInfo.getPhone(), textBoldFont));
			} else {
				table.addCell(PDFUtil.getPDFCell("", textBoldFont));
			}

			table.addCell(PDFUtil.mergeCol("账单编号：" + billInfo.getBillNumber(), textBoldFont, 4));

			String billStartTime = sdf.format(billInfo.getBillDate());
			String billEndTime = sdf.format(DateUtil.getThisMonthFinal(billInfo.getBillDate()));
			table.addCell(PDFUtil.mergeCol("计费周期：" + billStartTime + "---" + billEndTime, textBoldFont, 4));

			table.addCell(PDFUtil.getPDFCell("账号", textBoldFont));
			table.addCell(PDFUtil.getPDFCell("单价（元/条）", textBoldFont));
			table.addCell(PDFUtil.getPDFCell("计费条数（条）", textBoldFont));
			table.addCell(PDFUtil.getPDFCell("金额（元）", textBoldFont));

			if (!CollectionUtils.isEmpty(billInfo.getAccountInfos())) {
				for (DetailInfo detail : billInfo.getAccountInfos()) {
					table.addCell(PDFUtil.getPDFCell(detail.getAccountName(), textFont));
					table.addCell(PDFUtil.getPDFCellRight(dff.format(detail.getUnitPrice()), textFont));
					table.addCell(PDFUtil.getPDFCellRight(dft.format(detail.getFeeCount()), textFont));
					table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(detail.getFee()), textFont));
				}
			}

			table.addCell(PDFUtil.getPDFCell("实际计费", textBoldFont));
			table.addCell(PDFUtil.getPDFCellRight(dff.format(billInfo.getRealFeeInfo().getUnitPrice()), textFont));
			table.addCell(PDFUtil.getPDFCellRight(dft.format(billInfo.getRealFeeInfo().getFeeCount()), textFont));
			table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(billInfo.getRealFeeInfo().getFee().setScale(2, BigDecimal.ROUND_UP)), textFont));

			table.addCell(PDFUtil.getPDFCell("本期应付（大写金额）", textBoldFont));
			table.addCell(PDFUtil.mergeCol(Money2ChineseUtil.convert(billInfo.getRealFeeInfo().getFee()), textBoldFont, 2));
			table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(billInfo.getRealFeeInfo().getFee().setScale(2, BigDecimal.ROUND_UP)), textFont));

			table.addCell(PDFUtil.mergeCol(
					"出账日期：" + sdf.format(billInfo.getCreateDate()) + "                                   最后付款日期：" + sdf.format(billInfo.getFinalPayDate()),
					textBoldFont, 4));

			table.addCell(PDFUtil.mergeCol("收款银行账号信息", textBoldFont, 4));
			table.addCell(PDFUtil.mergeCol(bankAccount.getAccountName(), textFont, 4));
			table.addCell(PDFUtil.mergeCol(bankAccount.getAccountBank(), textFont, 4));
			table.addCell(PDFUtil.mergeCol(bankAccount.getBankAccount(), textFont, 4));
			table.addCell(PDFUtil.mergeCol(bankAccount.getCompanyAddress(), textFont, 4));

			table.addCell(PDFUtil.mergeCol("销售经理：" + billInfo.getSaleName(), textBoldFont, 4));
			table.addCell(
					PDFUtil.mergeCol("销售经理座机：" + (saler == null || StringUtils.isBlank(saler.getContactPhone()) ? "" : saler.getContactPhone()), textBoldFont, 4));
			table.addCell(PDFUtil.mergeCol("销售经理手机：" + billInfo.getSalePhone(), textBoldFont, 4));

			table.addCell(PDFUtil.mergeCol("温馨提示：本单据为您电子渠道的对账单，请妥善保管。", textBoldFont, 4, 0));

			p0.add(table);
			document.add(p0);

			// 关闭文档
			document.close();

			// 获取水印文件路径
			URL logoImageURL = loadMetaData(IText5PdfUtil.class.getClassLoader(), "static/common/imgs/dahan.png");
			addPdfImgMark(file, filePath, logoImageURL);

		} catch (Exception e) {
			logger.error("账单生成异常：", e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	public static URL loadMetaData(ClassLoader classLoader, String path) {
		try {
			Enumeration<URL> urls = (classLoader != null) ? classLoader.getResources(path) : ClassLoader.getSystemResources(path);
			UrlResource resource = new UrlResource(urls.nextElement());
			return resource.getURL();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	protected static boolean addPdfImgMark(File inFile, String outPdfFile, URL logoImageURL) {
		boolean result = false;
		try {
			PdfReader reader = new PdfReader(inFile.getPath(), "PDF".getBytes());
			File outFile = new File(outPdfFile);
			if (!outFile.exists()) {
				if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
			}
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outFile));

			String pwd = UUID.randomUUID().toString().replace("-", "");
			int permissions = PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING;
			stamp.setEncryption(null, pwd.getBytes(), permissions, false);

			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(0.2f);// 透明度设置

			int imgWidth = 20;
			int imgHeight = 480;

			Image img = Image.getInstance(logoImageURL);// 插入图片水印

			img.setAbsolutePosition(imgWidth, imgHeight); // 坐标
			img.setRotation(-20);// 旋转 弧度
			img.setRotationDegrees(-10);// 旋转 角度
			// img.scaleAbsolute(200,100);//自定义大小
			img.scalePercent(21);// 依照比例缩放

			int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
			PdfContentByte under;
			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				// under = stamp.getOverContent(i);//水印在之前文本上
				under.setGState(gs1);// 图片水印 透明度
				under.addImage(img);// 图片水印
			}
			int imgWidth2 = 320;
			int imgHeight2 = 300;
			Image img2 = Image.getInstance(logoImageURL);// 插入图片水印

			img2.setAbsolutePosition(imgWidth2, imgHeight2); // 坐标
			img2.setRotation(-20);// 旋转 弧度
			img2.setRotationDegrees(-10);// 旋转 角度
			img2.scalePercent(21);// 依照比例缩放

			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				under.setGState(gs1);// 图片水印 透明度
				under.addImage(img2);// 图片水印

			}
			stamp.close();// 关闭
			result = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * * @desc 获取某个月第一天
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:37:28
	 */
	public static Date preMonthFirstDate(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, day);
		return calendar.getTime();
	}

	/**
	 * * @desc 获取某个月月份
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:37:28
	 */
	public static int getMonth(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, day);

		int month = calendar.get(Calendar.MONTH) + 1;

		return month;
	}

	/**
	 * * @desc 获取某个月最后一天
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:37:28
	 */
	public static String preMonthLastDate(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, day);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());
	}

	/**
	 * * @desc 获取当前月最后一天
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:37:28
	 */
	public static int getLastDay() {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		int last = Integer.parseInt(sdfDayInt.format(ca.getTime()));
		return last;
	}

}