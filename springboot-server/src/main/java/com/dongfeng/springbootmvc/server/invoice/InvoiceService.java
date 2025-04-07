//package com.dongfeng.springbootmvc.server.invoice;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.time.Duration;
//
//@Service
//public class InvoiceService {
//
//    @Autowired
//    private StateMachine<InvoiceState, InvoiceEvent> stateMachine;
//
//    @Autowired
//    private EmailService emailService; // 假设的邮件服务
//
//    @Autowired
//    private TaskScheduler taskScheduler; // 用于定时任务
//
//    // 提交发票申请
//    public String submitInvoiceApplication(InvoiceRequest request) {
//        stateMachine.start();
//        stateMachine.sendEvent(InvoiceEvent.SUBMIT);
//        // 返回跳转URL
//        return "https://invoice-platform.com/apply?requestId=" + request.getRequestId();
//    }
//
//    // 申请通过后处理
//    public void approveInvoice(String requestId) {
//        stateMachine.sendEvent(InvoiceEvent.APPROVE);
//        // 发送发票文件和信息到邮箱
//        emailService.sendInvoiceEmail(requestId, "invoice.pdf");
//    }
//
//    // 定时任务轮询发票状态
//    @PostConstruct
//    public void scheduleStatusSync() {
//        taskScheduler.scheduleAtFixedRate(() -> {
//            // 假设从外部平台获取状态
//            String externalStatus = fetchInvoiceStatusFromPlatform();
//            syncInvoiceStatus(externalStatus);
//        }, Duration.ofMinutes(5)); // 每5分钟轮询一次
//    }
//
//    // 同步状态
//    private void syncInvoiceStatus(String externalStatus) {
//        if ("ISSUED".equals(externalStatus) && stateMachine.getState().getId() == InvoiceState.APPLIED) {
//            stateMachine.sendEvent(InvoiceEvent.APPROVE);
//        }
//        // 其他状态同步逻辑可根据需要扩展
//    }
//
//    // 红冲发票
//    public void cancelInvoice() {
//        if (stateMachine.getState().getId() == InvoiceState.ISSUED) {
//            stateMachine.sendEvent(InvoiceEvent.CANCEL);
//        } else {
//            throw new IllegalStateException("当前状态不支持红冲");
//        }
//    }
//
//    // 重开发票
//    public void reissueInvoice() {
//        if (stateMachine.getState().getId() == InvoiceState.CANCELLED) {
//            stateMachine.sendEvent(InvoiceEvent.REISSUE);
//        } else {
//            throw new IllegalStateException("当前状态不支持重开票");
//        }
//    }
//
//    // 模拟从外部平台获取状态
//    private String fetchInvoiceStatusFromPlatform() {
//        // 这里可以用 HTTP 调用真实平台API
//        return "ISSUED"; // 模拟返回
//    }
//}