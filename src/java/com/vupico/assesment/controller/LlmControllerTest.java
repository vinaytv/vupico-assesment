package com.vupico.assesment.controller;

import com.vupico.assesment.controller.gateway.LlmController;
import com.vupico.assesment.dto.gateway.GenerateRequest;
import com.vupico.assesment.dto.gateway.GenerateResponse;
import com.vupico.assesment.service.gateway.LocalLlmService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class LlmControllerTest {

	@Test
	void returnsBadRequestWhenPromptMissing() {
		LlmController controller = new LlmController(new LocalLlmService());

		ResponseEntity<GenerateResponse> response = controller.generate(new GenerateRequest(""));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().provider()).isEqualTo("local");
	}

	@Test
	void returnsResponseFromLocalProvider() {
		LlmController controller = new LlmController(new LocalLlmService());

		ResponseEntity<GenerateResponse> response = controller.generate(new GenerateRequest("hello"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().provider()).isEqualTo("local");
		assertThat(response.getBody().response()).contains("hello");
	}
}
