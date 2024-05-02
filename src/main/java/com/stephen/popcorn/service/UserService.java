package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author stephen qiu
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-12-02 10:50:44
 */
public interface UserService extends IService<User> {
	
	/**
	 * 用户注册
	 *
	 * @param userAccount   用户账户
	 * @param userPassword  用户密码
	 * @param checkPassword 校验密码
	 * @param studentNumber 学号
	 * @return 新用户 id
	 */
	long userRegister(String userAccount, String userPassword, String checkPassword, String studentNumber);
	
	/**
	 * 用户登录
	 *
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param request      http请求头
	 * @return 返回脱敏之后的用户信息
	 */
	User userLogin(String userAccount, String userPassword, HttpServletRequest request);
	
	/**
	 * 用户脱敏
	 *
	 * @param originUser 原始的用户信息
	 * @return 返回之后的脱敏用户信息
	 */
	User getSafetyUser(User originUser);
	
	
	/**
	 * 用户注销
	 *
	 * @param request http请求头
	 */
	int userLogout(HttpServletRequest request);
	
	
	/**
	 * 封装类 判断是不是管理员
	 *
	 * @param request http请求头
	 * @return 返回是不是管理员
	 */
	boolean isAdmin(HttpServletRequest request);
	
	/**
	 * 封装类 判断是不是管理员(根据当前登录用户判断)
	 *
	 * @param loginUser 当前的登录用户
	 * @return 是否是管理员
	 */
	boolean isAdmin(User loginUser);
	
	/**
	 * 根据标签搜索用户
	 *
	 * @param tagNameList 需要查询的标签列表
	 * @return 查询符合要求的查询结果
	 */
	List<User> searchUserByTags(List<String> tagNameList);
	
	
	int updateUser(User user, User loginUser);
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request http请求头
	 * @return 返回当前登录的用户信息
	 */
	User getLoginUser(HttpServletRequest request);
	
	
	/**
	 * 匹配用户
	 * @param num
	 * @param loginUser
	 * @return
	 */
	List<User> matchUsers(long num, User loginUser);
}
