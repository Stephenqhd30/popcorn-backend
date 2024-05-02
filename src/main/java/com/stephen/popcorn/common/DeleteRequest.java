package com.stephen.popcorn.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除请求参数
 *
 * @author: stephen qiu
 * @create: 2024-04-08 21:56
 **/
@Data
public class DeleteRequest implements Serializable {
	private static final long serialVersionUID = -4510170086009719658L;
	
	/**
	 * id
	 */
	private long id;
}
