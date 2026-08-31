package com.example.debatearena.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;
import com.example.debatearena.dto.PositionRequest;
import com.example.debatearena.dto.TopicRequest;
import com.example.debatearena.service.DebateService;

@RestController
@RequestMapping("/api/debates")
public class DebateController {
    
    private final DebateService debateService;

    public DebateController(DebateService debateService) {
        this.debateService = debateService;
    }

    @PostMapping
    public DebateSession createSession() {
        return debateService.createSession();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<DebateSession> getSession(
            @PathVariable String sessionId
    ) {
        DebateSession session =
                debateService.getSession(sessionId);

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/topic")
    public DebateTopic clarifyTopic(
            @PathVariable String sessionId,
            @RequestBody TopicRequest request
    ) {
        return debateService.clarifyTopic(
                sessionId,
                request.topic()
        );
    }

    @PostMapping("/{sessionId}/position")
    public DebateSession selectPosition(
            @PathVariable String sessionId,
            @RequestBody PositionRequest request
    ) {
        return debateService.selectPosition(
                sessionId,
                request.side()
        );
    }
}
