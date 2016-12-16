package com.creant.graph.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creant.graph.controller.AccountController;

/**
 * @author LamHa
 *
 */
@Service
public class BotFactoryService implements InitializingBean {

	@Autowired
	AccountController accountController;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String signInWithCustom = accountController.signInWithCustom("123456", "123456", 2);
		System.out.println("[ERROR] " + signInWithCustom);
		String signInWithCustom1 = accountController.signInWithCustom("rrrrrr", "123456", 2);
		System.out.println("[ERROR] " + signInWithCustom1);
	}

}
