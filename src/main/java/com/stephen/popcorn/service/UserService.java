package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.dto.user.UserMatchRequest;
import com.stephen.popcorn.model.dto.user.UserQueryRequest;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.LoginUserVO;
import com.stephen.popcorn.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 *
 * @author stephen qiu
 */
public interface UserService extends IService<User> {
	
	/**
	 * user
	 *
	 * @param user user
	 * @param add  是否添加
	 */
	void validUser(User user, boolean add);
	
	/**
	 * 用户注册
	 *
	 * @param userAccount   用户账户
	 * @param userPassword  用户密码
	 * @param checkPassword 校验密码
	 * @return 新用户 id
	 */
	long userRegister(String userAccount, String userPassword, String checkPassword);
	
	/**
	 * 用户登录
	 *
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param request
	 * @return 脱敏后的用户信息
	 */
	LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return User
	 */
	User getLoginUser(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param request
	 * @return
	 */
	boolean isAdmin(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param user
	 * @return
	 */
	boolean isAdmin(User user);
	
	/**
	 * 用户注销
	 *
	 * @param request
	 * @return
	 */
	boolean userLogout(HttpServletRequest request);
	
	/**
	 * 获取脱敏的已登录用户信息
	 *
	 * @return
	 */
	LoginUserVO getLoginUserVO(User user);
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	UserVO getUserVO(User user, HttpServletRequest request);
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param userList
	 * @param request
	 * @return
	 */
	List<UserVO> getUserVO(List<User> userList, HttpServletRequest request);
	
	/**
	 * 获取查询条件
	 *
	 * @param userQueryRequest
	 * @return
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
	
	/**
	 * 导入用户
	 *
	 * @param file file
	 * @return {@link Map}<{@link String}, {@link Object}>
	 */
	Map<String, Object> importUsers(MultipartFile file);
	
	/**
	 * 获取匹配的用户
	 *
	 * @param userMatchRequest userMatchRequest
	 * @param request          request
	 * @return {@link List<UserVO>}
	 */
	List<UserVO> cosMatchUsers(UserMatchRequest userMatchRequest, HttpServletRequest request);
}
