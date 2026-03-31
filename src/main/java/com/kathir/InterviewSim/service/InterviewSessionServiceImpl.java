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
    private final OllamaService ollamaService;

    private InterviewSessionResponse mapToResponse(InterviewSession session){
        return InterviewSessionResponse.builder()
                .id(session.getId())
                .problemStatement(session.getProblemStatement())
                .conversationLog(session.getConversationLog())
                .score(session.getScore())
                .status(session.getStatus().name())
                .feedback(session.getFeedback())
                .userSolution(session.getUserSolution())
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
        
        // Handle NaN or invalid scores
        if (currentScore == null || currentScore.isNaN() || currentScore.isInfinite()) {
            currentScore = 0.0;
        }
        
        // Calculate average score instead of accumulating
        // Count the number of answers submitted so far
        String[] solutionLines = updatedSolution.split("\n");
        int answerCount = 0;
        for (String line : solutionLines) {
            if (line.trim().length() > 0) {
                answerCount++;
            }
        }
        
        // Calculate running average score
        if (answerCount <= 1) {
            session.setScore(currentScore);
        } else {
            Double previousScore = session.getScore();
            if (previousScore == null || previousScore.isNaN() || previousScore.isInfinite()) {
                previousScore = 0.0;
            }
            double previousTotal = previousScore * (answerCount - 1);
            double newAverage = (previousTotal + currentScore) / answerCount;
            
            // Handle any NaN results and ensure score is between 0-10
            if (Double.isNaN(newAverage) || Double.isInfinite(newAverage)) {
                newAverage = currentScore;
            }
            session.setScore(Math.min(10.0, Math.max(0.0, newAverage)));
        }

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
        
        // Calculate final score based on overall performance
        Double finalScore = calculateFinalScore(session);
        session.setScore(finalScore);
        
        // Generate AI-powered personalized feedback based on actual conversation
        String feedback = ollamaService.generateFinalFeedback(
            session.getProblemStatement(), 
            session.getConversationLog(), 
            finalScore
        );
        session.setFeedback(feedback);
        
        InterviewSession updatedSession=repository.save(session);
        return mapToResponse(updatedSession);
    }
    
    private Double calculateFinalScore(InterviewSession session) {
        String conversationLog = session.getConversationLog();
        if (conversationLog == null || conversationLog.trim().isEmpty()) {
            return 0.0;
        }
        
        // Use the current score as base, but ensure it's reasonable
        Double currentScore = session.getScore();
        if (currentScore == null) {
            currentScore = 0.0;
        }
        
        // Ensure final score is between 0-10
        Double finalScore = Math.min(10.0, Math.max(0.0, currentScore));
        
        // If score seems unreasonable (like 825), calculate based on conversation quality
        if (finalScore > 10.0 || finalScore < 0.0) {
            finalScore = calculateScoreFromConversation(conversationLog);
        }
        
        return finalScore;
    }
    
    private Double calculateScoreFromConversation(String conversationLog) {
        String[] lines = conversationLog.split("\n");
        int candidateResponses = 0;
        boolean hasCode = false;
        boolean hasComplexity = false;
        boolean hasQuestions = false;
        
        for (String line : lines) {
            if (line.startsWith("Candidate:")) {
                candidateResponses++;
                String content = line.toLowerCase();
                if (content.contains("def ") || content.contains("function") || content.contains("class") || 
                    content.contains("{") || content.contains("return")) {
                    hasCode = true;
                }
                if (content.contains("complexity") || content.contains("o(")) {
                    hasComplexity = true;
                }
                if (content.contains("?") || content.contains("clarify")) {
                    hasQuestions = true;
                }
            }
        }
        
        // Calculate score based on conversation quality
        double score = 0.0;
        
        // Base score for participation
        if (candidateResponses > 0) score += 2.0;
        if (candidateResponses >= 3) score += 1.0;
        if (candidateResponses >= 5) score += 1.0;
        
        // Bonus for code
        if (hasCode) score += 3.0;
        
        // Bonus for complexity analysis
        if (hasComplexity) score += 2.0;
        
        // Bonus for asking questions
        if (hasQuestions) score += 1.0;
        
        return Math.min(10.0, score);
    }
    
    private String generatePersonalizedFeedback(InterviewSession session) {
        String conversationLog = session.getConversationLog();
        String[] lines = conversationLog.split("\n");
        
        // Analyze conversation content
        int candidateResponses = 0;
        int interviewerQuestions = 0;
        boolean mentionedComplexity = false;
        boolean providedCode = false;
        boolean askedQuestions = false;
        boolean discussedEdgeCases = false;
        boolean mentionedOptimization = false;
        
        for (String line : lines) {
            if (line.startsWith("Candidate:")) {
                candidateResponses++;
                String content = line.toLowerCase();
                
                if (content.contains("complexity") || content.contains("o(") || content.contains("time") || content.contains("space")) {
                    mentionedComplexity = true;
                }
                if (content.contains("def ") || content.contains("function") || content.contains("class") || 
                    content.contains("{") || content.contains("return") || content.contains("for") || content.contains("while")) {
                    providedCode = true;
                }
                if (content.contains("?") || content.contains("clarify") || content.contains("question") || 
                    content.contains("what if") || content.contains("should i")) {
                    askedQuestions = true;
                }
                if (content.contains("edge") || content.contains("null") || content.contains("empty") || 
                    content.contains("boundary") || content.contains("corner case")) {
                    discussedEdgeCases = true;
                }
                if (content.contains("optimize") || content.contains("improve") || content.contains("better") || 
                    content.contains("efficient") || content.contains("faster")) {
                    mentionedOptimization = true;
                }
            } else if (line.startsWith("Interviewer:")) {
                interviewerQuestions++;
            }
        }
        
        StringBuilder feedback = new StringBuilder();
        feedback.append("Interview Session Summary:\n\n");
        feedback.append("Problem: ").append(session.getProblemStatement()).append("\n");
        feedback.append("Final Score: ").append(String.format("%.1f", session.getScore())).append("/10\n\n");
        
        feedback.append("Performance Analysis:\n");
        
        // Communication analysis based on actual conversation
        if (candidateResponses >= 4 && askedQuestions) {
            feedback.append("- Communication: Excellent engagement with thoughtful questions and detailed explanations\n");
        } else if (candidateResponses >= 3 && askedQuestions) {
            feedback.append("- Communication: Good interaction with some clarifying questions\n");
        } else if (candidateResponses >= 2) {
            feedback.append("- Communication: Adequate responses, could benefit from more questions and elaboration\n");
        } else {
            feedback.append("- Communication: Limited interaction, practice thinking out loud and asking questions\n");
        }
        
        // Problem solving analysis based on actual approach
        if (providedCode && mentionedComplexity && discussedEdgeCases) {
            feedback.append("- Problem Solving: Comprehensive approach with implementation, analysis, and edge case consideration\n");
        } else if (providedCode && mentionedComplexity) {
            feedback.append("- Problem Solving: Good systematic approach with code and complexity analysis\n");
        } else if (providedCode) {
            feedback.append("- Problem Solving: Demonstrated implementation skills, could improve on analysis depth\n");
        } else {
            feedback.append("- Problem Solving: Focus on providing concrete implementations and systematic thinking\n");
        }
        
        // Technical knowledge based on conversation content
        if (mentionedComplexity && mentionedOptimization) {
            feedback.append("- Technical Knowledge: Strong understanding of algorithms, complexity, and optimization\n");
        } else if (mentionedComplexity) {
            feedback.append("- Technical Knowledge: Good grasp of algorithmic complexity concepts\n");
        } else {
            feedback.append("- Technical Knowledge: Could demonstrate deeper technical analysis and complexity understanding\n");
        }
        
        feedback.append("\nAreas for Improvement:\n");
        
        // Specific improvements based on what was missing
        if (!mentionedComplexity) {
            feedback.append("- Practice analyzing and explaining time/space complexity of your solutions\n");
        }
        if (!askedQuestions) {
            feedback.append("- Ask more clarifying questions to better understand problem requirements\n");
        }
        if (!discussedEdgeCases) {
            feedback.append("- Consider and discuss edge cases like empty inputs, null values, or boundary conditions\n");
        }
        if (!providedCode) {
            feedback.append("- Provide concrete code implementations to demonstrate your solutions\n");
        }
        if (!mentionedOptimization) {
            feedback.append("- Discuss potential optimizations and alternative approaches to your solution\n");
        }
        
        feedback.append("\nStrengths Demonstrated:\n");
        
        // Highlight actual strengths from the conversation
        if (candidateResponses >= 4) {
            feedback.append("- Active participation and thorough engagement throughout the interview\n");
        }
        if (providedCode) {
            feedback.append("- Ability to translate algorithmic thinking into working code\n");
        }
        if (mentionedComplexity) {
            feedback.append("- Understanding of algorithmic complexity and performance analysis\n");
        }
        if (askedQuestions) {
            feedback.append("- Good communication skills with appropriate clarifying questions\n");
        }
        if (discussedEdgeCases) {
            feedback.append("- Thoughtful consideration of edge cases and robustness\n");
        }
        if (session.getScore() >= 7) {
            feedback.append("- Strong overall technical performance and problem-solving approach\n");
        } else if (session.getScore() >= 4) {
            feedback.append("- Solid foundation with room for growth and improvement\n");
        }
        
        // Overall assessment based on actual performance
        String performance;
        if (session.getScore() >= 8) {
            performance = "Excellent";
        } else if (session.getScore() >= 6) {
            performance = "Strong";
        } else if (session.getScore() >= 4) {
            performance = "Good";
        } else {
            performance = "Needs improvement";
        }
        
        feedback.append("\nOverall Assessment: ").append(performance).append(" performance. ");
        
        if (session.getScore() >= 8) {
            feedback.append("Outstanding work! You demonstrated strong technical skills and excellent communication.");
        } else if (session.getScore() >= 6) {
            feedback.append("Great job! Continue practicing the areas mentioned above to reach the next level.");
        } else if (session.getScore() >= 4) {
            feedback.append("Good effort! Focus on the improvement areas to strengthen your interview skills.");
        } else {
            feedback.append("Keep practicing! Work on the fundamentals and you'll see significant improvement.");
        }
        
        return feedback.toString();
    }
}
