package com.stephen.popcorn.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍-用户表
 *
 * @author stephen qiu
 * @TableName team_user
 */
@TableName(value = "team_user")
@Data
public class TeamUser implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 队伍id
	 */
	private Long teamId;
	
	/**
	 * 加入时间
	 */
	private Date joinTime;
	
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