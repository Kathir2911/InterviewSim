package com.kathir.InterviewSim.service;

import org.springframework.stereotype.Service;
import com.kathir.InterviewSim.service.ScoreGeneratorService;
@Service
public class ScoreGeneratorServiceImpl implements ScoreGeneratorService {
    @Override
    public Double evaluateAnswer(String problemStatement,String answer,String conversationLog){
        if(answer == null || answer.isEmpty()){
            return 0.0;
        }
        if(answer.length() > 20){
            return 5.0;
        }
        return 2.0;
    }
}
