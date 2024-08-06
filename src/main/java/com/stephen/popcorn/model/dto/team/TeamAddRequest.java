package com.stephen.popcorn.model.dto.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: stephen qiu
 * @create: 2023-12-02 20:23
 * <p>
 * Serializable 实现序列化接口
 **/
@Data
public class TeamAddRequest implements Serializable {
	
	private static final long serialVersionUID = 8291123241018967699L;
	
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
	 * 过期时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date expireTime;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 0 - 公开， 1 - 私有， 2 - 加密
	 */
	private Integer status;
	
	/**
	 * 加入队伍的密码
	 */
	private String password;
	
}
