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

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.zaproxy.zap.extension.ai.AIClient;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for PayloadGenerator.
 */
public class PayloadGeneratorTest {

    @Test
    public void testGeneratePayloads() {
        // Mock AIClient
        AIClient client = mock(AIClient.class);
        PayloadGenerator generator = new PayloadGenerator(client);

        // Use count 5
        generator.generatePayloads("SQL Injection", "Login Form", 5, null, null);

        // Verify askOllama was called with expected prompt parts
        verify(client).askOllama(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String prompt) {
                return prompt.contains("SQL Injection") &&
                       prompt.contains("Login Form") &&
                       prompt.contains("5 unique and effective payloads");
            }
        }), any(), any());
    }
}
