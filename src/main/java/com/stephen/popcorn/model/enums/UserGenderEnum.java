package com.stephen.popcorn.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户性别枚举
 *
 * @author stephen qiu
 */
@Getter
public enum UserGenderEnum {
	
	MALE("男", 0),
	FEMALE("管理员", 1),
	SECURITY("被封号", 2);
	
	private final String text;
	
	private final int value;
	
	UserGenderEnum(String text, int value) {
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
	public static UserGenderEnum getEnumByValue(int value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (UserGenderEnum anEnum : UserGenderEnum.values()) {
			if (anEnum.value == value) {
				return anEnum;
			}
		}
		return null;
	}
	
}
