package com.vupico.assesment.service.rag;

import java.util.List;

public record StoredDocument(String id, String content, List<Double> embedding) {
}
