package com.stephen.popcorn.model.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TagDTO 封装类
 *
 * @author: stephen qiu
 * @create: 2024-09-22 20:22
 **/
@Data
public class TagDTO {
	
	/**
	 * 标签 id
	 */
	private Long id;
	/**
	 * 标签名称
	 */
	private String tagName;
	/**
	 * 标签子节点
	 */
	private List<TagChildren> tagChildrenList;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TagChildren {
		/**
		 * 标签子节点 id
		 */
		private Long id;
		/**
		 * 标签名称
		 */
		private String tagName;
		
		/**
		 * 子节点的子节点列表 (递归结构)
		 */
		private List<TagChildren> tagChildrenList;
	}
}
