package com.stephen.popcorn.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * 用于配置 RedissonClient，以连接 Redis 实例
 *
 * @author: stephen qiu
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {
	
	private String host;
	
	private String port;
	
	private Integer database;
	
	/**
	 * 配置 RedissonClient 实例
	 *
	 * @return 配置好的 RedissonClient 实例
	 */
	@Bean
	public RedissonClient redissonClient() {
		// 1. 创建配置
		Config config = new Config();
		// 构建 Redis 地址
		String redisAddress = String.format("redis://%s:%s", host, port);
		// 使用单节点模式配置
		config.useSingleServer().setAddress(redisAddress).setDatabase(database);
		// 2. 创建一个 RedissonClient 实例
		// 同步和异步 API
		return Redisson.create(config);
	}
}
