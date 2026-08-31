package com.example.debatearena.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateRound;
import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.FinalJudgeResult;
import com.example.debatearena.domain.JudgeResult;
import com.example.debatearena.domain.RoundScore;

@Service
public class JudgeService {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/judge.st")
    private Resource judgePrompt;

    @Value("classpath:/prompts/final-judge.st")
    private Resource finalJudgePrompt;

    public JudgeService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public JudgeResult evaluateRound(
            DebateSession session,
            DebateRound round,
            String roundObjective
    ) {
        return chatClient
                .prompt()
                .system(system -> system
                        .text(judgePrompt)
                        .param(
                                "topic",
                                session.getDebateTopic().topic()
                        )
                        .param(
                                "userPosition",
                                session.getUserPosition()
                        )
                        .param(
                                "aiPosition",
                                session.getAiPosition()
                        )
                        .param(
                                "roundNumber",
                                round.getRoundNumber()
                        )
                        .param(
                                "roundObjective",
                                roundObjective
                        )
                        .param(
                                "userArgument",
                                round.getUserArgument()
                        )
                        .param(
                                "aiArgument",
                                round.getAiArgument()
                        )
                )
                .user(
                        "두 토론자의 현재 라운드 발언을 공정하게 평가하세요."
                )
                .call()
                .entity(
                        JudgeResult.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }

    public FinalJudgeResult evaluateFinal(
            DebateSession session,
            RoundScore finalScore
    ) {

        DebateRound round1 = session.getRounds().get(0);
        DebateRound round2 = session.getRounds().get(1);
        DebateRound round3 = session.getRounds().get(2);

        return chatClient
                .prompt()
                .system(system -> system
                        .text(finalJudgePrompt)
                        .param(
                                "topic",
                                session.getDebateTopic().topic()
                        )
                        .param(
                                "userPosition",
                                session.getUserPosition()
                        )
                        .param(
                                "aiPosition",
                                session.getAiPosition()
                        )
                        .param(
                                "winner",
                                finalScore.winner()
                        )
                        .param(
                                "userPercentage",
                                finalScore.userPercentage()
                        )
                        .param(
                                "aiPercentage",
                                finalScore.aiPercentage()
                        )
                        .param(
                                "round1Reasoning",
                                round1.getJudgeResult().reasoning()
                        )
                        .param(
                                "round2Reasoning",
                                round2.getJudgeResult().reasoning()
                        )
                        .param(
                                "round3Reasoning",
                                round3.getJudgeResult().reasoning()
                        )
                )
                .user(
                        "세 라운드의 평가 결과를 바탕으로 최종 승리 이유를 설명하세요."
                )
                .call()
                .entity(
                        FinalJudgeResult.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }
}
