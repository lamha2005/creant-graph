package com.creant.graph.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.json.JsonObject;
import com.creant.graph.dao.AccountManager;
import com.creant.graph.om.User;
import com.creant.graph.service.CacheService;
import com.creant.graph.service.MessageFactory;

import util.Security;

/**
 * @author LamHa
 *
 */
@RestController()
@RequestMapping("api")
public class InternalApiController {
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private CacheService cacheService;

	@RequestMapping(path = "verify", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String verify(@RequestHeader(value = "key") String key, @RequestParam(value = "token") String token) {
		try {
			if (!isValidRequest(key))
				return MessageFactory.createErrorMessage(-1, "Bad Request.");

			String userInfo = cacheService.get(Security.encryptMD5(token));
			if (userInfo == null)
				return MessageFactory.createErrorMessage(1000, "User not found");

			JsonObject jo = JsonObject.fromJson(userInfo);
			String uid = jo.getString("uid");
			User user = accountManager.getUserInfo(uid);
			jo.put("money", user.getMoney());
			jo.put("user_id", user.getUserId());
			return MessageFactory.createMessage(jo, null);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return MessageFactory.createErrorMessage(1000, "User not found");
	}

	@RequestMapping(path = "user", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String getUserInfo(@RequestHeader(value = "key") String key, @RequestParam(value = "uid") String uid) {
		if (!isValidRequest(key))
			return MessageFactory.createErrorMessage(-1, "Bad Request.");

		User userInfo = accountManager.getUserInfo(uid);
		if (userInfo == null)
			return MessageFactory.createErrorMessage(1000, "User not found");

		return MessageFactory.createMessage(convertUserToJsonObject(userInfo), null);
	}

	private boolean isValidRequest(String key) {
		return "2|WqRVclir6nj4pk3PPxDCzqPTXl3J".equals(key) || "1|WqRVclir6nj4pk3PPxDCzqPTXl3J".equals(key);
	}

	private JsonObject convertUserToJsonObject(User user) {
		JsonObject jo = JsonObject.create();
		jo.put("user_id", user.getUserId());
		jo.put("uid", user.getUid());
		jo.put("username", user.getUsername());
		jo.put("full_name", user.getFullName());
		jo.put("avatar", user.getAvatar());
		jo.put("gender", user.getGender());
		jo.put("money", user.getMoney());
		jo.put("birthday", "01-01-1977");
		return jo;
	}

	@RequestMapping(path = "logout", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String logout(@RequestParam(value = "token") String token, @RequestParam(value = "key") int key) {
		try {
			cacheService.delete(Security.encryptMD5(token));
			return MessageFactory.createMessage(JsonObject.create(), null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return MessageFactory.createErrorMessage(1000, "User not found");
	}

	@RequestMapping(path = "money", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String updateMoney(@RequestParam(value = "token") String token, @RequestParam(value = "value") int value) {

		String userInfo;
		try {
			userInfo = cacheService.get(Security.encryptMD5(token));
			if (userInfo == null)
				return MessageFactory.createErrorMessage(1000, "User not found");

			JsonObject jo = JsonObject.fromJson(userInfo);
			String uid = jo.getString("uid");
			long currentMoney = accountManager.incrementUserMoney(uid, value);
			jo = JsonObject.create();
			jo.put("money", currentMoney);
			return MessageFactory.createMessage(jo, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return MessageFactory.createErrorMessage(1000, "User not found");
	}

}
