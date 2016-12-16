package com.creant.graph.exception;

/**
 * @author lamhm
 *
 */
public class CustomGenericException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public static final String NOT_YET_CONNECTED_NODES = "1000";

	private String errCode;
	private String errMsg;


	public CustomGenericException(String errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}


	public String getErrCode() {
		return errCode;
	}


	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}


	public String getErrMsg() {
		return errMsg;
	}


	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
