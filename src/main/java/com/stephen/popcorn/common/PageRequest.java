package com.stephen.popcorn.common;

import com.stephen.popcorn.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @author: stephen qiu
 * @create: 2024-03-28 20:35
 **/
@Data

public class PageRequest implements Serializable {
	
	
	private static final long serialVersionUID = 3529134357337490412L;
	
	
	/**
	 * 当前页号
	 */
	private int current = 1;
	
	/**
	 * 页面大小
	 */
	private int pageSize = 10;
	
	/**
	 * 排序字段
	 */
	private String sortField;
	
	/**
	 * 排序顺序（默认升序）
	 */
	private String sortOrder = CommonConstant.SORT_ORDER_ASC;
	
	
}
