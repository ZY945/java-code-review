package com.example.springscaffoldbase.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springscaffoldbase.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用于数据库操作的用户映射器接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper提供了所有基本的CRUD操作
    // 如果需要，可以在这里添加额外的自定义方法
}
