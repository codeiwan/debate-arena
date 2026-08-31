package com.example.debatearena.domain;

public class DebateRound {

    private final int roundNumber;

    private String aiArgument;
    private String userArgument;

    private JudgeResult judgeResult;
    private RoundScore roundScore;

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

    public JudgeResult getJudgeResult() {
        return judgeResult;
    }

    public void setJudgeResult(JudgeResult judgeResult) {
        this.judgeResult = judgeResult;
    }

    public RoundScore getRoundScore() {
        return roundScore;
    }

    public void setRoundScore(RoundScore roundScore) {
        this.roundScore = roundScore;
    }
}
