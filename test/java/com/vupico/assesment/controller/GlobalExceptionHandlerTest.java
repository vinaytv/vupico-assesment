package com.vupico.assesment.controller;

import com.vupico.assesment.dto.common.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

	@Test
	void handlesTransientAiException() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		ResponseEntity<ApiError> response = handler.handleTransientAi(new TransientAiException("tmp"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
		assertThat(response.getBody().error()).isEqualTo("LLM_UNAVAILABLE");
	}

	@Test
	void handlesNonTransientAiException() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		ResponseEntity<ApiError> response = handler.handleNonTransientAi(new NonTransientAiException("bad"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
		assertThat(response.getBody().error()).isEqualTo("LLM_ERROR");
	}

	@Test
	void handlesGenericException() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		ResponseEntity<ApiError> response = handler.handleGeneral(new RuntimeException("boom"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().error()).isEqualTo("INTERNAL_ERROR");
	}
}
