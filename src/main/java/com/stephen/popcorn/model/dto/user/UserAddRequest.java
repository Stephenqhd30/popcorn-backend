package com.stephen.popcorn.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *
 * @author stephen qiu
 */
@Data
public class UserAddRequest implements Serializable {
	
	private static final long serialVersionUID = -6510457969873015318L;
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 账号
	 */
	private String userAccount;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户角色: user, admin
	 */
	private String userRole;
	
	/**
	 * 用户简介
	 */
	private String userProfile;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
}