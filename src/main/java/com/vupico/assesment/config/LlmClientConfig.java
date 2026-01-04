package com.vupico.assesment.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

@Configuration
public class LlmClientConfig {

	@Bean
	@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "openai", matchIfMissing = true)
	public ChatClient openAiChatClient(@Qualifier("openAiChatModel") ChatModel chatModel) {
		return ChatClient.builder(chatModel).build();
	}

	@Bean
	@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "ollama")
	public ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") ChatModel chatModel) {
		return ChatClient.builder(chatModel).build();
	}

	@Bean
	@Primary
	@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "openai", matchIfMissing = true)
	public EmbeddingModel activeOpenAiEmbeddingModel(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
		return embeddingModel;
	}

	@Bean
	@Primary
	@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "ollama")
	public EmbeddingModel activeOllamaEmbeddingModel(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
		return embeddingModel;
	}
}
