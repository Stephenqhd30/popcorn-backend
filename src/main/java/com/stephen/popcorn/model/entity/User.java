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
	@ExcelProperty(value = "用户账号")
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	@ExcelProperty(value = "用户密码")
	private String userPassword;
	
	/**
	 * 开放平台id
	 */
	@ExcelProperty(value = "开放平台id")
	private String unionId;
	
	/**
	 * 公众号openId
	 */
	@ExcelProperty(value = "公众号openId")
	private String mpOpenId;
	
	/**
	 * 用户昵称
	 */
	@ExcelProperty(value = "用户昵称")
	private String userName;
	
	/**
	 * 用户头像
	 */
	@ExcelIgnore
	private String userAvatar;
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	@ExcelProperty(value = "性别（0-男，1-女，2-保密）")
	private Integer userGender;
	
	/**
	 * 用户简介
	 */
	@ExcelProperty(value = "用户简介")
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	@ExcelIgnore
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	@ExcelProperty(value = "用户邮箱")
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	@ExcelProperty(value = "手机号码")
	private String userPhone;
	
	/**
	 * 标签列表(使用JSON字符数组)
	 */
	@ExcelIgnore
	private String tags;
	
	/**
	 * 编辑时间
	 */
	@ExcelIgnore
	private Date editTime;
	
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