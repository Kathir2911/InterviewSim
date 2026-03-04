package com.kathir.InterviewSim.service;

import lombok.RequiredArgsConstructor;
import com.kathir.InterviewSim.service.QuestionGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class QuestionGeneratorServiceImpl implements QuestionGeneratorService {
    @Override
    public String generateNextQuestion(String problemStatement,String conversationLog){
        if(conversationLog.contains("Time complexity")){
            return "Can you implement the algorithm";
        }
        return "What is the time complexity of this approach?";
    }
}
