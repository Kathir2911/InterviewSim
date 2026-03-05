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
    private final QuestionGeneratorService questionGeneratorService;
    private final ScoreGeneratorService scoreGeneratorService;

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
        String firstQuestion=questionGeneratorService.generateNextQuestion(problemStatement,"");
        InterviewSession session=InterviewSession.builder().
                problemStatement(problemStatement).
                conversationLog("Interview Started...\nInterviewer:"+firstQuestion).
                status(InterviewSession.Status.ONGOING).
                createdAt(LocalDateTime.now()).
                score(0.0).
                build();
        InterviewSession savedSession=repository.save(session);
        return mapToResponse(savedSession);
    }

    @Override
    public InterviewSessionResponse submitAnswer(Long sessionId,String answer){
        InterviewSession session=repository.findById(sessionId).orElseThrow(()->new RuntimeException("Session not found."));
        if(session.getStatus()!= InterviewSession.Status.ONGOING){
            throw new RuntimeException("Interview is already finished.");
        }
        String previousSolution=session.getUserSolution();
        if(previousSolution==null) previousSolution="";
        String updatedSolution=previousSolution+"\n"+answer;
        session.setUserSolution(updatedSolution);

        Double currentScore=scoreGeneratorService.evaluateAnswer(
                session.getProblemStatement(),
                answer,
                session.getConversationLog()
        );
        session.setScore(session.getScore()+currentScore);

        String nextQuestion = questionGeneratorService.generateNextQuestion(
                session.getProblemStatement(),
                session.getConversationLog()
        );
        String updatedLog=session.getConversationLog()+"\nCandidate: "+answer+"\nInterviewer: "+nextQuestion;
        session.setConversationLog(updatedLog);

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
