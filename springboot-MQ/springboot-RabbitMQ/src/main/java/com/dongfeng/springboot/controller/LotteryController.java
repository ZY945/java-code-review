package com.dongfeng.springboot.controller;

import com.dongfeng.springboot.service.LotteryService;
import com.dongfeng.springboot.vo.LotteryRequest;
import com.dongfeng.springboot.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抽奖控制器
 */
@Slf4j
@RestController
@RequestMapping("/lottery")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    /**
     * 用户参与抽奖
     * 
     * @param request 抽奖请求
     * @return 结果
     */
    @PostMapping("/draw")
    public Result<String> draw(@RequestBody LotteryRequest request) {
        log.info("收到抽奖请求：{}", request);
        
        try {
            // 参数校验
            if (request.getUserId() == null || request.getActivityId() == null || request.getPrizeId() == null) {
                return Result.fail("参数不完整");
            }
            
            // 发送抽奖消息
            boolean success = lotteryService.sendLotteryMessage(
                    request.getUserId(), 
                    request.getActivityId(), 
                    request.getPrizeId());
            
            if (success) {
                return Result.success("抽奖请求已受理，正在处理中");
            } else {
                return Result.fail("抽奖请求处理失败");
            }
        } catch (Exception e) {
            log.error("抽奖请求处理异常", e);
            return Result.fail("系统异常，请稍后再试");
        }
    }
}
