package com.stephen.popcorn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍-用户表(硬删除)
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
	 * 队长id
	 */
	private Long captainId;
	
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
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}