package com.dongfeng.springboot.service;

import com.dongfeng.springboot.SpringbootMvcApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SpringbootMvcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NumServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MyMCPService mcpService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testJudgeIfOddDirectly() {
        // 直接调用服务方法
        String result1 = mcpService.judgeIfOdd(2);
        String result2 = mcpService.judgeIfOdd(3);
        
        assertEquals("2是双数", result1);
        assertEquals("3不是双数", result2);
        
        System.out.println("直接调用服务结果1: " + result1);
        System.out.println("直接调用服务结果2: " + result2);
    }
    
    @Test
    public void testMcpServiceViaApi() {
        // 构建MCP API请求体
        String requestBody = """
                {
                    "messages": [
                        {
                            "role": "user",
                            "content": "判断一下10是不是双数"
                        }
                    ]
                }
                """;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        // TODO 调用MCP API,需要提供接口
        String url = "http://localhost:" + port + "/v1/chat/completions";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        System.out.println("MCP API 响应状态码: " + response.getStatusCode());
        System.out.println("MCP API 响应内容: " + response.getBody());
        
        // 验证响应
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().contains("10是双数"));
    }
}
