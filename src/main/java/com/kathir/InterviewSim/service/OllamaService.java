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
        // Check if we're in a repetitive loop
        if (isRepetitivePattern(conversationHistory)) {
            return generateProgressiveQuestion(problemStatement, conversationHistory);
        }
        
        String prompt = buildQuestionPrompt(problemStatement, conversationHistory);
        return generateResponse(prompt);
    }
    
    private boolean isRepetitivePattern(String conversationHistory) {
        if (conversationHistory == null || conversationHistory.trim().isEmpty()) {
            return false;
        }
        
        String[] lines = conversationHistory.split("\n");
        if (lines.length < 4) return false;
        
        // Check if the last two interviewer questions are similar
        String lastQuestion = null;
        String secondLastQuestion = null;
        
        for (int i = lines.length - 1; i >= 0; i--) {
            if (lines[i].startsWith("Interviewer:")) {
                if (lastQuestion == null) {
                    lastQuestion = lines[i].toLowerCase();
                } else if (secondLastQuestion == null) {
                    secondLastQuestion = lines[i].toLowerCase();
                    break;
                }
            }
        }
        
        if (lastQuestion != null && secondLastQuestion != null) {
            // Check for similar keywords or phrases
            String[] lastWords = lastQuestion.split("\\s+");
            String[] secondLastWords = secondLastQuestion.split("\\s+");
            
            int commonWords = 0;
            for (String word : lastWords) {
                if (word.length() > 3) { // Only check meaningful words
                    for (String otherWord : secondLastWords) {
                        if (word.equals(otherWord)) {
                            commonWords++;
                        }
                    }
                }
            }
            
            // If more than 50% of meaningful words are common, it's likely repetitive
            return commonWords > Math.max(lastWords.length, secondLastWords.length) * 0.5;
        }
        
        return false;
    }
    
    private String generateProgressiveQuestion(String problemStatement, String conversationHistory) {
        // Force progression to next phase when stuck in repetitive pattern
        String[] lines = conversationHistory.split("\n");
        boolean hasCode = false;
        boolean hasComplexity = false;
        boolean hasEdgeCases = false;
        
        for (String line : lines) {
            if (line.startsWith("Candidate:")) {
                String content = line.toLowerCase();
                if (content.contains("def ") || content.contains("function") || content.contains("return") || 
                    content.contains("algorithm") || content.contains("loop") || content.contains("array")) {
                    hasCode = true;
                }
                if (content.contains("o(") || content.contains("complexity") || content.contains("time") || content.contains("space")) {
                    hasComplexity = true;
                }
                if (content.contains("edge") || content.contains("null") || content.contains("empty") || content.contains("boundary")) {
                    hasEdgeCases = true;
                }
            }
        }
        
        // Force progression based on what's been covered
        if (!hasCode) {
            return "I see we've been discussing the approach. Let's move forward - can you show me some code or pseudocode for your solution?";
        } else if (!hasComplexity) {
            return "Thanks for the implementation! Now let's analyze the efficiency - what's the time and space complexity?";
        } else if (!hasEdgeCases) {
            return "Good complexity analysis! Let's think about robustness - what edge cases should we handle?";
        } else {
            return "Excellent work! You've covered the key aspects. Are there any optimizations or alternative approaches you'd consider?";
        }
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
                    double score = Double.parseDouble(scoreStr);
                    // Ensure score is between 0 and 10
                    return Math.min(10.0, Math.max(0.0, score));
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
        // Analyze conversation to understand what has been covered
        String[] lines = conversationHistory.split("\n");
        boolean hasAskedClarification = false;
        boolean hasProvidedSolution = false;
        boolean hasDiscussedComplexity = false;
        boolean hasDiscussedEdgeCases = false;
        boolean hasDiscussedOptimization = false;
        int questionCount = 0;
        
        for (String line : lines) {
            if (line.startsWith("Interviewer:")) {
                questionCount++;
                String content = line.toLowerCase();
                if (content.contains("clarify") || content.contains("understand") || content.contains("explain")) {
                    hasAskedClarification = true;
                }
                if (content.contains("complexity") || content.contains("time") || content.contains("space")) {
                    hasDiscussedComplexity = true;
                }
                if (content.contains("edge") || content.contains("corner") || content.contains("boundary")) {
                    hasDiscussedEdgeCases = true;
                }
                if (content.contains("optimize") || content.contains("improve") || content.contains("better")) {
                    hasDiscussedOptimization = true;
                }
            } else if (line.startsWith("Candidate:")) {
                String content = line.toLowerCase();
                if (content.contains("def ") || content.contains("function") || content.contains("class") || 
                    content.contains("{") || content.contains("return") || content.contains("algorithm")) {
                    hasProvidedSolution = true;
                }
            }
        }
        
        return String.format("""
            You are an experienced technical interviewer conducting a coding interview. 
            
            Problem Statement: %s
            
            Conversation History: %s
            
            Interview Progress Analysis:
            - Questions asked so far: %d
            - Clarification phase completed: %s
            - Solution provided: %s
            - Complexity discussed: %s
            - Edge cases discussed: %s
            - Optimization discussed: %s
            
            Based on the conversation and progress analysis, generate the NEXT LOGICAL question in the interview sequence. 
            
            Interview Flow Guidelines:
            1. START: If no questions asked yet, ask the candidate to explain their understanding of the problem
            2. CLARIFICATION: If understanding not clear, ask clarifying questions about requirements
            3. APPROACH: If problem understood, ask about their approach/algorithm choice
            4. IMPLEMENTATION: If approach discussed, ask them to implement or walk through the code
            5. COMPLEXITY: If solution provided, ask about time/space complexity analysis
            6. EDGE CASES: If complexity covered, discuss edge cases and error handling
            7. OPTIMIZATION: If edge cases covered, discuss potential optimizations
            8. WRAP UP: If all covered, ask final questions or provide encouragement
            
            CRITICAL RULES:
            - DO NOT repeat questions that have already been asked
            - DO NOT ask about topics already thoroughly discussed
            - Progress naturally through the interview phases
            - If the candidate seems stuck, provide hints rather than repeating the same question
            - Keep questions concise and focused
            
            Generate only the next appropriate question/response, no additional formatting:
            """, problemStatement, conversationHistory, questionCount, hasAskedClarification, 
                hasProvidedSolution, hasDiscussedComplexity, hasDiscussedEdgeCases, hasDiscussedOptimization);
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
        // Extract conversation history to avoid repetition
        String conversationHistory = "";
        if (prompt.contains("Conversation History:")) {
            int start = prompt.indexOf("Conversation History:") + "Conversation History:".length();
            int end = prompt.indexOf("Interview Progress Analysis:");
            if (end == -1) end = prompt.indexOf("Based on the conversation");
            if (end > start) {
                conversationHistory = prompt.substring(start, end).trim();
            }
        }
        
        // Count questions and analyze what's been covered
        String[] lines = conversationHistory.split("\n");
        int questionCount = 0;
        boolean hasAskedUnderstanding = false;
        boolean hasAskedApproach = false;
        boolean hasAskedImplementation = false;
        boolean hasAskedComplexity = false;
        boolean hasAskedEdgeCases = false;
        boolean hasAskedOptimization = false;
        
        for (String line : lines) {
            if (line.startsWith("Interviewer:")) {
                questionCount++;
                String content = line.toLowerCase();
                if (content.contains("understand") || content.contains("explain") || content.contains("problem")) {
                    hasAskedUnderstanding = true;
                }
                if (content.contains("approach") || content.contains("algorithm") || content.contains("strategy")) {
                    hasAskedApproach = true;
                }
                if (content.contains("implement") || content.contains("code") || content.contains("write")) {
                    hasAskedImplementation = true;
                }
                if (content.contains("complexity") || content.contains("time") || content.contains("space")) {
                    hasAskedComplexity = true;
                }
                if (content.contains("edge") || content.contains("corner") || content.contains("boundary")) {
                    hasAskedEdgeCases = true;
                }
                if (content.contains("optimize") || content.contains("improve") || content.contains("better")) {
                    hasAskedOptimization = true;
                }
            }
        }
        
        // Generate next logical question based on what hasn't been covered
        if (questionCount == 0 || !hasAskedUnderstanding) {
            return "Let's start with understanding the problem. Can you explain what you think this problem is asking for in your own words?";
        } else if (!hasAskedApproach) {
            return "Great! Now, what approach or algorithm would you use to solve this problem?";
        } else if (!hasAskedImplementation) {
            return "That sounds like a good approach. Can you walk me through how you would implement this solution?";
        } else if (!hasAskedComplexity) {
            return "Excellent! Now, what would be the time and space complexity of your solution?";
        } else if (!hasAskedEdgeCases) {
            return "Good analysis! What edge cases should we consider for this problem?";
        } else if (!hasAskedOptimization) {
            return "Nice work on considering edge cases. Are there any ways we could optimize this solution further?";
        } else {
            // All main topics covered, provide encouragement or ask follow-up
            String[] encouragements = {
                "Excellent work! You've covered all the key aspects. Is there anything else you'd like to discuss about this solution?",
                "Great job! You've demonstrated a solid understanding. Any final thoughts on alternative approaches?",
                "Well done! You've shown good problem-solving skills. Would you like to discuss any trade-offs in your solution?"
            };
            return encouragements[questionCount % encouragements.length];
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