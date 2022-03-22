package com.cisco.maas.dto;

/**
 * This class contains AppD Error properties and its setters and getters.
 */
public class AppDError {

	private String type;
	private String msg;
	private int code;
	private String retry;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getRetry() {
		return retry;
	}

	public void setRetry(String retry) {
		this.retry = retry;
	}
}
