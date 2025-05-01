package com.example.springscaffoldbase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springscaffoldbase.dto.UserDTO;
import com.example.springscaffoldbase.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建新用户
     *
     * @param userDTO 用户数据
     * @return 创建的用户ID
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新现有用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     * @return 如果成功则返回true，否则返回false
     */
    boolean updateUser(Long id, UserDTO userDTO);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 如果成功则返回true，否则返回false
     */
    boolean deleteUser(Long id);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long id);

    /**
     * 获取所有用户列表
     *
     * @return 用户DTO列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 获取分页用户列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param username 用户名过滤（可选）
     * @param email    邮箱过滤（可选）
     * @return 分页的用户DTO列表
     */
    IPage<UserDTO> getUsersByPage(int page, int size, String username, String email);
}
