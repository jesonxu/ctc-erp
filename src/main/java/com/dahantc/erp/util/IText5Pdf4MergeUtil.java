package com.dahantc.erp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.dto.bill.BillDataDetailDto;
import com.dahantc.erp.dto.bill.DateDetail;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class IText5Pdf4MergeUtil extends IText5PdfUtil {

	private static IProductTypeService productTypeService;

	private static final Logger logger = LogManager.getLogger(IText5Pdf4MergeUtil.class);

	/**
	 * 合并账单生成对账单
	 * 
	 * @param billList
	 *            勾选的账单
	 * @param billInfoMap
	 *            账单数据对象
	 * @param realFeeInfo
	 *            实际计费
	 * @param bankAccount
	 *            我司银行信息
	 * @param filePath
	 *            生成的文件存放路径
	 * @param optionList
	 *            勾选的选项，包含电子账号、数据详情
	 * @param billDataDetailMap
	 *            每个账单的每个账号在每天的发送量信息
	 * @param customer
	 *            客户
	 * @param saler
	 *            销售
	 * @return
	 */
	public static boolean createBillPdf(List<ProductBills> billList, Map<String, BillInfo> billInfoMap, DetailInfo realFeeInfo, BankAccount bankAccount,
			String filePath, List<String> optionList, Map<String, BillDataDetailDto> billDataDetailMap, Customer customer, User saler) {
		df.setRoundingMode(RoundingMode.DOWN);// 舍去分的后一位

		Document document = new Document(PageSize.A4, 36, 36, 72, 36);
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

			// 对账单部分
			if (optionList.contains(Constants.BILL_OPTION_BILL_FILE)) {
				Paragraph p0 = new Paragraph();
				// 创建table对象
				PdfPTable table = new PdfPTable(6);
				table.setSpacingBefore(10);
				table.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.setTotalWidth(new float[] { 70, 130, 70, 90, 90, 90 }); // 设置列宽
				table.setLockedWidth(true); // 锁定列宽

				PdfPCell cell = new PdfPCell();
				// 添加表格内容
				cell = PDFUtil.mergeCol(Constants.EXCEL_BILL_TITLE, boldFont, 6);
				cell.setFixedHeight(60);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPaddingTop(25);
				table.addCell(cell);
				BillInfo billInfo = billInfoMap.values().iterator().next();
				table.addCell(PDFUtil.mergeCol("客户：" + billInfo.getCompanyName(), textBoldFont, 6));

				// 客户联系人
				// 联系人
				table.addCell(
						PDFUtil.mergeCol("客户联系人：" + (StringUtils.isBlank(billInfo.getContactsName()) ? "" : billInfo.getContactsName()), textBoldFont, 6));

				// 联系人部门
				table.addCell(PDFUtil.getPDFCell("部门", textBoldFont));
				if (StringUtil.isNotBlank(customer.getContactDept())) {
					table.addCell(PDFUtil.mergeCol(customer.getContactDept(), textBoldFont, 2));
				} else {
					table.addCell(PDFUtil.mergeCol("", textBoldFont, 2));
				}
				// 联系人职务
				table.addCell(PDFUtil.getPDFCell("联系人职务", textBoldFont));
				if (StringUtil.isNotBlank(customer.getContactPosition())) {
					table.addCell(PDFUtil.mergeCol(customer.getContactPosition(), textBoldFont, 2));
				} else {
					table.addCell(PDFUtil.mergeCol("", textBoldFont, 2));
				}
				// 联系人座机
				table.addCell(PDFUtil.getPDFCell("座机", textBoldFont));
				if (StringUtil.isNotBlank(customer.getContactTelephone())) {
					table.addCell(PDFUtil.mergeCol(customer.getContactTelephone(), textBoldFont, 2));
				} else {
					table.addCell(PDFUtil.mergeCol("", textBoldFont, 2));
				}
				// 联系人手机
				table.addCell(PDFUtil.getPDFCell("联系人手机", textBoldFont));
				if (StringUtil.isNotBlank(billInfo.getPhone())) {
					table.addCell(PDFUtil.mergeCol(billInfo.getPhone(), textBoldFont, 2));
				} else {
					table.addCell(PDFUtil.mergeCol("", textBoldFont, 2));
				}

				table.addCell(PDFUtil.getPDFCell("产品", textBoldFont));
				table.addCell(PDFUtil.getPDFCell("计费周期", textBoldFont));
				table.addCell(PDFUtil.getPDFCell("账号", textBoldFont));
				table.addCell(PDFUtil.getPDFCell("单价（元/条）", textBoldFont));
				table.addCell(PDFUtil.getPDFCell("计费条数（条）", textBoldFont));
				table.addCell(PDFUtil.getPDFCell("金额（元）", textBoldFont));

				Date createDate = new Date();
				for (ProductBills bill : billList) {
					BillInfo info = billInfoMap.get(bill.getId());
					if (info != null && !CollectionUtils.isEmpty(info.getAccountInfos())) {
						// 对账单的出账日期使用最后一个账单的出账日期
						if (info.getCreateDate().after(createDate)) {
							createDate = info.getCreateDate();
						}
						int mergeRowNum = info.getAccountInfos().size();
						table.addCell(PDFUtil.mergeRow(info.getProductType(), textFont, mergeRowNum));
						table.addCell(PDFUtil.mergeRow(DateUtil.convert(info.getBillDate(), DateUtil.format1) + "~"
								+ DateUtil.convert(DateUtil.getMonthFinal(info.getBillDate()), DateUtil.format1), textFont, mergeRowNum));
						if (!CollectionUtils.isEmpty(info.getAccountInfos())) {
							for (DetailInfo detail : info.getAccountInfos()) {
								table.addCell(PDFUtil.getPDFCell(detail.getAccountName(), textFont));
								table.addCell(PDFUtil.getPDFCellRight(dff.format(detail.getUnitPrice()), textFont));
								table.addCell(PDFUtil.getPDFCellRight(dft.format(detail.getFeeCount()), textFont));
								table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(detail.getFee()), textFont));
							}
						}
					}
				}

				table.addCell(PDFUtil.mergeCol("实际计费", textBoldFont, 3));
				table.addCell(PDFUtil.getPDFCellRight("", textFont));
				table.addCell(PDFUtil.getPDFCellRight(dft.format(realFeeInfo.getFeeCount()), textFont));
				table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(realFeeInfo.getFee().setScale(2, BigDecimal.ROUND_UP)), textFont));

				table.addCell(PDFUtil.mergeCol("本期应付（大写金额）", textBoldFont, 2));
				table.addCell(PDFUtil.mergeCol(Money2ChineseUtil.convert(realFeeInfo.getFee()), textBoldFont, 3));
				table.addCell(PDFUtil.getPDFCellRight("¥" + df.format(realFeeInfo.getFee().setScale(2, BigDecimal.ROUND_UP)), textFont));

				table.addCell(PDFUtil.mergeCol("出账日期：" + sdf.format(createDate), textBoldFont, 6));

				table.addCell(PDFUtil.mergeCol("收款银行账号信息", textBoldFont, 6));
				table.addCell(PDFUtil.mergeCol(bankAccount.getAccountName(), textFont, 6));
				table.addCell(PDFUtil.mergeCol(bankAccount.getAccountBank(), textFont, 6));
				table.addCell(PDFUtil.mergeCol(bankAccount.getBankAccount(), textFont, 6));
				table.addCell(PDFUtil.mergeCol(bankAccount.getCompanyAddress(), textFont, 6));

				table.addCell(PDFUtil.mergeCol("销售经理：" + billInfo.getSaleName(), textBoldFont, 6));
				table.addCell(PDFUtil.mergeCol("销售经理座机：" + (saler == null || StringUtils.isBlank(saler.getContactPhone()) ? "" : saler.getContactPhone()),
						textBoldFont, 6));
				table.addCell(PDFUtil.mergeCol("销售经理手机：" + billInfo.getSalePhone(), textBoldFont, 6));

				table.addCell(PDFUtil.mergeCol("温馨提示：本单据为您电子渠道的对账单，请妥善保管。", textBoldFont, 6, 0));

				p0.add(table);
				document.add(p0);
			}

			// 数据详情部分
			if (optionList.contains(Constants.BILL_OPTION_DATA_DETAIL)) {
				document.newPage();
				// 账单详情按产品类型分组
				Map<Integer, List<BillDataDetailDto>> typeBillDataMap = billDataDetailMap.values().stream()
						.collect(Collectors.groupingBy(BillDataDetailDto::getProductType));

				// {产品类型 -> [月账单数据详情]}
				for (Map.Entry<Integer, List<BillDataDetailDto>> typeBillDataEntry : typeBillDataMap.entrySet()) {
					// 该产品类型 的 月账单数据
					List<BillDataDetailDto> typeBillDataList = typeBillDataEntry.getValue();
					typeBillDataList.sort(Comparator.comparing(BillDataDetailDto::getBillMonth));
					StringBuffer title = new StringBuffer("");
					Set<String> billMonthSet = typeBillDataList.stream().map(BillDataDetailDto::getBillMonth).collect(Collectors.toSet());
					List<String> billMonthList = new ArrayList<>(billMonthSet);
					billMonthList.sort(String::compareTo);
					for (String billMonth : billMonthList) {
						title.append("、").append(billMonth);
					}
					String productTypeName = productTypeService.getProductTypeNameByValue(typeBillDataEntry.getKey());
					String productTypeKey = productTypeService.getProductTypeKeyByValue(typeBillDataEntry.getKey());
					boolean isVoice = StringUtils.equals(Constants.PRODUCT_TYPE_KEY_VOICE_TIME, productTypeKey);
					title.append(productTypeName).append("数据详情");

					// yyyy-MM + 产品类型 + 数据详情
					Paragraph detailParagraph = new Paragraph(title.substring(1), boldFont);
					detailParagraph.setAlignment(Element.ALIGN_CENTER);

					Paragraph temp = new Paragraph("", textFont);
					temp.setLeading(5);
					detailParagraph.add(temp);

					detailParagraph.setAlignment(Element.ALIGN_CENTER);
					PdfPTable detailTable = new PdfPTable(isVoice ? 7 : 6);
					detailTable.setSpacingBefore(10);
					detailTable.setHorizontalAlignment(Element.ALIGN_CENTER);
					// 产品，发送总数，成功数，失败数，成功率
					if (isVoice) {
						detailTable.setTotalWidth(new float[] { 140, 60, 60, 60, 60, 60, 60 });
					} else {
						detailTable.setTotalWidth(new float[] { 150, 70, 70, 70, 70, 70 });
					}
					detailTable.setLockedWidth(true); // 锁定列宽
					detailTable.addCell(PDFUtil.mergeCol("日发送明细", textFont, isVoice ? 7 : 6));
					detailTable.addCell(PDFUtil.getPDFCell("客户账号", textFont));
					detailTable.addCell(PDFUtil.getPDFCell("统计日期", textFont));
					detailTable.addCell(PDFUtil.getPDFCell("发送总数", textFont));
					detailTable.addCell(PDFUtil.getPDFCell("发送成功数", textFont));
					if (isVoice) {
						detailTable.addCell(PDFUtil.getPDFCell("计费条数", textFont));
					}
					detailTable.addCell(PDFUtil.getPDFCell("发送失败数", textFont));
					detailTable.addCell(PDFUtil.getPDFCell("成功率", textFont));

					DecimalFormat format = new DecimalFormat("##.##%");

					// 产品的总计
					Map<String, DateDetail> productTotalMap = new HashMap<>();
					// 账号的总计
					Map<String, DateDetail> loginNameTotalMap = new HashMap<>();
					// 产品类型的总计
					DateDetail productTypeTotal = new DateDetail();
					// 此产品类型的每个账单的数据集合。这里把每个账单的日发送明细取出放在一起
					List<DateDetail> typeAllDetailList = new ArrayList<>();
					for (BillDataDetailDto typeBillData : typeBillDataList) {
						// 一个账单每天的数据
						List<DateDetail> billDetailList = typeBillData.getDateDetailList();
						if (CollectionUtils.isEmpty(billDetailList)) {
							continue;
						}
						typeAllDetailList.addAll(billDetailList);
					}

					// 按统计日期、产品、账号排序
					typeAllDetailList.sort((o1, o2) -> {
						if (o1.getDate().equals(o2.getDate())) {
							if (o1.getProductName().equals(o2.getProductName())) {
								return o1.getLoginName().compareTo(o2.getLoginName());
							}
							return o1.getProductName().compareTo(o2.getProductName());
						}
						return o1.getDate().compareTo(o2.getDate());
					});

					for (DateDetail dateDetail : typeAllDetailList) {
						String detailName = dateDetail.getProductName() + "-" + dateDetail.getLoginName();
						detailTable.addCell(PDFUtil.getPDFCell(detailName, textFont));
						detailTable.addCell(PDFUtil.getPDFCell(dateDetail.getDate(), textFont));
						detailTable.addCell(PDFUtil.getPDFCell(dateDetail.getTotalCount() + "", textFont));
						// 语音产品，成功数 = 总数 - 失败数
						if (isVoice) {
							detailTable.addCell(PDFUtil.getPDFCell(dateDetail.getTotalCount() - dateDetail.getFailCount() + "", textFont));
						}
						// 语音产品，成功数 实际是 计费条数
						detailTable.addCell(PDFUtil.getPDFCell(dateDetail.getSuccessCount() + "", textFont));
						detailTable.addCell(PDFUtil.getPDFCell(dateDetail.getFailCount() + "", textFont));
						if (isVoice) {
							detailTable.addCell(PDFUtil.getPDFCell(format.format(new BigDecimal(dateDetail.getTotalCount() - dateDetail.getFailCount())
									.divide(new BigDecimal(dateDetail.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP)), textFont));
						} else {
							detailTable.addCell(PDFUtil.getPDFCell(format.format(dateDetail.getSuccessRatio()), textFont));
						}

						/*// 本条记录所属产品的总计
						DateDetail productTotal = productTotalMap.getOrDefault(dateDetail.getProductName(), new DateDetail());
						if (StringUtil.isBlank(productTotal.getProductName())) {
							productTotal.setProductName(dateDetail.getProductName());
						}
						productTotal.setTotalCount(productTotal.getTotalCount() + dateDetail.getTotalCount());
						productTotal.setSuccessCount(productTotal.getSuccessCount() + dateDetail.getSuccessCount());
						productTotal.setFailCount(productTotal.getFailCount() + dateDetail.getFailCount());
						productTotalMap.put(dateDetail.getProductName(), productTotal);*/

						// 本条记录的账号的总计
						DateDetail loginNameTotal = loginNameTotalMap.getOrDefault(detailName, new DateDetail());
						if (StringUtil.isBlank(loginNameTotal.getProductName())) {
							loginNameTotal.setProductName(detailName);
						}
						loginNameTotal.setTotalCount(loginNameTotal.getTotalCount() + dateDetail.getTotalCount());
						loginNameTotal.setSuccessCount(loginNameTotal.getSuccessCount() + dateDetail.getSuccessCount());
						loginNameTotal.setFailCount(loginNameTotal.getFailCount() + dateDetail.getFailCount());
						loginNameTotalMap.put(detailName, loginNameTotal);
					}

					PdfPTable totalTable = new PdfPTable(isVoice ? 6 : 5);
					totalTable.setSpacingBefore(10);
					totalTable.setHorizontalAlignment(Element.ALIGN_CENTER);
					// 账号，发送总数，成功数，失败数，成功率
					if (isVoice) {
						totalTable.setTotalWidth(new float[] { 150, 70, 70, 70, 70, 70 });
					} else {
						totalTable.setTotalWidth(new float[] { 160, 85, 85, 85, 85 });
					}
					totalTable.setLockedWidth(true); // 锁定列宽
					totalTable.addCell(PDFUtil.mergeCol("总计", textFont, isVoice ? 6 : 5));
					totalTable.addCell(PDFUtil.getPDFCell("账号", textFont));
					totalTable.addCell(PDFUtil.getPDFCell("发送总数", textFont));
					totalTable.addCell(PDFUtil.getPDFCell("发送成功数", textFont));
					if (isVoice) {
						totalTable.addCell(PDFUtil.getPDFCell("计费条数", textFont));
					}
					totalTable.addCell(PDFUtil.getPDFCell("发送失败数", textFont));
					totalTable.addCell(PDFUtil.getPDFCell("成功率", textFont));

					/*// 每个产品的总计
					List<DateDetail> allProductTotal = new ArrayList<>(productTotalMap.values());
					allProductTotal.sort(Comparator.comparing(DateDetail::getProductName));
					for (DateDetail productTotal : allProductTotal) {
						if (productTotal.getTotalCount() > 0) {
							if (isVoice) {
								// 语音产品，成功数 = 总数 - 失败数，成功数实际是计费数
								productTotal.setSuccessRatio(new BigDecimal(productTotal.getTotalCount() - productTotal.getFailCount())
										.divide(new BigDecimal(productTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
							} else {
								productTotal.setSuccessRatio(new BigDecimal(productTotal.getSuccessCount())
										.divide(new BigDecimal(productTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
							}
						}
						totalTable.addCell(PDFUtil.getPDFCell(productTotal.getProductName(), textFont));
						totalTable.addCell(PDFUtil.getPDFCell(productTotal.getTotalCount() + "", textFont));
						if (isVoice) {
							totalTable.addCell(PDFUtil.getPDFCell(productTotal.getTotalCount() - productTotal.getFailCount() + "", textFont));
						}
						totalTable.addCell(PDFUtil.getPDFCell(productTotal.getSuccessCount() + "", textFont));
						totalTable.addCell(PDFUtil.getPDFCell(productTotal.getFailCount() + "", textFont));
						totalTable.addCell(PDFUtil.getPDFCell(format.format(productTotal.getSuccessRatio()), textFont));

						// 产品类型的总计
						productTypeTotal.setTotalCount(productTypeTotal.getTotalCount() + productTotal.getTotalCount());
						productTypeTotal.setSuccessCount(productTypeTotal.getSuccessCount() + productTotal.getSuccessCount());
						productTypeTotal.setFailCount(productTypeTotal.getFailCount() + productTotal.getFailCount());
					}*/

					// 每个账号的总计
					List<DateDetail> allLoginNameTotal = new ArrayList<>(loginNameTotalMap.values());
					allLoginNameTotal.sort(Comparator.comparing(DateDetail::getProductName));
					for (DateDetail loginNameTotal : allLoginNameTotal) {
						if (loginNameTotal.getTotalCount() > 0) {
							if (isVoice) {
								// 语音产品，成功数 = 总数 - 失败数，成功数实际是计费数
								loginNameTotal.setSuccessRatio(new BigDecimal(loginNameTotal.getTotalCount() - loginNameTotal.getFailCount())
										.divide(new BigDecimal(loginNameTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
							} else {
								loginNameTotal.setSuccessRatio(new BigDecimal(loginNameTotal.getSuccessCount())
										.divide(new BigDecimal(loginNameTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
							}
						}
						totalTable.addCell(PDFUtil.getPDFCell(loginNameTotal.getProductName(), textFont));
						totalTable.addCell(PDFUtil.getPDFCell(loginNameTotal.getTotalCount() + "", textFont));
						if (isVoice) {
							totalTable.addCell(PDFUtil.getPDFCell(loginNameTotal.getTotalCount() - loginNameTotal.getFailCount() + "", textFont));
						}
						totalTable.addCell(PDFUtil.getPDFCell(loginNameTotal.getSuccessCount() + "", textFont));
						totalTable.addCell(PDFUtil.getPDFCell(loginNameTotal.getFailCount() + "", textFont));
						totalTable.addCell(PDFUtil.getPDFCell(format.format(loginNameTotal.getSuccessRatio()), textFont));

						// 产品类型的总计
						productTypeTotal.setTotalCount(productTypeTotal.getTotalCount() + loginNameTotal.getTotalCount());
						productTypeTotal.setSuccessCount(productTypeTotal.getSuccessCount() + loginNameTotal.getSuccessCount());
						productTypeTotal.setFailCount(productTypeTotal.getFailCount() + loginNameTotal.getFailCount());
					}

					// 产品类型总的成功率
					if (productTypeTotal.getTotalCount() > 0) {
						if (isVoice) {
							// 语音产品，成功数 = 总数 - 失败数，成功数实际是计费数
							productTypeTotal.setSuccessRatio(new BigDecimal(productTypeTotal.getTotalCount() - productTypeTotal.getFailCount())
									.divide(new BigDecimal(productTypeTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
						} else {
							productTypeTotal.setSuccessRatio(new BigDecimal(productTypeTotal.getSuccessCount())
									.divide(new BigDecimal(productTypeTotal.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP));
						}

					}
					totalTable.addCell(PDFUtil.getPDFCell("合计", textFont));
					totalTable.addCell(PDFUtil.getPDFCell(productTypeTotal.getTotalCount() + "", textFont));
					// 语音产品，成功数 = 总数 - 失败数，成功数实际是计费数
					if (isVoice) {
						totalTable.addCell(PDFUtil.getPDFCell(productTypeTotal.getTotalCount() - productTypeTotal.getFailCount() + "", textFont));
					}
					totalTable.addCell(PDFUtil.getPDFCell(productTypeTotal.getSuccessCount() + "", textFont));
					totalTable.addCell(PDFUtil.getPDFCell(productTypeTotal.getFailCount() + "", textFont));
					totalTable.addCell(PDFUtil.getPDFCell(format.format(productTypeTotal.getSuccessRatio()), textFont));

					detailParagraph.add(totalTable);
					detailParagraph.add(detailTable);
					document.add(detailParagraph);
				}

			}

			// 关闭文档
			document.close();

			// 获取水印文件路径
			URL logoImageURL = loadMetaData(IText5Pdf4MergeUtil.class.getClassLoader(), "static/common/imgs/dahan.png");
			return addPdfImgMark(file, filePath, logoImageURL);

		} catch (Exception e) {
			logger.error("合并账单生成对账单异常：", e);
			return false;
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	@Autowired
	public void setProductTypeService(IProductTypeService productTypeService) {
		IText5Pdf4MergeUtil.productTypeService = productTypeService;
	}
}