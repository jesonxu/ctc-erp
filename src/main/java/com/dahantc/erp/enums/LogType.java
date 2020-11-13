/**
 * 
 */
package com.dahantc.erp.enums;

/**
 * 日志类型
 * 
 * @author 8541
 */
public enum LogType {
	
	/**操作日志**/
	OperationLog("操作日志"),
	
	/**登录日志**/
	LoginLog("登录日志"),
	
	/**充值日志**/
	RechargeLog("充值日志"),
	
	/**用户日志【新建、配置修改、密码修改】**/
	UserLog("用户日志"),
	
	/**审核日志**/
	AuditLog("审核日志"),
	
	/**发送日志【单条、群发、个性、定时等】**/
	SendLog("发送日志"),
	
	/**通道日志【业务类型、新建、签名、配置等】**/
	ChanLog("通道日志"),
	
	/**接口日志**/
	ApiLog("接口日志");
	
	private String desc;
	
	private static String[] descs;
	
	LogType(){
		
	}
	
	LogType(String desc){
		this.desc = desc;
	}
	
	public String getDesc(){
		return this.desc;
	}
	
	public static String[] getDescs(){
		int len = values().length;
		descs = new String[len];
		for(int i = 0; i<len; i++){
			descs[i] = values()[i].getDesc();
		}
		return descs;
	}
}
