package com.stephen.popcorn.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constant.CommonConstant;
import com.stephen.popcorn.model.enums.TeamStatusEnum;
import com.stephen.popcorn.utils.ThrowUtils;
import com.stephen.popcorn.mapper.TeamMapper;
import com.stephen.popcorn.model.dto.team.TeamQueryRequest;
import com.stephen.popcorn.model.entity.Team;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TeamVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
	
	/**
	 * 校验数据
	 *
	 * @param team
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
			ThrowUtils.throwIf(teamName.length() > 20, ErrorCode.PARAMS_ERROR, "队伍名称过长");
		}
		if (StringUtils.isNotBlank(teamProfile)) {
			ThrowUtils.throwIf(teamProfile.length() > 50, ErrorCode.PARAMS_ERROR, "队伍简介过长");
		}
		if (Objects.equals(status, TeamStatusEnum.SECURITY.getValue())) {
			ThrowUtils.throwIf(teamPassword == null, ErrorCode.PARAMS_ERROR, "密码的长度需要为6位");
			if (StringUtils.isNotBlank(teamPassword)) {
				ThrowUtils.throwIf(teamPassword.length() != 6, ErrorCode.PARAMS_ERROR, "密码的长度需要为6位");
			}
		}
		
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param teamQueryRequest
	 * @return
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
	 * @param team
	 * @param request
	 * @return
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
		UserVO userVO = userService.getUserVO(user, request);
		teamVO.setUserVO(userVO);
		return teamVO;
	}
	
	/**
	 * 分页获取队伍封装
	 *
	 * @param teamPage
	 * @param request
	 * @return
	 */
	@Override
	public Page<TeamVO> getTeamVOPage(Page<Team> teamPage, HttpServletRequest request) {
		List<Team> teamList = teamPage.getRecords();
		Page<TeamVO> teamVOPage = new Page<>(teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal());
		if (CollUtil.isEmpty(teamList)) {
			return teamVOPage;
		}
		// 对象列表 => 封装对象列表
		List<TeamVO> teamVOList = teamList.stream().map(TeamVO::objToVo).collect(Collectors.toList());
		
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
	
}