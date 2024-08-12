package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.model.enums.TeamStatusEnum;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.constant.UserConstant;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.utils.ThrowUtils;
import com.stephen.popcorn.model.dto.team.TeamAddRequest;
import com.stephen.popcorn.model.dto.team.TeamEditRequest;
import com.stephen.popcorn.model.dto.team.TeamQueryRequest;
import com.stephen.popcorn.model.dto.team.TeamUpdateRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TeamVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Wrapper;

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
	
	// region 增删改查
	
	/**
	 * 创建队伍
	 *
	 * @param teamAddRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/add")
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
		// 写入数据库
		boolean result = teamService.save(team);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTeamId = team.getId();
		return ResultUtils.success(newTeamId);
	}
	
	/**
	 * 删除队伍
	 *
	 * @param deleteRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Team oldTeam = teamService.getById(id);
		ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldTeam.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = teamService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新队伍（仅管理员可用）
	 *
	 * @param teamUpdateRequest
	 * @return
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
	 * @param id
	 * @return
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
	 * @param teamQueryRequest
	 * @return
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
	 * @param teamQueryRequest
	 * @param request
	 * @return
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
	 * @param teamQueryRequest
	 * @param request
	 * @return
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
	 * @param teamEditRequest
	 * @param request
	 * @return
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
}
