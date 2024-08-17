package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.dto.team.TeamJoinRequest;
import com.stephen.popcorn.model.dto.team.TeamQueryRequest;
import com.stephen.popcorn.model.dto.team.TeamQuitRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.vo.TeamVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 队伍服务
 *
 * @author stephen qiu
 */
public interface TeamService extends IService<Team> {
	
	/**
	 * 校验数据
	 *
	 * @param team
	 * @param add  对创建的数据进行校验
	 */
	void validTeam(Team team, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param teamQueryRequest
	 * @return
	 */
	QueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest);
	
	/**
	 * 获取队伍封装
	 *
	 * @param team
	 * @param request
	 * @return
	 */
	TeamVO getTeamVO(Team team, HttpServletRequest request);
	
	/**
	 * 分页获取队伍封装
	 *
	 * @param teamPage
	 * @param request
	 * @return
	 */
	Page<TeamVO> getTeamVOPage(Page<Team> teamPage, HttpServletRequest request);
	
	
	/**
	 * 加入队伍
	 *
	 * @param teamJoinRequest
	 * @param request
	 * @return
	 */
	boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request);
	
	/**
	 * 退出队伍
	 *
	 * @param teamQuitRequest
	 * @param request
	 * @return
	 */
	boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request);
}