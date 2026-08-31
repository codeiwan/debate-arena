package com.example.debatearena.domain;

import java.util.List;

public record FinalJudgeResult(
        String summary,
        List<String> decisiveFactors
) {
}
