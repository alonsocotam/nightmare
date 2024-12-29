package com.dashboard.nightmare.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.dashboard.nightmare.domain.GoogleResponse;
import com.dashboard.nightmare.domain.Item;
import com.dashboard.nightmare.domain.ReceiptData;
import com.dashboard.nightmare.util.ReceiptGenerator;

@Service
public class ReceiptService {

    private final ReceiptGenerator receiptGenerator;
    private final GoogleDriveService googleDriveService;
    private final GoogleSheetsService googleSheetsService;

    public ReceiptService(ReceiptGenerator receiptGenerator, GoogleDriveService googleDriveService,
            GoogleSheetsService googleSheetsService) {
        this.receiptGenerator = receiptGenerator;
        this.googleDriveService = googleDriveService;
        this.googleSheetsService = googleSheetsService;
    }

    public GoogleResponse generateReceipt(ReceiptData receiptData) {
        File tempPDF = receiptGenerator.createPdf(receiptData);
        var response = googleDriveService.uploadFile(tempPDF);

        if (response.getStatus() != 500) {
            createRowToSave(receiptData);
        }

        tempPDF.delete();

        return response;
    }

    public List<com.google.api.services.drive.model.File> getReceipts() {
        return googleDriveService.getFilesFromGoogleDrive();
    }

    public void createRowToSave(ReceiptData receiptData) {
        for (Item item : receiptData.getItems()) {
            double total = item.getQuantity() * item.getPrice();
            List<Object> row = List.of(
                    receiptData.getClient(),
                    receiptData.getDate(),
                    item.getQuantity(),
                    item.getName(),
                    item.getPrice(),
                    total);
            googleSheetsService.appendDataToNextRow(row);
        }
    }

}