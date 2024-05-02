package com.stephen.popcorn.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: stephen qiu
 * @create: 2024-03-26 20:25
 **/
@SpringBootTest

public class RedissonTest {
	@Resource
	private RedissonClient redissonClient;
	
	@Test
	void test() {
		// list 数据存储在内存中
		List<String> list = new ArrayList<>();
		list.add("Stephen");
		// list.remove(0);
		System.out.println("list: " + list.get(0));
		
		// 数据储存在 redis 的内存中
		RList<String> rList = redissonClient.getList("test-list");
		// rList.add("Stephen");
		rList.remove(0);
		
		System.out.println("rList: " + rList.get(0));
		// map
		
		Map<String, Integer> map = new HashMap<>();
		map.put("Stephen", 10);
		map.get("Stephen");
		
		RMap<Object, Object> rMap = redissonClient.getMap("test-map");
		rMap.put("Stephen", 10);
		rMap.get("Stephen");
		
		// set
		
		// stack
	}
	@Test
	void testWatchDog() {
		// 得到锁的对象
		RLock lock = redissonClient.getLock("yupao:percachejob:docache:lock");
		try {
			// 只有一个线程能获取到锁
			if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
				Thread.sleep(300000);
				System.out.println("getLock" + Thread.currentThread().getId());
			}
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		} finally {
			// 只能释放当前线程创建的锁
			if (lock.isHeldByCurrentThread()) {
				System.out.println("unLock: " + Thread.currentThread().getId());
				lock.unlock();
			}
		}
	}
}
