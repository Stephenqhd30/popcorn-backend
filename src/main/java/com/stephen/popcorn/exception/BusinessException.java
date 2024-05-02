package com.stephen.popcorn.exception;

import com.stephen.popcorn.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author: stephen qiu
 * @create: 2023-12-03 17:54
 **/
public class BusinessException extends RuntimeException {
	
	/**
	 * 异常码
	 */
	private final int code;
	
	/**
	 * 描述
	 */
	private final String description;
	
	public BusinessException(String message, int code, String description) {
		super(message);
		this.code = code;
		this.description = description;
	}
	
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
		this.description = errorCode.getDescription();
	}
	
	public BusinessException(ErrorCode errorCode, String description) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
		this.description = description;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
}
