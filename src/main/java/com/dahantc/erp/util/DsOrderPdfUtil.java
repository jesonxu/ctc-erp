package com.dahantc.erp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;
import com.dahantc.erp.vo.dsOrderDetail.service.IDsOrderDetailService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class DsOrderPdfUtil {
	
	private static final Logger logger = LogManager.getLogger(DsOrderPdfUtil.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

//	private static SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy年MM月");

//	private static SimpleDateFormat sdfDayInt = new SimpleDateFormat("dd");

	private static SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");

	private static DecimalFormat df = new DecimalFormat(",###,##0.00"); // 保留2位小数

//	private static DecimalFormat dff = new DecimalFormat(",###,##0.0000"); // 保留4位小数

//	private static DecimalFormat dft = new DecimalFormat(",###,##0"); // 没有小数
	
	private static ISupplierService supplierService;
	
	private static IDsOrderService dsOrderService;
	
	private static IDsOrderDetailService dsOrderDetailService;
	
	private static HttpServletRequest request;
	
	@Value("${tittle.url}")
	private static String tittle;
	
	@Resource
	public void setSupplierService(ISupplierService supplierService) {
		DsOrderPdfUtil.supplierService = supplierService;
	}
	
	@Resource
	public void setDsOrderService(IDsOrderService dsOrderService) {
		DsOrderPdfUtil.dsOrderService = dsOrderService;
	}
	
	@Resource
	public void setDsOrderDetailService(IDsOrderDetailService dsOrderDetailService) {
		DsOrderPdfUtil.dsOrderDetailService = dsOrderDetailService;
	}
	
	@Resource
	public void setHttpServletRequest(HttpServletRequest request) {
		DsOrderPdfUtil.request = request;
	}
	
	/**
	 * 账单pdf
	 * 
	 */
	public static String createPurchaseOrderPdf(FlowEnt flowEnt, JSONObject flowJson) {
		if (flowJson == null) {
			logger.info("流程内容为空");
			return null;
		}
		String buyOrderId = flowJson.getString(Constants.DS_BUY_ORDER_NUMBER);
		Supplier supplier;
		try {
			supplier = supplierService.read(flowEnt.getSupplierId());
		} catch (ServiceException e1) {
			logger.info("查询供应商失败，供应商id：flowEnt.getSupplierId()");
			return null;
		}
		Document document = new Document(PageSize.A4, 36, 36, 126, 36);
		String filePath = null;
		try {
			String salesPdfPath = Constants.DS_BUY_ORDER_PATH + File.separator + "buyOrder";
			String date = sd.format(new Date());
			String pdfName = buyOrderId;
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 设置字体样式
			Font textFont = new Font(bfChinese, 10, Font.NORMAL);// 正常
//			Font textBoldFont = new Font(bfChinese, 10, Font.BOLD);// 加粗
			Font boldFont = new Font(bfChinese, 18, Font.BOLD); // 加粗

			File file = new File(salesPdfPath + File.separator + pdfName + "-temp.pdf");
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			PdfWriter.getInstance(document, new FileOutputStream(salesPdfPath + File.separator + pdfName + "-temp.pdf"));
			document.open();

			// 创建table对象
			PdfPTable table = new PdfPTable(2);
//			table.setSpacingBefore(10);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.setTotalWidth(new float[] { 220, 220 }); // 设置列宽
			table.setLockedWidth(true); // 锁定列宽

			PdfPCell cell = new PdfPCell();
			// 添加表格内容
			cell = PDFUtil.mergeCol("单笔采购订单", boldFont, 4);
			cell.setFixedHeight(60);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingTop(20);
			table.addCell(cell);

			PdfPCell nameCell = new PdfPCell();
			nameCell = PDFUtil.getPDFCell("  供应商公司名称：" + supplier.getCompanyName(), textFont);
			nameCell.setVerticalAlignment(Element.ALIGN_CENTER);
			nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(nameCell);
			
			String buyContractNo = flowJson.getString(Constants.DS_BUY_CONTRACT_NO);
			if (StringUtil.isBlank(buyContractNo)) {
				buyContractNo = "";
			}
			PdfPCell contractCell = new PdfPCell();
			contractCell = PDFUtil.getPDFCell("  框架合同编号：" + buyContractNo, textFont);
			contractCell.setVerticalAlignment(Element.ALIGN_CENTER);
			contractCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(contractCell);

			
			PdfPCell dateCell = new PdfPCell();
			Date buyDate = flowJson.getDate(Constants.DS_BUY_TIME);
			date = sdf.format(buyDate);
			dateCell = PDFUtil.getPDFCell("  采购日期：" + date, textFont);
			dateCell.setVerticalAlignment(Element.ALIGN_CENTER);
			dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(dateCell);

			PdfPCell orderCell = new PdfPCell();
			orderCell = PDFUtil.getPDFCell("  采购订单编号：" + buyOrderId, textFont);
			orderCell.setVerticalAlignment(Element.ALIGN_CENTER);
			orderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(orderCell);
			String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
			DsOrder dsOrder = dsOrderService.read(orderId);
			String sendAdderss = dsOrder.getSendAddress();
			if (StringUtil.isBlank(sendAdderss)) {
				sendAdderss = "";
			}
			String contactPerson = dsOrder.getContactPerson();
			if (StringUtil.isBlank(contactPerson)) {
				contactPerson = "";
			}
			String contactNo = dsOrder.getContactNo();
			if (StringUtil.isBlank(contactNo)) {
				contactNo = "";
			}
			PdfPCell addressCell = new PdfPCell();
			// 添加表格内容
			addressCell = PDFUtil.mergeCol("  收货信息：乙方应在" + sdf.format(dsOrder.getDueTime()) + "前将全部采购产品送达至下述交货地点。"+ "\n" 
					+ "  收货地址：" + sendAdderss + "\n" 
					+ "  联系人  ：" + contactPerson + "\n" 
					+ "  联系电话：" + contactNo, textFont, 2);
			addressCell.setFixedHeight(60);
			addressCell.setVerticalAlignment(Element.ALIGN_CENTER);
			addressCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(addressCell);
			PdfPCell remarkCell = new PdfPCell();
			// 添加表格内容
			String remark = flowJson.getString(Constants.DAHAN_REMARK_KEY);
			if (StringUtil.isBlank(remark)) {
				remark = "";
			}
			remarkCell = PDFUtil.mergeCol("  备注：" + remark, textFont, 2);
			remarkCell.setFixedHeight(30);
			remarkCell.setVerticalAlignment(Element.ALIGN_CENTER);
			remarkCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(remarkCell);
			
			document.add(table);
			
			// 创建table对象
			// 添加商品表格
			PdfPTable productTable = new PdfPTable(8);
//			productTable.setSpacingBefore(10);
			productTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			productTable.setTotalWidth(new float[] { 40, 80, 50, 50, 50, 50, 60, 60 }); // 设置列宽
			productTable.setLockedWidth(true); // 锁定列宽
			
			productTable.addCell(PDFUtil.getPDFCell("序号", textFont));
			productTable.addCell(PDFUtil.getPDFCell("物品名称", textFont));
			productTable.addCell(PDFUtil.getPDFCell("规格型号", textFont));
			productTable.addCell(PDFUtil.getPDFCell("物流费", textFont));
			productTable.addCell(PDFUtil.getPDFCell("数量", textFont));
			productTable.addCell(PDFUtil.getPDFCell("单价", textFont));
			productTable.addCell(PDFUtil.getPDFCell("总价", textFont));
			productTable.addCell(PDFUtil.getPDFCell("备注", textFont));

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("orderId", Constants.ROP_EQ, orderId));
	        filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, flowEnt.getSupplierId()));
			List<DsOrderDetail> dsOrderDetails = dsOrderDetailService.queryAllBySearchFilter(filter);
			int i = 1;
			for (DsOrderDetail dsOrderDetail : dsOrderDetails) {
				DecimalFormat df = new DecimalFormat("#.00"); 
				BigDecimal price = dsOrderDetail.getPrice();
				productTable.addCell(PDFUtil.getPDFCell( String.valueOf(i), textFont));
				productTable.addCell(PDFUtil.getPDFCell( dsOrderDetail.getProductName(), textFont));
				productTable.addCell(PDFUtil.getPDFCell( dsOrderDetail.getFormat(), textFont));
				productTable.addCell(PDFUtil.getPDFCell( dsOrderDetail.getLogisticsCost().toPlainString(), textFont));
				productTable.addCell(PDFUtil.getPDFCell( String.valueOf(dsOrderDetail.getAmount()), textFont));
				productTable.addCell(PDFUtil.getPDFCell( df.format(price), textFont));
				productTable.addCell(PDFUtil.getPDFCell( String.valueOf(dsOrderDetail.getTotal()), textFont));
				productTable.addCell(PDFUtil.getPDFCell( dsOrderDetail.getRemark(), textFont));
				i++;
			}
			
			document.add(productTable);
			
			// 添加总价表格
			PdfPTable totalTable = new PdfPTable(3);
//			totalTable.setSpacingBefore(10);
			totalTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			totalTable.setTotalWidth(new float[] { 320, 60, 60 }); // 设置列宽
			totalTable.setLockedWidth(true); // 锁定列宽
			String total = flowJson.getString(Constants.DS_TOTAL);
			
			if (StringUtil.isNotBlank(total)) {
				BigDecimal totalBill = new BigDecimal(total);
				totalTable.addCell(PDFUtil.getPDFCell("  合计（大写）：" + Money2ChineseUtil.convert(totalBill) , textFont));
				totalTable.addCell(PDFUtil.getPDFCell(df.format(totalBill), textFont));
				totalTable.addCell(PDFUtil.getPDFCell("", textFont));
			}
			document.add(totalTable);
			
			// 创建table对象
			PdfPTable promiseTable = new PdfPTable(2);
//			promiseTable.setSpacingBefore(10);
			promiseTable.setHorizontalAlignment(Element.ALIGN_CENTER);
			promiseTable.setTotalWidth(new float[] { 220, 220 }); // 设置列宽
			promiseTable.setLockedWidth(true); // 锁定列宽

			// 添加表格内容
			String productPromise = flowJson.getString(Constants.DS_PRODUCT_PROMISE);
			if (StringUtil.isBlank(productPromise)) {
				productPromise = "";
			}
			String packagePromise = flowJson.getString(Constants.DS_PACKAGE_PROMISE);
			if (StringUtil.isBlank(packagePromise)) {
				packagePromise = "";
			}
			String logisticsPromise = flowJson.getString(Constants.DS_LOGISTICS_PROMISE);
			if (StringUtil.isBlank(logisticsPromise)) {
				logisticsPromise = "";
			}
			PdfPCell promiseCell = new PdfPCell();
			promiseCell = PDFUtil.mergeCol("  采购订单对货物质量的约定：" + "\n" + "\n"
					+ "  1)包装约定：" + packagePromise + "\n" + "\n"
					+ "  2)产品约定：" + productPromise + "\n" + "\n"
					+ "  3)物流约定：" + logisticsPromise, textFont, 2);
			promiseCell.setFixedHeight(100);
			promiseCell.setVerticalAlignment(Element.ALIGN_CENTER);
			promiseCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			promiseTable.addCell(promiseCell);
			
			// 添加表格内容
			PdfPCell stampCell = new PdfPCell();
			stampCell = PDFUtil.mergeCol("  卖方（盖章）：" + "\n"+ "\n"
					+ "  授权代表：" + "" + "\n"+ "\n"
					+ "  日期：" + "", textFont, 2);
			stampCell.setFixedHeight(80);
			stampCell.setVerticalAlignment(Element.ALIGN_CENTER);
			stampCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			promiseTable.addCell(stampCell);
			document.add(promiseTable);
			// 关闭文档
			document.close();
			StringBuffer url = request.getRequestURL();  
			String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();  
			String logoImagePath = tempContextUrl + "common/imgs/dahan.png";
			String logoTittlePath = tempContextUrl + "common/imgs/tittle.png";
			addPdfImgMark(salesPdfPath + File.separator + pdfName + "-temp.pdf", salesPdfPath + File.separator + pdfName + ".pdf", logoImagePath, logoTittlePath);
			// 返回文件地址
			filePath = salesPdfPath + File.separator + pdfName + ".pdf";
		} catch (Exception e) {
			logger.error("账单生成异常：", e);
		}
		return filePath;
	}
