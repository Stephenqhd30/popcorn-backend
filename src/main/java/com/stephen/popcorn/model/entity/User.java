package com.stephen.popcorn.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @author stephen qiu
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 账号
	 */
	private String userAccount;
	
	/**
	 * 密码
	 */
	private String userPassword;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 性别
	 */
	private Integer gender;
	
	/**
	 * 用户简介
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
	 * 学号
	 */
	private String studentNumber;
	
	/**
	 * 用户角色
	 */
	private String userRole;
	
	/**
	 * 标签列表
	 */
	private String tags;
	
	/**
	 * 状态 - 0 正常 - 1禁止
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
	 * 是否删除
	 */
	@TableLogic
	private Integer isDelete;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}