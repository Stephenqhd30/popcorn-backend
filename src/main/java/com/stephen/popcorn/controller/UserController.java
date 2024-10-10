package com.stephen.popcorn.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constant.SaltConstant;
import com.stephen.popcorn.constant.UserConstant;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.model.dto.user.*;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.enums.UserGenderEnum;
import com.stephen.popcorn.model.enums.UserRoleEnum;
import com.stephen.popcorn.model.vo.LoginUserVO;
import com.stephen.popcorn.model.vo.UserExcelVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.ExcelUtils;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 用户接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Resource
	private UserService userService;
	
	// region 登录相关
	
	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return 注册是否成功
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		// 获取请求参数
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			return null;
		}
		long result = userService.userRegister(userAccount, userPassword, checkPassword);
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
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
		return ResultUtils.success(loginUserVO);
	}
	
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return {@link BaseResponse<LoginUserVO>}
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
		User user = userService.getLoginUser(request);
		return ResultUtils.success(userService.getLoginUserVO(user));
	}
	
	// endregion
	
	// region 增删改查
	
	/**
	 * 创建用户
	 *
	 * @param userAddRequest userAddRequest
	 * @param request        request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		user.setTags(JSONUtil.toJsonStr(userAddRequest.getTagList()));
		// 数据校验
		userService.validUser(user, true);
		// todo 填充默认值
		// 默认密码 12345678
		String encryptPassword = DigestUtils.md5DigestAsHex((SaltConstant.SALT + UserConstant.DEFAULT_PASSWORD).getBytes());
		user.setUserPassword(encryptPassword);
		// 给用户分配一个默认的头像
		user.setUserAvatar(UserConstant.USER_AVATAR);
		// 写入数据库
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTagId = user.getId();
		return ResultUtils.success(newTagId);
	}
	
	/**
	 * 删除用户
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldUser.getId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest userUpdateRequest
	 * @param request           request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
	                                        HttpServletRequest request) {
		if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);
		user.setTags(JSONUtil.toJsonStr(userUpdateRequest.getTagList()));
		// 数据校验
		userService.validUser(user, false);
		// 判断是否存在
		long id = userUpdateRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户（仅管理员）
	 *
	 * @param id      用户id
	 * @param request request
	 * @return BaseResponse<User>
	 */
	@GetMapping("/get")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(user);
	}
	
	/**
	 * 根据 id 获取包装类
	 *
	 * @param id      用户id
	 * @param request request
	 * @return 查询得到的用户包装类
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userService.getUserVO(user, request));
	}
	
	
	/**
	 * 分页获取用户列表（仅管理员）
	 *
	 * @param userQueryRequest userQueryRequest
	 * @param request          request
	 * @return BaseResponse<Page < User>>
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                               HttpServletRequest request) {
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		return ResultUtils.success(userPage);
	}
	
	/**
	 * 分页获取用户封装列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @param request          request
	 * @return
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
		List<UserVO> userVO = userService.getUserVO(userPage.getRecords(), request);
		userVOPage.setRecords(userVO);
		return ResultUtils.success(userVOPage);
	}
	
	// endregion
	
	/**
	 * 更新个人信息
	 *
	 * @param userEditRequest 用户编辑条件
	 * @param request         要求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/update/my")
	public BaseResponse<Boolean> updateMyUser(@RequestBody UserEditRequest userEditRequest,
	                                          HttpServletRequest request) {
		if (userEditRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userEditRequest, user);
		user.setTags(JSONUtil.toJsonStr(userEditRequest.getTagList()));
		user.setId(loginUser.getId());
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 用户数据批量导入
	 *
	 * @param file 用户 Excel 文件
	 * @return 导入结果
	 */
	@PostMapping("/import")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Map<String, Object>> importUserDataByExcel(@RequestPart("file") MultipartFile file) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		Map<String, Object> result = null;
		
		try {
			// 调用服务层处理用户导入
			result = userService.importUsers(file);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "导入信息有误");
		}
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void download(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<UserExcelVO> userExcelVOList = userService.list().stream().map(user -> {
					UserExcelVO userExcelVO = new UserExcelVO();
					BeanUtils.copyProperties(user, userExcelVO);
					userExcelVO.setId(String.valueOf(user.getId()));
					userExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
					userExcelVO.setUserRole(Objects.requireNonNull(UserRoleEnum.getEnumByValue(user.getUserRole())).getText());
					userExcelVO.setCreateTime(ExcelUtils.dateToString(user.getCreateTime()));
					userExcelVO.setUpdateTime(ExcelUtils.dateToString(user.getUpdateTime()));
					return userExcelVO;
				})
				.collect(Collectors.toList());
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, "用户信息");
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserExcelVO.class)
					.sheet("用户信息")
					.doWrite(userExcelVOList);
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 通过余弦相似度想法计算出当前用户和当前用户的相似度
	 *
	 * @param userMatchRequest userMatchRequest
	 * @param request          request
	 * @return {@link BaseResponse<List<UserVO>>}
	 */
	@PostMapping("/match")
	public BaseResponse<List<UserVO>> matchUsers(@RequestBody UserMatchRequest userMatchRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(userMatchRequest == null, ErrorCode.PARAMS_ERROR);
		int number = userMatchRequest.getNumber();
		// 检查参数是否合法
		ThrowUtils.throwIf(number <= 0 || number > 20, ErrorCode.PARAMS_ERROR, "匹配人数不能小于0人或者多于20人");
		return ResultUtils.success(userService.cosMatchUsers(userMatchRequest, request));
	}
}
