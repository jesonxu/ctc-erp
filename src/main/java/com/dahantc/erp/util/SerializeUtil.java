package com.dahantc.erp.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerializeUtil {

	private static final Logger logger = LogManager.getLogger(SerializeUtil.class);

	/**
	 * 序列化
	 * 
	 * @param object
	 * @param filePath
	 */
	public static void serialize(Serializable object, String filePath) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filePath);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException ie) {
				logger.error(ie.getMessage(), ie);
			}
		}
	}

	/**
	 * 反序列化
	 * 
	 * @param filePath
	 * @return
	 */
	public static Object deserialize(String filePath) {
		Object object = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			object = ois.readObject();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				ois.close();
				fis.close();
			} catch (IOException ie) {
				logger.error(ie.getMessage(), ie);
			}
		}
		return object;
	}

}
