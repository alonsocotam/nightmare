package com.dashboard.nightmare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dashboard.nightmare.domain.GoogleResponse;
import com.dashboard.nightmare.domain.ReceiptData;
import com.dashboard.nightmare.service.ReceiptService;
import com.google.api.services.drive.model.File;



@RestController
@CrossOrigin
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }
    
    @GetMapping("/receipt")
    public String getReceipts() {
        return "Hello";
    }

    @PostMapping("/generate")
    public ResponseEntity<GoogleResponse> generateReceipt(@RequestBody ReceiptData receiptData) {
        return ResponseEntity.ok().body(receiptService.generateReceipt(receiptData));
    }

    @GetMapping("/receipts")
    public ResponseEntity<List<File>> getAllReceipts() {
        return ResponseEntity.ok().body(receiptService.getReceipts());
    }
    
}
