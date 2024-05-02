package com.stephen.popcorn.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2023-12-02 20:23
 * <p>
 * Serializable 实现序列化接口
 **/
@Data
public class UserLoginRequest implements Serializable {
	
	private static final long serialVersionUID = 3450145564968117836L;
	/**
	 * 用户账号
	 */
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
}
