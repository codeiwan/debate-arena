package com.example.debatearena.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.debatearena.domain.DebateSession;
import com.example.debatearena.domain.DebateTopic;

@Service
public class DebateAiService {
    
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Value("classpath:/prompts/topic-clarifier.st")
    private Resource topicClarifierPrompt;

    @Value("classpath:/prompts/debater.st")
    private Resource debaterPrompt;

    @Value("classpath:/prompts/rebuttal.st")
    private Resource rebuttalPrompt;

    public DebateAiService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory
    ) {
        this.chatMemory = chatMemory;

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .build()
                )
                .build();
    }

    public DebateTopic clarifyTopic(String input) {
        return chatClient
                .prompt()
                .system(system -> system.text(topicClarifierPrompt))
                .user(input)
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        "topic-clarifier"
                ))
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
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        session.getSessionId()
                ))
                .call()
                .content();
    }

    public void rememberUserArgument(
            String sessionId,
            String argument
    ) {
        chatMemory.add(
                sessionId,
                new UserMessage(argument)
        );
    }

    public String generateRebuttal(DebateSession session) {
        return chatClient
                .prompt()
                .system(system -> system
                        .text(rebuttalPrompt)
                        .param("topic", session.getDebateTopic().topic())
                        .param("aiPosition", session.getAiPosition())
                        .param("userPosition", session.getUserPosition())
                )
                .user(
                        "Round 2입니다. 지금까지의 토론 내용을 바탕으로 상대방의 핵심 주장을 반박하세요."
                )
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        session.getSessionId()
                ))
                .call()
                .content();
    }
}
