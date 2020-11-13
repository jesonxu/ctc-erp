package com.dahantc.erp.dto.operate;

import java.io.Serializable;
import java.util.Date;

public class UploadFileRespDto implements Serializable {

	private static final long serialVersionUID = -4741061127730390179L;
	/**
	 * 上传的源文件名称
	 */
	private String fileName;
	/**
	 * 上传后文件保存的路径
	 */
	private String filePath;
	
	private Date time = new Date();

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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "UploadFileRespDto{" +
				"fileName='" + fileName + '\'' +
				", filePath='" + filePath + '\'' +
				", time=" + time +
				'}';
	}
}
