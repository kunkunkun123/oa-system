package com.office.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private Integer receiverId;
    private String title;
    private String content;
} 