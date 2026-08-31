package com.example.debatearena.domain;

import java.time.LocalDateTime;

public class DebateSession {

    private final String sessionId;
    private final LocalDateTime createdAt;

    private String status;
    private DebateTopic debateTopic;

    private String userSide;
    private String userPosition;
    private String aiPosition;

    private int currentRound;

    public DebateSession(String sessionId) {
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.status = "CREATED";
        this.currentRound = 0;
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

    public String getUserSide() {
        return userSide;
    }

    public void setUserSide(String userSide) {
        this.userSide = userSide;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }

    public String getAiPosition() {
        return aiPosition;
    }

    public void setAiPosition(String aiPosition) {
        this.aiPosition = aiPosition;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}
