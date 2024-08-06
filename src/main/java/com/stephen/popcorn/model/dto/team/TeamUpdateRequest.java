package com.stephen.popcorn.model.dto.team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: stephen qiu
 * @create: 2024-03-29 20:44
 **/
@Data
public class TeamUpdateRequest implements Serializable {
	private static final long serialVersionUID = 5197363948525048301L;
	
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
	 * 0 - 公开， 1 - 私有， 2 - 加密
	 */
	private Integer status;
	
	/**
	 * 加入队伍的密码
	 */
	private String password;
	
	/**
	 * 过期时间
	 */
	private Date expireTime;
	
}
