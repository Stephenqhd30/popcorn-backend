package com.stephen.popcorn.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户的包装类（脱敏）
 *
 * @author: stephen qiu
 * @create: 2024-03-29 15:41
 **/
@Data
public class UserVO implements Serializable {
	private static final long serialVersionUID = 2730413474990629864L;
	/**
	 * id
	 */
	private long id;
	
	/**
	 * 用户昵称
	 */
	private String username;
	
	/**
	 * 账号
	 */
	private String userAccount;
	
	/**
	 * 用户头像
	 */
	private String avatarUrl;
	
	/**
	 * 性别
	 */
	private Integer gender;
	
	/**
	 * 个人简介
	 */
	private String profile;
	
	/**
	 * 电话
	 */
	private String phone;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 状态 - 0 正常
	 */
	private Integer userStatus;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	
	/**
	 * 用户角色 0 - 普通用户 1 - 管理员
	 */
	private Integer userRole;
	
	/**
	 * 学号
	 */
	private String studentNumber;
	
	/**
	 * 标签列表
	 */
	private String tags;
	
}
