package com.dahantc.erp.util;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.NumberUtils;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.FileMd5Info;

/**
 * 用于帮助校验文件的Md5工具类
 *
 * @author 8520
 */
public class FileMd5Utl {

	private static final Logger logger = LoggerFactory.getLogger(FileMd5Utl.class);

	/** 一周内 文件Md5不能重复 */
	private static final long MD5_REMAIN_TIME = 7 * 24 * 60 * 60 * 1000;

	/** 上传文件的Md5文件 */
	private static final String FILE_MD5_FILE = "upFile/fsexpendincome/filemd5.txt";

	/** 读取md5值 */
	public static synchronized Map<String, FileMd5Info> readMd5() {
		long start = System.currentTimeMillis();
		// 读取的集合信息
		Map<String, FileMd5Info> md5MapInfos = new HashMap<>();
		// 文件是固定的
		File file = new File(Constants.RESOURCE + File.separator + FILE_MD5_FILE);
		if (!file.exists()) {
			try {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				// 创建的MD5文件
				boolean fileCreateResult = file.createNewFile();
				if (!fileCreateResult) {
					logger.error("创建文件失败" + file.getAbsolutePath());
					return md5MapInfos;
				}
			} catch (IOException e) {
				logger.error("创建md5文件异常", e);
			}
		} else if (file.canRead()) {
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);
				String lineStr = "";
				while ((lineStr = bufferedReader.readLine()) != null) {
					// 格式 Md5（文件的Md5值）###文件名（文件的上传名）###文件路径（存放实际路径）###写入时间（毫秒）###是否已经解析（0 否|1是）
					String[] strArr = lineStr.split("###");
					if (strArr.length >= 5) {
						if (NumberUtils.isNumeric(strArr[3])) {
							// 只需要一周内容的Md5文件
							if (System.currentTimeMillis() - Long.parseLong(strArr[3]) <= MD5_REMAIN_TIME) {
								FileMd5Info md5Info = new FileMd5Info(strArr);
								if (md5Info.right()) {
									md5MapInfos.put(strArr[0], md5Info);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("读取Md5文件异常", e);
			} finally {
				try {
					if (bufferedReader != null) {
						bufferedReader.close();
					}
					if (fileReader != null) {
						fileReader.close();
					}
				} catch (IOException e) {
					logger.error("流关闭异常", e);
				}
			}
		}
		logger.info("读取md5文件耗时：" + (System.currentTimeMillis() - start));
		return md5MapInfos;
	}

	/** 写md5值 */
	public static synchronized boolean writeMd5(Map<String, FileMd5Info> md5MapInfos) {
		long start = System.currentTimeMillis();
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(Constants.RESOURCE + File.separator + FILE_MD5_FILE);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				// 创建的MD5文件
				boolean fileCreateResult = file.createNewFile();
				if (!fileCreateResult) {
					logger.error("创建文件失败" + file.getAbsolutePath());
					return false;
				}
			}
			// 写入内容为空 直接删除文件
			if (md5MapInfos == null || md5MapInfos.isEmpty()) {
				return file.delete();
			}
			// 组装内容
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, FileMd5Info> entry : md5MapInfos.entrySet()) {
				sb.append(entry.getKey()).append("###").append(entry.getValue().getWriteFileInfo()).append(System.lineSeparator());
			}
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(sb.toString());
			bufferedWriter.flush();
		} catch (Exception e) {
			logger.error("md5文件写入异常", e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				logger.error("流关闭异常", e);
			}
		}
		logger.info("保存md5文件耗时：" + (System.currentTimeMillis() - start));
		return true;
	}

