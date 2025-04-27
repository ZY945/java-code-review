package com.dongfeng.springboot.config;

import com.dongfeng.springboot.entity.User;
import com.dongfeng.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();
    
    @Override
    @Transactional
    public void run(String... args) {
        log.info("检查数据库是否已初始化...");
        
        // 检查数据库中是否已有用户数据
        Long count = userRepository.count();
        if (count > 0) {
            log.info("数据库中已有 {} 个用户，跳过初始化", count);
            return;
        }
        
        log.info("开始初始化MySQL数据库测试数据...");
        
        try {
            // 创建批量插入的SQL语句
            StringBuilder sql = new StringBuilder("INSERT INTO users (username, email, created_at) VALUES ");
            List<Object[]> batchArgs = new ArrayList<>();
            
            // 创建1000个测试用户
            for (int i = 1; i <= 1000; i++) {
                String username = "user" + i;
                String email = "user" + i + "@example.com";
                LocalDateTime creationDate = LocalDateTime.now().minusDays(random.nextInt(60));
                
                // 为批量插入准备参数
                batchArgs.add(new Object[]{username, email, creationDate});
                
                if (i % 100 == 0) {
                    log.info("准备创建 {} 个测试用户", i);
                }
            }
            
            // 使用JdbcTemplate进行批量插入，提高性能
            jdbcTemplate.batchUpdate(
                    "INSERT INTO users (username, email, created_at) VALUES (?, ?, ?)",
                    batchArgs
            );
            
            log.info("数据库初始化完成。创建了 {} 个用户。", batchArgs.size());
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            throw e;
        }
    }
}
