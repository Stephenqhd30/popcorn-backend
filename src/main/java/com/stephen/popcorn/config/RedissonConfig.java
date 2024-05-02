package com.stephen.popcorn.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 *
 * @author: stephen qiu
 * @create: 2024-03-26 20:15
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {
	
	private String host;
	
	private String port;
	
	@Bean
	public RedissonClient redissonClient() {
		// 1. 创建配置
		Config config = new Config();
		String redisAddress = String.format("redis://%s:%s", host, port);
		config.useSingleServer().setAddress(redisAddress).setDatabase(3);
		// 2. 创建一个redisson的实例
		// Sync and Async API
		RedissonClient redisson = Redisson.create(config);
		return redisson;
	}
}
