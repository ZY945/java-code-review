package com.dongfeng.springbootmvc.entity;

import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类，映射到数据库中的 users 表
 */
@Entity
@Table(name = "users", schema = "springboot_review")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BIGINT")
    @Comment("用户ID，自增主键")
    private Long id; // 用户ID，类型为 Long

    @Column(name = "username", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    @Comment("用户名，唯一且非空")
    private String username; // 用户名，类型为 String

    // unique = true 表示该字段在数据库中是唯一的
    @Column(name = "email", nullable = false, unique = true, length = 100, columnDefinition = "VARCHAR(100)")
    @Comment("邮箱，唯一且非空")
    private String email; // 邮箱，类型为 String

    @Column(name = "password", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    @Comment("密码，存储加密后的值")
    private String password; // 密码，类型为 String

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    @Comment("创建时间，不可更新")
    private LocalDateTime createdAt; // 创建时间，类型为 LocalDateTime

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    @Comment("更新时间")
    private LocalDateTime updatedAt; // 更新时间，类型为 LocalDateTime

    // 默认构造函数（JPA 要求）
    public User() {
    }

    // 带参数的构造函数
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 重写 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}