//package com.dongfeng.springbootmvc.server.invoice;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/invoice")
//public class InvoiceController {
//
//    @Autowired
//    private InvoiceService invoiceService;
//
//    // 提交发票申请
//    @PostMapping("/apply")
//    public ResponseEntity<String> applyForInvoice(@RequestBody InvoiceRequest request) {
//        String url = invoiceService.submitInvoiceApplication(request);
//        return ResponseEntity.ok(url);
//    }
//
//    // 红冲发票
//    @PostMapping("/cancel")
//    public ResponseEntity<String> cancelInvoice() {
//        invoiceService.cancelInvoice();
//        return ResponseEntity.ok("发票已红冲");
//    }
//
//    // 重开发票
//    @PostMapping("/reissue")
//    public ResponseEntity<String> reissueInvoice() {
//        invoiceService.reissueInvoice();
//        return ResponseEntity.ok("发票已重开");
//    }
//}