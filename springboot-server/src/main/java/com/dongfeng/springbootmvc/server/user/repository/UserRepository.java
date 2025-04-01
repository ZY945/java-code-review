package com.dongfeng.springbootmvc.server.user.repository;

import com.dongfeng.springbootmvc.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
} 