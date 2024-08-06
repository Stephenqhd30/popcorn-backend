package com.stephen.popcorn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.mapper.UserMapper;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.stephen.popcorn.constant.UserConstant.ADMIN_ROLE;
import static com.stephen.popcorn.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author stephen qiu
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-12-02 10:50:44
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
		implements UserService {
	
	
	// Mapper注入
	@Resource
	private UserMapper userMapper;
	
	/**
	 * 盐值 混淆密码
	 */
	private static final String SALT = "Popcorn";
	
	
	/**
	 * 用户注册
	 *
	 * @param userAccount   用户账户
	 * @param userPassword  用户密码
	 * @param checkPassword 校验密码
	 * @return 返回
	 */
	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 校验用户账户的长度不小于4
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
			
		}
		// 校验密码的长度不小于8
		if (userPassword.length() < 8 || checkPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
		}
		
		// 账户不能包含特殊字符 [validPattern 有效模式]
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if (matcher.find()) {
			return -1;
		}
		// 密码和校验密码相同
		if (!userPassword.equals(checkPassword)) {
			return -1;
		}
		
		// 校验 用户不重复
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount", userAccount);
		long count = userMapper.selectCount(queryWrapper);
		if (count > 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
		}
		
		// 校验 学号不能重复
		queryWrapper = new QueryWrapper<>();
		count = userMapper.selectCount(queryWrapper);
		if (count > 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号重复");
		}
		
		// 2. 密码的加密 [单向的加密]
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		
		// 3. 向用户数据库插入数据
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptPassword);
		boolean saveResult = this.save(user);
		
		if (!saveResult) {
			return -1;
		}
		
		return user.getId();
	}
	
	/**
	 * 用户登录
	 *
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param request      http请求头
	 * @return 脱敏后的用户信息
	 */
	@Override
	public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			return null;
		}
		if (userAccount.length() < 4) {
			return null;
		}
		if (userPassword.length() < 8) {
			return null;
		}
		// 账户不能包含特殊字符
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if (matcher.find()) {
			return null;
		}
		
		// 2. 加密
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		// 查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount", userAccount);
		queryWrapper.eq("userPassword", encryptPassword);
		User user = userMapper.selectOne(queryWrapper);
		// 用户不存在
		if (user == null) {
			log.info("user login failed, userAccount cannot match userPassword");
			return null;
		}
		// 3. 用户脱敏
		User safetyUser = getSafetyUser(user);
		// 4. 记录用户的登录态
		request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
		return safetyUser;
	}
	
	
	/**
	 * 用户脱敏
	 *
	 * @param originUser 原始用户数据
	 * @return 返回脱敏之后的用户信息
	 */
	@Override
	public User getSafetyUser(User originUser) {
		if (originUser == null) {
			return null;
		}
		User safetyUser = new User();
		safetyUser.setId(originUser.getId());
		safetyUser.setUserName(originUser.getUserName());
		safetyUser.setUserAccount(originUser.getUserAccount());
		safetyUser.setUserAvatar(originUser.getUserAvatar());
		safetyUser.setGender(originUser.getGender());
		safetyUser.setPhone(originUser.getPhone());
		safetyUser.setEmail(originUser.getEmail());
		safetyUser.setUserStatus(originUser.getUserStatus());
		safetyUser.setCreateTime(originUser.getCreateTime());
		safetyUser.setUserRole(originUser.getUserRole());
		safetyUser.setStudentNumber(originUser.getStudentNumber());
		safetyUser.setTags(originUser.getTags());
		
		return safetyUser;
	}
	
	/**
	 * 用户注销
	 *
	 * @param request http请求头
	 */
	@Override
	public int userLogout(HttpServletRequest request) {
		// 移除登录态
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		return 1;
	}
	
	/**
	 * 获取当前的user
	 *
	 * @param request http请求头
	 * @return 返回当前登录的用户信息
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("HttpServletRequest is null");
		}
		// 从request提取session 进而获取当中的用户信息
		User userObj = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
		// 对获取到的用户做一次非空判断
		if (userObj == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		return userObj;
	}
	
	/**
	 * 匹配用户
	 *
	 * @param num       数量
	 * @param loginUser 当前登录的用户
	 * @return 匹配的用户列表
	 */
	@Override
	public List<User> matchUsers(long num, User loginUser) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id", "tags");
		queryWrapper.isNotNull("tags");
		List<User> userList = this.list(queryWrapper);
		String tags = loginUser.getTags();
		Gson gson = new Gson();
		// 将列表转换成字符串列表
		List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
		}.getType());
		// 用户列表的下标=> 相似度
		ArrayList<Pair<User, Long>> list = new ArrayList<>();
		// 依次计算所有用户和当前用户的相似度
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			String userTags = user.getTags();
			// 无标签或者为当前用户自己
			if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
				continue;
			}
			List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
			}.getType());
			
			// 计算分数
			long distance = AlgorithmUtils.minDistance(tagList, userTagList);
			list.add(new Pair<>(user, distance));
		}
		// 按照编辑距离由小到大排序
		List<Pair<User, Long>> topUserPairList = list.stream().sorted((a, b) -> (int) (a.getValue() - b.getValue())).limit(num).collect(Collectors.toList());
		// 记录了原本顺序的 userId 列表
		List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
		
		QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.in("id", userIdList);
		Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream().map(this::getSafetyUser).collect(Collectors.groupingBy(User::getId));
		
		List<User> finalUserList = new ArrayList<>();
		for (Long userId : userIdList) {
			finalUserList.add(userIdUserListMap.get(userId).get(0));
		}
		
		return finalUserList;
		
		
	}
	
	/**
	 * 根据标签搜索用户 (内存缓存)
	 *
	 * @param tagNameList 用户要拥有的标签
	 * @return 符合要求的用户列表
	 */
	@Override
	public List<User> searchUserByTags(List<String> tagNameList) {
		if (CollectionUtils.isEmpty(tagNameList)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		// 1. 先查询所有用户
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		List<User> userList = userMapper.selectList(queryWrapper);
		Gson gson = new Gson();
		// 2. 在内存中判断是否含有符合要求的标签
		return userList.stream().filter(user -> {
			String tagsStr = user.getTags();
			// 非空校验
			if (StringUtils.isBlank(tagsStr)) {
				return false;
			}
			// 序列化 => 转换为 JSON 序列化 => 转换为 Set<String>
			Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
			}.getType());
			
			tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
			
			for (String tagName : tempTagNameSet) {
				if (tempTagNameSet.contains(tagName)) {
					// 在此修复中，我更改了循环以正确检查用户的标签是否包含在传入的 tagNameList 中。
					// 现在，它将遍历传入的标签列表，并检查用户的标签是否包含在其中。
					// 如果不包含，则过滤掉该用户，否则保留该用户
					if (!tempTagNameSet.contains(tagName)) {
						return false;
					}
				}
			}
			// return true;
			// 现在，我们使用了 containsAll 方法来检查用户的标签集合是否包含传入的所有标签。
			// 这确保了只有当用户包含所有传入的标签时才会被包含在结果列表中。
			return tempTagNameSet.containsAll(tagNameList);
		}).map(this::getSafetyUser).collect(Collectors.toList());
		
	}
	
	/**
	 * 更新用户(判断权限 仅管理员和自己才可以)
	 *
	 * @param user      需要更改的用户
	 * @param loginUser 当前登录的用户
	 * @return
	 */
	@Override
	public int updateUser(User user, User loginUser) {
		long userId = user.getId();
		if (userId <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// TODO 补充校验 如果用户没有传任何需要更新的值 就直接报错 不用执行更新语句
		// 如果是管理员，允许更新任意用户
		// 如果不是管理员 只允许更新个人信息
		if (!isAdmin(loginUser) && userId != loginUser.getId()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		User oldUser = userMapper.selectById(userId);
		if (oldUser == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		return userMapper.updateById(user);
		
	}
	
	
	/**
	 * 根据标签搜索用户 (SQL 搜索)
	 *
	 * @param tagNameList 用户要拥有的标签
	 * @return
	 */
	@Deprecated
	private List<User> searchUserByTagsBySQL(List<String> tagNameList) {
		if (CollectionUtils.isEmpty(tagNameList)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long startTime = System.currentTimeMillis();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		
		// 拼接 and 查询
		// like '%Java%' and like '%Python%'
		for (String tagName : tagNameList) {
			queryWrapper = queryWrapper.like("tags", tagName);
		}
		
		List<User> userList = userMapper.selectList(queryWrapper);
		log.info("sql query time = " + (System.currentTimeMillis() - startTime));
		return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
		
		
	}
	
	
	/**
	 * 封装类 判断是不是管理员
	 *
	 * @param request 获取请求头
	 * @return 判断结果
	 */
	@Override
	public boolean isAdmin(HttpServletRequest request) {
		// 通过session获取该账号
		// 然后判断该账号是否为管理员
		Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
		User user = (User) attribute;
		return user != null && Objects.equals(user.getUserRole(), ADMIN_ROLE);
	}
	
	/**
	 * 封装类 判断是不是管理员(根据当前登录用户判断)
	 *
	 * @param loginUser 当前的登录用户
	 * @return 是否是管理员
	 */
	@Override
	public boolean isAdmin(User loginUser) {
		return false;
	}
}