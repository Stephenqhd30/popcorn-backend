package com.stephen.popcorn.model.dto.team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新队伍请求
 *
 * @author stephen qiu
 */
@Data
public class TeamUpdateRequest implements Serializable {
    
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
     * 队伍图标
     */
    private String coverImage;
    
    /**
     * 队伍过期时间
     */
    private Date expireTime;
    
    /**
     * 队伍状态（0-公开,1-私密,2-需要密码）
     */
    private Integer status;
    
    /**
     * 队伍密码
     */
    private String teamPassword;

    private static final long serialVersionUID = 1L;
}