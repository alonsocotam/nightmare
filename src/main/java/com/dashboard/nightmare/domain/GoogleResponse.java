package com.dashboard.nightmare.domain;

import lombok.Data;

@Data
public class GoogleResponse {
    private int status;
    private String message;
    private String url;
}
