package com.stephen.popcorn.model.vo;

import com.stephen.popcorn.model.entity.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

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
	 * 队伍过期时间
	 */
	private Date expireTime;
	
	/**
	 * 创建人id
	 */
	private Long userId;
	
	/**
	 * 队伍状态（0-公开,1-私密）
	 */
	private Integer status;
	
	/**
	 * 最大人数
	 */
	private Integer maxLength;
	
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
	 * 已经加入队伍的人数
	 */
	private Integer hasJoinNum;
	
	/**
	 * 封装类转对象
	 *
	 * @param teamVO teamVO
	 * @return Team
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
	 * @param team team
	 * @return TeamVO
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
