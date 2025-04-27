package com.dongfeng.springboot.service;

import com.dongfeng.springboot.entity.User;
import com.dongfeng.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final Random random = new Random();
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional
    public User createUser(User user) {
        log.info("Creating user: {}", user.getUsername());
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        
        log.info("Updating user: {}", user.getUsername());
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<User> searchByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }
    
    @Transactional(readOnly = true)
    public List<User> findByEmailDomain(String domain) {
        return userRepository.findByEmailDomain(domain);
    }
    
    @Transactional(readOnly = true)
    public List<User> findRecentUsers() {
        return userRepository.findRecentUsers();
    }
    
    // Method for QPS testing - simple operation
    @Transactional(readOnly = true)
    public User getRandomUser() {
        long count = userRepository.count();
        if (count == 0) {
            return null;
        }
        long randomId = random.nextInt((int) count) + 1;
        return userRepository.findById(randomId).orElse(null);
    }
    
    // Method for TPS testing - complex operation with write
    @Transactional
    public User createRandomUser() {
        User user = new User();
        user.setUsername("user" + System.currentTimeMillis());
        user.setEmail("user" + System.currentTimeMillis() + "@example.com");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
