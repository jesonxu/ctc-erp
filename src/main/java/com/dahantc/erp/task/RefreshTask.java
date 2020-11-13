package com.dahantc.erp.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;

@Component
public class RefreshTask {
	private static final Logger logger = LogManager.getLogger(RefreshTask.class);
	private final long sleepLong = 2 * 60 * 1000;

	@Autowired
	private IParameterService parameterService;

	@Scheduled(fixedDelay = sleepLong)
	public void propertyTask() {
		try {
			long startTime = System.currentTimeMillis();
			loadSystemParam();
			logger.info("刷新系统参数共耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Throwable e) {
			logger.error("刷新系统参数", e);
		}
	}

	/**
	 * 
	 * 方法描述：系统参数
	 * 
	 * @author: 8518
	 * @date: 2019年4月16日 下午1:37:32
	 */
	public void loadSystemParam() {
		Parameter parameter;
		try {
			parameter = parameterService.readOneByProperty("paramkey", "resource");
			if (parameter != null) {
				/** 缓存资源路径信息 */
				Constants.RESOURCE = parameter.getParamvalue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", "statistics_delay_days");
			if (parameter != null) {
				/** 缓存资源路径信息 */
				Constants.STATISTICS_DELAY_DAYS = Integer.parseInt(parameter.getParamvalue());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", "pdf_path");
			if (parameter != null) {
				/** 缓存资源路径信息 */
				Constants.DS_BUY_ORDER_PATH  = parameter.getParamvalue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", "ds_purchase_flow_name");
			if (parameter != null) {
				/** 缓存资源路径信息 */
				Constants.DS_PURCHASE_FLOW_NAME = parameter.getParamvalue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_SMS");
			if (parameter != null) {
				/** 短信产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_SMS = parameter.getParamvalue();
			}
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_MMS");
			if (parameter != null) {
				/** 彩信产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_MMS = parameter.getParamvalue();
			}
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_SUPER_MMS");
			if (parameter != null) {
				/** 超级短信产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_SUPER_MMS = parameter.getParamvalue();
			}
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_INTER_SMS");
			if (parameter != null) {
				/** 国际短信产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_INTER_SMS = parameter.getParamvalue();
			}
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_VOICE_TIME");
			if (parameter != null) {
				/** 语音(按时计费)产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_VOICE_TIME = parameter.getParamvalue();
			}
			parameter = parameterService.readOneByProperty("paramkey", "PRODUCT_TYPE_KEY_VOICE_TIME");
			if (parameter != null) {
				/** 移动认证产品类型标识 */
				Constants.PRODUCT_TYPE_KEY_MOBILE_AUTH = parameter.getParamvalue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", Constants.WORK_TIME_KEY);
			if (parameter != null) {
				/** 工作时间 */
				Constants.DEFAULT_WORK_TIME = parameter.getParamvalue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", Constants.TIME_DELAY_KEY_1);
			if (parameter != null) {
				/** 迟到分钟数 */
				Constants.DEFAULT_LATE_MINUTES = Integer.parseInt(parameter.getParamvalue());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			parameter = parameterService.readOneByProperty("paramkey", Constants.TIME_DELAY_KEY_2);
			if (parameter != null) {
				/** 旷工分钟数 */
				Constants.DEFAULT_ABSENTEEISM_MINUTES = Integer.parseInt(parameter.getParamvalue());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
