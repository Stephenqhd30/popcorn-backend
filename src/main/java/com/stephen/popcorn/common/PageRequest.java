package com.stephen.popcorn.common;

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
	 * 页面大小
	 */
	protected int pageSize = 10;
	
	/**
	 * 当前是第几页
	 */
	protected int pageNum = 1;
	
	
}
