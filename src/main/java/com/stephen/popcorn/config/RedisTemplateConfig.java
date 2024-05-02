package com.stephen.popcorn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: stephen qiu
 * @create: 2024-03-24 18:45
 **/
@Configuration
public class RedisTemplateConfig {
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		
		// 设置键的序列化器
		redisTemplate.setKeySerializer(RedisSerializer.string());
		
		return redisTemplate;
	}
	
}
