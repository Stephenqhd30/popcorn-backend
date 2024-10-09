package com.stephen.popcorn.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author stephen qiu
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
	
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@ExcelIgnore
	private Long id;
	
	/**
	 * 用户账号
	 */
	@ExcelProperty("用户账号")
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	@ExcelProperty("用户密码")
	private String userPassword;
	
	/**
	 * 用户昵称
	 */
	@ExcelProperty("用户昵称")
	private String userName;
	
	/**
	 * 用户性别（0-男 ，1-女，2-保密）
	 */
	@ExcelProperty("用户性别（0-男 ，1-女，2-保密）")
	private Integer userGender;
	
	/**
	 * 用户头像
	 */
	@ExcelIgnore
	private String userAvatar;
	
	/**
	 * 用户简介
	 */
	@ExcelProperty("用户简介")
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	@ExcelProperty("用户角色：user/admin/ban")
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	@ExcelProperty("用户邮箱")
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	@ExcelProperty("手机号码")
	private String userPhone;
	
	/**
	 * 标签列表
	 */
	@ExcelIgnore
	private String tags;
	
	/**
	 * 创建时间
	 */
	@ExcelIgnore
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	@ExcelIgnore
	private Date updateTime;
	
	/**
	 * 是否删除
	 */
	@TableLogic
	@ExcelIgnore
	private Integer isDelete;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}