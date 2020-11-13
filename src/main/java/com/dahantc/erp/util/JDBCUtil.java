package com.dahantc.erp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.dahantc.erp.commom.dao.BaseException;

public class JDBCUtil {
	private static final Logger logger = LogManager.getLogger(JDBCUtil.class);

	public static int excuteSql(Connection connection, String sql, Object[] values, Type[] typeValues) throws Exception {
		PreparedStatement _st = null;
		int result = 0;
		long _start = System.currentTimeMillis();
		try {
			connection.setAutoCommit(false);
			_st = connection.prepareStatement(sql);
			for (int i = 0; i < values.length; i++) {

				if (StandardBasicTypes.STRING.equals(typeValues[i])) {
					_st.setString(i + 1, (String) values[i]);
				} else if (StandardBasicTypes.INTEGER.equals(typeValues[i])) {
					if (values[i] == null) {
						_st.setObject(i + 1, values[i]);
					} else {
						_st.setInt(i + 1, (int) values[i]);
					}
				} else if (StandardBasicTypes.TIMESTAMP.equals(typeValues[i])) {
					_st.setTimestamp(i + 1, (Timestamp) values[i]);
				} else if (StandardBasicTypes.LONG.equals(typeValues[i])) {
					if (values[i] == null) {
						_st.setObject(i + 1, values[i]);
					} else {
						_st.setLong(i + 1, (long) values[i]);
					}
				}
			}
			logger.debug("JDBC封装数据耗时：" + (System.currentTimeMillis() - _start));
			result = _st.executeUpdate();
			connection.commit();
			logger.debug("JDBC插入数据耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (Exception e1) {
				logger.error("事务回滚异常：", e1);

			}
			throw new BaseException(e);
		} finally {
			if (null != _st) {
				try {
					_st.close();
				} catch (Exception e) {
					logger.error("关闭异常：", e);
				}
			}
		}
		return result;
	}

	public static void closeConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
