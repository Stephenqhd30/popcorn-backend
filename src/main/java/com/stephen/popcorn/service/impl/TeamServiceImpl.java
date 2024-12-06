package com.stephen.popcorn.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.common.ThrowUtils;
import com.stephen.popcorn.constants.CommonConstant;
import com.stephen.popcorn.mapper.TeamMapper;
import com.stephen.popcorn.model.dto.team.TeamJoinRequest;
import com.stephen.popcorn.model.dto.team.TeamQueryRequest;
import com.stephen.popcorn.model.dto.team.TeamQuitRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.entity.TeamUser;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.enums.TeamStatusEnum;
import com.stephen.popcorn.model.vo.TeamVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.TeamUserService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 队伍服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private @Lazy TeamUserService teamUserService;
	
	
	/**
	 * 校验数据
	 *
	 * @param team team
	 * @param add  对创建的数据进行校验
	 */
	@Override
	public void validTeam(Team team, boolean add) {
		ThrowUtils.throwIf(team == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String teamName = team.getTeamName();
		String teamProfile = team.getTeamProfile();
		String teamPassword = team.getTeamPassword();
		Integer status = team.getStatus();
		Integer maxLength = team.getMaxLength();
		Date expireTime = team.getExpireTime();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(teamName), ErrorCode.PARAMS_ERROR, "队伍名称不能为空");
			ThrowUtils.throwIf(TeamStatusEnum.getEnumByValue(status) == null, ErrorCode.PARAMS_ERROR, "队伍状态异常");
			ThrowUtils.throwIf(maxLength > 10, ErrorCode.PARAMS_ERROR, "队伍最多加入10人");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(teamName)) {
			ThrowUtils.throwIf(teamName.length() > 10, ErrorCode.PARAMS_ERROR, "队伍名称过长");
		}
		if (StringUtils.isNotBlank(teamProfile)) {
			ThrowUtils.throwIf(teamProfile.length() > 30, ErrorCode.PARAMS_ERROR, "队伍简介过长");
		}
		if (Objects.equals(status, TeamStatusEnum.SECURITY.getValue())) {
			ThrowUtils.throwIf(teamPassword == null, ErrorCode.PARAMS_ERROR, "密码不能为空");
			if (StringUtils.isNotBlank(teamPassword)) {
				ThrowUtils.throwIf(teamPassword.length() != 6, ErrorCode.PARAMS_ERROR, "密码的长度需要为6位");
			}
		}
		if (team.getExpireTime() != null) {
			ThrowUtils.throwIf(expireTime.after(new Date()), ErrorCode.OPERATION_ERROR, "队伍已过期");
		}
		
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param teamQueryRequest teamQueryRequest
	 * @return {@link QueryWrapper<Team>}
	 */
	@Override
	public QueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest) {
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		if (teamQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = teamQueryRequest.getId();
		String teamName = teamQueryRequest.getTeamName();
		String teamProfile = teamQueryRequest.getTeamProfile();
		Long userId = teamQueryRequest.getUserId();
		Integer status = teamQueryRequest.getStatus();
		String searchText = teamQueryRequest.getSearchText();
		String sortField = teamQueryRequest.getSortField();
		String sortOrder = teamQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("teamName", searchText).or().like("content", searchText));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(teamName), "teamName", teamName);
		queryWrapper.like(StringUtils.isNotBlank(teamProfile), "teamProfile", teamProfile);
		
		// 精确查询
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取队伍封装
	 *
	 * @param team    队伍信息
	 * @param request 请求
	 * @return {@link TeamVO}
	 */
	@Override
	public TeamVO getTeamVO(Team team, HttpServletRequest request) {
		// 对象转封装类
		TeamVO teamVO = TeamVO.objToVo(team);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = team.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		// 2. 获取当前队伍已经加入的人数
		LambdaQueryWrapper<TeamUser> queryWrapper = Wrappers.lambdaQuery(TeamUser.class).eq(TeamUser::getTeamId, team.getId());
		int count = (int) teamUserService.count(queryWrapper);
		
		UserVO userVO = userService.getUserVO(user, request);
		teamVO.setUserVO(userVO);
		teamVO.setHasJoinNum(count);
		return teamVO;
	}
	
	/**
	 * 分页获取队伍封装
	 *
	 * @param teamPage 队伍分页信息
	 * @param request  request
	 * @return {@link Page<TeamVO>}
	 */
	@Override
	public Page<TeamVO> getTeamVOPage(Page<Team> teamPage, HttpServletRequest request) {
		List<Team> teamList = teamPage.getRecords();
		Page<TeamVO> teamVOPage = new Page<>(teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal());
		if (CollUtil.isEmpty(teamList)) {
			return teamVOPage;
		}
		// 对象列表 => 封装对象列表
		List<TeamVO> teamVOList = teamList.stream().map(team -> getTeamVO(team, request)).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = teamList.stream().map(Team::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		
		// 填充信息
		teamVOList.forEach(teamVO -> {
			Long userId = teamVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			teamVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		
		teamVOPage.setRecords(teamVOList);
		return teamVOPage;
	}
	
	/**
	 * 加入队伍
	 *
	 * @param teamJoinRequest teamJoinRequest
	 * @param request         request
	 * @return {@link boolean}
	 */
	@Override
	public boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamJoinRequest == null, ErrorCode.PARAMS_ERROR);
		// 取出加入队伍中的信息
		Long teamId = teamJoinRequest.getTeamId();
		String joinTeamPassword = teamJoinRequest.getTeamPassword();
		// 获取当前队伍信息
		ThrowUtils.throwIf(teamId == null || teamId <= 0, ErrorCode.PARAMS_ERROR);
		Team team = this.getById(teamId);
		ThrowUtils.throwIf(team == null, ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
		// 校验队伍信息
		this.validTeam(team, false);
		String teamPassword = team.getTeamPassword();
		// 对数据进行校验
		if (TeamStatusEnum.SECURITY.equals(TeamStatusEnum.getEnumByValue(team.getStatus()))) {
			ThrowUtils.throwIf(StringUtils.isBlank(joinTeamPassword) || !joinTeamPassword.equals(teamPassword), ErrorCode.PARAMS_ERROR, "密码错误");
		}
		
		// 需要查询数据库的关系
		// 该用户及加入队伍的数量
		User loginUser = userService.getLoginUser(request);
		Long userId = loginUser.getId();
		LambdaQueryWrapper<TeamUser> teamUserQueryWrapper = Wrappers.lambdaQuery(TeamUser.class)
				.eq(TeamUser::getUserId, userId);
		// 不能重复加入
		long hasJoinNum = teamUserService.count(teamUserQueryWrapper);
		ThrowUtils.throwIf(hasJoinNum > 5, ErrorCode.PARAMS_ERROR, "最多创建和加入五个队伍");
		teamUserQueryWrapper.eq(TeamUser::getTeamId, teamId);
		// 不能重复加入
		long hasUserJoinTeam = teamUserService.count(teamUserQueryWrapper);
		ThrowUtils.throwIf(hasUserJoinTeam > 0, ErrorCode.PARAMS_ERROR, "用户已加入队伍");
		// 已加入队伍的人数
		QueryWrapper<TeamUser> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("teamId", teamId);
		long teamHasJoinNum = teamUserService.count(teamUserQueryWrapper);
		ThrowUtils.throwIf(teamHasJoinNum >= team.getMaxLength(), ErrorCode.OPERATION_ERROR, "队伍以满");
		// 修改队伍信息
		TeamUser teamUser = new TeamUser();
		teamUser.setUserId(userId);
		teamUser.setTeamId(teamId);
		return teamUserService.save(teamUser);
	}
	
	/**
	 * 退出队伍
	 *
	 * @param teamQuitRequest teamQuitRequest
	 * @param request         request
	 * @return boolean
	 */
	@Override
	public boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(teamQuitRequest == null, ErrorCode.PARAMS_ERROR);
		
		Long teamId = teamQuitRequest.getTeamId();
		Team team = this.getById(teamId);
		ThrowUtils.throwIf(team == null, ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
		
		User loginUser = userService.getLoginUser(request);
		Long userId = loginUser.getId();
		
		// 获取当前用户是否已经加入队伍
		long hasJoinNum = teamUserService.count(new QueryWrapper<TeamUser>()
				.eq("teamId", teamId)
				.eq("userId", userId));
		ThrowUtils.throwIf(hasJoinNum == 0, ErrorCode.OPERATION_ERROR, "未加入队伍");
		
		// 获取当前队伍人数
		long teamHasJoinNum = teamUserService.count(new QueryWrapper<TeamUser>()
				.eq("teamId", teamId));
		
		if (teamHasJoinNum == 1) {
			// 队伍只剩一个人，解散队伍
			return teamUserService.remove(new QueryWrapper<TeamUser>().eq("teamId", teamId));
		} else {
			// 处理队长转移
			if (Objects.equals(team.getUserId(), userId)) {
				List<TeamUser> userTeamList = teamUserService.list(new QueryWrapper<TeamUser>()
						.eq("teamId", teamId)
						.orderByAsc("id").last("limit 2"));
				
				ThrowUtils.throwIf(userTeamList.size() <= 1, ErrorCode.SYSTEM_ERROR, "没有足够的队员进行队长转移");
				Long nextTeamLeaderId = userTeamList.get(1).getUserId();
				Team newTeam = new Team();
				newTeam.setId(teamId);
				newTeam.setUserId(nextTeamLeaderId);
				boolean result = this.updateById(newTeam);
				ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
			}
		}
		
		// 移除当前用户和队伍的关系
		return teamUserService.remove(new QueryWrapper<TeamUser>()
				.eq("teamId", teamId)
				.eq("userId", userId));
	}
}