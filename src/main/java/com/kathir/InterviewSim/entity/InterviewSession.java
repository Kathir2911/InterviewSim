package com.kathir.InterviewSim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interview_session")
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "Text")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String conversationLog;

    @Column(columnDefinition = "TEXT")
    private String userSolution;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    public enum Status{
        ONGOING,
        COMPLETED,
        CANCELLED
    }
}
