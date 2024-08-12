package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.dto.teamUser.TeamUserQueryRequest;
import com.stephen.popcorn.model.entity.TeamUser;
import com.stephen.popcorn.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 队伍-用户服务
 *
 * @author stephen qiu
 */
public interface TeamUserService extends IService<TeamUser> {

    /**
     * 校验数据
     *
     * @param teamUser
     * @param add 对创建的数据进行校验
     */
    void validTeamUser(TeamUser teamUser, boolean add);

    /**
     * 获取查询条件
     *
     * @param teamUserQueryRequest
     * @return
     */
    QueryWrapper<TeamUser> getQueryWrapper(TeamUserQueryRequest teamUserQueryRequest);

    /**
     * 获取队伍-用户封装
     *
     * @param teamUser
     * @param request
     * @return
     */
    TeamUserVO getTeamUserVO(TeamUser teamUser, HttpServletRequest request);

    /**
     * 分页获取队伍-用户封装
     *
     * @param teamUserPage
     * @param request
     * @return
     */
    Page<TeamUserVO> getTeamUserVOPage(Page<TeamUser> teamUserPage, HttpServletRequest request);
}