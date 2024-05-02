package com.stephen.popcorn.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类
 *
 * @author: stephen qiu
 * @create: 2024-03-29 15:38
 **/
@Data
public class TeamUserVO implements Serializable {
	private static final long serialVersionUID = -9180128276219367178L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 队伍名称
	 */
	private String name;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 最大人数
	 */
	private Integer maxNum;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 0 - 公开， 1 - 私有， 2 - 加密
	 */
	private Integer status;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 过期时间
	 */
	private Date expireTime;
	
	/**
	 * 创建人用户信息
	 */
	private UserVO createUser;
	
	/**
	 * 已经加入的用户数
	 */
	private Integer hasJoinNum;
	
	/**
	 * 是否加入队伍
	 */
	private boolean hasJoin = false;
	
}
