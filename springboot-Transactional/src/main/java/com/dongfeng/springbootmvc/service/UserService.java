package com.dongfeng.springbootmvc.service;

import com.dongfeng.springbootmvc.entity.User;
import com.dongfeng.springbootmvc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 实现在一个事务里先新增数据，然后进行查询，通过断点和日志查看数据
    // 结论可以查询到数据,隔离级别为REPEATABLE-READ
    // SELECT @@transaction_isolation;
    @Transactional(rollbackFor = Exception.class)
    public void saveAndSelect() {
        // 1. 初始化用户数据
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            User user = new User();
            user.setUsername("test_user_" + ((long) (Math.random() * 100) + i));
            user.setEmail("test_user_" + ((long) (Math.random() * 100) + "@example.com"));
            user.setPassword("password_" + ((long) (Math.random() * 100) + i));
            // 随机时间
            user.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 100)));
            users.add(user);
        }
        userRepository.saveAll(users);

        // 2. 查询数据
        List<User> userList = userRepository.findAll();
        log.info("用户数据查询完成，共{}条", userList.size());
        for (User user : userList) {
            log.info("用户ID: {}, 用户名: {}, 创建时间: {}", user.getId(), user.getUsername(), user.getCreatedAt());
        }
//        throw new RuntimeException("测试异常，事务回滚");

    }
}
