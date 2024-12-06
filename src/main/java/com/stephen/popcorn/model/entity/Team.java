package com.stephen.popcorn.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 *
 * @author stephen qiu
 * @TableName team
 */
@TableName(value = "team")
@Data
public class Team implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 队伍名称
	 */
	private String teamName;
	
	/**
	 * 队伍简介
	 */
	private String teamProfile;
	
	/**
	 * 队伍过期时间
	 */
	private Date expireTime;
	
	/**
	 * 创建人id
	 */
	private Long userId;
	
	/**
	 * 队伍状态（0-公开,1-私密）
	 */
	private Integer status;
	
	/**
	 * 最大人数
	 */
	private Integer maxLength;
	
	/**
	 * 队伍密码
	 */
	private String teamPassword;
	
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