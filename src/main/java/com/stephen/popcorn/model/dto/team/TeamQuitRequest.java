package com.stephen.popcorn.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2024-08-17 16:20
 **/
@Data
public class TeamQuitRequest implements Serializable {
	
	private static final long serialVersionUID = -6173302335797008911L;
	/**
	 * 队伍Id
	 */
	private Long teamId;
}
