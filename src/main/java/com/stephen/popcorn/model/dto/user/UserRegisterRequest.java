package com.stephen.popcorn.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2023-12-02 20:23
 * <p>
 * Serializable 实现序列化接口
 **/
@Data
public class UserRegisterRequest implements Serializable {
	
	private static final long serialVersionUID = 1845160426363029093L;
	
	/**
	 * 用户账号
	 */
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
	/**
	 * 校验密码
	 */
	private String checkPassword;
	
}
