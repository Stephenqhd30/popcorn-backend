package com.stephen.popcorn.constant;

/**
 * @author: stephen qiu
 * @create: 2023-12-02 23:36
 * <p>
 * 接口类属性默认都是 public static final
 **/
public interface UserConstant {
	
	/**
	 * 用户登录态 键
	 */
	String USER_LOGIN_STATE = "userLoginState";
	
	// ---- 权限 ----
	/**
	 * DEFAULT_ROLE 默认权限
	 */
	String DEFAULT_ROLE = "user";
	
	/**
	 * ADMIN_ROLE 管理员权限
	 */
	String ADMIN_ROLE = "admin";
	
	
}
