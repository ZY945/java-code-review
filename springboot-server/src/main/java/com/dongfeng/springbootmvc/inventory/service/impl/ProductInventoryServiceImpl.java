package com.dongfeng.springbootmvc.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongfeng.springbootmvc.inventory.entity.ProductInventory;
import com.dongfeng.springbootmvc.inventory.lock.RedisDistributedLock;
import com.dongfeng.springbootmvc.inventory.mapper.ProductInventoryMapper;
import com.dongfeng.springbootmvc.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 商品库存服务实现类
 */
@Slf4j
@Service
public class ProductInventoryServiceImpl extends ServiceImpl<ProductInventoryMapper, ProductInventory> implements ProductInventoryService {

    private final ProductInventoryMapper productInventoryMapper;
    private final RedisDistributedLock redisDistributedLock;
    private final StringRedisTemplate redisTemplate;

    // 锁的过期时间（秒）
    private static final long LOCK_EXPIRE_TIME = 10;
    // Redis库存缓存key前缀
    private static final String INVENTORY_CACHE_PREFIX = "product:inventory:";

    public ProductInventoryServiceImpl(ProductInventoryMapper productInventoryMapper, 
                                      RedisDistributedLock redisDistributedLock,
                                      StringRedisTemplate redisTemplate) {
        this.productInventoryMapper = productInventoryMapper;
        this.redisDistributedLock = redisDistributedLock;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initProductInventory(Long productId, String productName, Integer stock) {
        // 构建锁的key
        String lockKey = "lock:product:init:" + productId;
        // 生成请求ID（用于释放锁时验证）
        String requestId = UUID.randomUUID().toString();
        
        try {
            // 尝试获取分布式锁
            boolean locked = redisDistributedLock.tryLock(lockKey, requestId, LOCK_EXPIRE_TIME);
            if (!locked) {
                log.warn("获取分布式锁失败，商品ID: {}", productId);
                return false;
            }
            
            // 检查商品库存是否已存在
            LambdaQueryWrapper<ProductInventory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductInventory::getProductId, productId);
            ProductInventory existingInventory = productInventoryMapper.selectOne(queryWrapper);
            
            if (existingInventory != null) {
                // 更新已存在的库存记录
                existingInventory.setStock(stock);
                existingInventory.setProductName(productName);
                existingInventory.setUpdateTime(new Date());
                existingInventory.setVersion(existingInventory.getVersion() + 1);
                productInventoryMapper.updateById(existingInventory);
            } else {
                // 创建新的库存记录
                ProductInventory newInventory = new ProductInventory();
                newInventory.setProductId(productId);
                newInventory.setProductName(productName);
                newInventory.setStock(stock);
                newInventory.setLockedStock(0);
                newInventory.setCreateTime(new Date());
                newInventory.setUpdateTime(new Date());
                newInventory.setVersion(1);
                productInventoryMapper.insert(newInventory);
            }
            
            // 将库存数据缓存到Redis中
            String cacheKey = INVENTORY_CACHE_PREFIX + productId;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(stock), 24, TimeUnit.HOURS);
            
            log.info("商品库存初始化成功，商品ID: {}, 库存: {}", productId, stock);
            return true;
        } catch (Exception e) {
            log.error("商品库存初始化异常，商品ID: {}", productId, e);
            throw e;
        } finally {
            // 释放分布式锁
            redisDistributedLock.releaseLock(lockKey, requestId);
        }
    }

    @Override
    public boolean decreaseStockWithRedisLock(Long productId, Integer quantity) {
        if (productId == null || quantity <= 0) {
            log.error("参数错误，商品ID: {}, 数量: {}", productId, quantity);
            return false;
        }
        
        // 构建锁的key
        String lockKey = "lock:product:stock:" + productId;
        // 生成请求ID（用于释放锁时验证）
        String requestId = UUID.randomUUID().toString();
        
        try {
            // 尝试获取分布式锁
            boolean locked = redisDistributedLock.tryLock(lockKey, requestId, LOCK_EXPIRE_TIME);
            if (!locked) {
                log.warn("获取分布式锁失败，商品ID: {}", productId);
                return false;
            }
            
            // 先从Redis缓存中获取库存
            String cacheKey = INVENTORY_CACHE_PREFIX + productId;
            String stockStr = redisTemplate.opsForValue().get(cacheKey);
            
            // 如果缓存中没有库存数据，则从数据库中获取
            if (stockStr == null) {
                LambdaQueryWrapper<ProductInventory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ProductInventory::getProductId, productId);
                ProductInventory inventory = productInventoryMapper.selectOne(queryWrapper);
                
                if (inventory == null) {
                    log.error("商品库存不存在，商品ID: {}", productId);
                    return false;
                }
                
                stockStr = String.valueOf(inventory.getStock());
                // 将库存数据缓存到Redis中
                redisTemplate.opsForValue().set(cacheKey, stockStr, 24, TimeUnit.HOURS);
            }
            
            // 判断库存是否足够
            int currentStock = Integer.parseInt(stockStr);
            if (currentStock < quantity) {
                log.warn("商品库存不足，商品ID: {}, 当前库存: {}, 需要: {}", productId, currentStock, quantity);
                return false;
            }
            
            // 更新Redis缓存中的库存
            int newStock = currentStock - quantity;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(newStock), 24, TimeUnit.HOURS);
            
            // 更新数据库中的库存
            int affected = productInventoryMapper.decreaseStock(productId, quantity);
            if (affected <= 0) {
                // 数据库更新失败，回滚Redis缓存
                redisTemplate.opsForValue().set(cacheKey, stockStr, 24, TimeUnit.HOURS);
                log.error("数据库库存更新失败，商品ID: {}", productId);
                return false;
            }
            
            log.info("商品库存扣减成功（Redis锁），商品ID: {}, 扣减数量: {}, 剩余库存: {}", productId, quantity, newStock);
            return true;
        } catch (Exception e) {
            log.error("商品库存扣减异常（Redis锁），商品ID: {}", productId, e);
            return false;
        } finally {
            // 释放分布式锁
            redisDistributedLock.releaseLock(lockKey, requestId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseStockWithMysqlLock(Long productId, Integer quantity) {
        if (productId == null || quantity <= 0) {
            log.error("参数错误，商品ID: {}, 数量: {}", productId, quantity);
            return false;
        }
        
        try {
            // 使用MySQL的FOR UPDATE排他锁获取商品库存
            ProductInventory inventory = productInventoryMapper.selectByProductIdWithLock(productId);
            
            if (inventory == null) {
                log.error("商品库存不存在，商品ID: {}", productId);
                return false;
            }
            
            // 判断库存是否足够
            if (inventory.getStock() < quantity) {
                log.warn("商品库存不足，商品ID: {}, 当前库存: {}, 需要: {}", productId, inventory.getStock(), quantity);
                return false;
            }
            
            // 更新库存
            inventory.setStock(inventory.getStock() - quantity);
            inventory.setUpdateTime(new Date());
            inventory.setVersion(inventory.getVersion() + 1);
            
            int affected = productInventoryMapper.updateById(inventory);
            if (affected <= 0) {
                log.error("商品库存更新失败，商品ID: {}", productId);
                return false;
            }
            
            // 更新Redis缓存中的库存
            String cacheKey = INVENTORY_CACHE_PREFIX + productId;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(inventory.getStock()), 24, TimeUnit.HOURS);
            
            log.info("商品库存扣减成功（MySQL锁），商品ID: {}, 扣减数量: {}, 剩余库存: {}", 
                    productId, quantity, inventory.getStock());
            return true;
        } catch (Exception e) {
            log.error("商品库存扣减异常（MySQL锁），商品ID: {}", productId, e);
            throw e;
        }
    }
}
