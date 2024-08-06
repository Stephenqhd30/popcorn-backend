package com.stephen.popcorn.common;

import java.io.Serializable;

import lombok.Data;

/**
 * 通用返回类
 *
 * @param <T>
 * @author stephen qiu
 */
@Data
public class BaseResponse<T> implements Serializable {
	
	private static final long serialVersionUID = 3801016192261040965L;
	
	/**
	 * 响应码
	 */
	private int code;
	
	/**
	 * 响应数据
	 */
	private T data;
	
	/**
	 * 响应消息
	 */
	private String message;
	
	public BaseResponse(int code, T data, String message) {
		this.code = code;
		this.data = data;
		this.message = message;
	}
	
	public BaseResponse(int code, T data) {
		this(code, data, "");
	}
	
	public BaseResponse(ErrorCode errorCode) {
		this(errorCode.getCode(), null, errorCode.getMessage());
	}
}
