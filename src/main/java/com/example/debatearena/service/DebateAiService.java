package com.example.debatearena.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateTopic;

@Service
public class DebateAiService {
    
    private final ChatClient chatClient;

    @Value("classpath:/prompts/topic-clarifier.st")
    private Resource topicClarifierPrompt;

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
}
