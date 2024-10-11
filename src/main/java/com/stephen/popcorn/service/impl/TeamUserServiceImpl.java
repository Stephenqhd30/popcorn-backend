package com.stephen.popcorn.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constants.CommonConstant;
import com.stephen.popcorn.mapper.TeamUserMapper;
import com.stephen.popcorn.model.dto.teamUser.TeamUserQueryRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.entity.TeamUser;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TeamUserVO;
import com.stephen.popcorn.model.vo.TeamVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.TeamUserService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.SqlUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 队伍-用户服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser> implements TeamUserService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private TeamService teamService;
	
	/**
	 * 校验数据
	 *
	 * @param teamUser
	 * @param add      对创建的数据进行校验
	 */
	@Override
	public void validTeamUser(TeamUser teamUser, boolean add) {
		ThrowUtils.throwIf(teamUser == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long userId = teamUser.getUserId();
		Long teamId = teamUser.getTeamId();
		
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (userId != null) {
			User user = userService.getById(userId);
			ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
		}
		if (teamId != null) {
			Team team = teamService.getById(teamId);
			ThrowUtils.throwIf(team == null, ErrorCode.PARAMS_ERROR, "队伍不存在");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param teamUserQueryRequest
	 * @return
	 */
	@Override
	public QueryWrapper<TeamUser> getQueryWrapper(TeamUserQueryRequest teamUserQueryRequest) {
		QueryWrapper<TeamUser> queryWrapper = new QueryWrapper<>();
		if (teamUserQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = teamUserQueryRequest.getId();
		Long userId = teamUserQueryRequest.getUserId();
		Long teamId = teamUserQueryRequest.getTeamId();
		String sortField = teamUserQueryRequest.getSortField();
		String sortOrder = teamUserQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 精确查询
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(teamId), "teamId", teamId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取队伍-用户封装
	 *
	 * @param teamUser
	 * @param request
	 * @return
	 */
	@Override
	public TeamUserVO getTeamUserVO(TeamUser teamUser, HttpServletRequest request) {
		// 对象转封装类
		TeamUserVO teamUserVO = TeamUserVO.objToVo(teamUser);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = teamUser.getUserId();
		Long teamId = teamUser.getTeamId();
		User user = null;
		Team team = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		if (teamId != null && teamId > 0) {
			team = teamService.getById(teamId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		TeamVO teamVO = teamService.getTeamVO(team, request);
		teamUserVO.setUserVO(userVO);
		teamUserVO.setTeamVO(teamVO);
		// endregion
		
		return teamUserVO;
	}
	
	/**
	 * 分页获取队伍-用户封装
	 *
	 * @param teamUserPage
	 * @param request
	 * @return
	 */
	@Override
	public Page<TeamUserVO> getTeamUserVOPage(Page<TeamUser> teamUserPage, HttpServletRequest request) {
		List<TeamUser> teamUserList = teamUserPage.getRecords();
		Page<TeamUserVO> teamUserVOPage = new Page<>(teamUserPage.getCurrent(), teamUserPage.getSize(), teamUserPage.getTotal());
		if (CollUtil.isEmpty(teamUserList)) {
			return teamUserVOPage;
		}
		// 对象列表 => 封装对象列表
		List<TeamUserVO> teamUserVOList = teamUserList.stream().map(teamUser -> {
			return TeamUserVO.objToVo(teamUser);
		}).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = teamUserList.stream().map(TeamUser::getUserId).collect(Collectors.toSet());
		Set<Long> teamIdSet = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		Map<Long, List<Team>> teamIdUserListMap = teamService.listByIds(teamIdSet).stream()
				.collect(Collectors.groupingBy(Team::getId));
		// 填充信息
		teamUserVOList.forEach(teamUserVO -> {
			Long userId = teamUserVO.getUserId();
			Long teamId = teamUserVO.getTeamId();
			User user = null;
			Team team = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			if (teamIdUserListMap.containsKey(teamId)) {
				team = teamIdUserListMap.get(teamId).get(0);
			}
			teamUserVO.setUserVO(userService.getUserVO(user, request));
			teamUserVO.setTeamVO(teamService.getTeamVO(team, request));
		});
		// endregion
		
		teamUserVOPage.setRecords(teamUserVOList);
		return teamUserVOPage;
	}
}