package com.dongfeng.springboot;

import com.dongfeng.springboot.rpc.ServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = RpcClientApplication.class)
@Slf4j
class SpringbootMvcApplicationTests {
    @Autowired
    private ServiceTest serviceTest;
    @Test
    void contextLoads() {
        serviceTest.test();
    }


    @Test
    public void initTestData() {

    }

}
