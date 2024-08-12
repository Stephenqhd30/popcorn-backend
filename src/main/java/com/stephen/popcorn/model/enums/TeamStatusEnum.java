package com.stephen.popcorn.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 队伍状态枚举类
 * 队伍状态（0-公开,1-私密,2-需要密码）
 *
 * @author stephen qiu
 */
@Getter
public enum TeamStatusEnum {
	
	PUBLIC("公开", 0),
	SECURITY("私密", 1),
	NEED_PASSWORD("需要密码", 2);
	
	private final String text;
	
	private final Integer value;
	
	TeamStatusEnum(String text, Integer value) {
		this.text = text;
		this.value = value;
	}
	
	/**
	 * 获取值列表
	 *
	 * @return
	 */
	public static List<Integer> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value
	 * @return
	 */
	public static TeamStatusEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (TeamStatusEnum anEnum : TeamStatusEnum.values()) {
			if (Objects.equals(anEnum.value, value)) {
				return anEnum;
			}
		}
		return null;
	}
}
