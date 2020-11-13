package com.dahantc.erp.dto.fsExpenseIncome;

import com.dahantc.erp.dto.operate.UploadFileRespDto;

/**
 * 携带文件Md5值的上传返回实体
 * 
 * @author 8520
 */
public class UploadFileInfoResp extends UploadFileRespDto {

	private static final long serialVersionUID = -839534553592189799L;
	/**
	 * 文件在系统的Md5值
	 */
	private String md5;

	public UploadFileInfoResp() {
	}

	public UploadFileInfoResp(String md5) {
		this.md5 = md5;
	}

	public UploadFileInfoResp(String fileName, String filePath, String md5) {
		super();
		super.setFileName(fileName);
		super.setFilePath(filePath);
		this.md5 = md5;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
