package com.dongfeng.springboot.controller;

import com.dongfeng.springboot.entity.User;
import com.dongfeng.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    // Metrics for monitoring
    private final AtomicLong requestCount = new AtomicLong(0);
    private final Map<String, AtomicLong> endpointMetrics = new ConcurrentHashMap<>();
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        incrementMetric("getAllUsers");
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        incrementMetric("getUserById");
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        incrementMetric("createUser");
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        incrementMetric("updateUser");
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        incrementMetric("deleteUser");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchByUsername(@RequestParam String username) {
        incrementMetric("searchByUsername");
        return ResponseEntity.ok(userService.searchByUsername(username));
    }
    
    @GetMapping("/email-domain")
    public ResponseEntity<List<User>> findByEmailDomain(@RequestParam String domain) {
        incrementMetric("findByEmailDomain");
        return ResponseEntity.ok(userService.findByEmailDomain(domain));
    }
    
    // QPS Testing Endpoint - Simple read operation
    @GetMapping("/qps-test")
    public ResponseEntity<User> qpsTest() {
        incrementMetric("qpsTest");
        return ResponseEntity.ok(userService.getRandomUser());
    }
    
    // TPS Testing Endpoint - Complex operation with database write
    @PostMapping("/tps-test")
    public ResponseEntity<User> tpsTest() {
        incrementMetric("tpsTest");
        return new ResponseEntity<>(userService.createRandomUser(), HttpStatus.CREATED);
    }
    
    // Endpoint for recent users - Complex query for testing
    @GetMapping("/recent")
    public ResponseEntity<List<User>> getRecentUsers() {
        incrementMetric("getRecentUsers");
        return ResponseEntity.ok(userService.findRecentUsers());
    }
    
    // Metrics endpoint
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        metrics.put("totalRequests", requestCount.get());
        metrics.put("endpointMetrics", endpointMetrics);
        return ResponseEntity.ok(metrics);
    }
    
    // Reset metrics
    @PostMapping("/metrics/reset")
    public ResponseEntity<Void> resetMetrics() {
        requestCount.set(0);
        endpointMetrics.clear();
        return ResponseEntity.noContent().build();
    }
    
    private void incrementMetric(String endpoint) {
        requestCount.incrementAndGet();
        endpointMetrics.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
    }
}
