package com.kathir.InterviewSim.service;
import com.kathir.InterviewSim.dto.*;
public interface InterviewSessionService {
    InterviewSessionResponse startSession(String problemStatement);
    InterviewSessionResponse getSession(Long id);
    InterviewSessionResponse submitAnswer(Long sessionId,String answer);
}
