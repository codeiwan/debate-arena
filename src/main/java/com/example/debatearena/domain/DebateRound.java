package com.example.debatearena.domain;

public class DebateRound {

    private final int roundNumber;

    private String aiArgument;
    private String userArgument;

    public DebateRound(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getAiArgument() {
        return aiArgument;
    }

    public void setAiArgument(String aiArgument) {
        this.aiArgument = aiArgument;
    }

    public String getUserArgument() {
        return userArgument;
    }

    public void setUserArgument(String userArgument) {
        this.userArgument = userArgument;
    }
}
