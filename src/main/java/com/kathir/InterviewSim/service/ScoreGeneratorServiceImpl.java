package com.kathir.InterviewSim.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.kathir.InterviewSim.service.ScoreGeneratorService;

@Service
@RequiredArgsConstructor
public class ScoreGeneratorServiceImpl implements ScoreGeneratorService {
    
    private final OllamaService ollamaService;
    
    @Override
    public Double evaluateAnswer(String problemStatement, String answer, String conversationLog) {
        if (answer == null || answer.isEmpty()) {
            return 0.0;
        }
        return ollamaService.evaluateAnswer(problemStatement, answer, conversationLog);
    }
}
