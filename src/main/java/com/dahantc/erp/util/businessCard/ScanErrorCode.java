package com.dahantc.erp.util.businessCard;

import com.dahantc.erp.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度的卡片扫描返回的错误码
 *
 * @author 8520
 */
public class ScanErrorCode {
    private static Map<String, String> errorCodeInfo = new HashMap<>();

    static {
        errorCodeInfo.put("4", "集群超限额");
        errorCodeInfo.put("17", "每天请求量超限额");
        errorCodeInfo.put("18", "QPS超限额");
        errorCodeInfo.put("19", "请求总量超限额");
        errorCodeInfo.put("216200", "图片为空");
        errorCodeInfo.put("216201", "上传的图片格式错误");
        errorCodeInfo.put("216202", "上传图片过大");
        errorCodeInfo.put("216630", "识别错误");
        errorCodeInfo.put("216631", "识别银行卡错误");
        errorCodeInfo.put("216633", "识别身份证错误");
        errorCodeInfo.put("216634", "检测错误");
        errorCodeInfo.put("282003", "请求参数缺失");
        errorCodeInfo.put("282100", "图片压缩转码错误");
        errorCodeInfo.put("282102", "未检测到图片中识别目标");
        errorCodeInfo.put("282103", "图片目标识别错误");
        errorCodeInfo.put("282810", "图像识别错误");
    }

    public static String getErrorDesc(String code) {
        if (StringUtil.isBlank(code)){
            return "识别成功";
        }
        String codeDesc = errorCodeInfo.get(code);
        if (StringUtil.isBlank(codeDesc)) {
            codeDesc = "请求接口错误";
        }
        return codeDesc;
    }

}
