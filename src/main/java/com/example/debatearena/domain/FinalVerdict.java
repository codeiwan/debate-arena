package com.example.debatearena.domain;

import java.util.List;

public record FinalVerdict(
        double userPercentage,
        double aiPercentage,
        String winner,
        String summary,
        List<String> decisiveFactors
) {
}
