package com.stephen.popcorn.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.stephen.popcorn.model.entity.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍视图
 *
 * @author stephen
 */
@Data
public class TeamVO implements Serializable {
    
    private static final long serialVersionUID = 3268915677530937138L;
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
     * 创建人id
     */
    private Long userId;
    
    /**
     * 队伍状态（0-公开,1-私密,2-需要密码）
     */
    private Integer status;
    
    /**
     * 队伍密码
     */
    private String teamPassword;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 封装类转对象
     *
     * @param teamVO
     * @return
     */
    public static Team voToObj(TeamVO teamVO) {
        if (teamVO == null) {
            return null;
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamVO, team);
        return team;
    }

    /**
     * 对象转封装类
     *
     * @param team
     * @return
     */
    public static TeamVO objToVo(Team team) {
        if (team == null) {
            return null;
        }
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        return teamVO;
    }
}
