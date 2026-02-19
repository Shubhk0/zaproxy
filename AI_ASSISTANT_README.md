# ZAP AI Assistant & UI Improvements

This extension integrates local AI capabilities (via Ollama) into OWASP ZAP and introduces a modern Dark Mode for a better bug hunting experience.

## Features

### 1. AI Assistant Integration
Connects ZAP to a local LLM (Large Language Model) running via Ollama to assist with vulnerability analysis and bug hunting strategies.

*   **Chat Interface**: A dedicated "AI Assistant" panel allows you to chat with the AI about security topics, payload generation, and more.
*   **Vulnerability Analysis**: Right-click on any Alert in the Alerts tab and select **"AI Analyze"**. The AI will explain the risk, impact, and remediation steps.
*   **Hunting Suggestions**: Right-click on any Site node in the Sites tree and select **"AI Suggest"**. The AI will provide potential attack vectors based on the context.

### 2. Modern Dark Mode (Caido-like)
A sleek, dark interface using the `FlatDarculaLaf` theme to reduce eye strain during long sessions.

*   **Enable/Disable**: Go to **Tools -> Options -> AI Assistant** and toggle "Enable Dark Mode". Restart ZAP for changes to take full effect.

### 3. Enhanced Toolbar
A new **"AI Assistant"** button is added to the main toolbar for quick access to the AI panel.

## Prerequisites

*   **Ollama**: You must have [Ollama](https://ollama.com/) installed and running.
*   **DeepSeek Model**: By default, the extension uses the `deepseek-r1` model. You can change this in the options.
    *   Run: `ollama pull deepseek-r1` (or your preferred model).

## Configuration

1.  Open ZAP.
2.  Navigate to **Tools -> Options -> AI Assistant**.
3.  **Ollama URL**: Default is `http://localhost:11434`.
4.  **Model Name**: Default is `deepseek-r1`.
5.  **Dark Mode**: Check to enable the dark theme.

## Usage

*   **Chat**: Open the "AI Assistant" tab (usually at the bottom or right, or click the toolbar button). Type your query and press Send.
*   **Analyze Alert**: In the Alerts tab, right-click a vulnerability -> **AI Analyze**. The explanation will appear in the AI Assistant panel.
*   **Quick Scan**: Use the "Quick Scan" button in the AI panel to trigger a simplified active scan (placeholder).

## Troubleshooting

*   **Connection Error**: Ensure Ollama is running (`ollama serve`). Check if `http://localhost:11434` is accessible.
*   **Model Not Found**: Verify you have pulled the model specified in Options (`ollama list`).

## Development

The core logic resides in `org.zaproxy.zap.extension.ai`.
*   `AIClient`: Handles HTTP communication with Ollama.
*   `ExtensionAIAssistant`: Manages the extension lifecycle and hooks.
*   `AIPanel`: The Swing UI for the chat.

Enjoy your enhanced bug hunting experience!
