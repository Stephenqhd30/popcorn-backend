package com.stephen.popcorn.once;

import com.stephen.popcorn.mapper.UserMapper;
import com.stephen.popcorn.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @author: stephen qiu
 * @create: 2024-03-22 21:40
 **/
@Component
public class InsertUsers {
	
	@Resource
	private UserMapper userMapper;
	
	/**
	 * 批量插入用户
	 *
	 * @Scheduled(fixedDelay = 5000, fixedRate = Long.MAX_VALUE) 开启定时任务经过多长时间再次执行
	 */
	// @Scheduled(fixedDelay = 5000, fixedRate = Long.MAX_VALUE)
	public void doInsertUsers() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSERT_NUM = 1000;
		for (int i = 0; i < INSERT_NUM; i++) {
			User user = new User();
			user.setUserName("假用户");
			user.setUserAccount("fakeStephen");
			user.setUserAvatar("https://pic.code-nav.cn/user_avatar/1634740539846852609/QQhGRH4F-%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230417215622.jpg");
			user.setGender(0);
			user.setProfile("balabla");
			user.setPhone("123");
			user.setEmail("456@qq.com");
			user.setUserStatus(0);
			user.setUserRole("user");
			user.setStudentNumber("1111111");
			user.setTags("[]");
			user.setUserPassword("12345678");
			// userMapper.insert(user);
		}
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}
}
