package com.example.springscaffoldbase.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用于API请求和响应的用户数据传输对象
 */
@Data
@Schema(description = "User DTO")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "User ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores and hyphens")
    @Schema(description = "Username", example = "john_doe", required = true)
    private String username;

    /**
     * 邮箱
     */
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    private String email;

    /**
     * 密码（仅用于创建/更新请求）
     */
    @Schema(description = "Password (only for create/update requests)", example = "password123")
    private String password;

    /**
     * 用户状态（0：禁用，1：启用）
     */
    @Schema(description = "User status (0: disabled, 1: enabled)", example = "1")
    private Integer status;
}
