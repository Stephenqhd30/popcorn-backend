package com.stephen.popcorn.model.vo;

import com.stephen.popcorn.model.entity.TeamUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍-用户视图
 *
 * @author stephen
 */
@Data
public class TeamUserVO implements Serializable {
	
	private static final long serialVersionUID = -430798399874075608L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 队伍id
	 */
	private Long teamId;
	
	/**
	 * 加入时间
	 */
	private Date joinTime;
	
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
	 * 创建用户信息
	 */
	private TeamVO teamVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param teamUserVO teamUserVO
	 * @return TeamUser
	 */
	public static TeamUser voToObj(TeamUserVO teamUserVO) {
		if (teamUserVO == null) {
			return null;
		}
		TeamUser teamUser = new TeamUser();
		BeanUtils.copyProperties(teamUserVO, teamUser);
		return teamUser;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param teamUser teamUser
	 * @return TeamUserVO
	 */
	public static TeamUserVO objToVo(TeamUser teamUser) {
		if (teamUser == null) {
			return null;
		}
		TeamUserVO teamUserVO = new TeamUserVO();
		BeanUtils.copyProperties(teamUser, teamUserVO);
		return teamUserVO;
	}
}
