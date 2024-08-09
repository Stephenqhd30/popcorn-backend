package com.stephen.popcorn.exception;

import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author stephen qiu
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BusinessException.class)
	public BaseResponse<?> businessExceptionHandler(BusinessException e) {
		log.error("BusinessException", e);
		return ResultUtils.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
		log.error("RuntimeException", e);
		return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
	}
}
