package com.stephen.popcorn.model.enums;



/**
 * 队伍状态枚举类
 *
 * @author: stephen qiu
 * @create: 2024-03-28 21:27
 **/

public enum TeamStatusEnum {
	PUBLIC(0, "公开"),
	PRIVATE(1, "私有"),
	SECRET(2, "加密");
	
	/**
	 * 值
	 */
	private int value;
	
	/**
	 * 描述
	 */
	private String text;
	
	
	TeamStatusEnum(int value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public static TeamStatusEnum getEnumByValue(Integer value) {
		if (value == null) {
			return null;
		}
		TeamStatusEnum[] values = TeamStatusEnum.values();
		for (TeamStatusEnum teamStatusEnum : values) {
			if (teamStatusEnum.getValue() == value) {
				return teamStatusEnum;
			}
		}
		return null;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
