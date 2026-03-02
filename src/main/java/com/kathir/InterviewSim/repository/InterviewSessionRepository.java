package com.kathir.InterviewSim.repository;

import com.kathir.InterviewSim.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InterviewSessionRepository extends JpaRepository<InterviewSession,Long> {
}
