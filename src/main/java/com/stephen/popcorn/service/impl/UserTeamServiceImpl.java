package com.stephen.popcorn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.model.entity.UserTeam;
import com.stephen.popcorn.service.UserTeamService;
import com.stephen.popcorn.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
 * @author stephen qiu
 * @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
 * @createDate 2024-03-28 14:25:47
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
		implements UserTeamService {
	
}




