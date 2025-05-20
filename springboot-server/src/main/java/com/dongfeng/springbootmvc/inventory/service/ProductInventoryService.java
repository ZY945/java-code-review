package com.dongfeng.springbootmvc.inventory.service;

/**
 * 商品库存服务接口
 */
public interface ProductInventoryService {

    /**
     * 初始化商品库存（预热）
     *
     * @param productId   商品ID
     * @param productName 商品名称
     * @param stock       库存数量
     * @return 是否初始化成功
     */
    boolean initProductInventory(Long productId, String productName, Integer stock);

    /**
     * 使用Redis分布式锁扣减库存
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    boolean decreaseStockWithRedisLock(Long productId, Integer quantity);

    /**
     * 使用MySQL排他锁扣减库存
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    boolean decreaseStockWithMysqlLock(Long productId, Integer quantity);
}
