package com.office.dto.request;

import lombok.Data;

@Data
public class TaskStatusRequest {
    private int status;
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
} 