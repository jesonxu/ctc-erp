package com.dahantc.erp.util;

/**
 * 客户变更任务的常量
 * 
 * @author 8520
 */
public class CustomerChangeConstant {
	/**
	 * 批处理客户数量
	 */
	public static int CUSTOMER_BATCH_SIZE = 100;
	/**
	 * 合同客户无联系日志警告时间间隔（单位 月）
	 */
	public static int CONTRACT_WARN_INTERVAL = 1;
	/**
	 * 合同客户消耗量时间（单位 月）
	 */
	public static int CONTRACT_COST_INTERVAL = 3;
	/**
	 * 合同将要过期通知时间（单位 月）
	 */
	public static int CONTRACT_BEFORE_EXPIRE_INTERVAL = 1;
	/**
	 * 合同过期降级时间间隔（单位 月）
	 */
	public static int CONTRACT_OVERDUE_DOWNGRADE = 3;

	/**
	 * 日志警告间隔时间（单位 月）
	 */
	public static int WARNING_INTERVAL = 1;
	/**
	 * 测试客户联系日志时间（单位 月）
	 */
	public static int TEST_LOG_INTERVAL = 1;
	/**
	 * 测试客户 消耗时间间隔（单位 月）
	 */
	public static int TEST_COST_INTERVAL = 1;
	/**
	 * 意向客户 联系日志间隔 （单位 天）
	 */
	public static int INTEREST_CHANGE_INTERVAL = 15;
	/**
	 * 沉默客户修改的时间间隔 （单位 月）
	 */
	public static int SILENCE_CHANGE_INTERVAL = 3;

}
