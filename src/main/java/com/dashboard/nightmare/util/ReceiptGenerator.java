package com.dashboard.nightmare.util;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Component;

import com.dashboard.nightmare.domain.ReceiptData;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class ReceiptGenerator {

    public File createPdf(ReceiptData receiptData) {
        File pdf = new File(receiptData.getClient() + "-" + receiptData.getDate());

        try {
            final double[] totalAmount = { 0.0 };
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdf));
            document.open();

            // Header Section
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10f);
            headerTable.setSpacingAfter(10f);

            PdfPCell titleCell = new PdfPCell(
                    new Phrase("Nota de venta", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK)));
            titleCell.setBorder(PdfPCell.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(titleCell);

            String imagePath = "/nightmare-logo.png";
            Image logo = Image.getInstance(getClass().getResource(imagePath));            
            logo.scaleToFit(80, 80);
            PdfPCell imageCell = new PdfPCell(logo);
            imageCell.setBorder(PdfPCell.NO_BORDER);
            imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            imageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(imageCell);

            document.add(headerTable);

            // Separator Line
            PdfPTable separator = new PdfPTable(1);
            separator.setWidthPercentage(100);
            PdfPCell line = new PdfPCell(new Phrase(" "));
            line.setBorderWidthBottom(2f);
            line.setBorderColorBottom(BaseColor.GRAY);
            line.setBorder(PdfPCell.BOTTOM);
            line.setFixedHeight(10f);
            separator.addCell(line);
            document.add(separator);

            // Client Information
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            document.add(new Paragraph("Cliente: " + receiptData.getClient(), bodyFont));
            document.add(new Paragraph("Fecha: " + receiptData.getDate(), bodyFont));
            document.add(new Paragraph(" "));

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Table Headers
            PdfPCell[] headers = {
                    new PdfPCell(new Phrase("Cantidad", bodyFont)),
                    new PdfPCell(new Phrase("Producto", bodyFont)),
                    new PdfPCell(new Phrase("Precio Unitario", bodyFont)),
                    new PdfPCell(new Phrase("Precio Total", bodyFont))
            };
            for (PdfPCell header : headers) {
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(5f);
                table.addCell(header);
            }

            // Table Content
            receiptData.getItems().forEach(item -> {
                PdfPCell[] cells = {
                        new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont)),
                        new PdfPCell(new Phrase(item.getName(), bodyFont)),
                        new PdfPCell(new Phrase(String.valueOf(item.getPrice()), bodyFont)),
                        new PdfPCell(new Phrase(String.valueOf(item.getPrice() * item.getQuantity()), bodyFont))
                };
                for (int i = 0; i < cells.length; i++) {
                    cells[i].setPadding(5f);
                    if (table.size() % 2 == 0) {
                        cells[i].setBackgroundColor(BaseColor.WHITE);
                    } else {
                        cells[i].setBackgroundColor(new BaseColor(240, 240, 240));
                    }
                    table.addCell(cells[i]);
                }
                totalAmount[0] += item.getPrice() * item.getQuantity();
            });

            document.add(table);

            // Total Amount
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph totalParagraph = new Paragraph("Total a pagar: " + totalAmount[0], totalFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Signature Section
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(40f);
            signatureTable.setSpacingAfter(10f);

            PdfPCell[] signatureCells = {
                    new PdfPCell(new Phrase("________________________\nFirma de Entrega", bodyFont)),
                    new PdfPCell(new Phrase("________________________\nFirma de Aceptado", bodyFont))
            };
            for (int i = 0; i < signatureCells.length; i++) {
                signatureCells[i].setBorder(PdfPCell.NO_BORDER);
                signatureCells[i].setHorizontalAlignment(i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
                signatureTable.addCell(signatureCells[i]);
            }

            document.add(signatureTable);

            document.close();
            return pdf;
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
