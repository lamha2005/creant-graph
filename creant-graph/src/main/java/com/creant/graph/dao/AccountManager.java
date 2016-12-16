package com.creant.graph.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creant.graph.om.User;

/**
 * @author LamHa
 *
 */
@Service
public class AccountManager {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDBManager.class);

	@Autowired
	private BaseDBManager dbManager;

	public int insertUser(User user) {
		int result = -100;
		try (Connection conn = dbManager.getAccountPoolConnection();
				CallableStatement cstmt = dbManager.callableStatementWithParam(conn, "sp_account_create", user.getUid(),
						user.getUsername(), user.getPassword(), user.getFullName(), user.getAvatar(), user.getGender(),
						user.getLocation(), user.getBirthday());
				ResultSet rs = cstmt.executeQuery()) {
			if (rs.next()) {
				result = rs.getInt("result");
				if (result != 1) {
					return result;
				}

				user.setMoney(rs.getLong("money"));
			}

		} catch (Exception e) {
			LOG.error("[ERROR] insertUser fail! ", e);
		}

		return result;
	}

	public User login(String username, String password) {
		try (Connection conn = dbManager.getAccountPoolConnection();
				CallableStatement cstmt = dbManager.callableStatementWithParam(conn, "sp_account_login", username,
						password);
				ResultSet rs = cstmt.executeQuery()) {

			// TODO Throw exception sai pass hoặc không tồn tại
			if (rs.next()) {
				int result = rs.getInt("result");
				if (result != 1) {
					return null;
				}

				User user = new User();
				user.setUserId(rs.getInt("user_id"));
				user.setUid(rs.getString("uid"));
				user.setUsername(rs.getString("username"));
				user.setAvatar(rs.getString("avatar"));
				user.setFullName(rs.getString("full_name"));
				user.setMoney(rs.getLong("money"));
				return user;
			}

		} catch (Exception e) {
			LOG.error("[ERROR] login fail! username: " + username, e);
		}

		return null;
	}

	public long incrementUserMoney(String uid, long value) {
		ResultSet rs = null;
		long result = 0;
		try (Connection conn = dbManager.getAccountPoolConnection();
				CallableStatement statement = conn.prepareCall("{ call sp_user_money_update(?, ?) }")) {
			statement.setString(1, uid);
			statement.setLong(2, value);

			rs = statement.executeQuery();
			if (rs.next()) {
				result = rs.getLong("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public User login(String uid) {
		try (Connection conn = dbManager.getAccountPoolConnection();
				CallableStatement cstmt = dbManager.callableStatementWithParam(conn, "sp_account_login_uid", uid);
				ResultSet rs = cstmt.executeQuery()) {

			if (rs.next()) {
				User user = new User();
				user.setUserId(rs.getInt("user_id"));
				user.setUid(rs.getString("uid"));
				user.setUsername(rs.getString("username"));
				user.setAvatar(rs.getString("avatar"));
				user.setFullName(rs.getString("full_name"));
				user.setMoney(rs.getLong("money"));
				return user;
			}

		} catch (Exception e) {
			LOG.error("[ERROR] login fail! uid: " + uid, e);
		}

		return null;
	}

	public User getUserInfo(String uid) {
		try (Connection conn = dbManager.getAccountPoolConnection();
				CallableStatement cstmt = dbManager.callableStatementWithParam(conn, "sp_account_login_uid", uid);
				ResultSet rs = cstmt.executeQuery()) {

			if (rs.next()) {
				User user = new User();
				user.setUserId(rs.getInt("user_id"));
				user.setUid(rs.getString("uid"));
				user.setUsername(rs.getString("username"));
				user.setAvatar(rs.getString("avatar"));
				user.setFullName(rs.getString("full_name"));
				user.setMoney(rs.getLong("money"));
				return user;
			}

		} catch (Exception e) {
			LOG.error("[ERROR] login fail! uid: " + uid, e);
		}

		return null;

	}

}
