package com.stephen.popcorn.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.stephen.popcorn.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 数据传输对象
 * 请求参数包装类
 *
 * @author: stephen qiu
 * @create: 2024-03-28 17:18
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {
	private static final long serialVersionUID = -7706443515187384765L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id 列表
	 */
	private List<Long> idList;
	
	/**
	 * 队伍名称
	 */
	private String searchText;
	
	/**
	 * 搜索关键词（同时对队伍名称和描述进行搜索）
	 */
	private String name;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 最大人数
	 */
	private Integer maxNum;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 0 - 公开， 1 - 私有， 2 - 加密
	 */
	private Integer status;
}
