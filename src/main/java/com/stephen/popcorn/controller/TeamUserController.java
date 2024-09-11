package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constant.UserConstant;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.model.dto.team.TeamJoinRequest;
import com.stephen.popcorn.model.dto.team.TeamQuitRequest;
import com.stephen.popcorn.model.dto.teamUser.TeamUserAddRequest;
import com.stephen.popcorn.model.dto.teamUser.TeamUserQueryRequest;
import com.stephen.popcorn.model.entity.TeamUser;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TeamUserVO;
import com.stephen.popcorn.service.TeamUserService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 队伍-用户接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/teamUser")
@Slf4j
public class TeamUserController {
	
	@Resource
	private TeamUserService teamUserService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建队伍-用户
	 *
	 * @param teamUserAddRequest teamUserAddRequest
	 * @param request            request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addTeamUser(@RequestBody TeamUserAddRequest teamUserAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamUserAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		TeamUser teamUser = new TeamUser();
		BeanUtils.copyProperties(teamUserAddRequest, teamUser);
		// 数据校验
		teamUserService.validTeamUser(teamUser, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		teamUser.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = teamUserService.save(teamUser);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTeamUserId = teamUser.getId();
		return ResultUtils.success(newTeamUserId);
	}
	
	/**
	 * 删除队伍-用户(硬删除)
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteTeamUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		TeamUser oldTeamUser = teamUserService.getById(id);
		ThrowUtils.throwIf(oldTeamUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldTeamUser.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = teamUserService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取队伍-用户（封装类）
	 *
	 * @param id id
	 * @return BaseResponse<TeamUserVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<TeamUserVO> getTeamUserVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		TeamUser teamUser = teamUserService.getById(id);
		ThrowUtils.throwIf(teamUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(teamUserService.getTeamUserVO(teamUser, request));
	}
	
	/**
	 * 分页获取队伍-用户列表（仅管理员可用）
	 *
	 * @param teamUserQueryRequest teamUserQueryRequest
	 * @return BaseResponse<Page < TeamUser>>
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<TeamUser>> listTeamUserByPage(@RequestBody TeamUserQueryRequest teamUserQueryRequest) {
		long current = teamUserQueryRequest.getCurrent();
		long size = teamUserQueryRequest.getPageSize();
		// 查询数据库
		Page<TeamUser> teamUserPage = teamUserService.page(new Page<>(current, size),
				teamUserService.getQueryWrapper(teamUserQueryRequest));
		return ResultUtils.success(teamUserPage);
	}
	
	/**
	 * 分页获取队伍-用户列表（封装类）
	 *
	 * @param teamUserQueryRequest teamUserQueryRequest
	 * @param request              request
	 * @return BaseResponse<Page < TeamUserVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<TeamUserVO>> listTeamUserVOByPage(@RequestBody TeamUserQueryRequest teamUserQueryRequest,
	                                                           HttpServletRequest request) {
		long current = teamUserQueryRequest.getCurrent();
		long size = teamUserQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<TeamUser> teamUserPage = teamUserService.page(new Page<>(current, size),
				teamUserService.getQueryWrapper(teamUserQueryRequest));
		// 获取封装类
		return ResultUtils.success(teamUserService.getTeamUserVOPage(teamUserPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的队伍-用户列表
	 *
	 * @param teamUserQueryRequest teamUserQueryRequest
	 * @param request              request
	 * @return BaseResponse<Page < TeamUserVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<TeamUserVO>> listMyTeamUserVOByPage(@RequestBody TeamUserQueryRequest teamUserQueryRequest,
	                                                             HttpServletRequest request) {
		ThrowUtils.throwIf(teamUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		teamUserQueryRequest.setUserId(loginUser.getId());
		long current = teamUserQueryRequest.getCurrent();
		long size = teamUserQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<TeamUser> teamUserPage = teamUserService.page(new Page<>(current, size),
				teamUserService.getQueryWrapper(teamUserQueryRequest));
		// 获取封装类
		return ResultUtils.success(teamUserService.getTeamUserVOPage(teamUserPage, request));
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
		boolean result = teamUserService.joinTeam(teamJoinRequest, request);
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
	public BaseResponse<Boolean> joinTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamQuitRequest == null, ErrorCode.PARAMS_ERROR);
		boolean result = teamUserService.quitTeam(teamQuitRequest, request);
		return ResultUtils.success(result);
	}
}
