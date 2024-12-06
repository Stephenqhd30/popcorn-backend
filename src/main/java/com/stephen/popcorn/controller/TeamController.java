package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.common.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constants.UserConstant;
import com.stephen.popcorn.common.exception.BusinessException;
import com.stephen.popcorn.model.dto.team.*;
import com.stephen.popcorn.model.dto.teamUser.TeamUserQueryRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.entity.TeamUser;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TeamVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.TeamUserService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 队伍接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
	
	@Resource
	private TeamService teamService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private TeamUserService teamUserService;
	
	// region 增删改查
	
	/**
	 * 创建队伍
	 *
	 * @param teamAddRequest teamAddRequest
	 * @param request        request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Team team = new Team();
		BeanUtils.copyProperties(teamAddRequest, team);
		// 数据校验
		teamService.validTeam(team, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		team.setUserId(loginUser.getId());
		TeamUser teamUser = new TeamUser();
		teamUser.setUserId(loginUser.getId());
		teamUser.setCaptainId(loginUser.getId());
		// 写入数据库
		boolean result = teamService.save(team);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTeamId = team.getId();
		// 将队伍id返回
		teamUser.setTeamId(newTeamId);
		// 写入 队伍-用户 数据库
		boolean teamUserSave = teamUserService.save(teamUser);
		ThrowUtils.throwIf(!teamUserSave, ErrorCode.OPERATION_ERROR);
		// 返回成功之后的队伍id
		return ResultUtils.success(newTeamId);
	}
	
	/**
	 * 删除队伍
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		TeamUserQueryRequest teamUserQueryRequest = new TeamUserQueryRequest();
		teamUserQueryRequest.setUserId(user.getId());
		teamUserQueryRequest.setTeamId(id);
		// 判断是否存在
		Team oldTeam = teamService.getById(id);
		QueryWrapper<TeamUser> queryWrapper = teamUserService.getQueryWrapper(teamUserQueryRequest);
		ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldTeam.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = teamService.removeById(id);
		boolean remove = teamUserService.remove(queryWrapper);
		ThrowUtils.throwIf(!result || !remove, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新队伍（仅管理员可用）
	 *
	 * @param teamUpdateRequest teamUpdateRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest) {
		if (teamUpdateRequest == null || teamUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Team team = new Team();
		BeanUtils.copyProperties(teamUpdateRequest, team);
		// 数据校验
		teamService.validTeam(team, false);
		// 判断是否存在
		long id = teamUpdateRequest.getId();
		Team oldTeam = teamService.getById(id);
		ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = teamService.updateById(team);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取队伍（封装类）
	 *
	 * @param id id
	 * @return BaseResponse<TeamVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<TeamVO> getTeamVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Team team = teamService.getById(id);
		ThrowUtils.throwIf(team == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(teamService.getTeamVO(team, request));
	}
	
	/**
	 * 分页获取队伍列表（仅管理员可用）
	 *
	 * @param teamQueryRequest teamQueryRequest
	 * @return BaseResponse<Page < Team>>
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<Team>> listTeamByPage(@RequestBody TeamQueryRequest teamQueryRequest) {
		long current = teamQueryRequest.getCurrent();
		long size = teamQueryRequest.getPageSize();
		// 查询数据库
		Page<Team> teamPage = teamService.page(new Page<>(current, size),
				teamService.getQueryWrapper(teamQueryRequest));
		return ResultUtils.success(teamPage);
	}
	
	/**
	 * 分页获取队伍列表（封装类）
	 *
	 * @param teamQueryRequest teamQueryRequest
	 * @param request          request
	 * @return BaseResponse<Page < TeamVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<TeamVO>> listTeamVOByPage(@RequestBody TeamQueryRequest teamQueryRequest,
	                                                   HttpServletRequest request) {
		long current = teamQueryRequest.getCurrent();
		long size = teamQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Team> teamPage = teamService.page(new Page<>(current, size),
				teamService.getQueryWrapper(teamQueryRequest));
		// 获取封装类
		return ResultUtils.success(teamService.getTeamVOPage(teamPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的队伍列表
	 *
	 * @param teamQueryRequest teamQueryRequest
	 * @param request          request
	 * @return BaseResponse<Page < TeamVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<TeamVO>> listMyTeamVOByPage(@RequestBody TeamQueryRequest teamQueryRequest,
	                                                     HttpServletRequest request) {
		ThrowUtils.throwIf(teamQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		teamQueryRequest.setUserId(loginUser.getId());
		long current = teamQueryRequest.getCurrent();
		long size = teamQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Team> teamPage = teamService.page(new Page<>(current, size),
				teamService.getQueryWrapper(teamQueryRequest));
		// 获取封装类
		return ResultUtils.success(teamService.getTeamVOPage(teamPage, request));
	}
	
	/**
	 * 编辑队伍（给用户使用）
	 *
	 * @param teamEditRequest teamEditRequest
	 * @param request         request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editTeam(@RequestBody TeamEditRequest teamEditRequest, HttpServletRequest request) {
		if (teamEditRequest == null || teamEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Team team = new Team();
		BeanUtils.copyProperties(teamEditRequest, team);
		// 数据校验
		teamService.validTeam(team, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = teamEditRequest.getId();
		Team oldTeam = teamService.getById(id);
		ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = teamService.updateById(team);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
	
	/**
	 * 加入队伍
	 *
	 * @param teamJoinRequest teamJoinRequest
	 * @param request         request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/join")
	public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamJoinRequest == null, ErrorCode.PARAMS_ERROR);
		boolean result = teamService.joinTeam(teamJoinRequest, request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 退出队伍
	 *
	 * @param teamQuitRequest teamQuitRequest
	 * @param request         request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/quit")
	public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamQuitRequest == null, ErrorCode.PARAMS_ERROR);
		boolean result = teamService.quitTeam(teamQuitRequest, request);
		return ResultUtils.success(result);
	}
	
}
