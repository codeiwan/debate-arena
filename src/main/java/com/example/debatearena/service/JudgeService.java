package com.example.debatearena.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateRound;
import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.JudgeResult;

@Service
public class JudgeService {
    
    private final ChatClient chatClient;

    @Value("classpath:/prompts/judge.st")
    private Resource judgePrompt;

    public JudgeService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public JudgeResult evaluateRound1(
            DebateSession session,
            DebateRound round
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
                                "각 진영이 자신의 핵심 주장과 근거를 명확하게 제시한다."
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
                .user("두 토론자의 Round 1 발언을 공정하게 평가하세요.")
                .call()
                .entity(
                        JudgeResult.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }
}
