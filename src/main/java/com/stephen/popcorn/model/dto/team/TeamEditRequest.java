package com.stephen.popcorn.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑队伍请求
 *
 * @author stephen qiu
 */
@Data
public class TeamEditRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 队伍名称
     */
    private String teamName;
    
    /**
     * 队伍简介
     */
    private String teamProfile;
    
    /**
     * 队伍状态（0-公开,1-私密）
     */
    private Integer status;
    
    /**
     * 最大人数
     */
    private Integer maxLength;
    
    /**
     * 队伍密码
     */
    private String teamPassword;
    

    private static final long serialVersionUID = 1L;
}