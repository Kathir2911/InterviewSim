package com.kathir.InterviewSim.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class InterviewMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 3000)
    private String content;

    private LocalDateTime timestamp;

    public enum Role {
        INTERVIEWER,
        CANDIDATE
    }
}
