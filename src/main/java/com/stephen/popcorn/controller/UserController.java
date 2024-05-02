package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.model.request.UserLoginRequest;
import com.stephen.popcorn.model.request.UserRegisterRequest;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.stephen.popcorn.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户
 *
 * @author: stephen qiu
 * @create: 2023-12-02 19:51
 **/
@RestController
@CrossOrigin(origins = {"http://localhost:5173/"})
@Slf4j
@RequestMapping("/user")
public class UserController {
	
	@Resource
	private UserService userService;
	
	@Resource
	private RedisTemplate redisTemplate;
	
	/**
	 * 用户注册
	 * RequestBody SpringMVC匹配JSON字符串
	 *
	 * @param userRegisterRequest 自己封装的处理前端JSON的请求对象
	 * @return
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		if (userRegisterRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		String studentNumber = userRegisterRequest.getStudentNumber();
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentNumber)) {
			return null;
		}
		long result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
		
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户登录
	 *
	 * @param userLoginRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/login")
	public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			return ResultUtils.error(ErrorCode.PARAMS_ERROR);
		}
		
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			return ResultUtils.error(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.userLogin(userAccount, userPassword, request);
		
		return ResultUtils.success(user);
	}
	
	/**
	 * 用户注销
	 *
	 * @param userLoginRequest 用户登录的请求体
	 * @param request
	 * @return
	 */
	@PostMapping("/logout")
	public BaseResponse<Integer> userLogout(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		int result = userService.userLogout(request);
		return ResultUtils.success(result);
	}
	
	
	/**
	 * 获取当前用户
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/current")
	public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
		// 得到用户的登录态
		User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN);
		}
		long userId = currentUser.getId();
		// TODO 后续需要修改
		User user = userService.getById(userId);
		User safetyUser = userService.getSafetyUser(user);
		return ResultUtils.success(safetyUser);
	}
	
	/**
	 * 查询用户名
	 *
	 * @param username 用户名
	 * @return user对象
	 */
	@GetMapping("/search")
	public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
		if (!userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
		}
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		
		if (StringUtils.isNotBlank(username)) {
			queryWrapper.like("username", username);
		}
		// 设置脱敏 返回给前端的时候不带上密码
		List<User> userList = userService.list(queryWrapper);
		List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
		return ResultUtils.success(list);
	}
	
	/**
	 * 推荐页（数据量大 使用分页的功能）
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/recommend")
	public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
		User loginUser = userService.getLoginUser(request);
		// 先判断缓存中有没有，有的话直接从缓存中取
		String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
		ValueOperations valueOperations = redisTemplate.opsForValue();
		Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
		if (userPage != null) {
			return ResultUtils.success(userPage);
		}
		// 构造分页查询条件
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
		// 检查页号和每页大小的有效性
		if (pageSize <= 0 || pageNum <= 0) {
			// 处理无效参数的情况，比如抛出异常或返回错误信息
			return ResultUtils.error(ErrorCode.PARAMS_ERROR);
		}
		// 对查询结果进行脱敏处理 返回给前端的时候不带上密码
		userPage.getRecords().forEach(user -> userService.getSafetyUser(user));
		
		// 写缓存
		try {
			valueOperations.set(redisKey, userPage, 12, TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("redis set key error");
		}
		
		return ResultUtils.success(userPage);
	}
	
	
	/**
	 * Mybatis-plus 开启了逻辑删除之后就会自动使用逻辑删除
	 *
	 * @param id 用户的id
	 * @return 返回结果
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
		
		if (!userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH);
		}
		
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.removeById(id);
		return ResultUtils.success(result);
	}
	
	/**
	 * 修改用户信息
	 *
	 * @param user    修改用户信息的user对象
	 * @param request 后端获取当前前端发起请求的用户信息
	 * @return
	 */
	@PostMapping("/update")
	public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
		// 1. 校验参数是否为空
		if (user == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 获取当前的登录用户信息
		User loginUser = userService.getLoginUser(request);
		
		// 获取返回得到的结果
		int result = userService.updateUser(user, loginUser);
		
		return ResultUtils.success(result);
	}
	
	
	/**
	 * @param tagNameList 标签名列表
	 * @return 查询到的标签列表
	 */
	@GetMapping("/search/tags")
	// 传过来的参数不比填 也就是可以非空 默认为true 是必填 不能为空
	// 识别前端发的数据 通过@RequestParam 识别参数
	public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
		// 判断异常 controller层常常可以用来校验是否为空 service逻辑层 常常用来校验是否合法
		if (CollectionUtils.isEmpty(tagNameList)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		List<User> users = userService.searchUserByTags(tagNameList);
		return ResultUtils.success(users);
	}
	
	
	/**
	 * 获取匹配的用户
	 *
	 * @param num
	 * @param request
	 * @return
	 */
	@GetMapping("/match")
	public BaseResponse<List<User>> matchUser(long num, HttpServletRequest request) {
		if (num <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		User loginUser = userService.getLoginUser(request);
		return ResultUtils.success(userService.matchUsers(num, loginUser));
		
	}
	
}
