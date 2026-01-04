package com.vupico.assesment.controller.gateway;

import com.vupico.assesment.dto.gateway.GenerateRequest;
import com.vupico.assesment.dto.gateway.GenerateResponse;
import com.vupico.assesment.service.gateway.LlmService;
import com.vupico.assesment.service.gateway.LocalLlmService;
import com.vupico.assesment.service.gateway.OllamaLlmService;
import com.vupico.assesment.service.gateway.OpenAiLlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LlmController {
	private static final Logger logger = LoggerFactory.getLogger(LlmController.class);
	private final LlmService llmService;

	public LlmController(LlmService llmService) {
		this.llmService = llmService;
	}

	@PostMapping("/generate")
	public ResponseEntity<GenerateResponse> generate(@RequestBody GenerateRequest request) {
		logger.info("Gateway /generate request received.");
		if (request == null || request.prompt() == null || request.prompt().isBlank()) {
			logger.warn("Gateway /generate request missing prompt.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new GenerateResponse(resolveProvider(), "Prompt is required."));
		}

		logger.info("Gateway /generate processing with provider {}.", resolveProvider());
		String response = llmService.generate(request.prompt());
		return ResponseEntity.ok(new GenerateResponse(resolveProvider(), response));
	}

	private String resolveProvider() {
		if (llmService instanceof OpenAiLlmService) {
			return "openai";
		}
		if (llmService instanceof OllamaLlmService) {
			return "ollama";
		}
		if (llmService instanceof LocalLlmService) {
			return "local";
		}
		return "unknown";
	}
}
