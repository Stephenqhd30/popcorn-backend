package com.stephen.popcorn.model.vo;

import cn.hutool.json.JSONUtil;
import com.stephen.popcorn.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户视图（脱敏）
 *
 * @author stephen qiu
 */
@Data
public class UserVO implements Serializable {
	
	private static final long serialVersionUID = -3280494130806111730L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户简介
	 */
	private String userProfile;
	
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	/**
	 * 用户性别（0-男 ，1-女，2-保密）
	 */
	private Integer userGender;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 标签列表
	 */
	private List<String> tagList;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 封装类转对象
	 *
	 * @param userVO
	 * @return
	 */
	public static User voToObj(UserVO userVO) {
		if (userVO == null) {
			return null;
		}
		User user = new User();
		BeanUtils.copyProperties(userVO, user);
		user.setTags(JSONUtil.toJsonStr(userVO.getTagList()));
		return user;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param user
	 * @return
	 */
	public static UserVO objToVo(User user) {
		if (user == null) {
			return null;
		}
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO);
		if (user.getTags() != null) {
			userVO.setTagList(JSONUtil.toList(user.getTags(), String.class));
		}
		return userVO;
	}
}