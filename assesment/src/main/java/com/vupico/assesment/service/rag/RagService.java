package com.vupico.assesment.service.rag;

import com.vupico.assesment.dto.rag.AskResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RagService {
	private static final Logger logger = LoggerFactory.getLogger(RagService.class);
	private static final String PROMPT_TEMPLATE = """
		You are a helpful assistant. Use only the provided context to answer.
		If the context is insufficient, say you do not know.
		Do not mention the word "context" or say "according to the provided context".
		Context:
		%s
		Question: %s
		Answer:
		""";
	private static final int MAX_EMBED_CHARS = 1200;
	private static final int MAX_CONTEXT_CHARS = 1200;
	private static final int CHUNK_OVERLAP = 100;
	private static final int MAX_DOCS = 2;
	private static final int MAX_CHUNKS_PER_DOC = 3;

	private final ChatClient chatClient;
	private final EmbeddingModel embeddingModel;
	private final List<String> sourceDocuments;
	private volatile InMemoryVectorStore vectorStore;
	private volatile int chunkCount;

	public RagService(ChatClient chatClient, EmbeddingModel embeddingModel) {
		this.chatClient = chatClient;
		this.embeddingModel = embeddingModel;
		this.sourceDocuments = loadDocuments();
	}

	@PostConstruct
	public void precomputeEmbeddings() {
		long start = System.currentTimeMillis();
		logger.info("Precomputing embeddings for RAG source documents...");
		ensureVectorStore();
		long elapsed = System.currentTimeMillis() - start;
		logger.info("RAG embeddings ready ({} chunks) in {} ms.", chunkCount, elapsed);
		warmupChatModel();
	}

	public AskResponse ask(String question) {
		logger.info("RAG ask invoked.");
		// Build context from nearest chunks before calling the chat model.
		InMemoryVectorStore store = ensureVectorStore();
		List<Double> questionEmbedding = toDoubleList(embeddingModel.embed(question));
		List<StoredDocument> matches = store.search(questionEmbedding, 2);

		String context = matches.stream()
				.map(StoredDocument::content)
				.reduce((left, right) -> left + "\n\n" + right)
				.map(text -> trimToLength(text, MAX_CONTEXT_CHARS))
				.orElse("");

		String prompt = PROMPT_TEMPLATE.formatted(context, question);
		String answer = chatClient.prompt().user(prompt).call().content();

		List<String> sources = matches.stream()
				.map(StoredDocument::id)
				.toList();
		return new AskResponse(answer, sources);
	}

	private List<String> loadDocuments() {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = resolver.getResources("classpath:/sourcedocs/*.txt");
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to load source documents.", ex);
		}

		List<String> documents = new java.util.ArrayList<>();
		for (int i = 0; i < resources.length && documents.size() < MAX_DOCS; i++) {
			Resource resource = resources[i];
			String content = readText(resource);
			if (content.isBlank()) {
				content = "Content unavailable for " + resource.getFilename();
			}
			documents.add(content);
		}
		logger.info("Loaded {} source documents for RAG.", documents.size());
		return documents;
	}

	private String readText(Resource resource) {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
			return builder.toString().trim();
		} catch (Exception ex) {
			return "";
		}
	}

	private List<Double> toDoubleList(float[] embedding) {
		List<Double> values = new java.util.ArrayList<>(embedding.length);
		for (float value : embedding) {
			values.add((double) value);
		}
		return values;
	}

	private InMemoryVectorStore ensureVectorStore() {
		InMemoryVectorStore store = this.vectorStore;
		if (store != null) {
			return store;
		}
		synchronized (this) {
			if (this.vectorStore == null) {
				this.vectorStore = new InMemoryVectorStore(embedDocuments());
			}
			return this.vectorStore;
		}
	}

	private List<StoredDocument> embedDocuments() {
		if (sourceDocuments.isEmpty()) {
			throw new IllegalStateException("No documents found in resources/sourcedocs.");
		}
		List<StoredDocument> stored = new java.util.ArrayList<>();
		int docIndex = 1;
		for (String content : sourceDocuments) {
			List<String> chunks = chunkText(content, MAX_EMBED_CHARS, CHUNK_OVERLAP);
			if (chunks.size() > MAX_CHUNKS_PER_DOC) {
				chunks = chunks.subList(0, MAX_CHUNKS_PER_DOC);
			}
			for (int chunkIndex = 0; chunkIndex < chunks.size(); chunkIndex++) {
				String chunk = chunks.get(chunkIndex);
				String id = "doc-" + docIndex + "-chunk-" + (chunkIndex + 1);
				stored.add(new StoredDocument(id, chunk, toDoubleList(embeddingModel.embed(chunk))));
			}
			docIndex++;
		}
		chunkCount = stored.size();
		logger.info("Embedded {} chunks for RAG.", stored.size());
		return stored;
	}

	private void warmupChatModel() {
		try {
			chatClient.prompt().user("Hello").call();
			logger.info("Chat model warmup complete.");
		} catch (Exception ex) {
			logger.warn("Chat model warmup failed: {}", ex.getMessage());
		}
	}

	private String trimToLength(String text, int maxChars) {
		if (text == null || text.length() <= maxChars) {
			return text == null ? "" : text;
		}
		return text.substring(0, maxChars);
	}

	private List<String> chunkText(String text, int maxChars, int overlap) {
		if (text == null || text.isBlank()) {
			return List.of("");
		}
		if (text.length() <= maxChars) {
			return List.of(text);
		}
		List<String> chunks = new java.util.ArrayList<>();
		int step = Math.max(1, maxChars - overlap);
		for (int start = 0; start < text.length(); start += step) {
			int end = Math.min(text.length(), start + maxChars);
			chunks.add(text.substring(start, end));
			if (end == text.length()) {
				break;
			}
		}
		return chunks;
	}
}
