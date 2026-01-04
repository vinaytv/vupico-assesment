#!/bin/sh
set -e

echo "Waiting for Ollama..."
while ! wget -qO- http://ollama:11434/api/tags >/dev/null 2>&1; do
  sleep 1
done

echo "Pulling chat model: ${OLLAMA_CHAT_MODEL}"
wget -qO- http://ollama:11434/api/pull \
  --header='Content-Type: application/json' \
  --post-data="{\"name\":\"${OLLAMA_CHAT_MODEL}\"}" >/dev/null

echo "Pulling embed model: ${OLLAMA_EMBED_MODEL}"
wget -qO- http://ollama:11434/api/pull \
  --header='Content-Type: application/json' \
  --post-data="{\"name\":\"${OLLAMA_EMBED_MODEL}\"}" >/dev/null
