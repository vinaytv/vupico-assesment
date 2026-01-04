package com.vupico.assesment.service.rag;

import com.vupico.assesment.dto.rag.AskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RagServiceTest {

	@Test
	void askReturnsAnswerAndSources() {
		ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
		EmbeddingModel embeddingModel = mock(EmbeddingModel.class);

		when(chatClient.prompt().user(anyString()).call().content()).thenReturn("ok");
		when(embeddingModel.embed(anyString())).thenReturn(new float[] {1.0f, 0.0f, 0.0f});

		RagService service = new RagService(chatClient, embeddingModel);
		service.precomputeEmbeddings();

		AskResponse response = service.ask("What is Spring AI?");

		assertThat(response.answer()).isEqualTo("ok");
		assertThat(response.sources()).isNotEmpty();
	}
}
