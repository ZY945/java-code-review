package com.dongfeng.springboot.config;

import com.dongfeng.springboot.service.MyMCPService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ToolCallbackProviderRegister {

    @Bean
    public ToolCallbackProvider numTools(MyMCPService numService) {
        return MethodToolCallbackProvider.builder().toolObjects(numService).build();
    }

}