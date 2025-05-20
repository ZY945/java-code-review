package com.dongfeng.springbootmvc.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongfeng.springbootmvc.inventory.entity.ProductInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 商品库存Mapper接口
 */
@Mapper
public interface ProductInventoryMapper extends BaseMapper<ProductInventory> {

    /**
     * 使用MySQL排他锁获取商品库存信息
     *
     * @param productId 商品ID
     * @return 商品库存信息
     */
    @Select("SELECT * FROM product_inventory WHERE product_id = #{productId} FOR UPDATE")
    ProductInventory selectByProductIdWithLock(@Param("productId") Long productId);

    /**
     * 减少商品库存
     *
     * @param productId 商品ID
     * @param quantity 减少的数量
     * @return 影响的行数
     */
    @Update("UPDATE product_inventory SET stock = stock - #{quantity}, update_time = NOW() " +
            "WHERE product_id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
