package com.stephen.popcorn.exception;

import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 * <p>
 * 利用Spring的AOP切面 再调用方法的前后进行额外的处理
 *
 * @author: stephen qiu
 * @create: 2023-12-03 21:38
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	/**
	 * 该方法只捕获 BusinessException
	 *
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public BaseResponse businessExceptionHandler(BusinessException e) {
		log.error("businessException : ", e.getMessage(), e);
		return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
	}
	
	/**
	 * 该方法捕获 RuntimeException
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	public BaseResponse runtimeException(RuntimeException e) {
		log.error("RuntimeException", e);
		return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
	}
	
}
