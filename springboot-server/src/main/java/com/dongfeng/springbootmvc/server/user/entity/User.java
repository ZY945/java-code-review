package com.dongfeng.springbootmvc.server.user.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "c_user")
@org.hibernate.annotations.Table(
    appliesTo = "c_user",
    comment = "用户表"
)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "主键")
    private Long id;
    
    @Column(name = "username", nullable = false, length = 64, columnDefinition = "用户名")
    private String username;
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "创建时间")
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false, columnDefinition = "更新时间")
    private LocalDateTime updateTime;
} 