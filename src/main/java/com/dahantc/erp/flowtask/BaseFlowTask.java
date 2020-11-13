package com.dahantc.erp.flowtask;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;

public abstract class BaseFlowTask {
	/** 金额格式 */
	public static Pattern amountPattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
	/** 占比格式 */
	public static Pattern ratioPattern = Pattern.compile("^\\d+\\.?\\d{0,4}$");
	/** 单价格式 */
	public static Pattern pricePattern = Pattern.compile("^\\d+\\.?\\d{0,6}$");

	public abstract String getFlowClass();

	public abstract String getFlowName();

	/**
	 * 流程标签信息校验
	 * 
	 * @param erpFlow
	 *            流程设计
	 * @param productId
	 *            产品id
	 * @param labelJsonVal
	 *            json格式的标签信息
	 * @return
	 */
	public abstract String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal);

	/**
	 * 流程标签信息校验
	 * 
	 * @param erpFlow
	 *            流程设计
	 * @param flowEnt
	 *            流程实体
	 * @param labelJsonVal
	 *            提交的流程内容
	 * @return
	 */
	public abstract String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal);

	/**
	 * 流程归档，自行实现归档操作
	 * 
	 * @param erpFlow
	 *            流程设计
	 * @param flowEnt
	 *            流程实体
	 * @return
	 */
	public abstract boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt);

	/**
	 * 流程信息修改
	 * 
	 * @param auditResult
	 *            审核操作
	 * @param flowEnt
	 *            流程实体
	 * @return
	 */
	public abstract void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException;

	/**
	 * 流程信息修改
	 *
	 * @param auditResult
	 *            审核操作
	 * @param flowEnt
	 *            流程实体
	 * @param changes
	 *            修改内容
	 * @return
	 */
	public abstract void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException;

	/**
	 * 校验付款的相关日期
	 * 
	 * @param json
	 * @param record
	 * @return
	 */
	protected String verifyDate(JSONObject json, ChargeRecord record) {
		String result = "";
		String time = json.getString("付款截止日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setFinalPayTime(new Timestamp(date.getTime()));
			} else {
				return "付款截止日期格式不正确";
			}
		}
		time = json.getString("收票截止日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setInvoiceTime(new Timestamp(date.getTime()));
			} else {
				return "收票截止日期格式不正确";
			}
		}
		time = json.getString("实际付款日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setActualPayTime(new Timestamp(date.getTime()));
			} else {
				return "实际付款日期格式不正确";
			}
		}
		time = json.getString("实际收票日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setActualInvoiceTime(new Timestamp(date.getTime()));
			} else {
				return "实际收票日期格式不正确";
			}
		}
		return result;
	}

	/**
	 * 校验收款的相关日期
	 * 
	 * @param json
	 * @param record
	 * @return
	 */
	protected String verifyReceiveDate(JSONObject json, ChargeRecord record) {
		String result = "";
		String time = json.getString("收款截止日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setFinalPayTime(new Timestamp(date.getTime()));
			} else {
				return "收款截止日期格式不正确";
			}
		}
		time = json.getString("开票截止日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setInvoiceTime(new Timestamp(date.getTime()));
			} else {
				return "开票截止日期格式不正确";
			}
		}
		time = json.getString("实际收款日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setActualPayTime(new Timestamp(date.getTime()));
			} else {
				return "实际收款日期格式不正确";
			}
		}
		time = json.getString("实际开票日期");
		if (StringUtil.isNotBlank(time)) {
			Date date = DateUtil.convert(time, DateUtil.format1);
			if (date != null) {
				record.setActualInvoiceTime(new Timestamp(date.getTime()));
			} else {
				return "实际开票日期格式不正确";
			}
		}
		return result;
	}

	protected String verifyDate(String dateValue, Timestamp timestamp, String format) {
		String result = "";
		if (StringUtils.isBlank(dateValue)) {
			result = "不能为空";
		} else {
			Date convert = DateUtil.convert(dateValue, format);
			if (convert != null) {
				timestamp.setTime(convert.getTime());
			} else {
				result = "格式不正确";
			}
		}
		return result;
	}

	protected String verifyRemark(JSONObject json) {
		String result = "";
		String remark = json.getString(Constants.DAHAN_REMARK_KEY);
		if (StringUtil.isNotBlank(remark) && remark.length() > 255) {
			result = "备注长度大于255";
		}
		return result;
	}
}
