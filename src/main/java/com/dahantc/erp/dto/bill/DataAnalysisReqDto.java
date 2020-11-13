package com.dahantc.erp.dto.bill;

import java.io.Serializable;
import java.util.List;

public class DataAnalysisReqDto implements Serializable {

	private static final long serialVersionUID = -8921975131366028610L;

	/**
	 * 任务id，使用对账流程的flowEntId
	 */
	private String taskId;

	/**
	 * 服务器验证用
	 */
	private String token;

	/**
	 * 处理服务器响应的回调地址
	 */
	private String callback;

	/**
	 * 请求生成数据分析报告的参数
	 */
	private List<ReqData> data;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getCallback() {
		return callback;
	}

	public void setData(List<ReqData> data) {
		this.data = data;
	}

	public List<ReqData> getData() {
		return data;
	}
}
