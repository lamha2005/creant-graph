package com.creant.graph.service;

import com.creant.graph.om.IUser;

/**
 * @author LamHa
 *
 */
public interface IAccountService {

	IUser verifyToken(String token);

	IUser getUser(String uid);
}
