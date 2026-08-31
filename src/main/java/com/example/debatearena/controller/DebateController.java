package com.example.debatearena.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.debatearena.domain.DebateRound;
import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;
import com.example.debatearena.dto.ArgumentRequest;
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

    @PostMapping("/{sessionId}/rounds/1/start")
    public DebateRound startRound1(
            @PathVariable String sessionId
    ) {
        return debateService.startRound1(sessionId);
    }

    @PostMapping("/{sessionId}/rounds/1/argument")
    public DebateRound submitRound1Argument(
            @PathVariable String sessionId,
            @RequestBody ArgumentRequest request
    ) {
        return debateService.submitRound1Argument(
                sessionId,
                request.argument()
        );
    }
    
    @PostMapping("/{sessionId}/rounds/1/evaluate")
    public DebateRound evaluateRound1(
            @PathVariable String sessionId
    ) {
        return debateService.evaluateRound1(sessionId);
    }

    @PostMapping("/{sessionId}/rounds/2/argument")
    public DebateRound submitRound2Argument(
            @PathVariable String sessionId,
            @RequestBody ArgumentRequest request
    ) {
        return debateService.submitRound2Rebuttal(
                sessionId,
                request.argument()
        );
    }

    @PostMapping("/{sessionId}/rounds/2/respond")
    public DebateRound generateRound2AiRebuttal(
            @PathVariable String sessionId
    ) {
        return debateService.generateRound2AiRebuttal(
                sessionId
        );
    }

    @PostMapping("/{sessionId}/rounds/2/evaluate")
    public DebateRound evaluateRound2(
            @PathVariable String sessionId
    ) {
        return debateService.evaluateRound2(sessionId);
    }

    @PostMapping("/{sessionId}/rounds/3/start")
    public DebateRound startRound3(
            @PathVariable String sessionId
    ) {
        return debateService.startRound3(sessionId);
    }

    @PostMapping("/{sessionId}/rounds/3/argument")
    public DebateRound submitRound3Argument(
            @PathVariable String sessionId,
            @RequestBody ArgumentRequest request
    ) {
        return debateService.submitRound3Argument(
                sessionId,
                request.argument()
        );
    }

    @PostMapping("/{sessionId}/rounds/3/evaluate")
    public DebateRound evaluateRound3(
            @PathVariable String sessionId
    ) {
        return debateService.evaluateRound3(sessionId);
    }
}
