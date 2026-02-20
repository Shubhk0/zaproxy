# ZAP AI Assistant & UI Improvements

This extension integrates local AI capabilities (via Ollama) into OWASP ZAP and introduces a modern Dark Mode for a better bug hunting experience.

## Features

### 1. AI Assistant Integration
Connects ZAP to a local LLM (Large Language Model) running via Ollama to assist with vulnerability analysis and bug hunting strategies.

*   **Chat Interface**: A dedicated "AI Assistant" panel allows you to chat with the AI about security topics.
*   **Vulnerability Analysis**: Right-click on any Alert in the Alerts tab and select **"AI Analyze"**. The AI will explain the risk, impact, and remediation steps.
*   **Web Search (RAG)**: The AI can optionally search the web (DuckDuckGo) for recent CVEs and exploit details to enrich its analysis of alerts.
*   **Hunting Suggestions**: Right-click on any Site node in the Sites tree and select **"AI Suggest"**. The AI will provide potential attack vectors based on the context.

### 2. LLM Security Testing
Tools to help you test *other* LLMs and AI applications.
*   **Generate Payload**: One-click generation of prompt injection and jailbreak payloads (inspired by Promptfoo).
*   **Resources**: Quick access to a curated list of AI security resources (Awesome AI Cybersecurity).

### 3. Modern Dark Mode (Caido-like)
A sleek, dark interface using the `FlatDarculaLaf` theme to reduce eye strain during long sessions.

### 4. Enhanced Toolbar
A new **"AI Assistant"** button is added to the main toolbar for quick access to the AI panel.

## Prerequisites

*   **Ollama**: You must have [Ollama](https://ollama.com/) installed and running.
*   **DeepSeek Model**: By default, the extension uses the `deepseek-r1` model. You can change this in the options.
    *   Run: `ollama pull deepseek-r1`

## Configuration

1.  Open ZAP.
2.  Navigate to **Tools -> Options -> AI Assistant**.
3.  **Ollama URL**: Default is `http://localhost:11434`.
4.  **Model Name**: Default is `deepseek-r1`.
5.  **Enable Web Search**: Check to allow the AI to search the internet for context (no API key required).
6.  **Dark Mode**: Check to enable the dark theme.

## Usage

*   **Chat**: Open the "AI Assistant" tab. Type your query and press Send.
*   **Analyze Alert**: In the Alerts tab, right-click a vulnerability -> **AI Analyze**.
    *   If Web Search is enabled, it will first fetch relevant info from the web.
*   **Gen Payload**: Click the "Gen Payload" button in the AI panel to get a list of LLM attack vectors.
*   **Resources**: Click "Resources" to see a list of helpful links.

## Troubleshooting

*   **Connection Error**: Ensure Ollama is running (`ollama serve`). Check if `http://localhost:11434` is accessible.
*   **Web Search Failed**: Ensure you have internet connectivity. The search scrapes DuckDuckGo HTML, which may be rate-limited if used excessively.

## Development

The core logic resides in `org.zaproxy.zap.extension.ai`.
*   `AIClient`: Handles HTTP communication with Ollama and Web Search.
*   `WebSearchEngine`: Scrapes search results using `Jericho HTML Parser`.
*   `ExtensionAIAssistant`: Manages the extension lifecycle.
*   `AIPanel`: The Swing UI.

Enjoy your enhanced bug hunting experience!
