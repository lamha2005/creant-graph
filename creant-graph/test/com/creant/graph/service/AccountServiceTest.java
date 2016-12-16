package com.creant.graph.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.creant.graph.AvengersGraphApplication;
import com.creant.graph.dao.AccountManager;
import com.creant.graph.om.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import util.Configs;
import util.IdGenerator;

/**
 * @author LamHa
 *
 */
public class AccountServiceTest {

	static AccountService accountService;
	static AccountManager accountManager;

	@BeforeClass
	public static void init() {
		PropertyConfigurator.configure("configs/log4j.properties");
		FirebaseOptions options;
		try {
			options = new FirebaseOptions.Builder()
					.setServiceAccount(new FileInputStream("configs/avg-graph-account-26f6103dd586.json"))
					.setDatabaseUrl("https://avg-graph.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);
			Configs.init();
			ConfigurableApplicationContext run = SpringApplication.run(AvengersGraphApplication.class, new String[] {});
			accountService = run.getBean(AccountService.class);
			accountManager = run.getBean(AccountManager.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void insertAccountTest() {
		String username = "testuser1";
		String password = "123456";

		String key = IdGenerator.randomString(28);
		User user = new User();
		user.setUid(key);
		user.setUsername(username);
		user.setPassword(password);
		user.setFullName(username);
		int insertUser = accountManager.insertUser(user);
		System.out.println(insertUser);
	}
}
