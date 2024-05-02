package com.stephen.popcorn.service;

import com.stephen.popcorn.model.domain.User;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试类
 *
 * @author stephen qiu
 */
@SpringBootTest
public class UserServiceTest {

  @Resource
  private UserService userService;

  @Test
  public void testAddUser() {
    User user = new User();
    user.setUsername("Stephen");
    user.setUserAccount("123");
    user.setAvatarUrl("avatar.jpg");
    user.setGender(0);
    user.setUserPassword("123456");
    user.setPhone("123");
    user.setEmail("456");

    boolean result = userService.save(user);
    System.out.println(user.getId());
    // 断言
    Assertions.assertTrue(result);
    // Assertions.assertEquals( true, result);
  }

  @Test
  public void testDeleteUser() {
    boolean result = userService.removeById(1L);
    Assertions.assertTrue(result);
  }

  @Test
  void userRegister() {
    String userAccount = "Stephen";
    String userPassword = "";
    String checkPassword = "123456";
    String studentNumber = "1";
    long result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    userAccount = "pig";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    userAccount = "Stephen";
    userPassword = "123456";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    userAccount = "Ste phen";
    userPassword = "12345678";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    checkPassword = "123456789";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    userAccount = "StephenPig";
    checkPassword = "12345678";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertEquals(-1, result);

    userAccount = "Stephen";
    result = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
    Assertions.assertTrue(result > 0);


  }


  @Test
  public void testSearchUsersByTags() {
    List<String> tagNameList = Arrays.asList("Java", "Python");
    List<User> userList = userService.searchUserByTags(tagNameList);

    Assertions.assertNotNull(userList);
  }
}