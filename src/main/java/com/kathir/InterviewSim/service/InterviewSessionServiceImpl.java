package com.kathir.InterviewSim.service;
import com.kathir.InterviewSim.entity.*;
import com.kathir.InterviewSim.repository.*;
import com.kathir.InterviewSim.service.InterviewSessionService;
import com.kathir.InterviewSim.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterviewSessionServiceImpl implements InterviewSessionService {
    private final InterviewSessionRepository repository;

    private InterviewSessionResponse mapToResponse(InterviewSession session){
        return InterviewSessionResponse.builder()
                .id(session.getId())
                .problemStatement(session.getProblemStatement())
                .conversationLog(session.getConversationLog())
                .score(session.getScore())
                .status(session.getStatus().name())
                .createdAt(session.getCreatedAt()).build();
    }

    @Override
    public InterviewSessionResponse startSession(String problemStatement){
        InterviewSession session=InterviewSession.builder().
                problemStatement(problemStatement).
                conversationLog("Interview Started...\n").
                status(InterviewSession.Status.ONGOING).
                createdAt(LocalDateTime.now()).
                build();
        InterviewSession savedSession=repository.save(session);
        return mapToResponse(savedSession);
    }

    @Override
    public InterviewSessionResponse submitAnswer(Long sessionId,String answer){
        InterviewSession session=repository.findById(sessionId).orElseThrow(()->new RuntimeException("Session not found."));
        String updatedLog=session.getConversationLog()+"\nCandidate: "+answer+"\nInterviewer: Thank you Let us continue.";
        session.setConversationLog(updatedLog);
        session.setUserSolution(answer);
        InterviewSession updatedSession=repository.save(session);
        return mapToResponse(updatedSession);
    }

    @Override
    public InterviewSessionResponse getSession(Long sessionId){
        InterviewSession session=repository.findById(sessionId).orElseThrow(()->new RuntimeException("Session not found."));
        return mapToResponse(session);
    }

    @Override
    public InterviewSessionResponse cancelSession(Long sessionId){
        InterviewSession session=repository.findById(sessionId).orElseThrow(()->new RuntimeException("session not found"));
        session.setStatus(InterviewSession.Status.CANCELLED);
        InterviewSession updatedSession=repository.save(session);
        return mapToResponse(updatedSession);
    }

    @Override
    public InterviewSessionResponse endSession(Long sessionId){
        InterviewSession session=repository.findById(sessionId).orElseThrow(()->new RuntimeException("Session not found"));
        session.setStatus(InterviewSession.Status.COMPLETED);
        InterviewSession updatedSession=repository.save(session);
        return mapToResponse(updatedSession);

    }
}
