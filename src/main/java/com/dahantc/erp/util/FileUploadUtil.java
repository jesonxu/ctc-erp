package com.dahantc.erp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.FileMd5Info;
import com.dahantc.erp.dto.fsExpenseIncome.UploadFileInfoResp;
import com.dahantc.erp.dto.operate.UploadFileRespDto;

/**
 * 用于文件上传的工具类
 */
public class FileUploadUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

	/**
	 * 将文件上传到指定位置
	 * 
	 * @param files
	 *            要上传的文件
	 * @param uploadPath
	 *            上传路径，仅文件夹
	 * @return
	 */
	public static BaseResponse<List<UploadFileInfoResp>> uploadFile(MultipartFile[] files, String uploadPath) {
		// 上次路径
		String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
		String resourceDir = Constants.RESOURCE + File.separator + uploadPath + File.separator + datePath;
		String md5FilePath = Constants.RESOURCE + File.separator + uploadPath + File.separator + "filemd5.txt";

		List<UploadFileInfoResp> uploadFileInfos = new ArrayList<>();
		// 文件的Md5信息
		Map<String, FileMd5Info> fileMd5Infos = new HashMap<>();
		// 上传文件的描述
		StringBuilder fileDepict = new StringBuilder();
		try {
			if (files != null && files.length > 0) {
				// 读取路径下的md5文件
				fileMd5Infos = FileMd5Utl.readMd5(md5FilePath);
				for (MultipartFile multipartFile : files) {
					// 文件的Md5编码
					String md5Val = DigestUtils.md5Hex(multipartFile.getInputStream());
					String docFileName = multipartFile.getOriginalFilename();
					// 是否上传过相同md5文件，获取文件信息
					FileMd5Info fileMd5Info = fileMd5Infos.get(md5Val);
					long now = System.currentTimeMillis();
					// 一周内没有上传 或者上传一小时后，没有解析
					if (fileMd5Info == null || (!fileMd5Info.getParsed() && (now - fileMd5Info.getUploadTime()) > 1000 * 60 * 60)) {
						UploadFileRespDto dto = new UploadFileRespDto();
						if (StringUtils.isNotBlank(docFileName)) {
							String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1);
							String reName = System.currentTimeMillis() + md5Val + "." + ext;
							File dir = new File(resourceDir);
							if (!dir.exists()) {
								boolean fileCreateResult = dir.mkdirs();
								if (!fileCreateResult) {
									fileDepict.append(" 文件").append(docFileName).append("上传失败 ");
									continue;
								}
							}
							String disPath = resourceDir + File.separator + reName;
							File disFile = new File(disPath);
							multipartFile.transferTo(disFile);
							dto.setFileName(docFileName);
							dto.setFilePath(disPath);
							uploadFileInfos.add(new UploadFileInfoResp(docFileName, disPath, md5Val));
							fileMd5Infos.put(md5Val, new FileMd5Info(docFileName, disPath, System.currentTimeMillis(), false));
						}
					} else {
						logger.info("文件已存在：" + JSON.toJSONString(fileMd5Infos.get(md5Val)));
						String uploadTime = DateUtil.convert(fileMd5Info.getUploadTime(), DateUtil.format2);
						fileDepict.append(" 文件").append(docFileName).append("在").append(uploadTime).append("上传 ");
					}
				}
				// 将md5信息写回到md5文件中
				if (!uploadFileInfos.isEmpty()) {
					FileMd5Utl.writeMd5(fileMd5Infos, md5FilePath);
				} else {
					return BaseResponse.error("上传失败：" + fileDepict.toString());
				}
			} else {
				logger.info("上传文件失败");
				return BaseResponse.error("上传失败：" + fileDepict.toString());
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
			return BaseResponse.error("上传异常");
		} finally {
			if (fileMd5Infos != null) {
				fileMd5Infos.clear();
				fileMd5Infos = null;
			}
		}
		return BaseResponse.success("上传成功：" + fileDepict.toString(), uploadFileInfos);
	}

	/**
	 * 删除已经上传过的文件的md5信息
	 * 
	 * @param md5s
	 *            文件md5，逗号分隔
	 * @param uploadPath
	 *            上传路径，仅文件夹
	 * @return
	 */
	public static BaseResponse<Boolean> delUploadFile(String md5s, String uploadPath) {
		List<String> fileMd5List = Arrays.asList(md5s.split(","));
		String md5FilePath = Constants.RESOURCE + File.separator + uploadPath + File.separator + "filemd5.txt";
		return FileMd5Utl.deleteMd5(fileMd5List, md5FilePath);
	}
}
