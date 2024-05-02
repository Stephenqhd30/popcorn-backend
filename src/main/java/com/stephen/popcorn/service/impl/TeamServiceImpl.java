package com.stephen.popcorn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.mapper.TeamMapper;
import com.stephen.popcorn.model.domain.Team;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.model.domain.UserTeam;
import com.stephen.popcorn.model.dto.TeamQuery;
import com.stephen.popcorn.model.enums.TeamStatusEnum;
import com.stephen.popcorn.model.request.TeamJoinRequest;
import com.stephen.popcorn.model.request.TeamQuitRequest;
import com.stephen.popcorn.model.request.TeamUpdateRequest;
import com.stephen.popcorn.model.vo.TeamUserVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.TeamService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author stephen qiu
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2024-03-28 14:15:35
 */

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
		implements TeamService {
	@Resource
	private UserService userService;
	
	
	@Resource
	private UserTeamService userTeamService;
	
	@Resource
	private RedissonClient redissonClient;
	
	
	/**
	 * 添加队伍
	 *
	 * @param team
	 * @param loginUser
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)  // 开始事务
	public long addTeam(Team team, User loginUser) {
		// 1. 求个球参数是否为空？
		if (team == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 2. 是否登录，未登录不允许创建
		if (loginUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN);
		}
		// 3. 校验信息
		final long userId = loginUser.getId();
		// 1. 队伍人数 > 1 且 <= 20
		int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
		if (maxNum < 1 || maxNum > 20) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
		}
		// 2. 队伍标题 <= 20
		String name = team.getName();
		if (StringUtils.isBlank(name) || name.length() > 20) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
		}
		// 3. 描述 <= 512
		String description = team.getDescription();
		if (StringUtils.isNotBlank(description) && description.length() > 512) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
			
		}
		// 4. status 是否公开 ( int ) 不传默认就是 0（公开）
		int status = Optional.ofNullable(team.getStatus()).orElse(0);
		TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
		if (statusEnum == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
		}
		// 5. 如果 status 是加密的状态，一定要有密码， 且密码 <= 32
		String password = team.getPassword();
		if (statusEnum.equals(TeamStatusEnum.SECRET)) {
			if (StringUtils.isBlank(password) || password.length() > 32) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
			}
		}
		// 6. 超时时间 > 当前时间
		Date expireTime = team.getExpireTime();
		if (new Date().after(expireTime)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
		}
		// 7. 校验用户最多创建 5 个队伍
		// TODO 这里有Bug 如果用户连续点击100下 可能会出现创建了100个队伍
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userId", userId);
		long hasTeamNum = this.count(queryWrapper);
		if (hasTeamNum >= 5) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
		}
		// 8. 插入用户信息到队伍表
		team.setId(null);
		team.setUserId(userId);
		boolean result = this.save(team);
		
		// 获取团队ID
		Long teamId = team.getId();
		if (!result || teamId == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
		}
		// 9. 插入用户 => 队伍  关系到关系表
		UserTeam userTeam = new UserTeam();
		userTeam.setUserId(userId);
		userTeam.setTeamId(teamId);
		userTeam.setJoinTime(new Date());
		
		result = userTeamService.save(userTeam);
		if (!result) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
		}
		return teamId;
	}
	
	/**
	 * 查询队伍
	 *
	 * @param teamQuery
	 * @param isAdmin
	 * @return
	 */
	@Override
	public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
		QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
		// 组合查询条件
		if (teamQuery != null) {
			
			// 根据状态查询
			Integer status = teamQuery.getStatus();
			TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
			if (statusEnum == null) {
				statusEnum = TeamStatusEnum.PUBLIC;
			}
			if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
				throw new BusinessException(ErrorCode.NO_AUTH);
			}
			
			queryWrapper.eq("status", statusEnum.getValue());
			Long id = teamQuery.getId();
			if (id != null && id > 0) {
				queryWrapper.eq("id", id);
			}
			List<Long> idList = teamQuery.getIdList();
			if (CollectionUtils.isNotEmpty(idList)) {
				queryWrapper.in("id", idList);
			}
			String searchText = teamQuery.getSearchText();
			if (StringUtils.isNotBlank(searchText)) {
				queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
			}
			String name = teamQuery.getName();
			if (StringUtils.isNotBlank(name)) {
				queryWrapper.like("name", name);
			}
			String description = teamQuery.getDescription();
			if (StringUtils.isNotBlank(description)) {
				queryWrapper.like("description", description);
			}
			Integer maxNum = teamQuery.getMaxNum();
			// 查询最大人数相等的
			if (maxNum != null && maxNum > 0) {
				queryWrapper.eq("maxNum", maxNum);
			}
			Long userId = teamQuery.getUserId();
			// 根据创建人来查询
			if (userId != null && userId > 0) {
				queryWrapper.eq("userId", userId);
			}
			
			
			
		}
		// 不展示已过期的用户信息
		// expireTime is not null or expire > now()
		queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
		List<Team> teamList = this.list(queryWrapper);
		if (CollectionUtils.isEmpty(teamList)) {
			return new ArrayList<>();
		}
		List<TeamUserVO> teamUserVOList = new ArrayList<>();
		// 关联查询创建人的用户信息
		for (Team team : teamList) {
			Long userId = team.getUserId();
			if (userId == null) {
			} else {
				User user = userService.getById(userId);
				TeamUserVO teamUserVO = new TeamUserVO();
				BeanUtils.copyProperties(team, teamUserVO);
				// 脱敏信息
				if (user != null) {
					UserVO userVO = new UserVO();
					BeanUtils.copyProperties(user, userVO);
					teamUserVO.setCreateUser(userVO);
				}
				teamUserVOList.add(teamUserVO);
			}
		}
		return teamUserVOList;
	}
	
	/**
	 * 更新队伍
	 *
	 * @param teamUpdateRequest
	 * @param loginUser
	 * @return
	 */
	@Override
	public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
		if (teamUpdateRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Long id = teamUpdateRequest.getId();
		Team oldTeam = getTeamById(id);
		// 只有管理员或者队伍的创建者才有权限更改密码
		if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
			throw new BusinessException(ErrorCode.NO_AUTH);
		}
		
		TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
		if (statusEnum.equals(TeamStatusEnum.SECRET)) {
			if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "请设置密码");
			}
		}
		Team updateTeam = new Team();
		BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
		return this.updateById(updateTeam);
	}
	
	/**
	 * 加入队伍
	 *
	 * @param teamJoinRequest
	 * @param loginUser
	 * @return
	 */
	@Override
	public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
		if (teamJoinRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		Long teamId = teamJoinRequest.getTeamId();
		Team team = getTeamById(teamId);
		Date expireTime = team.getExpireTime();
		if (expireTime != null && expireTime.before(new Date())) {
			throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
		}
		Integer status = team.getStatus();
		TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
		if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
			throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有的队伍");
		}
		String password = teamJoinRequest.getPassword();
		if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
			if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
				throw new BusinessException(ErrorCode.NULL_ERROR, "密码错误");
			}
		}
		
		
		// 需要查询数据库的关系
		// 该用户及加入队伍的数量
		long userId = loginUser.getId();
		// 分布式锁
		RLock lock = redissonClient.getLock("yupao:join_team");
		try {
			while (true) {
				// 只有一个线程能获取到锁
				if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
					QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
					userTeamQueryWrapper.eq("userId", userId);
					long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
					if (hasJoinNum > 5) {
						throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入五个队伍");
					}
					userTeamQueryWrapper = new QueryWrapper<>();
					userTeamQueryWrapper.eq("userId", userId);
					userTeamQueryWrapper.eq("teamId", teamId);
					long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
					if (hasUserJoinTeam > 0) {
						throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入队伍");
					}
					
					// 不能重复加入
					// 已加入队伍的人数
					long teamHasJoinNum = countTeamUserByTeamId(teamId);
					if (teamHasJoinNum >= team.getMaxNum()) {
						throw new BusinessException(ErrorCode.NULL_ERROR, "队伍以满");
					}
					// 修改队伍信息
					UserTeam userTeam = new UserTeam();
					userTeam.setUserId(userId);
					userTeam.setTeamId(teamId);
					userTeam.setJoinTime(new Date());
					
					return userTeamService.save(userTeam);
					
				}
			}
		} catch (InterruptedException e) {
			log.error("doCacheRecommendUser error", e);
			return false;
		} finally {
			// 只能释放当前线程创建的锁
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
	
	
	/**
	 * 退出队伍
	 *
	 * @param teamQuitRequest
	 * @param loginUser
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
		if (teamQuitRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		Long teamId = teamQuitRequest.getTeamId();
		Team team = getTeamById(teamId);
		long userId = loginUser.getId();
		UserTeam queryUserTeam = new UserTeam();
		queryUserTeam.setTeamId(teamId);
		queryUserTeam.setUserId(userId);
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
		long count = userTeamService.count(queryWrapper);
		if (count == 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
		}
		long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
		// 队伍只剩下一个人的时候解散
		if (teamHasJoinNum == 1) {
			// 删除队伍和加入队伍的关系
			return this.deleteTeam(teamId, loginUser);
		} else {
			// 是否为队长
			if (team.getUserId() == userId) {
				// 队伍还有其他人，将队长转移给其他人(最早加入队伍的人)
				// 1.获取队伍所有的加入用户和用户加入的时间
				QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
				userTeamQueryWrapper.eq("teamId", teamId);
				userTeamQueryWrapper.last("order by id asc limit 2");
				List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
				if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
					throw new BusinessException(ErrorCode.SYSTEM_ERROR);
				}
				UserTeam nexUserTeam = userTeamList.get(1);
				Long nextTeamLeaderId = nexUserTeam.getUserId();
				// 更新当前队伍的队长
				Team updateTeam = new Team();
				updateTeam.setId(teamId);
				updateTeam.setUserId(nextTeamLeaderId);
				boolean result = this.updateById(updateTeam);
				if (!result) {
					throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
				}
			}
		}
		// 移除当前用户和队伍的关系
		return this.removeById(teamId);
		
	}
	
	
	/**
	 * 删除队伍
	 *
	 * @param id
	 * @param loginUser
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteTeam(long id, User loginUser) {
		// 1. 检验请求参数
		if (id <= 0 || loginUser == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 2. 检验队伍是否存在
		Team team = this.getTeamById(id);
		Long teamId = team.getId();
		// 3. 检验你是不是队伍的队长
		if (team.getUserId() != loginUser.getId()) {
			throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
		}
		// 4. 移除所有加入队伍的关联关系
		QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
		userTeamQueryWrapper.eq("teamId", teamId);
		userTeamService.remove(userTeamQueryWrapper);
		// 5. 删除队伍
		boolean result = this.removeById(teamId);
		if (!result) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "移除队伍关联信息失败");
		}
		return this.removeById(teamId);
		
	}
	
	/**
	 * 获取当前队伍加入的人数
	 *
	 * @param teamId
	 * @return
	 */
	private long countTeamUserByTeamId(long teamId) {
		QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
		userTeamQueryWrapper.eq("teamId", teamId);
		return userTeamService.count(userTeamQueryWrapper);
	}
	
	/**
	 * 获取队伍信息(id)
	 *
	 * @param teamId
	 * @return
	 */
	private Team getTeamById(Long teamId) {
		if (teamId == null || teamId <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = this.getById(teamId);
		if (team == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
		}
		return team;
	}
	
}