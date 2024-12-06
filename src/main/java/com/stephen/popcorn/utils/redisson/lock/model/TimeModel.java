package com.stephen.popcorn.utils.redisson.lock.model;

import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.common.exception.BusinessException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具时间模型实体类
 *
 * @author stephen qiu
 */
@Data
@Accessors(chain = true)
public class TimeModel implements Serializable {
	
	private static final long serialVersionUID = -5679390240007999016L;
	/**
	 * 时间
	 */
	private Long time;
	
	/**
	 * 单位
	 */
	private TimeUnit unit;
	
	public TimeModel(Long time, TimeUnit unit) {
		if (ObjectUtils.isEmpty(time) || ObjectUtils.isEmpty(unit)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间和单位均不能为空");
		}
		if (time <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间需要大于0");
		}
		this.time = time;
		this.unit = unit;
	}
	
	/**
	 * 转换成毫秒值
	 */
	public long toMillis() {
		return unit.toMillis(time);
	}
	
}
