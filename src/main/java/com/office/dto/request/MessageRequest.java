package com.office.dto.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Integer receiverId;
    private String title;
    private String content;
} 