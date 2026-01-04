package com.vupico.assesment.service.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vupico.assesment.dto.ticket.TicketClassificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TicketClassifierService {
	private static final Logger logger = LoggerFactory.getLogger(TicketClassifierService.class);
	private static final String SYSTEM_PROMPT = """
		You are a support ticket classifier.
		Choose exactly one category from: BILLING, TECHNICAL, GENERAL.
		If unsure, choose GENERAL.
		Return strictly valid JSON only with fields: category, rationale.
		""";

	private final ChatClient chatClient;
	private final ObjectMapper objectMapper;

	public TicketClassifierService(ChatClient chatClient, ObjectMapper objectMapper) {
		this.chatClient = chatClient;
		this.objectMapper = objectMapper;
	}

	public TicketClassificationResponse classify(String text) {
		logger.info("Classifying ticket text.");
		String content = chatClient.prompt()
				.system(SYSTEM_PROMPT)
				.user("Ticket: " + text)
				.call()
				.content();

		try {
			String sanitized = sanitizeJson(content);
			TicketClassificationResponse response = objectMapper.readValue(sanitized,
					TicketClassificationResponse.class);
			return response.normalized();
		} catch (Exception ex) {
			logger.warn("Ticket classification response parse failed: {}", ex.getMessage());
			return TicketClassificationResponse.fallback(content);
		}
	}

	private String sanitizeJson(String content) {
		if (content == null) {
			return "";
		}
		String trimmed = content.trim();
		if (trimmed.startsWith("```")) {
			int firstNewline = trimmed.indexOf('\n');
			if (firstNewline != -1) {
				trimmed = trimmed.substring(firstNewline + 1);
			}
			int fenceIndex = trimmed.lastIndexOf("```");
			if (fenceIndex != -1) {
				trimmed = trimmed.substring(0, fenceIndex);
			}
			return trimmed.trim();
		}
		return trimmed;
	}
}
