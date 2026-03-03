package com.kathir.InterviewSim.service;
import com.kathir.InterviewSim.entity.*;
import com.kathir.InterviewSim.repository.*;
import com.kathir.InterviewSim.service.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class InterviewSessionServiceImpl implements InterviewSessionService {
    private final InterviewSessionRepository repository;
    public InterviewSession startSession(String problemStatement){
        InterviewSession session=InterviewSession.builder().
                problemStatement(problemStatement).
                conversationLog("Interview Started...\n").
                status(InterviewSession.Status.ONGOING).
                createdAt(LocalDateTime.now()).
                build();
    }
    public InterviewSession submitAnswer(Long id,String answer){

    }
    public InterviewSession getSession(Long sessionId){

    }
}
