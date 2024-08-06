package com.stephen.popcorn.service;

import com.stephen.popcorn.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author: stephen qiu
 * @create: 2024-03-23 21:38
 **/
@SpringBootTest
public class RedisTest {
	
	@Resource
	private RedisTemplate redisTemplate;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Test
	void test() {
		ValueOperations valueOperations = redisTemplate.opsForValue();
		
		// 做一个简单的增删改查
		
		// 增
		valueOperations.set("StephenString", "Popcorn");
		valueOperations.set("StephenInteger", 1);
		valueOperations.set("StephenDouble", 2.0);
		User user = new User();
		user.setId(1L);
		user.setUsername("Stephen");
		valueOperations.set("StephenUser", user);
		
		// 查
		Object stephen = valueOperations.get("StephenString");
		Assertions.assertTrue("Popcorn".equals((String) stephen));
		stephen = valueOperations.get("StephenInteger");
		Assertions.assertTrue(1 == ((Integer) stephen));
		stephen = valueOperations.get("StephenDouble");
		Assertions.assertTrue(2.0 == (Double) stephen);
		System.out.println(valueOperations.get("StephenUser"));

		
		
	}
}
