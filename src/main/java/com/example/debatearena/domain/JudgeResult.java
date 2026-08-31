package com.example.debatearena.domain;

public record JudgeResult(
        SideScore user,
        SideScore ai,
        String reasoning
) {

    public record SideScore(
            int logic,
            int relevance,
            int specificity,
            int persuasiveness
    ) {
    }
}
