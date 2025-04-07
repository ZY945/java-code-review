package com.dongfeng.springbootmvc.server.invoice;

// 发票事件
public enum InvoiceEvent {
    SUBMIT,         // 提交申请
    APPROVE,        // 申请通过
    CANCEL,         // 红冲
    REISSUE         // 重开票
}