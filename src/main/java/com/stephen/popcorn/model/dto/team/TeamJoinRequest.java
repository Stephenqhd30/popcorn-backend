package com.stephen.popcorn.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2024-08-17 15:50
 **/
@Data
public class TeamJoinRequest implements Serializable {
	
	private static final long serialVersionUID = -2577968141017140063L;
	/**
	 * 队伍id
	 */
	private Long teamId;
	
	/**
	 * 队伍密码
	 */
	private String teamPassword;
}
