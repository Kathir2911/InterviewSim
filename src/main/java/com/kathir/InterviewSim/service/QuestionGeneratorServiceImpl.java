package com.kathir.InterviewSim.service;

import lombok.RequiredArgsConstructor;
import com.kathir.InterviewSim.service.QuestionGeneratorService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionGeneratorServiceImpl implements QuestionGeneratorService {
    
    private final OllamaService ollamaService;
    
    @Override
    public String generateNextQuestion(String problemStatement, String conversationLog) {
        return ollamaService.generateInterviewQuestion(problemStatement, conversationLog);
    }
}
