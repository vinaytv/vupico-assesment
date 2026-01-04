package com.vupico.assesment.controller;

import com.vupico.assesment.controller.ticket.TicketClassifierController;
import com.vupico.assesment.dto.ticket.TicketClassificationRequest;
import com.vupico.assesment.dto.ticket.TicketClassificationResponse;
import com.vupico.assesment.service.ticket.TicketClassifierService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketClassifierControllerTest {

	@Test
	void returnsBadRequestWhenTextMissing() {
		TicketClassifierService service = mock(TicketClassifierService.class);
		TicketClassifierController controller = new TicketClassifierController(service);

		ResponseEntity<TicketClassificationResponse> response = controller.classify(new TicketClassificationRequest(""));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void returnsClassificationWhenValid() {
		TicketClassifierService service = mock(TicketClassifierService.class);
		TicketClassifierController controller = new TicketClassifierController(service);

		TicketClassificationResponse payload = new TicketClassificationResponse("GENERAL", "ok");
		when(service.classify("test")).thenReturn(payload);

		ResponseEntity<TicketClassificationResponse> response = controller.classify(new TicketClassificationRequest("test"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(payload);
	}
}
