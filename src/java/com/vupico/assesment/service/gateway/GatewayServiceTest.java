package com.vupico.assesment.service.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GatewayServiceTest {

	@Test
	void openAiServiceUsesChatClient() {
		ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
		when(chatClient.prompt().user(anyString()).call().content()).thenReturn("openai");

		OpenAiLlmService service = new OpenAiLlmService(chatClient);

		assertThat(service.generate("hi")).isEqualTo("openai");
	}

	@Test
	void ollamaServiceUsesChatClient() {
		ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
		when(chatClient.prompt().user(anyString()).call().content()).thenReturn("ollama");

		OllamaLlmService service = new OllamaLlmService(chatClient);

		assertThat(service.generate("hi")).isEqualTo("ollama");
	}

	@Test
	void localServicePrefixesPrompt() {
		LocalLlmService service = new LocalLlmService();
		assertThat(service.generate("test")).contains("test");
	}
}
