package com.dongfeng.springbootmvc.repository;

import com.dongfeng.springbootmvc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
} 