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
        DebateSession session = sessions.get(sessionId);

        if (session == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 토론 세션입니다."
            );
        }

        DebateTopic debateTopic =
                debateAiService.clarifyTopic(input);

        session.setDebateTopic(debateTopic);
        session.setStatus("TOPIC_READY");

        return debateTopic;
    }
}
