package com.vupico.assesment.dto;

import com.vupico.assesment.dto.ticket.TicketClassificationResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketClassificationResponseTest {

	@Test
	void normalizesValidCategory() {
		TicketClassificationResponse response = new TicketClassificationResponse("billing", "reason");
		TicketClassificationResponse normalized = response.normalized();

		assertThat(normalized.category()).isEqualTo("BILLING");
	}

	@Test
	void defaultsToGeneralForUnknownCategory() {
		TicketClassificationResponse response = new TicketClassificationResponse("other", "reason");
		TicketClassificationResponse normalized = response.normalized();

		assertThat(normalized.category()).isEqualTo("GENERAL");
	}

	@Test
	void fallbackUsesGeneral() {
		TicketClassificationResponse response = TicketClassificationResponse.fallback("raw");
		assertThat(response.category()).isEqualTo("GENERAL");
	}
}
