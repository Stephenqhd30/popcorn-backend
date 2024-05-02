package com.stephen.popcorn.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.model.domain.User;
import com.stephen.popcorn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @author: stephen qiu
 * @create: 2024-03-24 21:09
 **/
@Component
@Slf4j
public class PerCacheJob {
	
	@Resource
	private UserService userService;
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource
	private RedissonClient redissonClient;
	
	private final List<Long> mianUserList = Collections.singletonList(1L);
	
	/**
	 * 每天执行 预热推荐用户
	 * <p>
	 * 任务的执行时间不能太长
	 */
	@Scheduled(cron = "0 31 23 * * *")
	synchronized public void doCacheRecommendUser() {
		// 得到锁的对象
		RLock lock = redissonClient.getLock("yupao:percachejob:docache:lock");
		try {
			// 只有一个线程能获取到锁
			if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
				System.out.println("getLock" + Thread.currentThread().getId());
				for (Long userId : mianUserList) {
					// 构造分页查询条件
					QueryWrapper<User> queryWrapper = new QueryWrapper<>();
					Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
					// 先判断缓存中有没有，有的话直接从缓存中取
					String redisKey = String.format("yupao:user:recommend:%s", userId);
					ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
					// 写缓存
					try {
						valueOperations.set(redisKey, userPage, 10000, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						log.error("redis set key error");
					}
				}
			}
		} catch (InterruptedException e) {
			log.error("doCacheRecommendUser error", e);
		} finally {
			// 只能释放当前线程创建的锁
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		
	}
}
