package com.example.debatearena.domain;

import java.time.LocalDateTime;

public class DebateSession {

    private final String sessionId;
    private final LocalDateTime createdAt;

    private String status;
    private DebateTopic debateTopic;

    public DebateSession(String sessionId) {
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.status = "CREATED";
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DebateTopic getDebateTopic() {
        return debateTopic;
    }

    public void setDebateTopic(DebateTopic debateTopic) {
        this.debateTopic = debateTopic;
    }
}
