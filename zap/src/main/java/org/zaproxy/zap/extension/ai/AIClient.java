/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2024 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.ai;

import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Client for interacting with AI models, specifically Ollama.
 * This class handles the low-level HTTP communication and JSON parsing.
 */
public class AIClient {

    private static final Logger LOGGER = LogManager.getLogger(AIClient.class);
    private final ExtensionAIAssistant extension;
    private final ExecutorService executor;

    public AIClient(ExtensionAIAssistant extension) {
        this.extension = extension;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends a prompt to the configured Ollama instance.
     *
     * @param prompt The prompt to send.
     * @param onResponse Callback for the response text.
     * @param onComplete Callback when the request is complete.
     */
    public void askOllama(String prompt, Consumer<String> onResponse, Consumer<String> onComplete) {
        // Use a background thread for network IO
        executor.submit(() -> {
            try {
                String ollamaUrl = extension.getAIOptions().getOllamaUrl();
                // Ensure no trailing slash
                if (ollamaUrl.endsWith("/")) {
                    ollamaUrl = ollamaUrl.substring(0, ollamaUrl.length() - 1);
                }
                String modelName = extension.getAIOptions().getModelName();

                // Using URI to create URL to avoid deprecation warning
                URL url = new URI(ollamaUrl + "/api/generate").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Use JSONObject for safe JSON construction
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("model", modelName);
                jsonParams.put("prompt", prompt);
                jsonParams.put("stream", false);

                String jsonInputString = jsonParams.toString();

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                StringBuilder responseBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }

                // Parse response
                String jsonResponseString = responseBuilder.toString();
                JSONObject jsonResponse = JSONObject.fromObject(jsonResponseString);

                if (jsonResponse.has("response")) {
                    onResponse.accept(jsonResponse.getString("response"));
                } else {
                    onResponse.accept("[Error: AI response did not contain a 'response' field]");
                }

                if (onComplete != null) {
                    onComplete.accept("");
                }

            } catch (Exception e) {
                LOGGER.error("Error communicating with Ollama: " + e.getMessage(), e);
                onResponse.accept("\n[Error: " + e.getMessage() + "]\n");
                if (onComplete != null) {
                    onComplete.accept("");
                }
            }
        });
    }

    /**
     * Analyzes a ZAP Alert using the AI.
     *
     * @param alert The alert to analyze.
     * @param onResponse Callback for the response.
     * @param onComplete Callback for completion.
     */
    public String buildAnalyzeAlertPrompt(Alert alert) {
        return "You are a cybersecurity expert. Analyze the following vulnerability found by ZAP:\n\n" +
            "Name: " + alert.getName() + "\n" +
            "Risk: " + Alert.MSG_RISK[alert.getRisk()] + "\n" +
            "Description: " + alert.getDescription() + "\n" +
            "URL: " + alert.getUri() + "\n" +
            "Parameter: " + alert.getParam() + "\n" +
            "Evidence: " + alert.getEvidence() + "\n\n" +
            "Please explain why this is a risk, how an attacker might exploit it, and provide specific code examples (if applicable) on how to fix it.";
    }

    public void analyzeAlert(Alert alert, Consumer<String> onResponse, Consumer<String> onComplete) {
        String prompt = "You are a cybersecurity expert. Analyze the following vulnerability found by ZAP:\n\n" +
            "Name: " + alert.getName() + "\n" +
            "Risk: " + Alert.MSG_RISK[alert.getRisk()] + "\n" +
            "Description: " + alert.getDescription() + "\n" +
            "URL: " + alert.getUri() + "\n" +
            "Parameter: " + alert.getParam() + "\n" +
            "Evidence: " + alert.getEvidence() + "\n\n" +
            "Please explain why this is a risk, how an attacker might exploit it, and provide specific code examples (if applicable) on how to fix it.";

        askOllama(prompt, onResponse, onComplete);
    }

    /**
     * Generates bug hunting suggestions based on context.
     *
     * @param context The context (e.g., URLs, parameters).
     * @param onResponse Callback for the response.
     * @param onComplete Callback for completion.
     */
    public void generateBugHuntingSuggestions(String context, Consumer<String> onResponse, Consumer<String> onComplete) {
         String prompt = "You are a bug bounty hunter assistant. Based on the following context, suggest potential attack vectors and hunting strategies:\n\n" +
            "Context: " + context + "\n\n" +
            "Provide a checklist of things to test.";

        askOllama(prompt, onResponse, onComplete);
    }
}
