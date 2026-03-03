package com.kathir.InterviewSim.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewSessionResponse {
    private Long id;
    private String problemStatement;
    private String conversationLog;
    private Integer score;
    private String status;
    private LocalDateTime createdAt;
}
