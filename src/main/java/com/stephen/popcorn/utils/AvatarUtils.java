package com.stephen.popcorn.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * 随机生成头像(GitHub 风格的头像)
 *
 * @author stephen qiu
 */
public class AvatarUtils {
	
	// Base64编码前缀，用于在<img/>标签中直接预览图像
	public static final String BASE64_PREFIX = "data:image/png;base64,";
	
	// 头像的宽度和网格大小
	private static final int WIDTH = 64;
	private static final int GRID = 4;
	
	/**
	 * 根据给定的ID生成头像的Base64编码
	 *
	 * @param id 用户ID
	 * @return 头像的Base64编码字符串
	 * @throws IOException 如果生成图像时发生I/O错误
	 */
	public static String createBase64Avatar(int id) throws IOException {
		// 生成头像的字节数组
		byte[] imageBytes = create(id);
		// 返回带前缀的Base64字符串
		return BASE64_PREFIX + Base64.getEncoder().encodeToString(imageBytes);
	}
	
	/**
	 * 根据ID生成头像的字节数组，颜色随机
	 *
	 * @param id 用户ID
	 * @return 头像的字节数组
	 * @throws IOException 如果生成图像时发生I/O错误
	 */
	public static byte[] create(int id) throws IOException {
		// 计算填充量
		int padding = WIDTH / 2;
		// 计算图像的总大小
		int size = WIDTH * GRID + WIDTH;
		// 创建图像对象
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics2D graphics = img.createGraphics();
		// 设置背景颜色
		graphics.setColor(new Color(240, 240, 240));
		// 填充背景
		graphics.fillRect(0, 0, size, size);
		// 随机颜色
		graphics.setColor(randomColor());
		
		// 根据ID生成字符数组
		char[] idchars = createIdent(id);
		// 预计算绘制矩形的信息
		boolean[][] drawRectangles = computeDrawRectangles(idchars);
		
		// 绘制头像的图形
		for (int x = 0; x < Math.ceil(GRID / 2.0); x++) {
			for (int y = 0; y < GRID; y++) {
				// 判断是否绘制矩形
				if (drawRectangles[x][y]) {
					// 绘制矩形
					graphics.fillRect((padding + x * WIDTH), (padding + y * WIDTH), WIDTH, WIDTH);
					// 绘制对称矩形
					if (x < (double) (GRID / 2)) {
						graphics.fillRect((padding + ((GRID - 1) - x) * WIDTH), (padding + y * WIDTH), WIDTH, WIDTH);
					}
				}
			}
		}
		
		// 释放图形上下文
		graphics.dispose();
		
		// 将图像写入字节数组并返回
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(img, "png", byteArrayOutputStream);
		// 返回字节数组
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 生成随机颜色
	 *
	 * @return 随机颜色对象
	 */
	private static Color randomColor() {
		// 创建随机数生成器
		Random random = new Random();
		// 生成随机红色分量
		int r = random.nextInt(200 - 80) + 80;
		// 生成随机绿色分量
		int g = random.nextInt(200 - 80) + 80;
		// 生成随机蓝色分量
		int b = random.nextInt(200 - 80) + 80;
		// 返回生成的颜色对象
		return new Color(r, g, b);
	}
	
	/**
	 * 根据ID生成唯一标识字符数组
	 *
	 * @param id 用户ID
	 * @return 唯一标识字符数组
	 */
	private static char[] createIdent(int id) {
		// 创建基于ID的BigInteger对象
		BigInteger biContent = new BigInteger(String.valueOf(id).getBytes());
		// 生成唯一标识
		BigInteger bi = new BigInteger(id + "identicon" + id, 36).xor(biContent);
		// 返回字符数组
		return bi.toString(10).toCharArray();
	}
	
	/**
	 * 计算要绘制的矩形
	 *
	 * @param idchars 唯一标识字符数组
	 * @return 矩形绘制信息的布尔数组
	 */
	private static boolean[][] computeDrawRectangles(char[] idchars) {
		// 创建布尔数组以存储绘制信息
		boolean[][] drawRectangles = new boolean[AvatarUtils.GRID][AvatarUtils.GRID];
		// 根据ID字符生成绘制矩形的信息
		for (int x = 0; x < Math.ceil(AvatarUtils.GRID / 2.0); x++) {
			for (int y = 0; y < AvatarUtils.GRID; y++) {
				drawRectangles[x][y] = idchars[idchars.length - 1 - (x * AvatarUtils.GRID + y)] < 53;
			}
		}
		// 返回矩形绘制信息
		return drawRectangles;
	}
}
