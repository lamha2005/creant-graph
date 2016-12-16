package com.creant.graph.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import snaq.db.ConnectionPool;
import snaq.db.Select1Validator;
import util.Configs;

/**
 * @author LamHa
 *
 */
@Service
public class BaseDBManager {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDBManager.class);
	protected static ConnectionPool accountPool;

	private BaseDBManager() {
		try {
			LOG.info("------------------ INIT DATABASE ---------------------");
			DriverManager.registerDriver((Driver) Class.forName(Configs.sqlDriver).newInstance());

			// init connection pool
			accountPool = new ConnectionPool("ACCOUNT_POOL", Configs.sqlWriteMinPoolSize, Configs.sqlWriteMaxPoolSize,
					Configs.sqlMaxSize, Configs.sqlConnectTimeout, Configs.sqlUrl, Configs.sqlUsername,
					Configs.sqlPassword);
			accountPool.setAsyncDestroy(true);
			accountPool.setCaching(false);
			accountPool.setValidator(new Select1Validator());
			LOG.info("------------------ FINISHED INIT DATABASE ---------------------");
		} catch (Exception e) {
			LOG.error("Init driver fail! ", e);
		}

	}

	public Connection getAccountPoolConnection() throws SQLException {
		return accountPool.getConnection();
	}

	/**
	 * Build callable statement
	 * 
	 * @param conn
	 *            : Connection.
	 * @param procedure
	 *            : Procedure name.
	 * @param params
	 *            : Object array.
	 * @return CallableStatement instance
	 */
	public CallableStatement callableStatementWithParam(Connection conn, String procedure, Object... params)
			throws Exception {
		CallableStatement callableStatement = null;
		if (params != null) {
			String sql = formatCallableStatement(procedure, params.length);
			callableStatement = conn.prepareCall(sql);
			for (int i = 1; i <= params.length; i++) {
				callableStatement.setObject(i, params[i - 1]);
			}
		} else {
			callableStatement = conn.prepareCall(formatCallableStatement(procedure, 0));
		}

		return callableStatement;
	}

	/**
	 * Execute Single Query (Insert, delete, Update).
	 * 
	 * @param procedure
	 * @param params
	 * @return total row effected
	 */
	public boolean executeUpdate(String procedure, Object... params) {
		boolean result = true;
		try (Connection conn = getAccountPoolConnection();
				CallableStatement cstmt = callableStatementWithParam(conn, procedure, params)) {
			cstmt.executeUpdate();
		} catch (Exception e) {
			result = false;
			LOG.error("[ERROR] executeUpdatex fail! pro:" + procedure, e);
		}

		return result;
	}

	public void destroy() {
		if (accountPool != null) {
			accountPool.release();
		}
	}

	/**
	 * Build callable statement string from procedure & parameter length
	 * 
	 * @param procedureName
	 * @param paramLength
	 * @return String format {CALL prc_storename(?,?)}
	 */
	private String formatCallableStatement(String procedureName, int paramLength) {
		StringBuilder sql = new StringBuilder();
		sql.append("{CALL ");
		sql.append(procedureName);
		sql.append("(");
		for (int i = 1; i <= paramLength; i++) {
			sql.append("?");
			if (i < paramLength) {
				sql.append(",");
			}
		}
		sql.append(")");
		sql.append("}");
		return sql.toString();
	}

}
