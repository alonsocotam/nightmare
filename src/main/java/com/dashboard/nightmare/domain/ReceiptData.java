package com.dashboard.nightmare.domain;

import java.util.List;

import lombok.Data;

@Data
public class ReceiptData {
    private String date;
    private String client;
    private List<Item> items;
}
