package com.kathir.InterviewSim.service;
import com.kathir.InterviewSim.entity.*;
public interface InterviewSessionService {
    InterviewSession startSession(String problemStatement);
    InterviewSession getSession(Long id);
    InterviewSession submitAnswer(Long sessionId,String answer);
}
