package com.stephen.popcorn.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author: stephen qiu
 * @create: 2024-03-30 13:56
 **/
@Data
public class TeamQuitRequest implements Serializable {
	
	private static final long serialVersionUID = -2778720844933428388L;
	
	/**
	 * id
	 */
	private Long teamId;
	
	
	
}
