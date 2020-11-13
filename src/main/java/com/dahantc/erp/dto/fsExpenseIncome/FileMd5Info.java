package com.dahantc.erp.dto.fsExpenseIncome;

import com.dahantc.erp.commom.NumberUtils;
import com.dahantc.erp.util.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 文件的Md5信息
 * 
 * @author 8520
 */
public class FileMd5Info implements Serializable {
	
	private static final long serialVersionUID = 9120491279886414543L;
	/**
	 * 文件名称（用于提示）
	 */
	private String fileName;
	/**
	 * 文件存放路径
	 */
	private String filePath;
	/**
	 * 上传时间（用于判断 是在当前一个周内的文件）
	 */
	private Long uploadTime;
	/**
	 * 是否已经解析过（防止页面取消过后不能再次上传）
	 */
	private Boolean parsed;

	public FileMd5Info() {
	}

	public FileMd5Info(String fileName, String filePath, Long uploadTime, Boolean parsed) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.uploadTime = uploadTime;
		this.parsed = parsed;
	}

	public FileMd5Info(String[] fileInfos) {
		// fileInfos格式
		// Md5（文件的Md5值）###文件名（文件的上传名）###文件路径（存放实际路径）###写入时间（毫秒）###是否已经解析
		// （0否|1是）
		if (fileInfos != null && fileInfos.length >= 5) {
			this.fileName = fileInfos[1].trim();
			this.filePath = fileInfos[2].trim();
			if (StringUtil.isNotBlank(fileInfos[3]) && NumberUtils.isNumeric(fileInfos[3])) {
				this.uploadTime = new BigDecimal(fileInfos[3]).longValue();
			}
			// 默认已经是使用过的
			this.parsed = true;
			if (StringUtil.isNotBlank(fileInfos[4]) && NumberUtils.isNumeric(fileInfos[4])) {
				if (0 == Integer.parseInt(fileInfos[4])) {
					this.parsed = false;
				}
			}
		}
	}

	/**
	 * 文件存放路径和上传时间不能为空
	 */
	public boolean right() {
		return StringUtil.isNotBlank(this.filePath) && uploadTime != null;
	}

	/** 获取写文件信息 */
	public String getWriteFileInfo() {
		// 文件名（文件的上传名）###文件路径（存放实际路径）###写入时间（毫秒）###是否已经解析
		StringBuilder info = new StringBuilder();
		info.append(this.fileName).append("###").append(this.filePath).append("###").append(this.uploadTime).append("###");
		if (parsed) {
			info.append("1");
		} else {
			info.append("0");
		}
		return info.toString();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Long uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Boolean getParsed() {
		return parsed;
	}

	public void setParsed(Boolean parsed) {
		this.parsed = parsed;
	}
}
