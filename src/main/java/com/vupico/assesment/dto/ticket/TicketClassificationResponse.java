package com.vupico.assesment.dto.ticket;

public record TicketClassificationResponse(String category, String rationale) {
	public static TicketClassificationResponse fallback(String rationale) {
		return new TicketClassificationResponse("GENERAL", rationale);
	}

	public static TicketClassificationResponse invalid(String rationale) {
		return new TicketClassificationResponse("GENERAL", rationale);
	}

	public TicketClassificationResponse normalized() {
		if (category == null) {
			return new TicketClassificationResponse("GENERAL", rationale);
		}

		String normalized = category.trim().toUpperCase();
		return switch (normalized) {
			case "BILLING", "TECHNICAL", "GENERAL" -> new TicketClassificationResponse(normalized, rationale);
			default -> new TicketClassificationResponse("GENERAL", rationale);
		};
	}
}
