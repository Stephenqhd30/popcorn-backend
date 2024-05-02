package com.stephen.popcorn.service;

import com.stephen.popcorn.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.model.dto.TeamQuery;
import com.stephen.popcorn.model.request.TeamJoinRequest;
import com.stephen.popcorn.model.request.TeamQuitRequest;
import com.stephen.popcorn.model.request.TeamUpdateRequest;
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
	 * @param teamQuery
	 * @param isAdmin
	 * @return
	 */
	List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);
	
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
