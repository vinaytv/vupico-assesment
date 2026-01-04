package com.vupico.assesment.dto.rag;

import java.util.List;

public record AskResponse(String answer, List<String> sources) {
}
