package com.stephen.popcorn.utils.oss;

import com.stephen.popcorn.config.bean.SpringContextHolder;
import com.stephen.popcorn.manager.oss.MinioManager;
import com.stephen.popcorn.model.entity.LogFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * MinIO工具类
 *
 * @author stephen qiu
 */
@Slf4j
public class MinioUtils {
	
	/**
	 * 被封装的MinIO对象
	 */
	private static final MinioManager MINIO_MANAGER = SpringContextHolder.getBean(MinioManager.class);
	
	/**
	 * 上传文件
	 *
	 * @param file     上传的文件数据
	 * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
	 * @return {@link LogFiles}
	 */
	public static String uploadFile(MultipartFile file, String rootPath) throws IOException {
		return MINIO_MANAGER.uploadToMinio(file, rootPath);
	}
	
	
	/**
	 * 删除文件
	 *
	 * @param id 文件ID
	 */
	public static void deleteById(Long id) {
		MINIO_MANAGER.deleteInMinioById(id);
	}
	
}