	/**
	 * 读取指定路径下的md5文件中的记录
	 * 
	 * @param filePath
	 *            文件完整路径，包含路径和文件名
	 * @return {md5->文件信息}
	 */
	public static synchronized Map<String, FileMd5Info> readMd5(String filePath) {
		long start = System.currentTimeMillis();
		// 读取的集合信息
		Map<String, FileMd5Info> md5MapInfos = new HashMap<>();
		// 文件是固定的
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				// 创建的MD5文件
				boolean fileCreateResult = file.createNewFile();
				if (!fileCreateResult) {
					logger.error("创建文件失败" + file.getAbsolutePath());
					return md5MapInfos;
				}
			} catch (IOException e) {
				logger.error("创建md5文件异常", e);
			}
		} else if (file.canRead()) {
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);
				String lineStr = "";
				while ((lineStr = bufferedReader.readLine()) != null) {
					// 格式 Md5（文件的Md5值）###文件名（文件的上传名）###文件路径（存放实际路径）###写入时间（毫秒）###是否已经解析（0 否|1是）
					String[] strArr = lineStr.split("###");
					if (strArr.length >= 5) {
						if (NumberUtils.isNumeric(strArr[3])) {
							// 只需要一周内容的Md5文件
							if (System.currentTimeMillis() - Long.parseLong(strArr[3]) <= MD5_REMAIN_TIME) {
								FileMd5Info md5Info = new FileMd5Info(strArr);
								if (md5Info.right()) {
									md5MapInfos.put(strArr[0], md5Info);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("读取Md5文件异常", e);
			} finally {
				try {
					if (bufferedReader != null) {
						bufferedReader.close();
					}
					if (fileReader != null) {
						fileReader.close();
					}
				} catch (IOException e) {
					logger.error("流关闭异常", e);
				}
			}
		}
		logger.info("读取md5文件耗时：" + (System.currentTimeMillis() - start));
		return md5MapInfos;
	}

	/**
	 * 记录MD5到指定路径下的文件
	 * 
	 * @param md5MapInfos
	 *            {md5->文件信息}
	 * @param filePath
	 *            文件完整路径，包含路径和文件名
	 * @return
	 */
	public static synchronized boolean writeMd5(Map<String, FileMd5Info> md5MapInfos, String filePath) {
		long start = System.currentTimeMillis();
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				// 创建的MD5文件
				boolean fileCreateResult = file.createNewFile();
				if (!fileCreateResult) {
					logger.error("创建文件失败" + file.getAbsolutePath());
					return false;
				}
			}
			// 写入内容为空 直接删除文件
			if (md5MapInfos == null || md5MapInfos.isEmpty()) {
				return file.delete();
			}
			// 组装内容
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, FileMd5Info> entry : md5MapInfos.entrySet()) {
				sb.append(entry.getKey()).append("###").append(entry.getValue().getWriteFileInfo()).append(System.lineSeparator());
			}
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(sb.toString());
			bufferedWriter.flush();
		} catch (Exception e) {
			logger.error("md5文件写入异常", e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				logger.error("流关闭异常", e);
			}
		}
		logger.info("保存md5文件耗时：" + (System.currentTimeMillis() - start));
		return true;
	}

	/**
	 * 读取md5对应的文件信息，并更新其更新为已解析并写回
	 * 
	 * @param md5
	 *            文件的Md5值
	 */
	public static synchronized boolean readAndUpdate(String md5) {
		boolean result = false;
		Map<String, FileMd5Info> md5MapInfos = readMd5();
		FileMd5Info md5Info = md5MapInfos.get(md5);
		if (md5Info != null && !md5Info.getParsed()) {
			result = true;
			md5Info.setParsed(true);
			md5MapInfos.put(md5, md5Info);
		}
		result |= writeMd5(md5MapInfos);
		return result;
	}

	/**
	 * 读取md5对应的文件信息，并更新其更新为已解析并写回
	 *
	 * @param md5
	 *            文件的Md5值
	 */
	public static synchronized boolean readAndUpdate(String md5, String filePath) {
		boolean result = false;
		Map<String, FileMd5Info> md5MapInfos = readMd5(filePath);
		FileMd5Info md5Info = md5MapInfos.get(md5);
		if (md5Info != null && !md5Info.getParsed()) {
			result = true;
			md5Info.setParsed(true);
			md5MapInfos.put(md5, md5Info);
		}
		result |= writeMd5(md5MapInfos, filePath);
		return result;
	}

	/**
	 * 读取并删除
	 * 
	 * @param md5List
	 *            文件的Md5值
	 */
	public synchronized static BaseResponse<Boolean> readAndDelete(List<String> md5List) {
		Map<String, FileMd5Info> md5MapInfos = readMd5();
		for (String md5 : md5List) {
			FileMd5Info md5Info = md5MapInfos.get(md5);
			if (md5Info != null && !md5Info.getParsed()) {
				md5MapInfos.remove(md5);
			}
		}
		if (writeMd5(md5MapInfos)) {
			return BaseResponse.success("清除上传文件信息成功");
		}
		return BaseResponse.error("清除上传文件信息成功", false);
	}

	/**
	 * 从文件中删除已记录的md5信息
	 *
	 * @param md5List
	 *            文件的Md5值
	 */
	public synchronized static BaseResponse<Boolean> deleteMd5(List<String> md5List, String filePath) {
		Map<String, FileMd5Info> md5MapInfos = readMd5(filePath);
		for (String md5 : md5List) {
			FileMd5Info md5Info = md5MapInfos.get(md5);
			if (md5Info != null && !md5Info.getParsed()) {
				md5MapInfos.remove(md5);
			}
		}
		if (writeMd5(md5MapInfos, filePath)) {
			return BaseResponse.success("清除上传文件信息成功");
		}
		return BaseResponse.error("清除上传文件信息成功", false);
	}
}
