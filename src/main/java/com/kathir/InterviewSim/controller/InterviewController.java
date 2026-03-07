package com.kathir.InterviewSim.controller;
import com.kathir.InterviewSim.service.InterviewSessionService;
import com.kathir.InterviewSim.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/interview")
public class InterviewController {
    private final InterviewSessionService service;

    @PostMapping("/start")
    public InterviewSessionResponse startInterview(@RequestBody StartInterviewRequest request){
        return service.startSession(request.getProblemStatement());
    }

    @PostMapping("/{id}/answer")
    public InterviewSessionResponse submitAnswer(@PathVariable Long id,@RequestBody SubmitAnswerRequest request){
        return service.submitAnswer(id,request.getAnswer());
    }

    @GetMapping("/{id}")
    public InterviewSessionResponse getSession(@PathVariable Long id) {
        return service.getSession(id);
    }

    @PutMapping("/{id}/end")
    public InterviewSessionResponse endSession(@PathVariable Long id){
        return service.endSession(id);
    }

    @PutMapping("/{id}/cancel")
    public InterviewSessionResponse cancelSession(@PathVariable Long id){
        return service.cancelSession(id);
    }
}
