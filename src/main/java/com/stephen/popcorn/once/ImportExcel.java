package com.stephen.popcorn.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * @author: stephen qiu
 * @create: 2023-12-07 11:42
 **/
public class ImportExcel {
	
	/**
	 * 读取数据
	 */
	public static void main(String[] args) {
		String fileName = "D:\\Stephen\\PartnerMatching\\popcorn-backend\\src\\main\\resources\\testExcel.xlsx";
		// 监听器读
		// listenerRead(fileName);
		// 同步读
		synchronousRead(fileName);
	}
	
	/**
	 * 监听器读
	 *
	 * @param fileName
	 */
	public static void listenerRead(String fileName) {
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
		// 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
		EasyExcel.read(fileName, UserInfo.class, new UserInfoListener()).sheet().doRead();
	}
	
	/**
	 * 同步读
	 *
	 * @param fileName
	 */
	public static void synchronousRead(String fileName) {
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
		List<UserInfo> dateList = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
		for (UserInfo data : dateList) {
			System.out.println(data);
		}
	}
	
	
}

