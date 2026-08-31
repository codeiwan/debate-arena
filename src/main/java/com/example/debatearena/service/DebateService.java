package com.example.debatearena.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;

@Service
public class DebateService {

    private final Map<String, DebateSession> sessions =
            new ConcurrentHashMap<>();

    private final DebateAiService debateAiService;

    public DebateService(DebateAiService debateAiService) {
        this.debateAiService = debateAiService;
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
}
