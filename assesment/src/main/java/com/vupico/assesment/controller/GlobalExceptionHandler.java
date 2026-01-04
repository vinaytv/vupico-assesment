package com.vupico.assesment.controller;

import com.vupico.assesment.dto.common.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(TransientAiException.class)
	public ResponseEntity<ApiError> handleTransientAi(TransientAiException ex) {
		logger.warn("Transient LLM error: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiError("LLM_UNAVAILABLE",
						"The LLM is temporarily unavailable. Please retry shortly."));
	}

	@ExceptionHandler(NonTransientAiException.class)
	public ResponseEntity<ApiError> handleNonTransientAi(NonTransientAiException ex) {
		logger.warn("Non-transient LLM error: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(new ApiError("LLM_ERROR",
						"The LLM returned an error. Check model availability and request size."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneral(Exception ex) {
		logger.error("Unexpected error", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiError("INTERNAL_ERROR",
						"Unexpected server error."));
	}
}
