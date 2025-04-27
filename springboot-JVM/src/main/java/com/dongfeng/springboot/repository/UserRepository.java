package com.dongfeng.springboot.repository;

import com.dongfeng.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByUsernameContaining(String username);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findByEmailDomain(String domain);
    
    // This method will be used for TPS testing (complex query)
    @Query(value = "SELECT * FROM users u WHERE u.created_at > DATEADD('DAY', -30, CURRENT_TIMESTAMP()) ORDER BY u.created_at DESC", nativeQuery = true)
    List<User> findRecentUsers();
}
