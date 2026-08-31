package com.example.debatearena.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateRound;
import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;
import com.example.debatearena.domain.JudgeResult;
import com.example.debatearena.domain.RoundScore;

@Service
public class DebateService {

    private final Map<String, DebateSession> sessions =
            new ConcurrentHashMap<>();

    private final DebateAiService debateAiService;
    private final JudgeService judgeService;
    private final ScoreService scoreService;

    public DebateService(
            DebateAiService debateAiService,
            JudgeService judgeService,
            ScoreService scoreService
    ) {
        this.debateAiService = debateAiService;
        this.judgeService = judgeService;
        this.scoreService = scoreService;
    }

    public DebateSession createSession() {
        String sessionId = UUID.randomUUID().toString();

        DebateSession session =
                new DebateSession(sessionId);

        sessions.put(sessionId, session);

        return session;
    }

    public DebateSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public DebateTopic clarifyTopic(
            String sessionId,
            String input
    ) {
        DebateSession session = getRequiredSession(sessionId);

        DebateTopic debateTopic =
                debateAiService.clarifyTopic(input);

        session.setDebateTopic(debateTopic);
        session.setStatus("TOPIC_READY");

        return debateTopic;
    }

    public DebateSession selectPosition(
            String sessionId,
            String side
    ) {
        DebateSession session = getRequiredSession(sessionId);

        if (!"TOPIC_READY".equals(session.getStatus())) {
            throw new IllegalStateException(
                    "토론 주제가 준비된 후에 입장을 선택할 수 있습니다."
            );
        }

        if (side == null) {
            throw new IllegalArgumentException(
                    "A 또는 B 중 하나를 선택해야 합니다."
            );
        }

        String normalizedSide = side.trim().toUpperCase();

        DebateTopic topic = session.getDebateTopic();

        if ("A".equals(normalizedSide)) {
            session.setUserSide("A");
            session.setUserPosition(topic.positionA());
            session.setAiPosition(topic.positionB());
        } else if ("B".equals(normalizedSide)) {
            session.setUserSide("B");
            session.setUserPosition(topic.positionB());
            session.setAiPosition(topic.positionA());
        } else {
            throw new IllegalArgumentException(
                    "입장은 A 또는 B만 선택할 수 있습니다."
            );
        }

        session.setCurrentRound(1);
        session.setStatus("POSITION_SELECTED");

        return session;
    }

    private DebateSession getRequiredSession(String sessionId) {
        DebateSession session = sessions.get(sessionId);

        if (session == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 토론 세션입니다."
            );
        }

        return session;
    }

    public DebateRound startRound1(String sessionId) {

        DebateSession session = getRequiredSession(sessionId);

        if (!"POSITION_SELECTED".equals(session.getStatus())) {
            throw new IllegalStateException(
                    "입장을 선택한 후 Round 1을 시작할 수 있습니다."
            );
        }

        String aiArgument =
                debateAiService.generateOpeningArgument(session);

        DebateRound round = new DebateRound(1);
        round.setAiArgument(aiArgument);

        session.getRounds().add(round);
        session.setStatus("ROUND_1_AI_DONE");

        return round;
    }

    public DebateRound submitRound1Argument(
            String sessionId,
            String userArgument
    ) {

        DebateSession session = getRequiredSession(sessionId);

        if (!"ROUND_1_AI_DONE".equals(session.getStatus())) {
            throw new IllegalStateException(
                    "AI의 Round 1 주장이 완료된 후 사용자 주장을 제출할 수 있습니다."
            );
        }

        if (userArgument == null || userArgument.isBlank()) {
            throw new IllegalArgumentException(
                    "토론 주장을 입력해야 합니다."
            );
        }

        DebateRound round = session.getRounds()
                .stream()
                .filter(item -> item.getRoundNumber() == 1)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Round 1 정보를 찾을 수 없습니다."
                        )
                );

        round.setUserArgument(userArgument);

        debateAiService.rememberUserArgument(
                session.getSessionId(),
                userArgument
        );

        session.setStatus("ROUND_1_ARGUMENTS_COMPLETE");

        return round;
    }

    public DebateRound evaluateRound1(String sessionId) {

        DebateSession session =
                getRequiredSession(sessionId);

        if (!"ROUND_1_ARGUMENTS_COMPLETE"
                .equals(session.getStatus())) {

            throw new IllegalStateException(
                    "양측의 Round 1 발언이 완료된 후 평가할 수 있습니다."
            );
        }

        DebateRound round = session.getRounds()
                .stream()
                .filter(item ->
                        item.getRoundNumber() == 1
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Round 1 정보를 찾을 수 없습니다."
                        )
                );

        JudgeResult judgeResult =
                judgeService.evaluateRound1(
                        session,
                        round
                );

        RoundScore roundScore =
                scoreService.calculate(judgeResult);

        round.setJudgeResult(judgeResult);
        round.setRoundScore(roundScore);

        session.setStatus("ROUND_1_EVALUATED");

        return round;
    }

    public DebateRound submitRound2Rebuttal(
            String sessionId,
            String userArgument
    ) {

        DebateSession session =
                getRequiredSession(sessionId);

        if (!"ROUND_1_EVALUATED".equals(session.getStatus())) {
            throw new IllegalStateException(
                    "Round 1 평가가 완료된 후 Round 2를 시작할 수 있습니다."
            );
        }

        if (userArgument == null || userArgument.isBlank()) {
            throw new IllegalArgumentException(
                    "반박 내용을 입력해야 합니다."
            );
        }

        DebateRound round = new DebateRound(2);
        round.setUserArgument(userArgument);

        session.getRounds().add(round);

        debateAiService.rememberUserArgument(
                session.getSessionId(),
                userArgument
        );

        session.setCurrentRound(2);
        session.setStatus("ROUND_2_USER_DONE");

        return round;
    }

    public DebateRound generateRound2AiRebuttal(
            String sessionId
    ) {

        DebateSession session =
                getRequiredSession(sessionId);

        if (!"ROUND_2_USER_DONE".equals(session.getStatus())) {
            throw new IllegalStateException(
                    "사용자의 Round 2 반박이 완료된 후 AI가 반박할 수 있습니다."
            );
        }

        DebateRound round = session.getRounds()
                .stream()
                .filter(item -> item.getRoundNumber() == 2)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Round 2 정보를 찾을 수 없습니다."
                        )
                );

        String aiArgument =
                debateAiService.generateRebuttal(session);

        round.setAiArgument(aiArgument);

        session.setStatus("ROUND_2_ARGUMENTS_COMPLETE");

        return round;
    }
}
