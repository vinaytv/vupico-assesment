package com.vupico.assesment.controller.ticket;

import com.vupico.assesment.dto.ticket.TicketClassificationRequest;
import com.vupico.assesment.dto.ticket.TicketClassificationResponse;
import com.vupico.assesment.service.ticket.TicketClassifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketClassifierController {
	private static final Logger logger = LoggerFactory.getLogger(TicketClassifierController.class);
	private final TicketClassifierService classifierService;

	public TicketClassifierController(TicketClassifierService classifierService) {
		this.classifierService = classifierService;
	}

	@PostMapping("/classify")
	public ResponseEntity<TicketClassificationResponse> classify(@RequestBody TicketClassificationRequest request) {
		logger.info("Ticket /classify request received.");
		if (request == null || request.text() == null || request.text().isBlank()) {
			logger.warn("Ticket /classify request missing text.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(TicketClassificationResponse.invalid("Text is required."));
		}

		logger.info("Ticket /classify processing.");
		return ResponseEntity.ok(classifierService.classify(request.text()));
	}
}
