package com.vupico.assesment.service.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "local")
public class LocalLlmService implements LlmService {
	private static final Logger logger = LoggerFactory.getLogger(LocalLlmService.class);
	@Override
	public String generate(String prompt) {
		logger.info("Generating response with local stub provider.");
		return "[local-model] " + prompt;
	}
}
