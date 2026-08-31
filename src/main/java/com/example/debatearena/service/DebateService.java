package com.example.debatearena.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateSession;

@Service
public class DebateService {

    private final Map<String, DebateSession> sessions =
            new ConcurrentHashMap<>();

    public DebateSession createSession() {

        String sessionId = UUID.randomUUID().toString();

        DebateSession session = new DebateSession(sessionId);

        sessions.put(sessionId, session);

        return session;
    }

    public DebateSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}
