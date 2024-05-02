package com.stephen.popcorn.util;

import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.ErrorCode;

/**
 * 返回工具类
 *
 * @author: stephen qiu
 * @create: 2023-12-03 17:18
 **/
public class ResultUtils {
	
	/**
	 * 成功
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> BaseResponse<T> success(T data) {
		return new BaseResponse(0, data, "ok");
	}
	
	/**
	 * 失败
	 *
	 * @param errorCode
	 * @return
	 */
	public static BaseResponse error(ErrorCode errorCode) {
		return new BaseResponse<>(errorCode);
	}
	
	/**
	 * 失败
	 *
	 * @param code
	 * @param message
	 * @param description
	 * @return
	 */
	public static BaseResponse error(int code, String message, String description) {
		return new BaseResponse(code, null, message, description);
	}
	
	/**
	 * @param errorCode
	 * @param message
	 * @param description
	 * @return
	 */
	public static BaseResponse error(ErrorCode errorCode, String message, String description) {
		return new BaseResponse(errorCode.getCode(), null, message, description);
	}
	
	
	/**
	 * @param errorCode
	 * @param description
	 * @return
	 */
	public static BaseResponse error(ErrorCode errorCode, String description) {
		return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
	}
}
