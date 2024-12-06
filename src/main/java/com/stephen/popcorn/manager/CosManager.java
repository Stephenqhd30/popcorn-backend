package com.stephen.popcorn.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.stephen.popcorn.config.oss.cos.properties.CosProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Cos 对象存储操作
 *
 * @author stephen qiu
 */
@Component
public class CosManager {

	@Resource
	private CosProperties cosProperties;

	@Resource
	private COSClient cosClient;

	/**
	 * 上传对象
	 *
	 * @param key           唯一键
	 * @param localFilePath 本地文件路径
	 * @return
	 */
	public PutObjectResult putObject(String key, String localFilePath) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucket(), key,
				new File(localFilePath));
		return cosClient.putObject(putObjectRequest);
	}

	/**
	 * 上传对象
	 *
	 * @param key  唯一键
	 * @param file 文件
	 * @return
	 */
	public PutObjectResult putObject(String key, File file) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucket(), key,
				file);
		return cosClient.putObject(putObjectRequest);
	}
}
