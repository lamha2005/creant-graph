package com.creant.graph.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.creant.graph.dao.AccountManager;
import com.creant.graph.om.User;
import com.creant.graph.service.CacheService;
import com.creant.graph.service.MessageFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.OnFailureListener;
import com.google.firebase.tasks.OnSuccessListener;

import util.GsonUtils;
import util.IdGenerator;

/**
 * @author LamHa
 *
 */
@RestController
public class AccountController {
	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
	private static final String[] avatars = { "http://i.imgur.com/PeWm62C.png", "http://i.imgur.com/997n24i.png",
			"http://i.imgur.com/eScEwQI.png", "http://i.imgur.com/qAfV9wE.png", "http://i.imgur.com/SLXXutF.png",
			"http://i.imgur.com/QY8Vbva.png", "http://i.imgur.com/bKR9OK6.png", "http://i.imgur.com/za7t0cm.png",
			"http://i.imgur.com/5wuOP8g.png", "http://i.imgur.com/NZBYPlC.png" };

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private CacheService cacheService;

	@RequestMapping(path = "user/login/fb", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String loginFacebook(@RequestParam(value = "token") String token,
			@RequestParam(value = "app_id") int appId) {

		LoginFireBaseListener listener = new LoginFireBaseListener();
		FirebaseAuth.getInstance().verifyIdToken(token).addOnSuccessListener(listener);
		int result = listener.awaitUninterruptibly().getResult();

		LOG.debug("[DEBUG] loginFacebook. Result = " + result);
		if (result == 1) {
			JsonObject data = JsonObject.create();
			JsonObject jo = convertUserToJsonObject(listener.getUser());
			data.put("user", jo);
			JsonArray servers = JsonArray.create();
			// TODO refactor duplicate code
			JsonObject server = null;
			for (int i = 0; i < 2; i++) {
				server = JsonObject.create();
				server.put("host", "10.72.100.32:123" + i);
				server.put("name", "Server Test " + (i + 1));
				servers.add(server);
			}
			data.put("server", servers);
			String createMessage = MessageFactory.createMessage(data, null);
			LOG.debug("[DEBUG] loginFacebook. response = " + createMessage);
			cacheService.login(token, jo.toString());
			return createMessage;
		}

		LOG.debug("[DEBUG] loginFacebook fail! User not found");
		return MessageFactory.createErrorMessage(1000, "User not found");
	}

	@RequestMapping(path = "user/login", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String signInWithCustom(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, @RequestParam(value = "app_id") int appId) {

		User user = accountManager.login(username, password);
		if (user == null) {
			return MessageFactory.createErrorMessage(1000, "User not found");
		}

		String token = FirebaseAuth.getInstance().createCustomToken(user.getUid());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);

		JsonObject jo = convertUserToJsonObject(user);
		// TODO refactor duplicate code
		JsonObject data = JsonObject.create();
		data.put("user", jo);

		// TODO tùy theo app gửi lên là gì, sẽ gửi danh sách server để nó thực
		// hiện login, danh sách này có thể cập nhật động từ server
		JsonArray servers = JsonArray.create();
		JsonObject server = null;
		for (int i = 0; i < 2; i++) {
			server = JsonObject.create();
			server.put("host", "10.72.100.32:1235" + i);
			server.put("name", "Server Test " + (i + 1));
			servers.add(server);
		}
		data.put("server", servers);

		cacheService.login(token, jo.toString());
		return MessageFactory.createMessage(data, params);
	}

	@RequestMapping(path = "user/signup", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String register(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, @RequestParam(value = "app_id") int appId) {

		username = username.trim();
		if (username.length() < 6) {
			return MessageFactory.createErrorMessage(1001, "Tên tài khoản phải từ 6-18 ký tự");
		}

		if (password.length() < 3) {
			return MessageFactory.createErrorMessage(1002, "Password phải từ 3-18 ký tự");
		}

		String key = IdGenerator.randomString(28);
		User user = new User();
		user.setUid(key);
		user.setUsername(username);
		user.setPassword(password);
		user.setAvatar(avatars[new Random().nextInt(avatars.length - 1)]);
		user.setFullName(username);
		int result = accountManager.insertUser(user);
		if (result == -1 || result == -2) {
			return MessageFactory.createErrorMessage(1004, "Tài khoản đã tồn tại");
		}

		if (result == -100) {
			return MessageFactory.createErrorMessage(1003, "Đăng ký thất bại");
		}

		return signInWithCustom(username, password, appId);
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

		return jo;
	}

	@RequestMapping(path = "/user", produces = "text/plain;charset=UTF-8")
	public String getUserInfo(@RequestParam(value = "uid") String uid) {
		User user = accountManager.getUserInfo(uid);
		if (user == null) {
			return MessageFactory.createErrorMessage(1004, "Không tìm thấy user này");
		}

		return MessageFactory.createMessage(convertUserToJsonObject(user), null);
	}

	public class LoginFireBaseListener implements OnSuccessListener<FirebaseToken>, OnFailureListener {
		Integer result;
		User user;

		@Override
		public void onSuccess(FirebaseToken decodedToken) {
			String uid = decodedToken.getUid();
			LOG.debug("[DEBUG] login with uid: " + uid);
			user = accountManager.login(uid);
			if (user != null) {
				result = 1;
				return;
			}

			// chưa có tài khoản thực hiện đăng ký mới
			Map<String, Object> claims = decodedToken.getClaims();
			user = new User();
			user.setUid(uid);
			user.setFullName((String) claims.get("name"));
			String avatar = (String) claims.get("picture");
			user.setAvatar(avatar);
			LOG.debug("[DEBUG] insert new user " + GsonUtils.toGsonString(user));
			accountManager.insertUser(user);
			result = 1;
		}

		@Override
		public void onFailure(Exception e) {
			e.printStackTrace();
			result = -1;
			LOG.error("[ERROR] onFailure login firebase!" + GsonUtils.toGsonString(user));
		}

		public Integer getResult() {
			return result;
		}

		public User getUser() {
			return user;
		}

		public LoginFireBaseListener awaitUninterruptibly() {
			while (result == null) {
				try {
					Thread.sleep(500);
					// TODO Kill after time
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOG.error("[ERROR] loginFacebook fail! token: ");
				}
			}

			return this;
		}

	}

}
