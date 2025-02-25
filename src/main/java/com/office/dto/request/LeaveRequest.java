package com.office.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LeaveRequest {
    private Integer type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
} 