package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MessageType {
    /**
     * 0 新增消息
     */
    ADD_MESSAGE(0, "新增消息"),
    /**
     * 1.新增客户
     **/
    ADD_CUSTOMER(1, "新增客户"),

    /**
     * 2.新增供应商
     **/
    ADD_SUPPLIER(2, "新增供应商"),

    /**
     * 3.新增日志
     **/
    ADD_LOG(3, "新增日志"),

    /**
     * 4.新增联系供应商日志
     **/
    BULLETIN(4, "新增联系供应商日志"),

    /**
     * 5 公告信息
     */
    PUBLIC_INFO(5, "公告信息"),

    /**
     * 6 客户警告信息
     */
    CUSTOMER_WARNING(6, "客户警告通知"),

    /**
     * 7 异常账单告警
     */
    ANOMALOUS_BILL(7, "异常账单告警"),

    /**
     * 8 异常流程告警
     */
    ANOMALOUS_FLOW(8, "异常流程告警")
    ;


    private int code;

    private String desc;

    private static String[] descs;


    MessageType() {

    }

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getCode() {
        return this.code;
    }

    public static String[] getDescs() {
        MessageType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

    public static Optional<MessageType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<MessageType> getEnumsByMsg(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }

    public static String getMessageType(int type) {
		String result = "";
		Optional<MessageType> messageType = MessageType.getEnumsByCode(type);
		if (messageType.isPresent()) {
			result = messageType.get().getDesc();
		}
		return result;
	}
}
