package com.dahantc.erp.util;

import java.io.File;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用JavaScript工具类
 * 
 * @author 8520
 */
public class JavaScriptUtil {
    private static final Logger logger = LoggerFactory.getLogger(JavaScriptUtil.class);

    private static final String SCRIPT_TYPE = "javascript";

    /**
     * 调用JS中的方法
     * @param methodName 方法名
     * @param scriptPath 脚本路径
     * @param clazz 返回类型
     * @param params 参数
     * @param <T> 返回类型
     * @return T
     */
	@SuppressWarnings("unchecked")
	public static <T> T invokeScript(String methodName, String scriptPath,Class<T> clazz, Object... params) {
		try {
			File file = new File(scriptPath);
			if (!file.exists()) {
				return null;
			}
			ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(SCRIPT_TYPE);
			scriptEngine.eval(new FileReader(file));
			if (scriptEngine instanceof Invocable) {
				Invocable invocable = (Invocable) scriptEngine;
				Object result = invocable.invokeFunction(methodName, params);
				if (result != null && result.getClass() == clazz) {
					return (T) result;
				}
			}
		} catch (Exception e) {
			logger.error("调用脚本的时候出现异常", e);
		}
		return null;
	}
}
