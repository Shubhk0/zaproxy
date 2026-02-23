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
package org.zaproxy.zap.extension.ai.fuzz;

import org.zaproxy.zap.extension.ai.AIClient;

import java.util.function.Consumer;

/**
 * Helper class to generate fuzzing payloads using the AI client.
 */
public class PayloadGenerator {

    private final AIClient aiClient;

    public PayloadGenerator(AIClient aiClient) {
        this.aiClient = aiClient;
    }

    public void generatePayloads(String attackType, String context, int count, String modelName, Consumer<String> onResponse, Consumer<String> onComplete) {
        String prompt = "You are a specialized fuzzer payload generator. Generate a list of " + count + " unique and effective payloads for a '" + attackType + "' attack.\n" +
            "Context: " + context + "\n\n" +
            "Return ONLY the raw payloads, one per line. Do not include numbering or explanations.";

        // Use AIClient's askOllama, passing modelName as override?
        // AIClient currently uses the extension's configured model.
        // We'll need to update AIClient to support overriding model name per request.
        // Or we can just set it temporarily, but that's messy.
        // Let's modify AIClient to accept an optional modelName.
        aiClient.askOllama(prompt, modelName, onResponse, onComplete);
    }
}
