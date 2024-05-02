package com.stephen.popcorn.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2024-03-30 13:56
 **/
@Data
public class TeamJoinRequest implements Serializable {
	private static final long serialVersionUID = 8736556318233678064L;
	
	/**
	 * id
	 */
	private Long teamId;
	
	/**
	 * 密码
	 */
	private String password;
	
}
