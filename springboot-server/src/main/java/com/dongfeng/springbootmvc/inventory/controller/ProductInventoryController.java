package com.dongfeng.springbootmvc.inventory.controller;

import com.dongfeng.springbootmvc.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品库存控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/inventory")
public class ProductInventoryController {

    private final ProductInventoryService productInventoryService;

    public ProductInventoryController(ProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    /**
     * 初始化商品库存
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initInventory(
            @RequestParam("productId") Long productId,
            @RequestParam("productName") String productName,
            @RequestParam("stock") Integer stock) {
        
        Map<String, Object> result = new HashMap<>();
        
        boolean success = productInventoryService.initProductInventory(productId, productName, stock);
        
        if (success) {
            result.put("success", true);
            result.put("message", "商品库存初始化成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "商品库存初始化失败");
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 使用Redis分布式锁扣减库存
     */
    @PostMapping("/decrease/redis-lock")
    public ResponseEntity<Map<String, Object>> decreaseWithRedisLock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        
        Map<String, Object> result = new HashMap<>();
        
        boolean success = productInventoryService.decreaseStockWithRedisLock(productId, quantity);
        
        if (success) {
            result.put("success", true);
            result.put("message", "使用Redis分布式锁扣减库存成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "使用Redis分布式锁扣减库存失败");
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 使用MySQL排他锁扣减库存
     */
    @PostMapping("/decrease/mysql-lock")
    public ResponseEntity<Map<String, Object>> decreaseWithMysqlLock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        
        Map<String, Object> result = new HashMap<>();
        
        boolean success = productInventoryService.decreaseStockWithMysqlLock(productId, quantity);
        
        if (success) {
            result.put("success", true);
            result.put("message", "使用MySQL排他锁扣减库存成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "使用MySQL排他锁扣减库存失败");
            return ResponseEntity.badRequest().body(result);
        }
    }
}
