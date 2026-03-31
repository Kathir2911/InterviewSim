package com.kathir.InterviewSim.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {
    
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.model}")
    private String ollamaModel;
    
    @Value("${ollama.timeout}")
    private int timeoutSeconds;
    
    public String generateResponse(String prompt) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(ollamaBaseUrl)
                    .build();
            
            Map<String, Object> requestBody = Map.of(
                    "model", ollamaModel,
                    "prompt", prompt,
                    "stream", false
            );
            
            String response = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("response").asText();
            
        } catch (Exception e) {
            log.error("Error calling Ollama API: {}", e.getMessage());
            
            // Fallback response when Ollama is not available
            if (prompt.toLowerCase().contains("question") || prompt.toLowerCase().contains("interview")) {
                return generateFallbackQuestion(prompt);
            } else if (prompt.toLowerCase().contains("evaluate") || prompt.toLowerCase().contains("score")) {
                return "Score: 6.0\nReasoning: Unable to connect to AI service for detailed evaluation. Default score provided.";
            } else {
                return generateFallbackFeedback(prompt);
            }
        }
    }
    
    public String generateInterviewQuestion(String problemStatement, String conversationHistory) {
        String prompt = buildQuestionPrompt(problemStatement, conversationHistory);
        return generateResponse(prompt);
    }
    
    public Double evaluateAnswer(String problemStatement, String answer, String conversationHistory) {
        String prompt = buildEvaluationPrompt(problemStatement, answer, conversationHistory);
        String response = generateResponse(prompt);
        
        try {
            // Extract score from response (expecting format like "Score: 8.5")
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.toLowerCase().contains("score:")) {
                    String scoreStr = line.replaceAll("[^0-9.]", "");
                    return Double.parseDouble(scoreStr);
                }
            }
            return 5.0; // Default score if parsing fails
        } catch (Exception e) {
            log.warn("Could not parse score from response: {}", response);
            return 5.0;
        }
    }
    
    public String generateFinalFeedback(String problemStatement, String conversationHistory, Double finalScore) {
        String prompt = buildFeedbackPrompt(problemStatement, conversationHistory, finalScore);
        return generateResponse(prompt);
    }
    
    private String buildQuestionPrompt(String problemStatement, String conversationHistory) {
        return String.format("""
            You are an experienced technical interviewer conducting a coding interview. 
            
            Problem Statement: %s
            
            Conversation History: %s
            
            Based on the conversation so far, generate the next appropriate question or response as an interviewer would. 
            
            Guidelines:
            - If this is the start of the interview, ask clarifying questions about the problem
            - If the candidate has provided a solution, ask about time/space complexity, edge cases, or optimizations
            - If they're stuck, provide gentle hints
            - Keep questions focused and professional
            - Encourage the candidate to think out loud
            
            Respond only with the question/response, no additional formatting:
            """, problemStatement, conversationHistory);
    }
    
    private String buildEvaluationPrompt(String problemStatement, String answer, String conversationHistory) {
        return String.format("""
            You are evaluating a candidate's response in a technical interview.
            
            Problem Statement: %s
            
            Candidate's Answer: %s
            
            Conversation Context: %s
            
            Evaluate this answer on a scale of 0-10 considering:
            - Correctness of the approach
            - Code quality and clarity
            - Understanding of concepts
            - Communication skills
            - Problem-solving approach
            
            Provide your evaluation in this format:
            Score: [0-10 number]
            Reasoning: [Brief explanation of the score]
            """, problemStatement, answer, conversationHistory);
    }
    
    private String buildFeedbackPrompt(String problemStatement, String conversationHistory, Double finalScore) {
        return String.format("""
            You are an experienced technical interviewer providing detailed feedback on a coding interview.
            
            Problem Statement: %s
            
            Complete Interview Conversation: %s
            
            Final Score: %.1f/10
            
            Based on the actual conversation above, provide a comprehensive interview feedback report with the following structure:
            
            Interview Session Summary:
            
            Problem: [Restate the problem briefly]
            Final Score: [Score]/10
            
            Performance Analysis:
            - Communication: [Analyze how well the candidate communicated their thoughts, asked clarifying questions, and explained their approach based on the actual conversation]
            - Problem Solving: [Evaluate their problem-solving methodology, approach to breaking down the problem, and logical thinking based on their actual responses]
            - Technical Knowledge: [Assess their technical understanding, use of appropriate algorithms/data structures, and coding concepts demonstrated in the conversation]
            - Code Quality: [If code was provided, evaluate its correctness, efficiency, and style]
            
            Areas for Improvement:
            [Provide 3-4 specific, actionable recommendations based on what was actually missing or could be improved in this specific interview conversation]
            
            Strengths Demonstrated:
            [Highlight 2-3 specific positive aspects observed in this interview]
            
            Overall Assessment: [Provide a personalized summary based on the actual performance - Strong/Good/Needs improvement] performance. [Add specific encouragement or next steps based on their actual responses]
            
            Make sure your feedback is:
            1. Specific to this actual conversation
            2. Constructive and actionable
            3. Balanced (highlighting both strengths and areas for improvement)
            4. Professional and encouraging
            """, problemStatement, conversationHistory, finalScore);
    }
    
    private String generateFallbackQuestion(String prompt) {
        if (prompt.contains("complexity")) {
            return "Great! Now can you walk me through your solution step by step and explain how you would implement it?";
        } else if (prompt.contains("implement")) {
            return "Excellent! Can you also discuss the time and space complexity of your approach?";
        } else {
            return "Let's start with understanding the problem. Can you explain what you think this problem is asking for and what approach you would take?";
        }
    }
    
    private String generateFallbackFeedback(String prompt) {
        return """
            Interview Session Summary:
            
            Problem: Technical coding problem
            Final Score: 6.0/10
            
            Performance Analysis:
            - Communication: Good engagement throughout the interview
            - Problem Solving: Demonstrated systematic approach to problem-solving
            - Technical Knowledge: Showed understanding of key programming concepts
            - Code Quality: Provided working solution with room for optimization
            
            Areas for Improvement:
            - Practice explaining algorithmic complexity analysis
            - Work on optimizing solutions for better performance
            - Consider discussing edge cases and error handling
            - Practice coding under time pressure
            
            Strengths Demonstrated:
            - Clear communication of thought process
            - Ability to break down complex problems
            - Good understanding of programming fundamentals
            
            Overall Assessment: Good performance. Continue practicing coding problems and focus on the improvement areas mentioned above. Keep up the good work!
            
            Note: This feedback was generated in offline mode. For personalized AI-powered feedback, please ensure Ollama is running and accessible.
            """;
    }
}