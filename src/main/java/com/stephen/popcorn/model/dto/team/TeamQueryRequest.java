package com.stephen.popcorn.model.dto.team;

import com.stephen.popcorn.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询队伍请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 队伍名称
	 */
	private String teamName;
	
	/**
	 * 队伍简介
	 */
	private String teamProfile;
	
	/**
	 * 创建人id
	 */
	private Long userId;
	
	/**
	 * 队伍状态（0-公开,1-私密）
	 */
	private Integer status;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 搜索关键词
	 */
	private String searchText;
	
	private static final long serialVersionUID = 1L;
}