package com.stephen.popcorn.model.dto.teamUser;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建队伍-用户请求
 *
 * @author stephen qiu
 */
@Data
public class TeamUserAddRequest implements Serializable {
    
    /**
     * 用户id
     */
    private Long userId;
    
    /**
     * 队伍id
     */
    private Long teamId;

    private static final long serialVersionUID = 1L;
}