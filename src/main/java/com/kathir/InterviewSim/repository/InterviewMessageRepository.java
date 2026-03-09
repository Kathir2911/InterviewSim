package com.kathir.InterviewSim.repository;

import com.kathir.InterviewSim.entity.InterviewMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewMessageRepository extends JpaRepository<InterviewMessage, Long> {
    List<InterviewMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);
}
