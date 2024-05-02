package com.stephen.popcorn.service;

import com.mysql.cj.util.TimeUtil;
import com.stephen.popcorn.mapper.UserMapper;
import com.stephen.popcorn.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: stephen qiu
 * @create: 2024-03-22 22:09
 **/
@SpringBootTest
public class InsertUsersTest {
	
	@Resource
	private UserService userService;
	
	private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
	
	/**
	 * 批量插入用户
	 *
	 * @Scheduled(fixedRate = 3000) 开启定时任务经过多长时间再次执行
	 */
	@Test
	public void doInsertUsers() {
		StopWatch stopWatch = new StopWatch();
		List<User> userList = new ArrayList<>();
		System.out.println("gogogo");
		stopWatch.start();
		final int INSERT_NUM = 10000;
		for (int i = 0; i < INSERT_NUM; i++) {
			User user = new User();
			user.setUsername("假用户");
			user.setUserAccount("fakeStephen");
			user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1634740539846852609/QQhGRH4F-%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230417215622.jpg");
			user.setGender(0);
			user.setProfile("balabla");
			user.setPhone("123");
			user.setEmail("456@qq.com");
			user.setUserStatus(0);
			user.setUserRole(0);
			user.setStudentNumber("1111111");
			user.setTags("[]");
			user.setUserPassword("12345678");
			userList.add(user);
		}
		// 22秒十万条数据
		userService.saveBatch(userList, 10000);
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}
	
	/**
	 * 并发批量插入用户(使用的是高并发多线程里的知识 7S)
	 *
	 * @Scheduled(fixedRate = 3000) 开启定时任务经过多长时间再次执行
	 */
	@Test
	public void doConcurrencyInsertUsers() {
		StopWatch stopWatch = new StopWatch();
		System.out.println("gogogo");
		stopWatch.start();
		int batchSize = 2500;
		// 分十组
		int j = 0;
		// 定义一个线程池的列表 转换成线程安全的集合
		List<CompletableFuture<Void>> futureList = Collections.synchronizedList(new ArrayList<>());
		for (int i = 0; i < 40; i++) {
			List<User> userList = new ArrayList<>();
			while (true) {
				j++;
				User user = new User();
				user.setUsername("假用户");
				user.setUserAccount("fakeStephen");
				user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1634740539846852609/QQhGRH4F-%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230417215622.jpg");
				user.setGender(0);
				user.setProfile("balabla");
				user.setPhone("123");
				user.setEmail("456@qq.com");
				user.setUserStatus(0);
				user.setUserRole(0);
				user.setStudentNumber("1111111");
				user.setTags("[]");
				user.setUserPassword("12345678");
				userList.add(user);
				if (j % 10000 == 0) {
					break;
				}
			}
			// 异步任务
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				System.out.println(Thread.currentThread().getName());
				userService.saveBatch(userList, batchSize);
			}, executorService);
			futureList.add(future);
			
		}
		CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
		
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}
	
	
}
