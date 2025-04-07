package com.dongfeng.springbootmvc;

import com.dongfeng.springbootmvc.entity.User;
import com.dongfeng.springbootmvc.repository.UserRepository;
import com.dongfeng.springbootmvc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class SpringbootMvcApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;
	@Test
	void contextLoads() {
	}


	@Test
	public void initTestData() {
//		// 1. 初始化用户数据
//		List<User> users = new ArrayList<>();
//		for (int i = 0; i < 1000; i++) {
//			User user = new User();
//			user.setUsername("test_user_" + (10001 + i));
//			// 随机时间
//			user.setCreateTime(LocalDateTime.now().minusDays((long) (Math.random() * 100)));
//			users.add(user);
//		}
//		userRepository.saveAll(users);
//		log.info("用户数据初始化完成，共{}条", users.size());

	}

	@Test
	public void testOnOneTransactionalSaveAndSelect() {
		userService.saveAndSelect();
	}
}
