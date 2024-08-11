package com.stephen.popcorn.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 已登录用户视图（脱敏）
 *
 * @author stephen qiu
 **/
@Data
public class LoginUserVO implements Serializable {
	
	private static final long serialVersionUID = 2837672255648064012L;
	/**
	 * 用户 id
	 */
	private Long id;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户简介
	 */
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 用户性别（0-男 ，1-女，2-保密）
	 */
	private Integer userGender;
	
	/**
	 * 标签列表
	 */
	private List<String> tagList;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
}