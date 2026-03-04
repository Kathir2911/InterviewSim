package com.kathir.InterviewSim.service;
import com.kathir.InterviewSim.dto.*;
public interface InterviewSessionService {
    InterviewSessionResponse startSession(String problemStatement);
    InterviewSessionResponse getSession(Long sessionId);
    InterviewSessionResponse submitAnswer(Long sessionId,String answer);
    InterviewSessionResponse endSession(Long sessionId);
    InterviewSessionResponse cancelSession(Long sessionId);
}
