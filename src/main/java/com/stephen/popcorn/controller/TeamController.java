package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.model.domain.Team;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.model.domain.UserTeam;
import com.stephen.popcorn.model.dto.TeamQuery;
import com.stephen.popcorn.model.request.TeamAddRequest;
import com.stephen.popcorn.model.request.TeamJoinRequest;
import com.stephen.popcorn.model.request.TeamQuitRequest;
import com.stephen.popcorn.model.request.TeamUpdateRequest;
import com.stephen.popcorn.model.vo.TeamUserVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.service.UserTeamService;
import com.stephen.popcorn.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍
 *
 * @author: stephen qiu
 * @create: 2024-03-28 17:02
 **/
@RestController
@RequestMapping(value = "/team")
@Slf4j
public class TeamController {
	
	@Resource
	private UserService userService;
	
	@Resource
	private TeamService teamService;
	
	@Resource
	private UserTeamService userTeamService;
	
	/**
	 * 增
	 *
	 * @param teamAddRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
		if (teamAddRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		Team team = new Team();
		BeanUtils.copyProperties(teamAddRequest, team);
		long teamId = teamService.addTeam(team, loginUser);
		
		return ResultUtils.success(teamId);
	}
	
	/**
	 * 删
	 *
	 * @param deleteRequest
	 * @return
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
			
		}
		boolean result = teamService.removeById(deleteRequest.getId());
		if (!result) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入失败");
		}
		return ResultUtils.success(true);
		
	}
	
	/**
	 * 改
	 *
	 * @param teamUpdateRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
		if (teamUpdateRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
		if (!result) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");
		}
		
		return ResultUtils.success(true);
	}
	
	/**
	 * 查
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/get")
	public BaseResponse<Team> getTeamById(long id) {
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		Team team = teamService.getById(id);
		if (team == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR);
		}
		
		return ResultUtils.success(team);
	}
	
	/**
	 * 查询返回队伍信息列表
	 *
	 * @param teamQuery 请求参数封装类
	 * @return
	 */
	@GetMapping("/list")
	public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
		if (teamQuery == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean isAdmin = userService.isAdmin(request);
		// 1、查询队伍列表
		List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
		// 队伍的id列表
		final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
		// 2、判断当前用户是否已经加入了队伍
		QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
		try {
			User loginUser = userService.getLoginUser(request);
			userTeamQueryWrapper.eq("userId", loginUser.getId());
			userTeamQueryWrapper.in("teamId", teamIdList);
			List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
			Set<Long> hasJoinTeamIdList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
			teamList.forEach(team -> {
				boolean haoJoin = hasJoinTeamIdList.contains(team.getId());
				team.setHasJoin(haoJoin);
			});
			
		} catch (Exception e) {
		
		}
		// 3、关联查询队伍已经加入的用户的用户信息（人数）
		QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
		userTeamJoinQueryWrapper.in("teamId", teamIdList);
		List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
		// 队伍 id => 加入队伍的用户列表
		Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
		teamList.forEach(team -> {
			team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size());
		});
		return ResultUtils.success(teamList);
	}
	
	/**
	 * 分页查询返回队伍信息列表
	 *
	 * @param teamQuery 请求参数封装类
	 * @return
	 */
	@GetMapping("/list/page")
	// TODO 改造分页
	public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
		if (teamQuery == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = new Team();
		BeanUtils.copyProperties(team, teamQuery);
		Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		Page<Team> resultPage = teamService.page(page, queryWrapper);
		
		return ResultUtils.success(resultPage);
	}
	
	/**
	 * 用户加入队伍的接口
	 *
	 * @param teamJoinRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/join")
	public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
		if (teamJoinRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
		return ResultUtils.success(result);
		
	}
	
	/**
	 * 用户退出队伍的接口
	 *
	 * @param teamQuitRequest
	 * @param request
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("/quit")
	public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
		if (teamQuitRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
		return ResultUtils.success(result);
		
	}
	
	/**
	 * 删除队伍
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("/deleteById")
	public BaseResponse<Boolean> deleteTeamById(@RequestBody long id, HttpServletRequest request) {
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		boolean result = teamService.deleteTeam(id, loginUser);
		return ResultUtils.success(result);
		
	}
	
	/**
	 * 获取我加入的队伍
	 *
	 * @param teamQuery
	 * @param request
	 * @return
	 */
	@GetMapping("/list/my/join")
	public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
		if (teamQuery == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userId", loginUser.getId());
		List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
		// 取出不重复的队伍id
		// 把相同teamId的分到一组
		Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
		
		ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
		teamQuery.setIdList(idList);
		List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
		return ResultUtils.success(teamList);
	}
	
	/**
	 * 获取我创建的队伍
	 *
	 * @param teamQuery
	 * @param request
	 * @return
	 */
	@GetMapping("/list/my/create")
	public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
		if (teamQuery == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		teamQuery.setUserId(loginUser.getId());
		List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
		return ResultUtils.success(teamList);
	}
	
	
}