package com.vupico.assesment.service.rag;

import java.util.Comparator;
import java.util.List;

public class InMemoryVectorStore {
	private final List<StoredDocument> documents;

	public InMemoryVectorStore(List<StoredDocument> documents) {
		this.documents = List.copyOf(documents);
	}

	public List<StoredDocument> search(List<Double> queryEmbedding, int topK) {
		return documents.stream()
				.map(document -> new ScoredDocument(document, cosineSimilarity(queryEmbedding, document.embedding())))
				.sorted(Comparator.comparingDouble(ScoredDocument::score).reversed())
				.limit(topK)
				.map(ScoredDocument::document)
				.toList();
	}

	private static double cosineSimilarity(List<Double> left, List<Double> right) {
		int size = Math.min(left.size(), right.size());
		double dot = 0.0;
		double leftNorm = 0.0;
		double rightNorm = 0.0;

		// Basic cosine similarity for deterministic scoring.
		for (int i = 0; i < size; i++) {
			double l = left.get(i);
			double r = right.get(i);
			dot += l * r;
			leftNorm += l * l;
			rightNorm += r * r;
		}

		if (leftNorm == 0.0 || rightNorm == 0.0) {
			return 0.0;
		}

		return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
	}

	private record ScoredDocument(StoredDocument document, double score) {
	}
}
