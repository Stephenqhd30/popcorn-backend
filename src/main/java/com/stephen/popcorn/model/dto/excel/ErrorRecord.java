package com.stephen.popcorn.model.dto.excel;

import com.stephen.popcorn.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误记录
 * @author stephen qiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRecord {
	/**
	 * 导入的用户数据
	 */
	private User user;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
}
