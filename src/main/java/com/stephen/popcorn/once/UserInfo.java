package com.stephen.popcorn.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 用户信息
 *
 * @author stephen qiu
 */
@Data
public class UserInfo {
	
	/**
	 * 用户昵称
	 */
	@ExcelProperty("姓名")
	private String username;
	
	/**
	 * studentNumber
	 */
	@ExcelProperty("学号")
	private String studentNumber;
	
	
}