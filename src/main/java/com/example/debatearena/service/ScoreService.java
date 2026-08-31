package com.example.debatearena.service;

import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateRound;
import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.JudgeResult;
import com.example.debatearena.domain.RoundScore;

@Service
public class ScoreService {
    
    public RoundScore calculate(JudgeResult result) {

        double userScore = average(result.user());
        double aiScore = average(result.ai());

        double totalScore = userScore + aiScore;

        double userPercentage;
        double aiPercentage;

        if (totalScore == 0) {
            userPercentage = 50.0;
            aiPercentage = 50.0;
        } else {
            userPercentage =
                    userScore / totalScore * 100.0;

            aiPercentage =
                    aiScore / totalScore * 100.0;
        }

        String winner;

        if (userScore > aiScore) {
            winner = "USER";
        } else if (aiScore > userScore) {
            winner = "AI";
        } else {
            winner = "DRAW";
        }

        return new RoundScore(
                round(userScore),
                round(aiScore),
                round(userPercentage),
                round(aiPercentage),
                winner
        );
    }

    private double average(JudgeResult.SideScore score) {
        return (
                score.logic()
                + score.relevance()
                + score.specificity()
                + score.persuasiveness()
        ) / 4.0;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public RoundScore calculateFinalScore(DebateSession session) {

        double userScore = session.getRounds()
                .stream()
                .map(DebateRound::getRoundScore)
                .mapToDouble(RoundScore::userScore)
                .average()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "라운드 점수를 찾을 수 없습니다."
                        )
                );

        double aiScore = session.getRounds()
                .stream()
                .map(DebateRound::getRoundScore)
                .mapToDouble(RoundScore::aiScore)
                .average()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "라운드 점수를 찾을 수 없습니다."
                        )
                );

        double totalScore = userScore + aiScore;

        double userPercentage;
        double aiPercentage;

        if (totalScore == 0) {
            userPercentage = 50.0;
            aiPercentage = 50.0;
        } else {
            userPercentage = userScore / totalScore * 100.0;
            aiPercentage = aiScore / totalScore * 100.0;
        }

        String winner;

        if (userScore > aiScore) {
            winner = "USER";
        } else if (aiScore > userScore) {
            winner = "AI";
        } else {
            winner = "DRAW";
        }

        return new RoundScore(
                round(userScore),
                round(aiScore),
                round(userPercentage),
                round(aiPercentage),
                winner
        );
    }
}
