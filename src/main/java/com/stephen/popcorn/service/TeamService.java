package com.stephen.popcorn.service;

import com.stephen.popcorn.model.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.dto.team.TeamQueryRequest;
import com.stephen.popcorn.model.dto.team.TeamJoinRequest;
import com.stephen.popcorn.model.dto.team.TeamQuitRequest;
import com.stephen.popcorn.model.dto.team.TeamUpdateRequest;
import com.stephen.popcorn.model.vo.TeamUserVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author stephen qiu
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2024-03-28 14:15:35
 */
public interface TeamService extends IService<Team> {
	
	/**
	 * 添加队伍
	 *
	 * @param team
	 * @param loginUser
	 * @return
	 */
	long addTeam(Team team, User loginUser);
	
	
	/**
	 * 列表查询
	 *
	 * @param teamQueryRequest
	 * @param isAdmin
	 * @return
	 */
	List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);
	
	/**
	 * 更新队伍
	 *
	 * @param teamUpdateRequest
	 * @param loginUser
	 * @return
	 */
	boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
	
	
	/**
	 * 加入队伍
	 *
	 * @param teamJoinRequest
	 * @param loginUser
	 * @return
	 */
	boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
	
	/**
	 *
	 * @param teamQuitRequest
	 * @param loginUser
	 * @return
	 */
	boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
	
	
	
	/**
	 * 删除队伍
	 *
	 * @param id
	 * @param loginUser
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	boolean deleteTeam(long id, User loginUser);
}
