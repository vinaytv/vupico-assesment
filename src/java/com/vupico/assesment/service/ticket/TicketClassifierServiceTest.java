package com.vupico.assesment.service.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vupico.assesment.dto.ticket.TicketClassificationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketClassifierServiceTest {

	@Test
	void classifyParsesJsonResponse() {
		ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
		ObjectMapper mapper = new ObjectMapper();

		String json = "{\"category\":\"BILLING\",\"rationale\":\"charge issue\"}";
		when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
				.thenReturn(json);

		TicketClassifierService service = new TicketClassifierService(chatClient, mapper);
		TicketClassificationResponse response = service.classify("Charged twice");

		assertThat(response.category()).isEqualTo("BILLING");
		assertThat(response.rationale()).isEqualTo("charge issue");
	}

	@Test
	void classifyStripsFencedJson() {
		ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
		ObjectMapper mapper = new ObjectMapper();

		String fenced = "```json\n{\"category\":\"TECHNICAL\",\"rationale\":\"bug\"}\n```";
		when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
				.thenReturn(fenced);

		TicketClassifierService service = new TicketClassifierService(chatClient, mapper);
		TicketClassificationResponse response = service.classify("App fails");

		assertThat(response.category()).isEqualTo("TECHNICAL");
		assertThat(response.rationale()).isEqualTo("bug");
	}
}
