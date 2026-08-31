package com.example.debatearena.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;

@Service
public class DebateAiService {
    
    private final ChatClient chatClient;

    @Value("classpath:/prompts/topic-clarifier.st")
    private Resource topicClarifierPrompt;

    @Value("classpath:/prompts/debater.st")
    private Resource debaterPrompt;

    public DebateAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public DebateTopic clarifyTopic(String input) {
        return chatClient
                .prompt()
                .system(system -> system.text(topicClarifierPrompt))
                .user(input)
                .call()
                .entity(
                        DebateTopic.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );
    }

    public String generateOpeningArgument(DebateSession session) {
        return chatClient
                .prompt()
                .system(system -> system
                        .text(debaterPrompt)
                        .param("topic", session.getDebateTopic().topic())
                        .param("aiPosition", session.getAiPosition())
                        .param("userPosition", session.getUserPosition())
                )
                .user("Round 1의 첫 주장을 시작하세요.")
                .call()
                .content();
    }
}
