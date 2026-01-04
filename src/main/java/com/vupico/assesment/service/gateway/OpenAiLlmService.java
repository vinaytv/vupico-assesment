package com.vupico.assesment.service.gateway;

import org.springframework.ai.chat.client.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "openai", matchIfMissing = true)
public class OpenAiLlmService implements LlmService {
	private static final Logger logger = LoggerFactory.getLogger(OpenAiLlmService.class);
	private final ChatClient chatClient;

	public OpenAiLlmService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@Override
	public String generate(String prompt) {
		logger.info("Generating response with OpenAI provider.");
		return chatClient.prompt()
				.user(prompt)
				.call()
				.content();
	}
}
