package com.example.springscaffoldbase.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus的create_time和update_time字段自动填充处理器
 */
@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入记录时自动填充字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("插入时自动填充 createTime 和 updateTime 字段");
        
        // 如果字段存在且为空，设置createTime
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 如果字段存在且为空，设置updateTime
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新记录时自动填充字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("更新时自动填充 updateTime 字段");
        
        // 如果字段存在，设置updateTime
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
