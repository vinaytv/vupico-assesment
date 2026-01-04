package com.vupico.assesment.controller.rag;

import com.vupico.assesment.dto.rag.AskRequest;
import com.vupico.assesment.dto.rag.AskResponse;
import com.vupico.assesment.service.rag.RagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {
	private static final Logger logger = LoggerFactory.getLogger(RagController.class);
	private final RagService ragService;

	public RagController(RagService ragService) {
		this.ragService = ragService;
	}

	@PostMapping("/ask")
	public ResponseEntity<AskResponse> ask(@RequestBody AskRequest request) {
		logger.info("RAG /ask request received.");
		if (request == null || request.question() == null || request.question().isBlank()) {
			logger.warn("RAG /ask request missing question.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AskResponse("Question is required.", java.util.List.of()));
		}

		logger.info("RAG /ask processing question.");
		return ResponseEntity.ok(ragService.ask(request.question()));
	}
}
