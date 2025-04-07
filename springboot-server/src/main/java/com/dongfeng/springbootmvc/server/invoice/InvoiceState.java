package com.dongfeng.springbootmvc.server.invoice;

// 发票状态
public enum InvoiceState {
    INITIAL,        // 初始状态（未申请）
    APPLIED,        // 已申请
    ISSUED,         // 已开票
    CANCELLED       // 已红冲
}
