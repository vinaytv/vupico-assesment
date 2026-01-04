# Assessment Spring AI Demo

## Overview
This project demonstrates:
- RAG-style question answering (`POST /ask`)
- Ticket classification (`POST /classify`)
- LLM gateway abstraction (`POST /generate`)

## RAG Flow
1. Load source documents from `src/main/resources/sourcedocs/*.txt`
2. Split into chunks and embed them (in-memory vector store)
3. Embed the incoming question
4. Run similarity search
5. Build a prompt with retrieved context
6. Generate the answer with the chat model

## Models Used (Ollama)
- Chat model: `phi3:mini`
- Embedding model: `nomic-embed-text`

You can override these with environment variables:
- `OLLAMA_CHAT_MODEL`
- `OLLAMA_EMBED_MODEL`

## Run Locally (Docker Compose)
1. Make sure Rancher Desktop or Docker is running with enough memory.
2. Start the stack:
   ```bash
   docker compose up --build
   ```

This will:
- start Ollama
- pull the chat + embedding models
- start the Spring Boot app on port `8080`

## Run Locally (Without Docker)
1. Ensure Ollama is running locally:
   ```bash
   ollama serve
   ```
2. Pull required models:
   ```bash
   ollama pull phi3:mini
   ollama pull nomic-embed-text
   ```
3. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Examples
### RAG Ask
```bash
curl -X POST http://localhost:8080/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"What is Spring AI?"}'
```

### Ticket Classify
```bash
curl -X POST http://localhost:8080/classify \
  -H 'Content-Type: application/json' \
  -d '{"text":"Customer charged twice on credit card"}'
```

### Gateway Generate
```bash
curl -X POST http://localhost:8080/generate \
  -H 'Content-Type: application/json' \
  -d '{"prompt":"Summarize Spring AI in one sentence."}'
```