//
//	public static URL loadMetadata(ClassLoader classLoader, String path) {
//		try {
//			Enumeration<URL> urls = (classLoader != null) ? classLoader.getResources(path) : ClassLoader.getSystemResources(path);
//			UrlResource resource = new UrlResource(urls.nextElement());
//			return resource.getURL();
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//		return null;
//	}
	
	
	/**
	 * * @desc 添加水印
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:18:02 /**
	 * @param InPdfFile
	 *            要加水印的原pdf文件路径
	 * @param outPdfFile
	 *            加了水印后要输出的路径
	 * @param markImagePath
	 *            水印图片路径
	 * @param imgWidth
	 *            图片横坐标
	 * @param imgHeight
	 *            图片纵坐标
	 * @throws Exception
	 * @see void
	 */
	public static boolean addPdfImgMark(String InPdfFile, String outPdfFile, String markImagePath, String tittleImagePath) {
		boolean result = false;
		try {
			PdfReader reader = new PdfReader(InPdfFile, "PDF".getBytes());
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(new File(outPdfFile)));

//			String pwd = UUID.randomUUID().toString().replace("-", "");
//			int permissions = PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING;
//			stamp.setEncryption(null, pwd.getBytes(), permissions, false);

			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(1f);// 透明度设置

			int imgWidth = 50;
			int imgHeight = 790;

			Image img = Image.getInstance(tittleImagePath);// 插入图片水印

			img.setAbsolutePosition(imgWidth, imgHeight); // 坐标
			img.scaleAbsolute(496,45);//自定义大小

			int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
			PdfContentByte under;
			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				// under = stamp.getOverContent(i);//水印在之前文本上
				under.setGState(gs1);// 图片水印 透明度
				under.addImage(img);// 图片水印
			}
			
			PdfGState gs2 = new PdfGState();
			gs2.setFillOpacity(0.2f);// 透明度设置

			int imgWidth2 = 20;
			int imgHeight2 = 480;

			Image img2 = Image.getInstance(markImagePath);// 插入图片水印

			img2.setAbsolutePosition(imgWidth2, imgHeight2); // 坐标
			img2.setRotation(-20);// 旋转 弧度
			img2.setRotationDegrees(-10);// 旋转 角度
			// img.scaleAbsolute(200,100);//自定义大小
			img2.scalePercent(21);// 依照比例缩放

			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				// under = stamp.getOverContent(i);//水印在之前文本上
				under.setGState(gs2);// 图片水印 透明度
				under.addImage(img2);// 图片水印
			}
			int imgWidth3 = 320;
			int imgHeight3 = 300;
			Image img3 = Image.getInstance(markImagePath);// 插入图片水印

			img3.setAbsolutePosition(imgWidth3, imgHeight3); // 坐标
			img3.setRotation(-20);// 旋转 弧度
			img3.setRotationDegrees(-10);// 旋转 角度
			img3.scalePercent(21);// 依照比例缩放

			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				under.setGState(gs2);// 图片水印 透明度
				under.addImage(img3);// 图片水印

			}
			stamp.close();// 关闭
			// // 删除不带水印文件
			File tempfile = new File(InPdfFile);
			if (tempfile.exists()) {
				tempfile.delete();
			}
			result = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * * @desc 添加页眉
	 * 
	 * @author 8515
	 * @date 2018年8月16日 下午5:18:02 /**
	 * @param InPdfFile
	 *            要加页眉的原pdf文件路径
	 * @param outPdfFile
	 *            加了页眉后要输出的路径
	 * @param markImagePath
	 *            页眉图片路径
	 * @param imgWidth
	 *            图片横坐标
	 * @param imgHeight
	 *            图片纵坐标
	 * @throws Exception
	 * @see void
	 */
	public static boolean addPdfTittle(String InPdfFile, String outPdfFile, String markImagePath) {
		boolean result = false;
		try {
			PdfReader reader = new PdfReader(InPdfFile, "PDF".getBytes());
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(new File(outPdfFile)));

//			String pwd = UUID.randomUUID().toString().replace("-", "");
//			int permissions = PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING;
//			stamp.setEncryption(null, pwd.getBytes(), permissions, false);

			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(1f);// 透明度设置

			int imgWidth = 20;
			int imgHeight = 20;

			Image img = Image.getInstance(markImagePath);// 插入图片水印

			img.setAbsolutePosition(imgWidth, imgHeight); // 坐标
			img.scaleAbsolute(540,40);//自定义大小

			int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
			PdfContentByte under;
			for (int i = 1; i <= pageSize; i++) {
				under = stamp.getUnderContent(i);// 水印在之前文本下
				// under = stamp.getOverContent(i);//水印在之前文本上
				under.setGState(gs1);// 图片水印 透明度
				under.addImage(img);// 图片水印
			}
			stamp.close();// 关闭
			// // 删除不带水印文件
			File tempfile = new File(InPdfFile);
			if (tempfile.exists()) {
				tempfile.delete();
			}
			result = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}
	
}