package com.creant.graph.service;

import java.util.Map;

import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author LamHa
 *
 */
public class MessageFactory {

	public static String createErrorMessage(int errorCode, String message) {
		JsonObject response = JsonObject.create();
		response.put("code", errorCode);
		response.put("msg", message);
		return response.toString();
	}

	public static String createMessage(JsonObject data, Map<String, Object> params) {
		JsonObject response = JsonObject.create();
		response.put("code", 1);
		response.put("msg", "Thành công");
		response.put("data", data);
		if (params != null) {
			for (String key : params.keySet()) {
				response.put(key, params.get(key));
			}
		}

		return response.toString();
	}
}
