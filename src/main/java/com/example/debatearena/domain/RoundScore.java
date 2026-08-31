package com.example.debatearena.domain;

public record RoundScore(
        double userScore,
        double aiScore,
        double userPercentage,
        double aiPercentage,
        String winner
) {
}
