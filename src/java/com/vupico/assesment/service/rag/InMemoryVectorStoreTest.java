package com.vupico.assesment.service.rag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryVectorStoreTest {

	@Test
	void searchReturnsMostSimilarDocuments() {
		StoredDocument docA = new StoredDocument("a", "A", List.of(1.0, 0.0));
		StoredDocument docB = new StoredDocument("b", "B", List.of(0.0, 1.0));
		StoredDocument docC = new StoredDocument("c", "C", List.of(0.9, 0.1));

		InMemoryVectorStore store = new InMemoryVectorStore(List.of(docA, docB, docC));

		List<StoredDocument> results = store.search(List.of(1.0, 0.0), 2);

		assertThat(results).hasSize(2);
		assertThat(results.get(0).id()).isEqualTo("a");
		assertThat(results.get(1).id()).isEqualTo("c");
	}
}
