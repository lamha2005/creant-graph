package com.creant.graph.service;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.document.json.JsonObject;
import com.creant.graph.dao.AccountManager;
import com.creant.graph.om.IUser;
import com.creant.graph.om.User;

import util.Security;

/**
 * @author LamHa
 *
 */
@Service
public class AccountService implements IAccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private CacheService cacheService;

	@Override
	public IUser verifyToken(String token) {
		LOG.debug("[DEBUG] Request verifyToken");
		try {
			String userInfo = cacheService.get(Security.encryptMD5(token));
			if (userInfo == null)
				return null;

			JsonObject jo = JsonObject.fromJson(userInfo);
			User user = new User();
			user.setUid(jo.getString("uid"));
			user.setUsername(jo.getString("username"));
			user.setFullName(jo.getString("full_name"));
			user.setAvatar(jo.getString("avatar"));
			user.setGender(jo.getInt("gender"));
			user.setMoney(jo.getLong("money"));
			user.setLocation("vn");
			LOG.debug("[DEBUG]" + user.toString());
			return user;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			LOG.error("[ERROR] verifyToken fail!", e);
		}

		return null;
	}

	@Override
	public IUser getUser(String uid) {
		return accountManager.getUserInfo(uid);
	}

}
